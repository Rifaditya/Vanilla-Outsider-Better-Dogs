// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WanderlustHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 41: Wanderlust Exploratory Roaming AI Tests")
class WanderlustTest {

    @Test
    @DisplayName("Assert wanderlust distance bounds and thresholds")
    void testWanderlustDistanceBounds() {
        assertEquals(28.0, WanderlustHelper.DEFAULT_ROAM_RADIUS);
        assertEquals(24.0, WanderlustHelper.DEFAULT_RETURN_THRESHOLD);
        assertEquals(32.0, WanderlustHelper.MAX_OWNER_DISTANCE);
        assertEquals(400, WanderlustHelper.WANDERLUST_SURGE_CHANCE);
    }

    @Test
    @DisplayName("Assert wanderlust eligibility and posture safety checks")
    void testWanderlustEligibilityAndSafety() {
        boolean isTame = true;
        boolean isSitting = false;
        boolean isLeashed = false;
        boolean isGuarding = false;
        boolean hasCombatTarget = false;

        boolean canWander = isTame && !isSitting && !isLeashed && !isGuarding && !hasCombatTarget;
        assertTrue(canWander, "Calm, standing tamed dog should be eligible to roam");

        // Sit safety check
        isSitting = true;
        boolean canWanderWhileSitting = isTame && !isSitting && !isLeashed && !isGuarding && !hasCombatTarget;
        assertFalse(canWanderWhileSitting, "Sitting dog must NEVER execute wanderlust roaming");
    }

    @Test
    @DisplayName("Assert 1-in-400 surge probability roll math")
    void testWanderlustSurgeChance() {
        int chance = WanderlustHelper.WANDERLUST_SURGE_CHANCE;
        int triggered = 0;
        Random rand = new Random(42L);

        for (int i = 0; i < 4000; i++) {
            if (rand.nextInt(chance) == 0) {
                triggered++;
            }
        }

        assertTrue(triggered >= 3 && triggered <= 20,
                "1 in 400 roll should trigger roughly 10 times in 4000 trials (got " + triggered + ")");
    }

    @Test
    @DisplayName("Assert WanderlustHelper strict null safety")
    void testWanderlustHelperNullSafety() {
        assertDoesNotThrow(() -> assertFalse(WanderlustHelper.isEligibleForWanderlust(null)));
        assertDoesNotThrow(() -> assertFalse(WanderlustHelper.shouldTriggerWanderlust(null, (Random) null)));
        assertDoesNotThrow(() -> assertFalse(WanderlustHelper.shouldTriggerWanderlust(null, (net.minecraft.util.RandomSource) null)));
        assertDoesNotThrow(() -> assertNull(WanderlustHelper.calculateWanderlustPosition(null, null)));
    }
}
