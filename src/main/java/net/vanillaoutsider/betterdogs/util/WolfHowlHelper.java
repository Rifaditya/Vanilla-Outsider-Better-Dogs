// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.BetterDogs;
import net.vanillaoutsider.betterdogs.WolfExtensions;

import java.util.List;

/**
 * Dedicated single-purpose helper for spontaneous pack howling propagation,
 * musical harmonic pitch variation, and nocturnal pack chorus synchronization.
 */
public final class WolfHowlHelper {

    public static final double DEFAULT_HOWL_RADIUS = 24.0D;
    public static final int BASE_HOWL_DURATION = 60; // 3 seconds (60 ticks)

    private WolfHowlHelper() {
    }

    /**
     * Pure math: Computes harmonized pitch variation between 0.85F and 1.20F.
     */
    public static float calculateHarmonicPitch(float randomFloat) {
        float clamped = Math.max(0.0f, Math.min(1.0f, randomFloat));
        return 0.85f + (clamped * 0.35f);
    }

    /**
     * Pure math: Computes staggered chorus delay between 10 and 34 ticks.
     */
    public static int calculateChorusDelay(int randomOffset) {
        int clamped = Math.max(0, Math.min(24, randomOffset));
        return 10 + clamped;
    }

    /**
     * Verifies if a wolf is eligible to start or join a group chorus howl.
     */
    public static boolean canJoinHowl(Wolf wolf) {
        if (wolf == null || !wolf.isAlive() || !wolf.isTame() || wolf.isBaby()) {
            return false;
        }

        if (wolf.isOrderedToSit() || wolf.getTarget() != null) {
            return false;
        }

        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$isGuardMode() || ext.betterdogs$isSittingManually()) {
                return false;
            }
            return ext.betterdogs$getHowlingTicks() <= 0;
        }

        return true;
    }

    /**
     * Initiates a pack chorus howl from an initiating wolf, alerting nearby packmates within radius.
     */
    public static void initiateChorusHowl(Wolf initiator, double radius) {
        if (initiator == null) {
            return;
        }

        Level level = initiator.level();
        if (level == null || level.isClientSide()) {
            return;
        }

        // 1. Play initiator howl
        startHowl(initiator, 1.0f);

        // 2. Alert and stagger nearby packmates
        double searchRadius = radius > 0 ? radius : DEFAULT_HOWL_RADIUS;
        List<Wolf> nearbyWolves = level.getEntitiesOfClass(
                Wolf.class,
                initiator.getBoundingBox().inflate(searchRadius),
                w -> w != initiator && canJoinHowl(w)
        );

        for (Wolf packWolf : nearbyWolves) {
            if (packWolf instanceof WolfExtensions ext) {
                int delay = calculateChorusDelay(packWolf.getRandom().nextInt(25));
                ext.betterdogs$setHowlingTicks(BASE_HOWL_DURATION + delay);
            }
        }
    }

    /**
     * Plays the howl sound effect and emits musical note particles for an individual wolf.
     */
    public static void startHowl(Wolf wolf, float pitch) {
        if (wolf == null) {
            return;
        }

        Level level = wolf.level();
        if (level == null || level.isClientSide()) {
            return;
        }

        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setHowlingTicks(BASE_HOWL_DURATION);
        }

        level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), BetterDogs.WOLF_HOWL, SoundSource.NEUTRAL, 1.2f, pitch);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.NOTE, wolf.getX(), wolf.getY() + 0.8, wolf.getZ(), 2, 0.2, 0.2, 0.2, pitch / 24.0);
        }
    }
}
