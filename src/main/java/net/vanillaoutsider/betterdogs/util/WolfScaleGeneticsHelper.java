// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for calculating wild wolf scales and offspring scale inheritance.
 */
public final class WolfScaleGeneticsHelper {

    private WolfScaleGeneticsHelper() {
    }

    public static float calculateOffspringScale(Level level, float parentScaleA, float parentScaleB, RandomSource random) {
        if (parentScaleA <= 0.0f) {
            parentScaleA = 1.0f;
        }
        if (parentScaleB <= 0.0f) {
            parentScaleB = 1.0f;
        }
        if (random == null) {
            return (parentScaleA + parentScaleB) / 2.0f;
        }

        float midScale = (parentScaleA + parentScaleB) / 2.0f;
        // Continuous +/- 10% genetic variance
        float variance = (random.nextFloat() - 0.5f) * 0.20f;
        float finalScale = midScale * (1.0f + variance);

        int minPct = level != null ? BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_WOLF_MIN_SCALE_PERCENT, 70) : 70;
        int maxPct = level != null ? BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_WOLF_MAX_SCALE_PERCENT, 145) : 145;

        float minScale = minPct / 100.0f;
        float maxScale = maxPct / 100.0f;

        return Math.max(minScale, Math.min(maxScale, finalScale));
    }

    public static float generateWildWolfScale(Level level, RandomSource random) {
        if (random == null) {
            return 1.0f;
        }

        // Gaussian bell curve centered at 1.0 with std dev 0.12
        float gaussian = (float) random.nextGaussian();
        float rawScale = 1.0f + (gaussian * 0.12f);

        int minPct = level != null ? BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_WOLF_MIN_SCALE_PERCENT, 70) : 70;
        int maxPct = level != null ? BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_WOLF_MAX_SCALE_PERCENT, 145) : 145;

        float minScale = minPct / 100.0f;
        float maxScale = maxPct / 100.0f;

        return Math.max(minScale, Math.min(maxScale, rawScale));
    }
}
