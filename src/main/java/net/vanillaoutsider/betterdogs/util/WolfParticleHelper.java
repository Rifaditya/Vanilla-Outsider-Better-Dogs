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

    private static final net.minecraft.core.particles.ItemParticleOption RUNT_PARTICLE = new net.minecraft.core.particles.ItemParticleOption(net.minecraft.core.particles.ParticleTypes.ITEM, net.minecraft.world.item.Items.ROTTEN_FLESH);

    public static void tickRuntParticles(Wolf wolf, ServerLevel serverLevel) {
        if (wolf == null || serverLevel == null) return;
        if (DynamicGameRuleManager.getBoolean(serverLevel, BetterDogsGameRules.BD_SHOW_RUNT_PARTICLES)
                || DynamicGameRuleManager.getBoolean(serverLevel, BetterDogsGameRules.BD_DEBUGGING)) {
            double px = wolf.getRandomX(0.4);
            double py = wolf.getRandomY() + 0.2;
            double pz = wolf.getRandomZ(0.4);
            serverLevel.sendParticles(RUNT_PARTICLE, px, py, pz, 1, 0.01, 0.01, 0.01, 0.01);
        }
    }

    public static void tickAdoptableParticles(Wolf wolf, ServerLevel serverLevel) {
        if (wolf == null || serverLevel == null) return;
        net.minecraft.util.RandomSource random = serverLevel.getRandom();
        for (int i = 0; i < 4; ++i) {
            net.minecraft.world.phys.Vec3 source = wolf.position().add(random.nextDouble() * 0.6 - 0.3, random.nextDouble() * 0.5, random.nextDouble() * 0.6 - 0.3);
            net.minecraft.world.phys.Vec3 destination = wolf.position().add(random.nextDouble() * 0.8 - 0.4, wolf.getEyeHeight() + 0.5 + random.nextDouble() * 0.5, random.nextDouble() * 0.8 - 0.4);
            net.minecraft.core.particles.TrailParticleOption trail = new net.minecraft.core.particles.TrailParticleOption(destination, 0xFF99BB, random.nextInt(20) + 15);
            serverLevel.sendParticles(trail, true, true, source.x, source.y, source.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}
