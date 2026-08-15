// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Dedicated single-purpose helper for thermal hazards (Lava, Fire, Magma, Campfires) and extinguish logic.
 */
public class WolfHazardHelper {

    /**
     * Identifies if a given block state constitutes an active thermal hazard (Lava, Fire, Magma, Lit Campfire).
     */
    public static boolean isThermalHazard(BlockState state) {
        if (state == null || state.isAir()) {
            return false;
        }

        if (state.is(Blocks.LAVA) || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE) || state.is(Blocks.MAGMA_BLOCK)) {
            return true;
        }

        if (state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)) {
            return state.hasProperty(CampfireBlock.LIT) && state.getValue(CampfireBlock.LIT);
        }

        return false;
    }

    /**
     * Checks if there is any active thermal hazard in the specified position.
     */
    public static boolean isDirectHazard(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        return isThermalHazard(level.getBlockState(pos));
    }

    /**
     * Checks if any thermal hazard exists in the proximity bounding area around the center position.
     */
    public static boolean isHazardNear(Level level, BlockPos centerPos, int horizontalRadius, int verticalRadius) {
        if (level == null || centerPos == null) {
            return false;
        }

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int dx = -horizontalRadius; dx <= horizontalRadius; dx++) {
            for (int dz = -horizontalRadius; dz <= horizontalRadius; dz++) {
                for (int dy = -verticalRadius; dy <= verticalRadius; dy++) {
                    mutablePos.set(centerPos.getX() + dx, centerPos.getY() + dy, centerPos.getZ() + dz);
                    if (isThermalHazard(level.getBlockState(mutablePos))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Finds nearby safe water within the specified radius to extinguish burning dogs.
     */
    public static BlockPos findNearbyWater(Level level, BlockPos center, int radius) {
        if (level == null || center == null) {
            return null;
        }

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -1; dy <= 2; dy++) {
                    mutable.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (level.getBlockState(mutable).is(Blocks.WATER)) {
                        return mutable.immutable();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Calculates an escape impulse vector pushing the wolf directly away from the hazard position.
     */
    public static Vec3 calculateEscapeVector(Wolf wolf, BlockPos hazardPos) {
        if (wolf == null || hazardPos == null) {
            return Vec3.ZERO;
        }
        double dx = wolf.getX() - (hazardPos.getX() + 0.5);
        double dz = wolf.getZ() - (hazardPos.getZ() + 0.5);
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist < 0.001) {
            dx = 1.0;
            dz = 0.0;
            dist = 1.0;
        }
        return new Vec3((dx / dist) * 0.35, 0.15, (dz / dist) * 0.35);
    }
}
