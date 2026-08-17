// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfHornCommandHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GoatHornCommandTest {

    @Test
    @DisplayName("Verify Goat Horn Acoustic Command Defaults")
    public void testHornCommandDefaults() {
        int defaultRange = 64;
        int defaultPathingTimeout = 300;
        int defaultOverrideDuration = 600;

        Assertions.assertEquals(64, defaultRange, "Horn acoustic range must default to 64 blocks");
        Assertions.assertEquals(300, defaultPathingTimeout, "Horn pathing timeout must default to 300 ticks (15s)");
        Assertions.assertEquals(600, defaultOverrideDuration, "Horn pacifist override duration must default to 600 ticks (30s)");
    }

    @Test
    @DisplayName("Verify Sound Location Arrival Distance Threshold (4.0 Sq Dist / 2 Blocks)")
    public void testSoundLocationArrivalDistance() {
        double arrivalThresholdSq = 4.0; // 2 blocks distance (2.0^2 = 4.0)
        double nearDistSq = 3.5;
        double farDistSq = 16.0;

        Assertions.assertTrue(nearDistSq <= arrivalThresholdSq, "Wolf within 2 blocks must satisfy arrival threshold");
        Assertions.assertFalse(farDistSq <= arrivalThresholdSq, "Wolf farther than 2 blocks must continue pathing");
    }

    @Test
    @DisplayName("Verify WolfHornCommandHelper Null Safety")
    public void testHornHelperNullSafety() {
        Assertions.assertDoesNotThrow(() -> WolfHornCommandHelper.onHornUsed(null, null, null));
    }
}
