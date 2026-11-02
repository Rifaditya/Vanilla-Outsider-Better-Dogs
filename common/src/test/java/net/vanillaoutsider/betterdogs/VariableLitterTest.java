// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.minecraft.util.RandomSource;
import net.vanillaoutsider.betterdogs.util.WolfLitterHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class VariableLitterTest {

    @Test
    @DisplayName("Verify Litter Size Bounds and Null Safety")
    public void testLitterSizeBounds() {
        RandomSource random = RandomSource.create(42L);
        int size = WolfLitterHelper.determineLitterSize(null, random);
        Assertions.assertTrue(size >= 1 && size <= 4, "Litter size out of range: " + size);
    }

    @Test
    @DisplayName("Verify Litter Size Probability Distribution")
    public void testLitterDistribution() {
        RandomSource random = RandomSource.create(12345L);
        int ones = 0;
        int twos = 0;
        int threes = 0;
        int fours = 0;
        int trials = 1000;

        for (int i = 0; i < trials; i++) {
            int size = WolfLitterHelper.determineLitterSize(null, random);
            switch (size) {
                case 1 -> ones++;
                case 2 -> twos++;
                case 3 -> threes++;
                case 4 -> fours++;
            }
        }

        // Expected ~45% ones, ~35% twos, ~15% threes, ~5% fours
        double oneRatio = (double) ones / trials;
        double twoRatio = (double) twos / trials;
        double threeRatio = (double) threes / trials;
        double fourRatio = (double) fours / trials;

        Assertions.assertTrue(oneRatio >= 0.35 && oneRatio <= 0.55, "Unexpected single puppy ratio: " + oneRatio);
        Assertions.assertTrue(twoRatio >= 0.25 && twoRatio <= 0.45, "Unexpected twin puppy ratio: " + twoRatio);
        Assertions.assertTrue(threeRatio >= 0.08 && threeRatio <= 0.22, "Unexpected triplet puppy ratio: " + threeRatio);
        Assertions.assertTrue(fourRatio >= 0.01 && fourRatio <= 0.10, "Unexpected quadruplet puppy ratio: " + fourRatio);
    }
}
