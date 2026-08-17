// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for playful hyperactive zoomies activation and particle dispatch.
 */
public final class WolfZoomiesHelper {

    private WolfZoomiesHelper() {
    }

    public static boolean canTriggerZoomies(Wolf wolf) {
        if (wolf == null || !wolf.isTame()) {
            return false;
        }
        if (wolf.isOrderedToSit() || wolf.isInSittingPose() || wolf.getTarget() != null) {
            return false;
        }
        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$isGuardMode() || ext.betterdogs$getZoomiesTicks() > 0) {
                return false;
            }
        }
        return BetterDogsGameRules.isZoomiesEnabled(wolf.level());
    }

    public static boolean triggerZoomies(Wolf wolf) {
        if (!canTriggerZoomies(wolf)) {
            return false;
        }
        int duration = BetterDogsGameRules.getZoomiesDurationTicks(wolf.level());
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setZoomiesTicks(Math.max(20, duration));
        }
        return true;
    }

    public static void tickZoomiesParticles(Wolf wolf) {
        if (wolf == null) {
            return;
        }
        if (wolf.level() instanceof ServerLevel serverLevel) {
            if (wolf.getRandom().nextFloat() < 0.35F) {
                serverLevel.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        wolf.getX(), wolf.getY() + 0.3, wolf.getZ(),
                        1, 0.2, 0.2, 0.2, 0.02
                );
            }
            if (wolf.getRandom().nextFloat() < 0.20F) {
                serverLevel.sendParticles(
                        ParticleTypes.POOF,
                        wolf.getX(), wolf.getY() + 0.1, wolf.getZ(),
                        1, 0.1, 0.1, 0.1, 0.01
                );
            }
        }
    }
}
