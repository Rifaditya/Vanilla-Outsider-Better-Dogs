// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for thunderstorm weather evaluation, personality anxiety scaling, and shelter search.
 */
public class WolfStormHelper {

    public static boolean isStormAnxietyActive(Wolf wolf) {
        if (wolf == null || !wolf.isTame()) {
            return false;
        }
        if (WolfPettingHelper.isSoothed(wolf)) {
            return false;
        }
        Level level = wolf.level();
        if (level == null || !level.isThundering()) {
            return false;
        }
        if (!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_STORM_ANXIETY, true)) {
            return false;
        }
        return getPersonalityMultiplier(wolf) > 0.0f;
    }

    public static float getPersonalityMultiplier(Wolf wolf) {
        if (wolf instanceof WolfExtensions ext) {
            WolfPersonality personality = ext.betterdogs$hasPersonality()
                    ? ext.betterdogs$getPersonality()
                    : WolfPersonality.NORMAL;
            return switch (personality) {
                case PACIFIST -> 3.0f;
                case NORMAL -> 1.0f;
                case AGGRESSIVE -> 0.0f;
            };
        }
        return 1.0f;
    }

    public static BlockPos findShelterTarget(Wolf wolf) {
        if (wolf == null) {
            return null;
        }
        Level level = wolf.level();
        if (level == null) {
            return null;
        }

        LivingEntity owner = wolf.getOwner();
        BlockPos basePos = (owner != null && wolf.distanceToSqr(owner) < 1024.0)
                ? owner.blockPosition()
                : wolf.blockPosition();

        if (!level.canSeeSky(basePos) && isSafeStandBlock(level, basePos)) {
            if (owner != null && wolf.distanceToSqr(owner) <= 9.0) {
                return wolf.blockPosition();
            }
            return basePos;
        }

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockPos bestPos = null;
        double bestDistance = Double.MAX_VALUE;

        for (int x = -12; x <= 12; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -12; z <= 12; z++) {
                    mutable.set(basePos.getX() + x, basePos.getY() + y, basePos.getZ() + z);
                    if (!level.canSeeSky(mutable) && isSafeStandBlock(level, mutable)) {
                        double dist = wolf.blockPosition().distSqr(mutable);
                        if (dist < bestDistance) {
                            bestDistance = dist;
                            bestPos = mutable.immutable();
                        }
                    }
                }
            }
        }
        return bestPos;
    }

    public static boolean isSafeStandBlock(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        BlockPos below = pos.below();
        return level.isEmptyBlock(pos)
                && !level.isEmptyBlock(below)
                && !level.getBlockState(below).is(Blocks.LAVA)
                && !level.getBlockState(below).is(Blocks.MAGMA_BLOCK)
                && !level.getBlockState(below).is(Blocks.FIRE)
                && !level.getBlockState(below).is(Blocks.SOUL_FIRE);
    }
}
