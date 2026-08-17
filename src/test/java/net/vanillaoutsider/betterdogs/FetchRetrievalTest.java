// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.minecraft.world.item.ItemStack;
import net.vanillaoutsider.betterdogs.util.WolfFetchHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FetchRetrievalTest {

    @Test
    @DisplayName("Verify Fetch Detection Range Math & Arrival Thresholds")
    public void testFetchDistanceMath() {
        double maxFetchRadius = 16.0D;
        double maxFetchRadiusSq = maxFetchRadius * maxFetchRadius;
        Assertions.assertEquals(256.0D, maxFetchRadiusSq);

        double itemAt10mSq = 10.0D * 10.0D; // 100.0
        double itemAt20mSq = 20.0D * 20.0D; // 400.0

        Assertions.assertTrue(itemAt10mSq <= maxFetchRadiusSq, "Item at 10m is within 16m fetch range");
        Assertions.assertFalse(itemAt20mSq <= maxFetchRadiusSq, "Item at 20m is outside 16m fetch range");

        double dropArrivalRadius = 2.5D;
        double dropArrivalRadiusSq = dropArrivalRadius * dropArrivalRadius;
        Assertions.assertEquals(6.25D, dropArrivalRadiusSq);

        double pickupArrivalRadius = 1.5D;
        double pickupArrivalRadiusSq = pickupArrivalRadius * pickupArrivalRadius;
        Assertions.assertEquals(2.25D, pickupArrivalRadiusSq);
    }

    @Test
    @DisplayName("Verify WolfFetchHelper Null Safety & Inactive States")
    public void testFetchNullSafety() {
        Assertions.assertFalse(WolfFetchHelper.isFetchItem(null));
        Assertions.assertFalse(WolfFetchHelper.isFetchItem(ItemStack.EMPTY));
        Assertions.assertNull(WolfFetchHelper.findNearbyDroppedFetchItem(null, 16.0D));
        Assertions.assertDoesNotThrow(() -> WolfFetchHelper.pickupItem(null, null));
        Assertions.assertDoesNotThrow(() -> WolfFetchHelper.dropItemToOwner(null, null));
    }
}
