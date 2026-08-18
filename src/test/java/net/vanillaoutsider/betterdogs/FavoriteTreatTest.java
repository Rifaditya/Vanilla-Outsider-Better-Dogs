// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.DogTreatHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 22: High-Value Dog Treat Buff System Tests")
class FavoriteTreatTest {

    @Test
    @DisplayName("Assert deterministic bit-mixing hash constancy for UUIDs")
    void testDeterministicHashConstancy() {
        UUID dog1 = UUID.fromString("12345678-1234-1234-1234-123456789abc");
        int indexFirst = DogTreatHelper.calculateTreatIndex(dog1, 9);
        int indexSecond = DogTreatHelper.calculateTreatIndex(dog1, 9);

        assertTrue(indexFirst >= 0 && indexFirst < 9, "Index must be within [0, poolSize)");
        assertEquals(indexFirst, indexSecond, "Favorite treat index for identical UUID must be 100% deterministic");
    }

    @Test
    @DisplayName("Assert treat index distribution across distinct UUIDs")
    void testTreatPoolDistribution() {
        Set<Integer> sampledIndices = new HashSet<>();
        for (int i = 0; i < 500; i++) {
            UUID testUuid = new UUID(i * 1000000L, i * 7919L);
            sampledIndices.add(DogTreatHelper.calculateTreatIndex(testUuid, 9));
        }

        // Must cover multiple distinct slots from the pool
        assertTrue(sampledIndices.size() > 1, "Treat index distribution must cover multiple slots across UUIDs");
    }

    @Test
    @DisplayName("Assert strict null and degenerate input safety across helper methods")
    void testNullSafety() {
        assertEquals(0, DogTreatHelper.calculateTreatIndex(null, 9));
        assertEquals(0, DogTreatHelper.calculateTreatIndex(UUID.randomUUID(), 0));
        assertEquals(0, DogTreatHelper.calculateTreatIndex(UUID.randomUUID(), -5));
        assertFalse(DogTreatHelper.isFavoriteTreat(null, null));
        assertFalse(DogTreatHelper.isHoldingFavoriteTreat(null, null));
        assertFalse(DogTreatHelper.canFeedFavoriteTreat(null, null, null, null));
        assertDoesNotThrow(() -> DogTreatHelper.tryFeedFavoriteTreat(null, null, null, null));
    }
}
