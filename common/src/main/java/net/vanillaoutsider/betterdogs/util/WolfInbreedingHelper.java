// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose helper for tracking wolf lineages, detecting inbreeding, and applying runt penalties.
 */
public class WolfInbreedingHelper {

    public static boolean isInbredPair(Wolf parentA, AgeableMob otherParent) {
        if (parentA == null || !(otherParent instanceof Wolf parentB)) {
            return false;
        }

        UUID idA = parentA.getUUID();
        UUID idB = parentB.getUUID();

        if (idA == null || idB == null) {
            return false;
        }

        UUID aP1 = parentA instanceof WolfExtensions extA ? extA.betterdogs$getParentUUID1() : null;
        UUID aP2 = parentA instanceof WolfExtensions extA ? extA.betterdogs$getParentUUID2() : null;

        UUID bP1 = parentB instanceof WolfExtensions extB ? extB.betterdogs$getParentUUID1() : null;
        UUID bP2 = parentB instanceof WolfExtensions extB ? extB.betterdogs$getParentUUID2() : null;

        // 1. Parent-Child inbreeding
        if (idA.equals(bP1) || idA.equals(bP2) || idB.equals(aP1) || idB.equals(aP2)) {
            return true;
        }

        // 2. Sibling inbreeding (share at least 1 parent)
        if (aP1 != null && (aP1.equals(bP1) || aP1.equals(bP2))) {
            return true;
        }
        if (aP2 != null && (aP2.equals(bP1) || aP2.equals(bP2))) {
            return true;
        }

        return false;
    }

    public static void applyInbreeding(Wolf child, Wolf parentA, AgeableMob otherParent) {
        if (child == null || !(child instanceof WolfExtensions childExt)) {
            return;
        }

        if (parentA != null) {
            childExt.betterdogs$setParentUUID1(parentA.getUUID());
        }
        if (otherParent != null) {
            childExt.betterdogs$setParentUUID2(otherParent.getUUID());
        }

        if (isInbredPair(parentA, otherParent)) {
            childExt.betterdogs$setInbred(true);

            // Cap scale strictly between 0.70x and 0.80x (small runt)
            float currentScale = childExt.betterdogs$getSocialScale();
            float runtScale = Math.min(currentScale, 0.80f);
            childExt.betterdogs$setSocialScale(Math.max(0.70f, runtScale));

            // Re-apply personality stats with runt penalty applied
            WolfPersonalityStatHelper.applyPersonalityStats(child, childExt.betterdogs$getPersonality());

            // Dense smoke puff on birth
            if (child.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SMOKE, child.getX(), child.getY() + 0.3, child.getZ(), 8, 0.2, 0.2, 0.2, 0.02);
            }
        }
    }

    public static void tickRuntAmbientParticles(Wolf wolf) {
        if (wolf == null || wolf.level() == null) {
            return;
        }

        if (wolf instanceof WolfExtensions ext && ext.betterdogs$isInbred()) {
            if (wolf.level() instanceof ServerLevel serverLevel) {
                if (wolf.tickCount % 50 == 0 && wolf.getDeltaMovement().horizontalDistanceSqr() > 0.001) {
                    serverLevel.sendParticles(ParticleTypes.SMOKE, wolf.getX(), wolf.getY() + 0.2, wolf.getZ(), 1, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }
    }
}
