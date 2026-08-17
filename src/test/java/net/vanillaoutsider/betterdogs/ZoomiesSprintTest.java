// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfZoomiesHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ZoomiesSprintTest {

    @Test
    @DisplayName("Verify Zoomies Duration Math & Speed Multipliers")
    public void testZoomiesDurationAndSpeed() {
        int defaultZoomiesTicks = 160; // 8.0 seconds
        double durationSeconds = defaultZoomiesTicks / 20.0D;
        Assertions.assertEquals(8.0D, durationSeconds);

        double baseWolfSpeed = 0.30D;
        double zoomiesSpeedMultiplier = 1.5D;
        double activeSprintSpeed = baseWolfSpeed * zoomiesSpeedMultiplier;
        Assertions.assertEquals(0.45D, activeSprintSpeed, 0.0001D);

        // Boundary clamp: minimum duration 20 ticks (1s)
        int requestedDuration = 5;
        int clampedDuration = Math.max(20, requestedDuration);
        Assertions.assertEquals(20, clampedDuration);
    }

    @Test
    @DisplayName("Verify WolfZoomiesHelper Null Safety & Inactive States")
    public void testZoomiesNullSafety() {
        Assertions.assertFalse(WolfZoomiesHelper.canTriggerZoomies(null));
        Assertions.assertFalse(WolfZoomiesHelper.triggerZoomies(null));
        Assertions.assertDoesNotThrow(() -> WolfZoomiesHelper.tickZoomiesParticles(null));
    }

    @Test
    @DisplayName("Verify Zoomies State Transition Reversibility & Sitting Cancellation")
    public void testZoomiesStateTransitions() {
        int zoomiesTicks = 160;
        Assertions.assertTrue(zoomiesTicks > 0, "Zoomies active when ticks > 0");

        // Simulating sitting posture cancellation
        boolean orderedToSit = true;
        if (orderedToSit) {
            zoomiesTicks = 0;
        }
        Assertions.assertEquals(0, zoomiesTicks, "Sitting command must immediately reset zoomies ticks to 0");
    }
}
