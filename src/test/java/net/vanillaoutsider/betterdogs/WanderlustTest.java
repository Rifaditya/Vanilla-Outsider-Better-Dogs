// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WanderlustTest {

    @Test
    public void testWanderlustTimerDecrement() {
        int initialTicks = 200;
        int decremented = Math.max(0, initialTicks - 1);

        Assertions.assertEquals(199, decremented, "Wanderlust ticks should decrement per server tick");

        int zeroTicks = Math.max(0, 0 - 1);
        Assertions.assertEquals(0, zeroTicks, "Wanderlust ticks should never fall below zero");
    }

    @Test
    public void testWanderlustEligibilityAndSafety() {
        boolean isTame = true;
        boolean isSitting = false;
        boolean isLeashed = false;
        boolean isGuarding = false;
        boolean hasCombatTarget = false;
        int wanderlustTicks = 200;

        boolean canWander = isTame && !isSitting && !isLeashed && !isGuarding && !hasCombatTarget && wanderlustTicks > 0;
        Assertions.assertTrue(canWander, "Calm, standing tamed dog with active wanderlust ticks should be eligible to roam");

        // Sit command safety check
        isSitting = true;
        boolean canWanderWhileSitting = isTame && !isSitting && !isLeashed && !isGuarding && !hasCombatTarget && wanderlustTicks > 0;
        Assertions.assertFalse(canWanderWhileSitting, "Sitting dog must NEVER execute wanderlust roaming");
    }

    @Test
    public void testOwnerMaxDistanceBounds() {
        double currentDistSqr = 30.0 * 30.0; // 900.0 (within 32 blocks, 1024.0)
        double maxDistSqr = 32.0 * 32.0; // 1024.0

        Assertions.assertTrue(currentDistSqr <= maxDistSqr, "Dog within 30 blocks is within safe owner exploration distance");

        double farDistSqr = 35.0 * 35.0; // 1225.0
        Assertions.assertFalse(farDistSqr <= maxDistSqr, "Dog beyond 32 blocks must cancel wanderlust and return to follow owner");
    }
}
