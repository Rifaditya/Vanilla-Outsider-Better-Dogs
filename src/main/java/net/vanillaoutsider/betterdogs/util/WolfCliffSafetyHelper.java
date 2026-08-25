// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;

/**
 * Dedicated single-purpose helper for cliff edge drop height probing, safe water landing evaluation, and push resistance.
 */
public class WolfCliffSafetyHelper {

    /**
     * Checks if pushing a wolf in the given horizontal direction will shove it over a dangerous cliff edge (>3 block drop).
     * If the drop lands safely in water, it is not considered dangerous.
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

        if (!hasGround) {
            // Check if drop lands safely in water (river bank / water drop)
            if (isSafeWaterLanding(level, bx, by, bz, 8)) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the target horizontal coordinates drop safely into water within maxDrop blocks.
     */
    public static boolean isSafeWaterLanding(Level level, int bx, int startY, int bz, int maxDrop) {
        if (level == null) {
            return false;
        }
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dy = 0; dy <= maxDrop; dy++) {
            pos.set(bx, startY - dy, bz);
            if (level.getFluidState(pos).is(FluidTags.WATER)) {
                return !WolfHazardHelper.isThermalHazard(level.getBlockState(pos));
            }
            if (!level.isEmptyBlock(pos) && level.getBlockState(pos).isSolidRender()) {
                return false;
            }
        }
        return false;
    }
}
