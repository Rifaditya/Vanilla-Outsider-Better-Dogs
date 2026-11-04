// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.SmallFightHelper;

import java.util.EnumSet;
import java.util.List;

/**
 * Priority 1 reactive evasion AI goal for canines.
 * Detects incoming high-threat ballistic projectiles (Ghast fireballs, Blaze fireballs,
 * Wither skulls) on collision course, executes emergency unseating scramble if sitting,
 * evaluates ground safety, and applies lateral dodge hop impulse.
 */
public class AvoidGhastFireballGoal extends Goal {

    public static final double DEFAULT_THREAT_RADIUS = 1.75;
    public static final double EMERGENCY_SCRAMBLE_DISTANCE = 1.5;
    public static final double SEARCH_RADIUS = 16.0;

    private final Wolf wolf;
    private Projectile targetProjectile;
    private Vec3 resolvedDodgeImpulse = Vec3.ZERO;
    private boolean wasSitting = false;
    private int cooldownTicks = 0;
    private int activeTicks = 0;

    public AvoidGhastFireballGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    /**
     * Predicate evaluating whether an entity is an incoming high-threat ballistic projectile.
     */
    public static boolean isThreateningProjectile(Entity entity) {
        if (entity == null || !entity.isAlive()) {
            return false;
        }
        return entity instanceof AbstractHurtingProjectile;
    }

    /**
     * Pure testable predicate checking if a sitting dog must scramble out of sitting posture.
     * Sitting dogs break posture to evade when the incoming projectile path passes within 1.5m
     * and impact is imminent (0 < tau <= 20 ticks).
     */
    public static boolean shouldScrambleFromSitting(double perpDistance, double timeToImpact) {
        return perpDistance <= EMERGENCY_SCRAMBLE_DISTANCE && timeToImpact > 0.0 && timeToImpact <= 20.0;
    }

    /**
     * Evaluates whether the ballistic evasion reflex can trigger given posture, distance, and timing.
     */
    public static boolean canTriggerReflex(boolean isSitting, double perpDistance, double timeToImpact, double reactionTicks) {
        if (timeToImpact <= 0.0 || timeToImpact > reactionTicks || perpDistance > DEFAULT_THREAT_RADIUS) {
            return false;
        }
        if (isSitting) {
            return shouldScrambleFromSitting(perpDistance, timeToImpact);
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

        if (this.cooldownTicks > 0) {
            this.cooldownTicks--;
            return false;
        }

        if (!BetterDogsGameRules.isBallisticEvasionEnabled(this.wolf.level())) {
            return false;
        }

        int reactionTicks = BetterDogsGameRules.getEvasionReactionTicks(this.wolf.level());
        int lateralImpulse = BetterDogsGameRules.getEvasionLateralImpulse(this.wolf.level());

        AABB searchBox = this.wolf.getBoundingBox().inflate(SEARCH_RADIUS);
        List<Projectile> projectiles = this.wolf.level().getEntitiesOfClass(
                Projectile.class,
                searchBox,
                AvoidGhastFireballGoal::isThreateningProjectile
        );

        if (projectiles.isEmpty()) {
            return false;
        }

        boolean isSitting = this.wolf.isOrderedToSit() || this.wolf.isInSittingPose();
        Projectile bestProjectile = null;
        FireballTrajectoryHelper.TrajectoryInterception bestInterception = null;
        double minTau = Double.MAX_VALUE;

        Vec3 wolfPos = this.wolf.position();

        for (Projectile proj : projectiles) {
            Vec3 projPos = proj.position();
            Vec3 projVel = proj.getDeltaMovement();

            FireballTrajectoryHelper.TrajectoryInterception interception =
                    FireballTrajectoryHelper.computeInterception(wolfPos, projPos, projVel, reactionTicks, DEFAULT_THREAT_RADIUS, lateralImpulse);

            if (!interception.intercepted()) {
                continue;
            }

            if (!canTriggerReflex(isSitting, interception.perpendicularDistance(), interception.timeToImpactTicks(), reactionTicks)) {
                continue;
            }

            if (interception.timeToImpactTicks() < minTau) {
                minTau = interception.timeToImpactTicks();
                bestProjectile = proj;
                bestInterception = interception;
            }
        }

        if (bestProjectile == null || bestInterception == null) {
            return false;
        }

        Vec3 dodgeImpulse = bestInterception.dodgeImpulse();

        // Ground safety check: verify destination coordinates
        Vec3 landingOffset = new Vec3(dodgeImpulse.x * 1.5, 0.0, dodgeImpulse.z * 1.5);
        BlockPos targetBlock = BlockPos.containing(wolfPos.add(landingOffset));

        if (!FireballTrajectoryHelper.isGroundSafe(this.wolf.level(), targetBlock)) {
            // Invert lateral dodge vector away from hazard
            Vec3 invertedImpulse = new Vec3(-dodgeImpulse.x, dodgeImpulse.y, -dodgeImpulse.z);
            BlockPos invertedTarget = BlockPos.containing(wolfPos.add(new Vec3(invertedImpulse.x * 1.5, 0.0, invertedImpulse.z * 1.5)));
            if (FireballTrajectoryHelper.isGroundSafe(this.wolf.level(), invertedTarget)) {
                dodgeImpulse = invertedImpulse;
            }
        }

        this.targetProjectile = bestProjectile;
        this.resolvedDodgeImpulse = dodgeImpulse;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.activeTicks > 0 && this.wolf != null && this.wolf.isAlive();
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

        // Apply dodge velocity and jump hop
        this.wolf.setDeltaMovement(this.resolvedDodgeImpulse);
        this.wolf.getJumpControl().jump();

        if (this.targetProjectile != null) {
            this.wolf.getLookControl().setLookAt(this.targetProjectile, 30.0F, 30.0F);
        }

        playSensoryFeedback();
        this.activeTicks = 10;
    }

    @Override
    public void tick() {
        if (this.activeTicks > 0) {
            this.activeTicks--;
        }

        if (this.wolf == null || this.wolf.level() == null || this.wolf.level().isClientSide()) {
            return;
        }

        if (this.wolf.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    this.wolf.getX(),
                    this.wolf.getY() + 0.1,
                    this.wolf.getZ(),
                    2,
                    0.1, 0.05, 0.1,
                    0.02
            );
        }
    }

    @Override
    public void stop() {
        if (this.wasSitting) {
            this.wolf.setOrderedToSit(true);
            this.wolf.setInSittingPose(true);
            this.wasSitting = false;
        }

        this.targetProjectile = null;
        this.resolvedDodgeImpulse = Vec3.ZERO;
        this.cooldownTicks = 15;
    }

    private void playSensoryFeedback() {
        if (this.wolf == null || this.wolf.level() == null || this.wolf.level().isClientSide()) {
            return;
        }

        if (this.wolf.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    this.wolf.getX(),
                    this.wolf.getY() + 0.2,
                    this.wolf.getZ(),
                    8,
                    0.25, 0.15, 0.25,
                    0.05
            );

            boolean isFearless = this.wolf instanceof WolfExtensions ext
                    && ext.betterdogs$getCanineArchetype() == CanineArchetype.FEARLESS;

            SoundEvent sound = isFearless ? SmallFightHelper.getWolfPantSound(this.wolf) : SmallFightHelper.getWolfWhineSound(this.wolf);

            serverLevel.playSound(
                    null,
                    this.wolf.getX(),
                    this.wolf.getY(),
                    this.wolf.getZ(),
                    sound,
                    SoundSource.NEUTRAL,
                    0.9F,
                    (this.wolf.isBaby() ? 1.1F : 0.9F) + this.wolf.getRandom().nextFloat() * 0.2F
            );
        }
    }

    public Projectile getTargetProjectile() {
        return this.targetProjectile;
    }

    public Vec3 getResolvedDodgeImpulse() {
        return this.resolvedDodgeImpulse;
    }

    public boolean wasSitting() {
        return this.wasSitting;
    }

    public int getActiveTicks() {
        return this.activeTicks;
    }

    public int getCooldownTicks() {
        return this.cooldownTicks;
    }

    public void setCooldownTicks(int cooldownTicks) {
        this.cooldownTicks = cooldownTicks;
    }
}
