// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.dasik.social.util.FastRandom;
import net.vanillaoutsider.betterdogs.registry.BetterDogsTags;
import net.vanillaoutsider.betterdogs.util.BabyCuriosityHelper;
import net.vanillaoutsider.betterdogs.util.DogTreatHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Data-Driven Tags & Zero-Allocation Performance Tests")
class DataDrivenAndPerformanceTest {

    @Test
    @DisplayName("Assert FastRandom.INSTANCE integer distribution within [0, bound)")
    void testFastRandomDistribution() {
        int bound = 100;
        int trials = 10000;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < trials; i++) {
            int roll = FastRandom.INSTANCE.nextInt(bound);
            assertTrue(roll >= 0 && roll < bound, "Roll must be within [0, 100)");
            if (roll < min) min = roll;
            if (roll > max) max = roll;
        }

        // Over 10k trials, minimum should be near 0 and maximum should be near 99
        assertTrue(min <= 5, "Min roll should be close to 0, was: " + min);
        assertTrue(max >= 95, "Max roll should be close to 99, was: " + max);
    }

    @Test
    @DisplayName("Assert all BetterDogsTags TagKey definitions are registered and namespaced")
    void testTagKeyRegistrationIntegrity() {
        assertNotNull(BetterDogsTags.RAW_FOOD, "RAW_FOOD tag must not be null");
        assertNotNull(BetterDogsTags.COOKED_FOOD, "COOKED_FOOD tag must not be null");
        assertNotNull(BetterDogsTags.COMMAND_ITEMS, "COMMAND_ITEMS tag must not be null");
        assertNotNull(BetterDogsTags.FETCH_ITEMS, "FETCH_ITEMS tag must not be null");
        assertNotNull(BetterDogsTags.TREATS, "TREATS tag must not be null");
        assertNotNull(BetterDogsTags.CURIOSITY_BLOCKS, "CURIOSITY_BLOCKS tag must not be null");
        assertNotNull(BetterDogsTags.SEATS, "SEATS tag must not be null");
        assertNotNull(BetterDogsTags.COMMON_CHAIRS, "COMMON_CHAIRS tag must not be null");

        assertEquals("vanilla-outsider-better-dogs", BetterDogsTags.TREATS.location().getNamespace());
        assertEquals("vanilla-outsider-better-dogs", BetterDogsTags.CURIOSITY_BLOCKS.location().getNamespace());
        assertEquals("vanilla-outsider-better-dogs", BetterDogsTags.SEATS.location().getNamespace());
        assertEquals("c", BetterDogsTags.COMMON_CHAIRS.location().getNamespace());
    }

    @Test
    @DisplayName("Assert BabyCuriosityHelper block evaluation safe fallback and null safety")
    void testCuriosityBlockFallbackSafety() {
        assertFalse(BabyCuriosityHelper.isInterestingBlock(null), "Null block state must safely return false");
        assertFalse(BabyCuriosityHelper.canExhibitCuriosity(null), "Null wolf must safely return false");
        assertEquals(80, BabyCuriosityHelper.calculateCuriosityDelay(null), "Null personality should return default delay");
        assertEquals(40, BabyCuriosityHelper.calculateCuriosityDelay(WolfPersonality.PACIFIST));
        assertEquals(80, BabyCuriosityHelper.calculateCuriosityDelay(WolfPersonality.NORMAL));
        assertEquals(-1, BabyCuriosityHelper.calculateCuriosityDelay(WolfPersonality.AGGRESSIVE));
    }

    @Test
    @DisplayName("Assert DogTreatHelper fallback treat pool and hash constancy")
    void testTreatPoolFallbackSafety() {
        assertNotNull(DogTreatHelper.getActiveTreatPool(), "Active treat pool must never be null");
        assertFalse(DogTreatHelper.getActiveTreatPool().isEmpty(), "Active treat pool must not be empty");

        UUID testId = UUID.fromString("98765432-4321-4321-4321-210987654321");
        int index1 = DogTreatHelper.calculateTreatIndex(testId, 9);
        int index2 = DogTreatHelper.calculateTreatIndex(testId, 9);
        assertEquals(index1, index2, "Deterministic treat index must remain constant across evaluations");
    }
}
