// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import org.jspecify.annotations.Nullable;

import java.util.Random;

/**
 * Dedicated single-purpose helper for autonomous exploratory roaming surges (Wanderlust).
 */
public final class WanderlustHelper {

    public static final double DEFAULT_ROAM_RADIUS = 28.0;
    public static final double DEFAULT_RETURN_THRESHOLD = 24.0;
    public static final double MAX_OWNER_DISTANCE = 32.0;
    public static final int WANDERLUST_SURGE_CHANCE = 400; // 1 in 400 calm ticks

    private WanderlustHelper() {
    }

    /**
     * Checks whether the wolf is eligible to engage in wanderlust roaming.
     */
    public static boolean isEligibleForWanderlust(Wolf wolf) {
        if (wolf == null || !wolf.isAlive() || !wolf.isTame() || wolf.isOrderedToSit() || wolf.isLeashed() || wolf.getTarget() != null) {
            return false;
        }

        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$isGuardMode()) {
                return false;
            }
        }

        LivingEntity owner = wolf.getOwner();
        if (owner == null || !owner.isAlive()) {
            return false;
        }

        return wolf.distanceToSqr(owner) <= (MAX_OWNER_DISTANCE * MAX_OWNER_DISTANCE);
    }

    /**
     * Evaluates random roll for spontaneous wanderlust activation on idle calm ticks.
     */
    public static boolean shouldTriggerWanderlust(Wolf wolf, RandomSource random) {
        if (!isEligibleForWanderlust(wolf) || random == null) {
            return false;
        }
        return random.nextInt(WANDERLUST_SURGE_CHANCE) == 0;
    }

    /**
     * Overload for java.util.Random (for tests).
     */
    public static boolean shouldTriggerWanderlust(Wolf wolf, Random random) {
        if (!isEligibleForWanderlust(wolf) || random == null) {
            return false;
        }
        return random.nextInt(WANDERLUST_SURGE_CHANCE) == 0;
    }

    /**
     * Calculates exploration wander target vector respecting the 28m perimeter and 24m return pull.
     */
    public static @Nullable Vec3 calculateWanderlustPosition(Wolf wolf, LivingEntity owner) {
        if (wolf == null || owner == null) {
            return null;
        }

        Vec3 ownerPos = new Vec3(owner.getX(), owner.getY(), owner.getZ());
        double distToOwner = wolf.distanceTo(owner);

        // If further than 24 blocks from owner, steer path back towards owner perimeter
        if (distToOwner > DEFAULT_RETURN_THRESHOLD) {
            Vec3 target = DefaultRandomPos.getPosTowards(wolf, 12, 7, ownerPos, 1.5707963705062866);
            if (target != null) {
                return target;
            }
        }

        // Expanded exploration perimeter (up to 28 blocks from owner)
        for (int i = 0; i < 10; i++) {
            Vec3 pos = DefaultRandomPos.getPos(wolf, 16, 7);
            if (pos != null && pos.distanceToSqr(ownerPos) <= (DEFAULT_ROAM_RADIUS * DEFAULT_ROAM_RADIUS)) {
                return pos;
            }
        }

        return null;
    }
}
