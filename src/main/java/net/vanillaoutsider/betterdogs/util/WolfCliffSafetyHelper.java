// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;

/**
 * Dedicated single-purpose helper for cliff edge drop height probing and push resistance.
 */
public class WolfCliffSafetyHelper {

    /**
     * Checks if pushing a wolf in the given horizontal direction will shove it over a cliff edge (>3 block drop).
     */
    public static boolean isDangerousPushDirection(Wolf wolf, double xa, double za) {
        if (wolf == null) {
            return false;
        }
        Level level = wolf.level();
        if (level == null) {
            return false;
        }

        double len = Math.sqrt(xa * xa + za * za);
        if (len < 0.0001) {
            return false;
        }

        double pushDist = 0.8;
        double targetX = wolf.getX() + (xa / len) * pushDist;
        double targetZ = wolf.getZ() + (za / len) * pushDist;
        int bx = Mth.floor(targetX);
        int by = Mth.floor(wolf.getY());
        int bz = Mth.floor(targetZ);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dy = -1; dy <= 1; dy++) {
            pos.set(bx, by + dy, bz);
            if (WolfHazardHelper.isThermalHazard(level.getBlockState(pos))) {
                return true;
            }
        }

        boolean hasGround = false;
        for (int dy = 0; dy <= 3; dy++) {
            pos.set(bx, by - dy, bz);
            if (!level.isEmptyBlock(pos)) {
                hasGround = true;
                break;
            }
        }
        return !hasGround;
    }
}
