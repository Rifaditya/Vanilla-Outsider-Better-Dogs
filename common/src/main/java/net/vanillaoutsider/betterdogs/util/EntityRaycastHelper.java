// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Dedicated single-purpose helper for raycasting clear line-of-sight and obstacle-free positions.
 */
public class EntityRaycastHelper {

    /**
     * Checks whether an unobstructed line of sight exists between two vectors.
     */
    public static boolean hasLineOfSight(Level level, Vec3 from, Vec3 to) {
        if (level == null || from == null || to == null) {
            return false;
        }
        BlockHitResult hit = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (net.minecraft.world.entity.Entity) null));
        return hit.getType() == HitResult.Type.MISS;
    }

    /**
     * Finds a safe and clear flanking position relative to a target at a given angle and distance.
     */
    public static Vec3 findClearFlankPos(Level level, Vec3 targetPos, double angleRad, double distance) {
        if (level == null || targetPos == null) {
            return targetPos;
        }

        double ox = Math.cos(angleRad) * distance;
        double oz = Math.sin(angleRad) * distance;
        Vec3 proposed = targetPos.add(ox, 0.0, oz);

        int bx = Mth.floor(proposed.x);
        int by = Mth.floor(proposed.y);
        int bz = Mth.floor(proposed.z);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        // Check for solid ground below
        boolean solidGround = false;
        for (int dy = 0; dy <= 2; dy++) {
            pos.set(bx, by - dy, bz);
            if (!level.isEmptyBlock(pos) && !WolfHazardHelper.isThermalHazard(level.getBlockState(pos))) {
                solidGround = true;
                proposed = new Vec3(proposed.x, pos.getY() + 1.0, proposed.z);
                break;
            }
        }

        if (!solidGround) {
            return targetPos;
        }

        // Check if head/feet space is free of solid block and hazards
        pos.set(bx, Mth.floor(proposed.y), bz);
        if (!level.isEmptyBlock(pos) || WolfHazardHelper.isThermalHazard(level.getBlockState(pos))) {
            return targetPos;
        }

        return proposed;
    }
}
