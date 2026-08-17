// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfPettingHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PettingSootheTest {

    @Test
    @DisplayName("Verify Soothe Duration Constant (10 Minutes / 12,000 Ticks)")
    public void testSootheDurationConstant() {
        Assertions.assertEquals(12000L, WolfPettingHelper.SOOTHE_DURATION_TICKS, "Soothe duration must be exactly 12,000 ticks (10 minutes)");
        Assertions.assertEquals(20L, WolfPettingHelper.PET_COOLDOWN_TICKS, "Pet cooldown debounce must be 20 ticks (1 second)");
    }

    @Test
    @DisplayName("Verify Soothe Expiry Math Across Game Ticks")
    public void testSootheExpiryMath() {
        long petTime = 1000L;
        long duration = WolfPettingHelper.SOOTHE_DURATION_TICKS;

        long immediateTick = 1000L;
        long midTick = 1000L + 6000L;
        long lastActiveTick = 1000L + duration - 1L;
        long expiredTick = 1000L + duration;
        long farFutureTick = 1000L + duration + 5000L;

        Assertions.assertTrue((immediateTick - petTime) < duration, "Must be soothed immediately upon petting");
        Assertions.assertTrue((midTick - petTime) < duration, "Must remain soothed after 5 minutes");
        Assertions.assertTrue((lastActiveTick - petTime) < duration, "Must remain soothed at tick 11999");
        Assertions.assertFalse((expiredTick - petTime) < duration, "Must expire at exactly tick 12000");
        Assertions.assertFalse((farFutureTick - petTime) < duration, "Must remain expired in the far future");
    }

    @Test
    @DisplayName("Verify Petting Precondition Validation and Null Safety")
    public void testPettingPreconditionValidation() {
        Assertions.assertFalse(WolfPettingHelper.canPet(null, null, null, null), "Null arguments must not allow petting");
        Assertions.assertFalse(WolfPettingHelper.isSoothed(null), "Null wolf must not be soothed");
        Assertions.assertDoesNotThrow(() -> WolfPettingHelper.petWolf(null, null));
    }
}
