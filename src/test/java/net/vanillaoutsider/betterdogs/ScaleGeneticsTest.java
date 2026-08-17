// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.minecraft.util.RandomSource;
import net.vanillaoutsider.betterdogs.util.WolfScaleGeneticsHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ScaleGeneticsTest {

    @Test
    @DisplayName("Verify Offspring Scale Inheritance Bounds and Null Safety")
    public void testScaleGeneticsBounds() {
        RandomSource random = RandomSource.create(42L);
        float scale = WolfScaleGeneticsHelper.calculateOffspringScale(null, 1.0f, 1.0f, random);
        Assertions.assertTrue(scale >= 0.70f && scale <= 1.45f, "Offspring scale out of bounds: " + scale);
    }

    @Test
    @DisplayName("Verify Midpoint Average Calculation with Variance")
    public void testScaleMidpointVariance() {
        RandomSource random = RandomSource.create(9999L);
        float parentA = 0.80f;
        float parentB = 1.20f;
        // Midpoint is 1.00f, variance is +/- 10% -> [0.85, 1.15]
        for (int i = 0; i < 100; i++) {
            float scale = WolfScaleGeneticsHelper.calculateOffspringScale(null, parentA, parentB, random);
            Assertions.assertTrue(scale >= 0.85f && scale <= 1.15f, "Scale variance exceeded expected range: " + scale);
        }
    }

    @Test
    @DisplayName("Verify Wild Wolf Scale Gaussian Generation")
    public void testWildWolfScaleGeneration() {
        RandomSource random = RandomSource.create(777L);
        for (int i = 0; i < 500; i++) {
            float scale = WolfScaleGeneticsHelper.generateWildWolfScale(null, random);
            Assertions.assertTrue(scale >= 0.70f && scale <= 1.45f, "Wild wolf scale out of bounds: " + scale);
        }
    }
}
