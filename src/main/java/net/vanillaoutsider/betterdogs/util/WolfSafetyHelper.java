// Verified against: Block.java (26.2+), DefaultRandomPos.java (26.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class WolfSafetyHelper {

    /**
     * Checks if pushing a wolf in the specified direction (dx, dz) would push it over a cliff
     * or into a dangerous hazard (lava, magma, fire).
     */
    public static boolean isDangerousPushDirection(Wolf wolf, double dx, double dz) {
        if (Mth.absMax(dx, dz) < 0.001) {
            return false;
        }

        // Normalize direction
        double len = Math.sqrt(dx * dx + dz * dz);
        double nx = dx / len;
        double nz = dz / len;

        // Lookahead 1.2 blocks in the push direction
        double lookX = wolf.getX() + nx * 1.2;
        double lookY = wolf.getY();
        double lookZ = wolf.getZ() + nz * 1.2;

        int checkX = Mth.floor(lookX);
        int checkY = Mth.floor(lookY);
        int checkZ = Mth.floor(lookZ);

        Level level = wolf.level();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        boolean solidGround = false;
        for (int i = 0; i <= 3; i++) {
            mutablePos.set(checkX, checkY - i, checkZ);
            if (!level.isEmptyBlock(mutablePos)) {
                BlockState state = level.getBlockState(mutablePos);
                // Check if the block is a known hazard
                if (state.is(Blocks.LAVA) || state.is(Blocks.MAGMA_BLOCK) || 
                    state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
                    return true;
                }
                solidGround = true;
                break;
            }
        }

        // If no solid ground is found within 3 blocks down, it's a cliff drop hazard
        return !solidGround;
    }
}
