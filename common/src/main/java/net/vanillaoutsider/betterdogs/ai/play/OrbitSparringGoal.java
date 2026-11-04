// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs.ai.play;

import java.util.EnumSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.jetbrains.annotations.Nullable;

/**
 * Dedicated AI Goal for playful circular sparring / orbit choreography around a partner dog.
 * Navigates in an arc around the target partner using circular vector math, adopting
 * playful play-bow head tilts, happy villager particle bursts, and playful vocalizations.
 */
public class OrbitSparringGoal extends Goal {

    public static final double DEFAULT_PARTNER_RADIUS = 16.0;
    public static final double DEFAULT_SPEED_MODIFIER = 1.15;
    public static final double DEFAULT_ORBIT_RADIUS = 3.0; // 2.5 - 3.5 blocks
    public static final double DEFAULT_ANGULAR_STEP = 0.25; // radians (~14.3 degrees per step)
    public static final int MAX_ORBIT_TICKS = 200; // ~10 seconds
    public static final double DEFAULT_MAX_PLAY_DISTANCE_SQR = 256.0;

    private final Wolf wolf;
    private @Nullable Wolf partner;
    private double orbitRadius = DEFAULT_ORBIT_RADIUS;
    private double angularStep = DEFAULT_ANGULAR_STEP;
    private int orbitTicks = 0;

    public OrbitSparringGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!PlayChoreographyHelper.canInitiatePlay(this.wolf)) {
            return false;
        }
        Wolf found = PlayChoreographyHelper.findPlayPartner(this.wolf, DEFAULT_PARTNER_RADIUS);
        if (found == null) {
            return false;
        }
        this.partner = found;
        return true;
    }

    @Override
    public void start() {
        this.orbitTicks = 0;
        if (this.wolf != null) {
            this.wolf.setIsInterested(true);
        }
        if (this.partner != null) {
            this.partner.setIsInterested(true);
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf == null || !this.wolf.isAlive() || this.partner == null || !this.partner.isAlive()) {
            return false;
        }
        if (this.orbitTicks >= MAX_ORBIT_TICKS) {
            return false;
        }

        double maxDist = 16.0;
        if (this.wolf.level() != null) {
            maxDist = BetterDogsGameRules.getMaxPlayDistance(this.wolf.level());
        }
        double maxDistSqr = maxDist * maxDist;

        boolean abort = shouldAbortOrbit(
                false,
                this.partner.isAlive(),
                this.partner.isOrderedToSit() || this.partner.isInSittingPose(),
                this.wolf.isOrderedToSit() || this.wolf.isInSittingPose(),
                this.wolf.getTarget() != null,
                this.partner.getTarget() != null,
                this.wolf.getHealth(),
                this.wolf.getMaxHealth(),
                this.partner.getHealth(),
                this.partner.getMaxHealth(),
                this.wolf.distanceToSqr(this.partner),
                maxDistSqr
        );
        return !abort;
    }

    @Override
    public void tick() {
        if (this.partner == null) {
            return;
        }

        this.orbitTicks++;

        if (this.wolf.getLookControl() != null) {
            this.wolf.getLookControl().setLookAt(this.partner, 30.0F, 30.0F);
        }

        // Maintain playful head tilt interest pose
        this.wolf.setIsInterested(true);

        // Recalculate circular orbit destination every 8 ticks or when path is complete
        if (this.orbitTicks % 8 == 0 || (this.wolf.getNavigation() != null && this.wolf.getNavigation().isDone())) {
            Vec3 nextPos = PlayChoreographyHelper.calculateOrbitVector(
                    this.partner.position(),
                    this.wolf.position(),
                    this.angularStep,
                    this.orbitRadius
            );

            // Hazard and cliff safety check before moving
            if (PlayChoreographyHelper.isSafePath(this.wolf, nextPos)) {
                if (this.wolf.getNavigation() != null) {
                    this.wolf.getNavigation().moveTo(nextPos.x, nextPos.y, nextPos.z, DEFAULT_SPEED_MODIFIER);
                }
            } else {
                // Reverse direction if path hits a cliff or thermal hazard
                this.angularStep = -this.angularStep;
                Vec3 reversePos = PlayChoreographyHelper.calculateOrbitVector(
                        this.partner.position(),
                        this.wolf.position(),
                        this.angularStep,
                        this.orbitRadius
                );
                if (PlayChoreographyHelper.isSafePath(this.wolf, reversePos)) {
                    if (this.wolf.getNavigation() != null) {
                        this.wolf.getNavigation().moveTo(reversePos.x, reversePos.y, reversePos.z, DEFAULT_SPEED_MODIFIER);
                    }
                }
            }
        }

        // Periodic happy villager particles and ambient/pant sounds every 30 ticks (~1.5s)
        if (this.orbitTicks % 30 == 0) {
            if (this.wolf.level() instanceof ServerLevel serverLevel) {
                SoundEvent sound = (this.orbitTicks % 60 == 0) ? getAmbientSound(this.wolf) : getPantSound(this.wolf);
                float pitch = 1.1F + (this.wolf.getRandom() != null ? this.wolf.getRandom().nextFloat() * 0.2F : 0.1F);
                serverLevel.playSound(null, this.wolf.getX(), this.wolf.getY(), this.wolf.getZ(),
                        sound, SoundSource.NEUTRAL, 0.8F, pitch);
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        this.wolf.getX(), this.wolf.getY() + 0.3, this.wolf.getZ(),
                        2, 0.2, 0.2, 0.2, 0.02);
            }
        }
    }

    @Override
    public void stop() {
        if (this.wolf != null) {
            if (this.wolf.getNavigation() != null) {
                this.wolf.getNavigation().stop();
            }
            this.wolf.setIsInterested(false);
        }
        if (this.partner != null && this.partner.isAlive()) {
            this.partner.setIsInterested(false);
        }
        this.partner = null;
        this.orbitTicks = 0;
    }

    public @Nullable Wolf getPartner() {
        return this.partner;
    }

    public void setPartner(@Nullable Wolf partner) {
        this.partner = partner;
    }

    public double getOrbitRadius() {
        return this.orbitRadius;
    }

    public void setOrbitRadius(double orbitRadius) {
        this.orbitRadius = orbitRadius;
    }

    public double getAngularStep() {
        return this.angularStep;
    }

    public void setAngularStep(double angularStep) {
        this.angularStep = angularStep;
    }

    public int getOrbitTicks() {
        return this.orbitTicks;
    }

    public void setOrbitTicks(int ticks) {
        this.orbitTicks = ticks;
    }

    public static SoundEvent getPantSound(Wolf wolf) {
        return SoundEvents.WOLF_PANT;
    }

    public static SoundEvent getAmbientSound(Wolf wolf) {
        return SoundEvents.WOLF_AMBIENT;
    }

    /**
     * Pure logical predicate for evaluating orbit sparring abort conditions.
     */
    public static boolean shouldAbortOrbit(boolean isPartnerNull, boolean isPartnerAlive,
                                           boolean isPartnerSitting, boolean isWolfSitting,
                                           boolean hasWolfTarget, boolean hasPartnerTarget,
                                           float wolfHealth, float wolfMaxHealth,
                                           float partnerHealth, float partnerMaxHealth,
                                           double distanceSqr, double maxPlayDistanceSqr) {
        if (isPartnerNull || !isPartnerAlive || isPartnerSitting || isWolfSitting) {
            return true;
        }
        if (hasWolfTarget || hasPartnerTarget) {
            return true;
        }
        if (wolfMaxHealth <= 0.0f || Float.isNaN(wolfHealth) || wolfHealth < 0.7f * wolfMaxHealth) {
            return true;
        }
        if (partnerMaxHealth <= 0.0f || Float.isNaN(partnerHealth) || partnerHealth < 0.7f * partnerMaxHealth) {
            return true;
        }
        return distanceSqr > maxPlayDistanceSqr;
    }
}
