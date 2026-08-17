// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
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

    @Test
    @DisplayName("Verify Interdimensional Teleport Sync Null Safety")
    public void testSyncDimensionNullSafety() {
        Assertions.assertDoesNotThrow(() -> WolfCatchupHelper.syncOwnerDimensionTeleport(null, null, null, null));
        Assertions.assertDoesNotThrow(() -> WolfCatchupHelper.checkAndPerformCatchUp(null));
    }
}
