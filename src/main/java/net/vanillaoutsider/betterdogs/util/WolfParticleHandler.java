// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class WolfParticleHandler {

    public static void playTameParticles(Wolf wolf, WolfPersonality personality) {
        if (wolf == null || !(wolf.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        switch (personality) {
            case AGGRESSIVE -> {
                for (int i = 0; i < 7; i++) {
                    double ox = (wolf.getRandom().nextDouble() - 0.5) * wolf.getBbWidth();
                    double oy = wolf.getRandom().nextDouble() * wolf.getBbHeight();
                    double oz = (wolf.getRandom().nextDouble() - 0.5) * wolf.getBbWidth();
                    serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, wolf.getX() + ox, wolf.getY() + oy, wolf.getZ() + oz, 1, 0, 0, 0, 0);
                }
            }
            case PACIFIST -> {
                for (int i = 0; i < 12; i++) {
                    double ox = (wolf.getRandom().nextDouble() - 0.5) * wolf.getBbWidth();
                    double oy = wolf.getRandom().nextDouble() * wolf.getBbHeight();
                    double oz = (wolf.getRandom().nextDouble() - 0.5) * wolf.getBbWidth();
                    serverLevel.sendParticles(ParticleTypes.HEART, wolf.getX() + ox, wolf.getY() + oy, wolf.getZ() + oz, 1, 0, 0, 0, 0);
                }
            }
            case NORMAL -> {
                for (int i = 0; i < 8; i++) {
                    double ox = (wolf.getRandom().nextDouble() - 0.5) * wolf.getBbWidth();
                    double oy = wolf.getRandom().nextDouble() * wolf.getBbHeight();
                    double oz = (wolf.getRandom().nextDouble() - 0.5) * wolf.getBbWidth();
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, wolf.getX() + ox, wolf.getY() + oy, wolf.getZ() + oz, 1, 0, 0, 0, 0);
                }
            }
        }
    }
}
