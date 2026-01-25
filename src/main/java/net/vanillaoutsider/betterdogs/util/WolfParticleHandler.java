package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class WolfParticleHandler {

    public static void playTameParticles(Wolf wolf, WolfPersonality personality) {
        if (wolf.level() instanceof ServerLevel serverLevel) {
            switch (personality) {
                case AGGRESSIVE -> {
                    for (int i = 0; i < 4; i++) {
                        serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                                wolf.getX() + wolf.getRandom().nextGaussian() * 0.5,
                                wolf.getY() + 0.5 + wolf.getRandom().nextDouble() * 0.5,
                                wolf.getZ() + wolf.getRandom().nextGaussian() * 0.5, 1, 0, 0, 0, 0);
                    }
                }
                case PACIFIST -> {
                    for (int i = 0; i < 4; i++) {
                        serverLevel.sendParticles(ParticleTypes.HEART,
                                wolf.getX() + wolf.getRandom().nextGaussian() * 0.5,
                                wolf.getY() + 0.5 + wolf.getRandom().nextDouble() * 0.5,
                                wolf.getZ() + wolf.getRandom().nextGaussian() * 0.5, 1, 0, 0, 0, 0);
                    }
                }
                case NORMAL -> {
                    for (int i = 0; i < 4; i++) {
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                wolf.getX() + wolf.getRandom().nextGaussian() * 0.5,
                                wolf.getY() + 0.5 + wolf.getRandom().nextDouble() * 0.5,
                                wolf.getZ() + wolf.getRandom().nextGaussian() * 0.5, 1, 0, 0, 0, 0);
                    }
                }
            }
        }
    }
}
