// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class WolfParticleHandler {

    public static void playTameParticles(Wolf wolf, WolfPersonality personality) {
        if (wolf.level() instanceof ServerLevel serverLevel) {
            double wx = wolf.getX();
            double wy = wolf.getY() + 0.5;
            double wz = wolf.getZ();

            switch (personality) {
                case AGGRESSIVE -> {
                    // Minimal Signature (~6 particles): 3 Angry Villager icons + 3 Crimson dust sparkles
                    for (int i = 0; i < 3; i++) {
                        serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                                wx + wolf.getRandom().nextGaussian() * 0.3,
                                wy + wolf.getRandom().nextDouble() * 0.4,
                                wz + wolf.getRandom().nextGaussian() * 0.3, 1, 0, 0, 0, 0);
                    }
                    DustParticleOptions crimsonDust = new DustParticleOptions(0xFF3333, 0.8f);
                    for (int i = 0; i < 3; i++) {
                        serverLevel.sendParticles(crimsonDust,
                                wx + wolf.getRandom().nextGaussian() * 0.3,
                                wy + wolf.getRandom().nextDouble() * 0.3,
                                wz + wolf.getRandom().nextGaussian() * 0.3, 1, 0.02, 0.02, 0.02, 0.01);
                    }
                }
                case PACIFIST -> {
                    // Minimal Signature (~6 particles): 3 Heart icons + 3 Mint dust sparkles
                    for (int i = 0; i < 3; i++) {
                        serverLevel.sendParticles(ParticleTypes.HEART,
                                wx + wolf.getRandom().nextGaussian() * 0.3,
                                wy + wolf.getRandom().nextDouble() * 0.4,
                                wz + wolf.getRandom().nextGaussian() * 0.3, 1, 0, 0, 0, 0);
                    }
                    DustParticleOptions mintDust = new DustParticleOptions(0x00FF88, 0.8f);
                    for (int i = 0; i < 3; i++) {
                        serverLevel.sendParticles(mintDust,
                                wx + wolf.getRandom().nextGaussian() * 0.3,
                                wy + wolf.getRandom().nextDouble() * 0.3,
                                wz + wolf.getRandom().nextGaussian() * 0.3, 1, 0.02, 0.02, 0.02, 0.01);
                    }
                }
                case NORMAL -> {
                    // Minimal Signature (~6 particles): 3 Happy Villager icons + 3 Golden dust sparkles
                    for (int i = 0; i < 3; i++) {
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                wx + wolf.getRandom().nextGaussian() * 0.3,
                                wy + wolf.getRandom().nextDouble() * 0.4,
                                wz + wolf.getRandom().nextGaussian() * 0.3, 1, 0, 0, 0, 0);
                    }
                    DustParticleOptions goldenDust = new DustParticleOptions(0xFFD700, 0.8f);
                    for (int i = 0; i < 3; i++) {
                        serverLevel.sendParticles(goldenDust,
                                wx + wolf.getRandom().nextGaussian() * 0.3,
                                wy + wolf.getRandom().nextDouble() * 0.3,
                                wz + wolf.getRandom().nextGaussian() * 0.3, 1, 0.02, 0.02, 0.02, 0.01);
                    }
                }
            }
        }
    }
}
