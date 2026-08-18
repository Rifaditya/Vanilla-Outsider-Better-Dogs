// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
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
}
