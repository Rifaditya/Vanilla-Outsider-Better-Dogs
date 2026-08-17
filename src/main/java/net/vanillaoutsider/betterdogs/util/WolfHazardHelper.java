// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Single-purpose helper for environmental and thermal hazard identification.
 */
public class WolfHazardHelper {

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

        if (state.is(Blocks.CACTUS) || state.is(Blocks.SWEET_BERRY_BUSH) || state.is(Blocks.POWDER_SNOW)) {
            return true;
        }

        return false;
    }

    public static boolean isHazardNearby(Wolf wolf, double radius) {
        if (wolf == null) {
            return false;
        }
        Level level = wolf.level();
        if (level == null) {
            return false;
        }

        BlockPos center = wolf.blockPosition();
        int r = (int) Math.ceil(radius);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = -r; x <= r; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -r; z <= r; z++) {
                    pos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    if (isThermalHazard(level.getBlockState(pos))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
