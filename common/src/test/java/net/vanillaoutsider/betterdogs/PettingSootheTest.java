// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfPettingHelper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PettingSootheTest {

    @Test
    public void testSootheDurationConstant() {
        // 10 minutes = 10 * 60 * 20 = 12,000 ticks
        assertEquals(12000L, WolfPettingHelper.SOOTHE_DURATION_TICKS, "Soothe duration must be exactly 12,000 ticks (10 minutes)");
    }

    @Test
    public void testSootheExpiryMath() {
        long petTime = 1000L;
        long duration = WolfPettingHelper.SOOTHE_DURATION_TICKS;

        long immediateTick = 1000L;
        long midTick = 1000L + 6000L;
        long lastActiveTick = 1000L + duration - 1L;
        long expiredTick = 1000L + duration;
        long farFutureTick = 1000L + duration + 5000L;

        assertTrue((immediateTick - petTime) < duration, "Must be soothed immediately upon petting");
        assertTrue((midTick - petTime) < duration, "Must remain soothed after 5 minutes");
        assertTrue((lastActiveTick - petTime) < duration, "Must remain soothed at tick 11999");
        assertFalse((expiredTick - petTime) < duration, "Must expire at exactly tick 12000");
        assertFalse((farFutureTick - petTime) < duration, "Must remain expired in the far future");
    }

    @Test
    public void testPettingPreconditionValidation() {
        // Null checks
        assertFalse(WolfPettingHelper.canPet(null, null, null, null), "Null arguments must not allow petting");
    }
}
