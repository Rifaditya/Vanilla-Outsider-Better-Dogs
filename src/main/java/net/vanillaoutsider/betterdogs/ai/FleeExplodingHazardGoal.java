// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfParticleHelper;

import java.util.EnumSet;

/**
 * AI goal providing canine explosive reflex: detects primed TNT, End Crystals,
 * and swelling/ignited Creepers within threat radius, sprinting away at 1.7x speed.
 * Features an emergency blast scramble for sitting dogs within 4 blocks.
 */
public class FleeExplodingHazardGoal extends Goal {

    public static final double EMERGENCY_SCRAMBLE_DISTANCE = 4.0;
    public static final double EMERGENCY_SCRAMBLE_DISTANCE_SQ = EMERGENCY_SCRAMBLE_DISTANCE * EMERGENCY_SCRAMBLE_DISTANCE; // 16.0

    private final Wolf wolf;
    private HazardDetectionHelper.DetonationHazard currentHazard;
    private boolean wasSitting = false;
    private double fleePosX;
    private double fleePosY;
    private double fleePosZ;

    public FleeExplodingHazardGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    /**
     * Pure predicate checking if a sitting dog should scramble away given the squared hazard distance.
     * Sitting dogs break posture to evade explosions only if the hazard is within 4.0 blocks (distanceSq <= 16.0).
     */
    public static boolean shouldScrambleFromSitting(double distanceSq) {
        return distanceSq <= EMERGENCY_SCRAMBLE_DISTANCE_SQ;
    }

    /**
     * Evaluates whether a wolf with the specified sitting status can trigger the flee reflex.
     */
    public static boolean canTriggerReflex(boolean isSitting, double distanceSq, double fleeRadiusSq) {
        if (distanceSq > fleeRadiusSq) {
            return false;
        }
        if (isSitting) {
            return shouldScrambleFromSitting(distanceSq);
        }
        return true;
    }

    @Override
    public boolean canUse() {
        if (this.wolf == null || !this.wolf.isAlive() || this.wolf.isPassenger()) {
            return false;
        }

        if (this.wolf.level() == null || this.wolf.level().isClientSide()) {
            return false;
        }

        if (!BetterDogsGameRules.isCanineExplosiveReflexEnabled(this.wolf.level())) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$getCanineArchetype() == CanineArchetype.FEARLESS) {
            return false;
        }

        double fleeRadius = BetterDogsGameRules.getHazardFleeRadius(this.wolf.level());
        HazardDetectionHelper.DetonationHazard hazard = HazardDetectionHelper.findNearestHazard(this.wolf, fleeRadius);
        if (hazard == null) {
            return false;
        }

        boolean isSitting = this.wolf.isOrderedToSit() || this.wolf.isInSittingPose();
        if (isSitting && !shouldScrambleFromSitting(hazard.distanceSq())) {
            return false;
        }

        Vec3 posAway = DefaultRandomPos.getPosAway(this.wolf, 16, 7, hazard.position());
        if (posAway == null) {
            posAway = DefaultRandomPos.getPos(this.wolf, 16, 7);
        }

        if (posAway == null) {
            return false;
        }

        this.currentHazard = hazard;
        this.fleePosX = posAway.x;
        this.fleePosY = posAway.y;
        this.fleePosZ = posAway.z;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf == null || !this.wolf.isAlive()) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$getCanineArchetype() == CanineArchetype.FEARLESS) {
            return false;
        }

        return !this.wolf.getNavigation().isDone();
    }

    @Override
    public void start() {
        boolean sitting = this.wolf.isOrderedToSit() || this.wolf.isInSittingPose();
        if (sitting) {
            this.wasSitting = true;
            this.wolf.setOrderedToSit(false);
            this.wolf.setInSittingPose(false);
        } else {
            this.wasSitting = false;
        }

        // Creeper combat disengage: if target is a swelling/ignited creeper or the hazard itself, disengage target
        if (this.wolf.getTarget() instanceof Creeper creeper && (creeper.isIgnited() || creeper.getSwellDir() > 0)) {
            this.wolf.setTarget(null);
        } else if (this.currentHazard != null && this.wolf.getTarget() == this.currentHazard.entity()) {
            this.wolf.setTarget(null);
        }

        double speed = BetterDogsGameRules.getFleeSpeedMultiplier(this.wolf.level()) / 100.0;
        this.wolf.getNavigation().moveTo(this.fleePosX, this.fleePosY, this.fleePosZ, speed);

        playWhineFeedback();
    }

    @Override
    public void stop() {
        if (this.wasSitting) {
            this.wolf.setOrderedToSit(true);
            this.wolf.setInSittingPose(true);
            this.wasSitting = false;
        }
        this.currentHazard = null;
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.wolf == null || this.wolf.level() == null || this.wolf.level().isClientSide()) {
            return;
        }

        WolfParticleHelper.spawnParticles(
                this.wolf,
                ParticleTypes.POOF,
                0.2,
                0.1,
                0.05,
                0.1,
                0.02
        );
    }

    private void playWhineFeedback() {
        if (this.wolf == null || this.wolf.level() == null || this.wolf.level().isClientSide()) {
            return;
        }
        if (this.wolf.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(
                    null,
                    this.wolf.getX(),
                    this.wolf.getY(),
                    this.wolf.getZ(),
                    SoundEvents.WOLF_WHINE_BABY.value(),
                    SoundSource.NEUTRAL,
                    0.9F,
                    (this.wolf.isBaby() ? 1.0F : 0.8F) + this.wolf.getRandom().nextFloat() * 0.2F
            );
        }
    }

    public HazardDetectionHelper.DetonationHazard getCurrentHazard() {
        return this.currentHazard;
    }

    public boolean wasSitting() {
        return this.wasSitting;
    }
}
