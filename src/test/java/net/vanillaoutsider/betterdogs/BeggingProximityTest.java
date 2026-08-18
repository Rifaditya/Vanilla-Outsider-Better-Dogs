// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.ai.WolfBegGoal;
import net.vanillaoutsider.betterdogs.util.DogTreatHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 24: Dynamic Owner Begging Proximity AI Tests")
class BeggingProximityTest {

    @Test
    @DisplayName("Assert 5-block distance threshold math (d <= 5.0m, d^2 <= 25.0)")
    void testBeggingDistanceThreshold() {
        float lookDistance = WolfBegGoal.DEFAULT_LOOK_DISTANCE;
        assertEquals(5.0f, lookDistance, 0.001f);

        double maxDistSq = (double) (lookDistance * lookDistance);
        assertEquals(25.0D, maxDistSq, 0.001D);

        // Boundary tests
        assertTrue(0.0D <= maxDistSq);
        assertTrue(16.0D <= maxDistSq); // 4 blocks away
        assertTrue(25.0D <= maxDistSq); // 5 blocks away
        assertFalse(25.01D <= maxDistSq); // Beyond 5 blocks
        assertFalse(36.0D <= maxDistSq); // 6 blocks away
    }

    @Test
    @DisplayName("Assert canine food and treat null safety")
    void testFoodAndTreatNullSafety() {
        assertFalse(DogTreatHelper.isCanineFood(null));
        assertFalse(DogTreatHelper.isHoldingFoodOrTreat(null, null));
        assertFalse(DogTreatHelper.isFavoriteTreat(null, null));
        assertFalse(DogTreatHelper.isHoldingFavoriteTreat(null, null));
    }

    @Test
    @DisplayName("Assert deterministic treat index consistency for begging AI")
    void testTreatIndexConsistency() {
        UUID dogId = UUID.fromString("12345678-1234-1234-1234-123456789abc");
        int index1 = DogTreatHelper.calculateTreatIndex(dogId, 9);
        int index2 = DogTreatHelper.calculateTreatIndex(dogId, 9);
        assertEquals(index1, index2);
        assertTrue(index1 >= 0 && index1 < 9);

        assertEquals(0, DogTreatHelper.calculateTreatIndex(null, 9));
        assertEquals(0, DogTreatHelper.calculateTreatIndex(dogId, 0));
    }
}
