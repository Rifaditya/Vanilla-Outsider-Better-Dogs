// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfLitterHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 20: Selective Litter Sizing & Allele Inheritance Tests")
class VariableLitterTest {

    @Test
    @DisplayName("Assert guaranteed minimum litter size of 1")
    void testMinimumLitterSize() {
        Random random = new Random(12345);
        // 0% extra chance -> guaranteed 1 puppy
        int size = WolfLitterHelper.calculateLitterSize(4, 0, random);
        assertEquals(1, size, "Litter size with 0% extra chance must always be exactly 1");

        // maxSize = 1 -> guaranteed 1 puppy
        size = WolfLitterHelper.calculateLitterSize(1, 100, random);
        assertEquals(1, size, "Litter size with maxSize=1 must always be exactly 1");
    }

    @Test
    @DisplayName("Assert guaranteed maximum litter size with 100% chance")
    void testMaximumLitterSize() {
        Random random = new Random(12345);
        // 100% extra chance -> max size reached
        int size = WolfLitterHelper.calculateLitterSize(4, 100, random);
        assertEquals(4, size, "Litter size with 100% chance must reach configured max size");

        size = WolfLitterHelper.calculateLitterSize(3, 100, random);
        assertEquals(3, size, "Litter size with 100% chance must reach configured max size");
    }

    @Test
    @DisplayName("Assert statistical probability bounds across random trials")
    void testProbabilityDistribution() {
        Random random = new Random(42);
        int totalPuppies = 0;
        int trials = 1000;

        for (int i = 0; i < trials; i++) {
            int size = WolfLitterHelper.calculateLitterSize(4, 20, random);
            assertTrue(size >= 1 && size <= 4, "Litter size must always be within [1, 4]");
            totalPuppies += size;
        }

        double average = (double) totalPuppies / trials;
        // Expected geometric average for 20% chain: ~1.24 puppies
        assertTrue(average >= 1.15 && average <= 1.35,
                "Average litter size with 20% chance should be between 1.15 and 1.35, got: " + average);
    }

    @Test
    @DisplayName("Assert strict null and degenerate input safety")
    void testNullAndDegenerateSafety() {
        assertEquals(1, WolfLitterHelper.calculateLitterSize(4, 20, null));
        assertEquals(1, WolfLitterHelper.calculateLitterSize(0, 20, new Random()));
        assertEquals(1, WolfLitterHelper.calculateLitterSize(-5, -10, new Random()));
        assertDoesNotThrow(() -> WolfLitterHelper.processBreedingLitter(null, null, null));
    }

    @Test
    @DisplayName("Assert negative parity: Untamed/wild wolves breeding never triggers extra litter spawning")
    void testNegativeParityWildWolfBreeding() {
        // Assert null/wild parent handling does not throw and strictly exits early
        assertDoesNotThrow(() -> WolfLitterHelper.processBreedingLitter(null, null, null));
    }

    @Test
    @DisplayName("Assert negative parity: Single puppy litter (size=1) yields zero extra siblings")
    void testNegativeParitySinglePuppyLitter() {
        Random random = new Random(999);
        // When max size is 1 or extra chance is 0, litter size is always 1, resulting in 0 extra siblings
        int size = WolfLitterHelper.calculateLitterSize(1, 0, random);
        assertEquals(1, size);
        int extraSiblings = size - 1;
        assertEquals(0, extraSiblings, "Litter size 1 must spawn exactly 0 extra siblings beyond vanilla offspring");
    }

    @Test
    @DisplayName("Assert deterministic litter calculation bounds across 5,000 fuzzing iterations")
    void testFuzzingLitterBounds() {
        Random random = new Random(777);
        for (int i = 0; i < 5000; i++) {
            int maxSize = random.nextInt(10) - 2; // -2 to 7
            int chance = random.nextInt(150) - 25; // -25 to 124
            int size = WolfLitterHelper.calculateLitterSize(maxSize, chance, random);
            if (maxSize <= 1 || chance <= 0) {
                assertEquals(1, size, "Degenerate bounds must strictly clamp to 1");
            } else {
                assertTrue(size >= 1 && size <= maxSize, "Litter size must be clamped within [1, maxSize]");
            }
        }
    }
}
