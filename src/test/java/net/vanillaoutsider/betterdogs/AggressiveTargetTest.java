// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.ai.AggressiveTargetGoal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 26: Aggressive Autonomous Stalking AI Tests")
class AggressiveTargetTest {

    @Test
    @DisplayName("Assert 16-block detection perimeter math (d <= 16.0m, d^2 <= 256.0)")
    void testDetectionPerimeterMath() {
        double range = AggressiveTargetGoal.DEFAULT_DETECT_RANGE;
        assertEquals(16.0D, range, 0.001D);

        double rangeSq = range * range;
        assertEquals(256.0D, rangeSq, 0.001D);

        assertTrue(100.0D <= rangeSq); // 10 blocks away
        assertTrue(256.0D <= rangeSq); // Exactly 16 blocks away
        assertFalse(256.01D <= rangeSq); // Beyond 16 blocks
    }

    @Test
    @DisplayName("Assert guard mode expanded perimeter (24m for Aggressive, 16m for Normal)")
    void testGuardModePerimeter() {
        double aggroRange = AggressiveTargetGoal.GUARD_AGGRO_RANGE;
        double normalRange = AggressiveTargetGoal.GUARD_NORMAL_RANGE;

        assertEquals(24.0D, aggroRange, 0.001D);
        assertEquals(16.0D, normalRange, 0.001D);

        double aggroSq = aggroRange * aggroRange;
        assertEquals(576.0D, aggroSq, 0.001D);
    }

    @Test
    @DisplayName("Assert personality enum validity for Aggressive filtering")
    void testPersonalityFiltering() {
        assertEquals(WolfPersonality.AGGRESSIVE, WolfPersonality.valueOf("AGGRESSIVE"));
        assertNotEquals(WolfPersonality.AGGRESSIVE, WolfPersonality.PACIFIST);
        assertNotEquals(WolfPersonality.AGGRESSIVE, WolfPersonality.NORMAL);
    }

    @Test
    @DisplayName("Assert null safety across target eligibility helpers")
    void testTargetEligibilityNullSafety() {
        assertFalse(AggressiveTargetGoal.isEligibleTarget(null, null));
    }
}
