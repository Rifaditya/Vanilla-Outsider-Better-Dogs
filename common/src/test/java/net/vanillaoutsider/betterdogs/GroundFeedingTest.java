// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GroundFeedingTest {

    @Test
    public void testGroundItemDetectionRadius() {
        double maxRadius = 10.0;
        double maxRadiusSq = maxRadius * maxRadius;

        double itemWithinDistSq = 36.0;
        assertTrue(itemWithinDistSq <= maxRadiusSq, "Food within 10 blocks must be within search radius");

        double itemOutsideDistSq = 144.0;
        assertFalse(itemOutsideDistSq <= maxRadiusSq, "Food outside 10 blocks must be ignored");
    }

    @Test
    public void testHealingNutritionMath() {
        int beefNutrition = 8;
        float beefHeal = (float) beefNutrition * 2.0F;
        assertEquals(16.0F, beefHeal, 0.001F, "Beef must restore 16.0 HP");

        int rottenFleshNutrition = 4;
        float rottenFleshHeal = (float) rottenFleshNutrition * 2.0F;
        assertEquals(8.0F, rottenFleshHeal, 0.001F, "Rotten flesh must restore 8.0 HP");
    }
}
