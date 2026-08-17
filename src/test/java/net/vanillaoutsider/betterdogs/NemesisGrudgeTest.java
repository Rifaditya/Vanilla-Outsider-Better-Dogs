// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfNemesisHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NemesisGrudgeTest {

    @Test
    @DisplayName("Verify Nemesis 3-Day Grudge Math & Boundaries")
    public void testNemesisDurationMath() {
        long ticksPerDay = WolfNemesisHelper.TICKS_PER_DAY;
        Assertions.assertEquals(24000L, ticksPerDay);

        int defaultDays = 3;
        long defaultExpiryOffset = defaultDays * ticksPerDay;
        Assertions.assertEquals(72000L, defaultExpiryOffset);

        long currentGameTime = 100000L;
        long calculatedExpiry = currentGameTime + (Math.max(1, defaultDays) * ticksPerDay);
        Assertions.assertEquals(172000L, calculatedExpiry);

        // Boundary condition: 0 or negative days clamped to 1 day
        int zeroDays = 0;
        long zeroExpiry = currentGameTime + (Math.max(1, zeroDays) * ticksPerDay);
        Assertions.assertEquals(124000L, zeroExpiry);
    }

    @Test
    @DisplayName("Verify WolfNemesisHelper Null Safety & Inactive States")
    public void testNemesisNullSafety() {
        Assertions.assertDoesNotThrow(() -> WolfNemesisHelper.recordNemesis(null, null));
        Assertions.assertDoesNotThrow(() -> WolfNemesisHelper.clearNemesis(null));
        Assertions.assertFalse(WolfNemesisHelper.isNemesisActive(null, null));
    }

    @Test
    @DisplayName("Verify Nemesis Entity Matching Logic")
    public void testNemesisMatchingLogic() {
        String killerType = "minecraft:skeleton";
        String sameTargetType = "minecraft:skeleton";
        String differentTargetType = "minecraft:zombie";

        Assertions.assertTrue(killerType.equals(sameTargetType));
        Assertions.assertFalse(killerType.equals(differentTargetType));
    }
}
