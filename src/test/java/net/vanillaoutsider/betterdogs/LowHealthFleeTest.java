// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfFleeHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 17: Low-Health Tactical Disengagement Tests")
class LowHealthFleeTest {

    @Test
    @DisplayName("Assert 30% Max HP threshold boundary math")
    void testLowHealthThresholdMath() {
        float maxHealth = 20.0f;
        float threshold = 0.30f; // 6.0 HP

        // Healthy states (>= 30%)
        assertFalse(WolfFleeHelper.isLowHealthMath(20.0f, maxHealth, threshold), "Full HP should not trigger flee");
        assertFalse(WolfFleeHelper.isLowHealthMath(10.0f, maxHealth, threshold), "50% HP should not trigger flee");
        assertFalse(WolfFleeHelper.isLowHealthMath(6.0f, maxHealth, threshold), "Exactly 30% HP should not trigger flee");

        // Low health states (< 30%)
        assertTrue(WolfFleeHelper.isLowHealthMath(5.9f, maxHealth, threshold), "5.9 HP should trigger flee");
        assertTrue(WolfFleeHelper.isLowHealthMath(3.0f, maxHealth, threshold), "15% HP should trigger flee");
        assertTrue(WolfFleeHelper.isLowHealthMath(1.0f, maxHealth, threshold), "Critical 1 HP should trigger flee");

        // Boundary safety
        assertFalse(WolfFleeHelper.isLowHealthMath(0.0f, maxHealth, threshold), "Dead entity (0 HP) should not flee");
        assertFalse(WolfFleeHelper.isLowHealthMath(-1.0f, maxHealth, threshold), "Negative HP should not flee");
        assertFalse(WolfFleeHelper.isLowHealthMath(5.0f, 0.0f, threshold), "Zero max HP should not flee");
    }

    @Test
    @DisplayName("Assert personality flee probability weights and roll thresholds")
    void testPersonalityFleeProbabilities() {
        int pacifistChance = 100;
        int normalChance = 50;
        int aggroChance = 10;

        // Pacifist: 100% chance -> any roll (0..99) flees
        assertTrue(WolfFleeHelper.shouldFleeWithRoll(0, pacifistChance));
        assertTrue(WolfFleeHelper.shouldFleeWithRoll(50, pacifistChance));
        assertTrue(WolfFleeHelper.shouldFleeWithRoll(99, pacifistChance));

        // Normal: 50% chance -> rolls 0..49 flee, 50..99 stand ground
        assertTrue(WolfFleeHelper.shouldFleeWithRoll(0, normalChance));
        assertTrue(WolfFleeHelper.shouldFleeWithRoll(49, normalChance));
        assertFalse(WolfFleeHelper.shouldFleeWithRoll(50, normalChance));
        assertFalse(WolfFleeHelper.shouldFleeWithRoll(99, normalChance));

        // Aggressive: 10% chance -> rolls 0..9 flee, 10..99 fight to the death
        assertTrue(WolfFleeHelper.shouldFleeWithRoll(0, aggroChance));
        assertTrue(WolfFleeHelper.shouldFleeWithRoll(9, aggroChance));
        assertFalse(WolfFleeHelper.shouldFleeWithRoll(10, aggroChance));
        assertFalse(WolfFleeHelper.shouldFleeWithRoll(99, aggroChance));
    }

    @Test
    @DisplayName("Assert deterministic roll edge cases")
    void testDeterministicRollThresholds() {
        assertFalse(WolfFleeHelper.shouldFleeWithRoll(50, 0), "0% chance should never flee");
        assertTrue(WolfFleeHelper.shouldFleeWithRoll(50, 100), "100% chance should always flee");
        assertFalse(WolfFleeHelper.shouldFleeWithRoll(-1, 50), "Negative roll should reject");
    }

    @Test
    @DisplayName("Assert strict null safety across helper methods")
    void testNullSafety() {
        assertFalse(WolfFleeHelper.isLowHealth(null));
        assertEquals(50, WolfFleeHelper.getPersonalityFleeChance(null));
        assertFalse(WolfFleeHelper.shouldFlee(null));
        assertNull(WolfFleeHelper.calculateEscapePosition(null, null, 10, 5));
        assertDoesNotThrow(() -> WolfFleeHelper.playDisengagementFeedback(null));
    }
}
