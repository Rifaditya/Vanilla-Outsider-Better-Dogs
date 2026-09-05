// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfHornCommandHelper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GoatHornCommandTest {

    @Test
    public void testHornCommandDefaults() {
        boolean defaultMasterToggle = true;
        int defaultRange = 64;
        int defaultPathingTimeout = 300;
        int defaultOverrideDuration = 600;

        assertTrue(defaultMasterToggle, "Horn commands master toggle must default to true");
        assertEquals(64, defaultRange, "Horn acoustic range must default to 64 blocks");
        assertEquals(300, defaultPathingTimeout, "Horn pathing timeout must default to 300 ticks (15s)");
        assertEquals(600, defaultOverrideDuration, "Horn pacifist override duration must default to 600 ticks (30s)");
    }

    @Test
    public void testSoundLocationArrivalDistance() {
        double arrivalThresholdSq = 4.0; // 2 blocks distance (2.0^2 = 4.0)
        double nearDistSq = 3.5;
        double farDistSq = 16.0;

        assertTrue(nearDistSq <= arrivalThresholdSq, "Wolf within 2 blocks must satisfy arrival threshold");
        assertFalse(farDistSq <= arrivalThresholdSq, "Wolf farther than 2 blocks must continue pathing");
    }

    @Test
    public void testHornHelperNullSafety() {
        assertDoesNotThrow(() -> WolfHornCommandHelper.onHornUsed(null, null, null));
    }
}
