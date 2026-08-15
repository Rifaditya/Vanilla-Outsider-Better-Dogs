// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfCatchupHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FastTravelCatchupTest {

    @Test
    @DisplayName("Verify Fast Travel Speed Scaling Null Safety")
    public void testCatchupSpeedNullSafety() {
        double speed = WolfCatchupHelper.calculateCatchupSpeed(null, null, 1.25);
        Assertions.assertEquals(1.25, speed);
    }
}
