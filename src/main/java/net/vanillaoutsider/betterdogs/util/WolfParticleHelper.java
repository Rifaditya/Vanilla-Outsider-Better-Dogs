// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Single-purpose helper managing particle density scaling and server dispatch for wolf cues.
 */
public final class WolfParticleHelper {

    private WolfParticleHelper() {
    }

    /**
     * Retrieves the current ParticleDensity from the level's GameRule.
     */
    public static ParticleDensity getDensity(Level level) {
        if (level == null) {
            return ParticleDensity.MEDIUM;
        }
        try {
            int levelInt = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_PARTICLE_DENSITY);
            return ParticleDensity.fromInt(levelInt);
        } catch (Throwable ignored) {
            return ParticleDensity.MEDIUM;
        }
    }

    /**
     * Calculates the scaled particle count based on density tier.
     */
    public static int getScaledCount(Level level, ParticleDensity defaultDensity) {
        ParticleDensity current = getDensity(level);
        return current.getDefaultCount();
    }

    /**
     * Spawns particles at the wolf's location if particle density is greater than NONE.
     */
    public static void spawnParticles(Wolf wolf, ParticleOptions particle, double yOffset, double xOffset, double ySpread, double zSpread, double speed) {
        if (wolf == null || wolf.level() == null || wolf.level().isClientSide()) {
            return;
        }

        int count = getScaledCount(wolf.level(), ParticleDensity.MEDIUM);
        if (count <= 0) {
            return;
        }

        if (wolf.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    particle,
                    wolf.getX(),
                    wolf.getY() + yOffset,
                    wolf.getZ(),
                    count,
                    xOffset,
                    ySpread,
                    zSpread,
                    speed
            );
        }
    }

    /**
     * Calculates the scaled subtle particle count (NONE: 0, LOW: 1, MEDIUM/HIGH: 2).
     */
    public static int getSubtleCount(Level level) {
        ParticleDensity current = getDensity(level);
        return switch (current) {
            case NONE -> 0;
            case LOW -> 1;
            default -> 2;
        };
    }

    /**
     * Spawns subtle particles at the wolf's location scaled by the density GameRule.
     */
    public static void spawnSubtleParticles(Wolf wolf, ParticleOptions particle, double yOffset, double xOffset, double ySpread, double zSpread, double speed) {
        if (wolf == null || wolf.level() == null || wolf.level().isClientSide()) {
            return;
        }

        int count = getSubtleCount(wolf.level());
        if (count <= 0) {
            return;
        }

        if (wolf.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    particle,
                    wolf.getX(),
                    wolf.getY() + yOffset,
                    wolf.getZ(),
                    count,
                    xOffset,
                    ySpread,
                    zSpread,
                    speed
            );
        }
    }
}
