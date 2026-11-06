// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier2;

import java.util.Random;
import net.vanillaoutsider.betterdogs.util.WolfLitterHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 2 - Boundary 08: Multi-Puppy Litter Breeding Edge Cases")
public class Tier2_08_BreedingEdgeCasesTest {

    @Test
    @DisplayName("Assert litter size is guaranteed 1 when maxSize <= 1")
    public void testLitterMaxSizeOne() {
        Random rand = new Random(42L);
        assertEquals(1, WolfLitterHelper.calculateLitterSize(1, 100, rand));
        assertEquals(1, WolfLitterHelper.calculateLitterSize(0, 100, rand));
        assertEquals(1, WolfLitterHelper.calculateLitterSize(-5, 100, rand));
    }

    @Test
    @DisplayName("Assert litter size is strictly 1 when extraChance <= 0")
    public void testLitterExtraChanceZero() {
        Random rand = new Random(42L);
        assertEquals(1, WolfLitterHelper.calculateLitterSize(5, 0, rand));
        assertEquals(1, WolfLitterHelper.calculateLitterSize(5, -10, rand));
    }

    @Test
    @DisplayName("Assert litter size reaches exact maxSize when extraChance is 100%")
    public void testLitterExtraChanceHundredPercent() {
        Random rand = new Random(42L);
        int maxSize = 4;
        int size = WolfLitterHelper.calculateLitterSize(maxSize, 100, rand);
        assertEquals(maxSize, size, "100% extra chance must spawn maximum possible litter size");
    }

    @Test
    @DisplayName("Assert calculateLitterSize with null Random safely returns 1")
    public void testNullRandomHandling() {
        assertDoesNotThrow(() -> {
            int size = WolfLitterHelper.calculateLitterSize(4, 50, null);
            assertEquals(1, size, "Null random must fallback safely to default litter size 1");
        });
    }

    @Test
    @DisplayName("Assert calculated litter size never exceeds configured maxSize cap")
    public void testLitterSizeNeverExceedsCap() {
        Random rand = new Random(12345L);
        int maxSize = 6;
        for (int i = 0; i < 1000; i++) {
            int size = WolfLitterHelper.calculateLitterSize(maxSize, 90, rand);
            assertTrue(size >= 1 && size <= maxSize, "Litter size must stay between 1 and " + maxSize);
        }
    }
}
