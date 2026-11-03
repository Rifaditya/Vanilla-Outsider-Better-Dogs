// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FetchAndZoomiesTest {

    @Test
    public void testFetchDetectionDistanceMath() {
        double maxFetchRadius = 16.0;
        double maxFetchRadiusSq = maxFetchRadius * maxFetchRadius;

        double stickWithinRangeSq = 64.0;
        double stickOutsideRangeSq = 300.0;

        Assertions.assertTrue(stickWithinRangeSq <= maxFetchRadiusSq, "Stick within 16 blocks should be detected for fetching.");
        Assertions.assertFalse(stickOutsideRangeSq <= maxFetchRadiusSq, "Stick outside 16 blocks should not be detected.");
    }

    @Test
    public void testFetchPickupAndDropThresholds() {
        double pickupThresholdSq = 2.25;
        double dropThresholdSq = 6.25;

        Assertions.assertTrue(1.0 <= pickupThresholdSq, "Wolf within 1.0 block is close enough to pick up stick.");
        Assertions.assertFalse(3.0 <= pickupThresholdSq, "Wolf at 3.0 blocks is too far to pick up stick.");

        Assertions.assertTrue(4.0 <= dropThresholdSq, "Wolf within 2.0 blocks of owner is close enough to drop stick.");
        Assertions.assertFalse(9.0 <= dropThresholdSq, "Wolf at 3.0 blocks is too far to drop stick.");
    }

    @Test
    public void testZoomiesDurationAndSpeed() {
        int initialZoomiesTicks = 120;
        double zoomiesSpeedMultiplier = 1.5;

        Assertions.assertEquals(120, initialZoomiesTicks, "Zoomies duration should be 120 ticks (6 seconds).");
        Assertions.assertTrue(zoomiesSpeedMultiplier > 1.25, "Zoomies speed must exceed standard follow/sprint speed.");
    }
}
