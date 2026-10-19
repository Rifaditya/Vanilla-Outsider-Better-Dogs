// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.11
package net.vanillaoutsider.betterdogs.ai.play;

import java.util.EnumSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.SmallFightHelper;
import org.jetbrains.annotations.Nullable;

/**
 * Dedicated AI Goal for follow-the-leader chase play between two compatible wolves.
 * Guides the chasing dog toward its play partner at an accelerated pace (1.25x),
 * periodically emitting happy villager particles and playful panting vocalizations.
 */
public class PlayChaseGoal extends Goal {

    public static final double DEFAULT_PARTNER_RADIUS = 16.0;
    public static final double DEFAULT_SPEED_MODIFIER = 1.25;
    public static final int MAX_CHASE_TICKS = 240; // ~12 seconds
    public static final double DEFAULT_MAX_PLAY_DISTANCE_SQR = 256.0; // 16 blocks squared

    private final Wolf wolf;
    private @Nullable Wolf partner;
    private int chaseTicks = 0;

    public PlayChaseGoal(Wolf wolf) {
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
        this.chaseTicks = 0;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf == null || !this.wolf.isAlive() || this.partner == null || !this.partner.isAlive()) {
            return false;
        }
        if (this.chaseTicks >= MAX_CHASE_TICKS) {
            return false;
        }

        double maxDist = 16.0;
        if (this.wolf.level() != null) {
            maxDist = BetterDogsGameRules.getMaxPlayDistance(this.wolf.level());
        }
        double maxDistSqr = maxDist * maxDist;

        boolean abort = shouldAbortChase(
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

        this.chaseTicks++;

        if (this.wolf.getLookControl() != null) {
            this.wolf.getLookControl().setLookAt(this.partner, 30.0F, 30.0F);
        }

        if (this.wolf.getNavigation() != null) {
            this.wolf.getNavigation().moveTo(this.partner, DEFAULT_SPEED_MODIFIER);
        }

        // Periodic playful pant audio and happy villager particles every 40 ticks (~2 seconds)
        if (this.chaseTicks % 40 == 0) {
            if (this.wolf.level() instanceof ServerLevel serverLevel) {
                SoundEvent pantSound = getPantSound(this.wolf);
                float pitch = 0.9F + (this.wolf.getRandom() != null ? this.wolf.getRandom().nextFloat() * 0.2F : 0.1F);
                serverLevel.playSound(null, this.wolf.getX(), this.wolf.getY(), this.wolf.getZ(),
                        pantSound, SoundSource.NEUTRAL, 0.8F, pitch);
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        this.wolf.getX(), this.wolf.getY() + 0.3, this.wolf.getZ(),
                        2, 0.2, 0.2, 0.2, 0.02);
            }
        }
    }

    @Override
    public void stop() {
        if (this.wolf != null && this.wolf.getNavigation() != null) {
            this.wolf.getNavigation().stop();
        }
        this.partner = null;
        this.chaseTicks = 0;
    }

    public @Nullable Wolf getPartner() {
        return this.partner;
    }

    public void setPartner(@Nullable Wolf partner) {
        this.partner = partner;
    }

    public int getChaseTicks() {
        return this.chaseTicks;
    }

    public void setChaseTicks(int ticks) {
        this.chaseTicks = ticks;
    }

    public static SoundEvent getPantSound(Wolf wolf) {
        return SmallFightHelper.getWolfPantSound(wolf);
    }

    /**
     * Pure logical predicate for evaluating play chase abort conditions.
     */
    public static boolean shouldAbortChase(boolean isPartnerNull, boolean isPartnerAlive,
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
