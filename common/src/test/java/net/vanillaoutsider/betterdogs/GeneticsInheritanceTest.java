// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.minecraft.util.RandomSource;
import net.vanillaoutsider.betterdogs.util.WolfGeneticsHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GeneticsInheritanceTest {

    @Test
    @DisplayName("Verify Genetic Personality Inheritance Null Safety")
    public void testGeneticsNullSafety() {
        RandomSource random = RandomSource.create(42L);
        WolfPersonality personality = WolfGeneticsHelper.calculateOffspringPersonality(null, null, null, random);
        Assertions.assertNotNull(personality);
    }

    @Test
    @DisplayName("Verify Same Parent Personality Inheritance Distribution")
    public void testSamePersonalityInheritance() {
        RandomSource random = RandomSource.create(12345L);
        int aggroCount = 0;
        int trials = 1000;

        for (int i = 0; i < trials; i++) {
            WolfPersonality result = WolfGeneticsHelper.calculateOffspringPersonality(null, WolfPersonality.AGGRESSIVE, WolfPersonality.AGGRESSIVE, random);
            if (result == WolfPersonality.AGGRESSIVE) {
                aggroCount++;
            }
        }

        // Expected ~80% (around 800 out of 1000)
        double ratio = (double) aggroCount / trials;
        Assertions.assertTrue(ratio >= 0.70 && ratio <= 0.90, "Expected ~80% inheritance for same parents, got: " + ratio);
    }

    @Test
    @DisplayName("Verify Mixed Parent Personality Inheritance Distribution")
    public void testMixedPersonalityInheritance() {
        RandomSource random = RandomSource.create(54321L);
        int normalCount = 0;
        int trials = 1000;

        for (int i = 0; i < trials; i++) {
            WolfPersonality result = WolfGeneticsHelper.calculateOffspringPersonality(null, WolfPersonality.AGGRESSIVE, WolfPersonality.PACIFIST, random);
            if (result == WolfPersonality.NORMAL) {
                normalCount++;
            }
        }

        // Expected ~20% (around 200 out of 1000)
        double ratio = (double) normalCount / trials;
        Assertions.assertTrue(ratio >= 0.12 && ratio <= 0.28, "Expected ~20% dilution for mixed parents, got: " + ratio);
    }
}
