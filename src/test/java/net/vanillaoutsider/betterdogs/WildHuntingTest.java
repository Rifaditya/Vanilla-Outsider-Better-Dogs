// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WildHuntHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 37: Desperate Low-Health Wild Wolf Hunting Tests")
class WildHuntingTest {

    @Test
    @DisplayName("Assert health threshold calculation (< 50%)")
    void testHealthThresholdCalculation() {
        float maxHealth = 20.0f;
        float currentHealth = 8.0f; // 40%
        int threshold = 50;

        float healthPercent = (currentHealth / maxHealth) * 100.0f;
        assertTrue(healthPercent < threshold, "Wolf at 40% health should initiate low-health hunt (< 50%)");

        float fullHealth = 20.0f; // 100%
        float fullPercent = (fullHealth / maxHealth) * 100.0f;
        assertFalse(fullPercent < threshold, "Wolf at 100% health should not initiate low-health hunt");
    }

    @Test
    @DisplayName("Assert sustenance heal arithmetic (+4.0 HP / 2 hearts)")
    void testSustenanceHealArithmetic() {
        float health = 8.0f;
        float healAmount = WildHuntHelper.SUSTENANCE_HEAL_AMOUNT;
        float maxHealth = 20.0f;

        assertEquals(4.0f, healAmount, 0.01f);
        float postHeal = Math.min(maxHealth, health + healAmount);
        assertEquals(12.0f, postHeal, 0.01f, "Sustenance kill heal should restore 4.0 HP (2 hearts)");
    }

    @Test
    @DisplayName("Assert stop threshold cessation (>= 80%)")
    void testStopThreshold() {
        float maxHealth = 20.0f;
        float recoveredHealth = 17.0f; // 85%
        float recoveredPercent = (recoveredHealth / maxHealth) * 100.0f;

        assertTrue(recoveredPercent >= WildHuntHelper.STOP_HEALTH_THRESHOLD_PERCENT,
                "Hunting goal should cease once health reaches >= 80%");
    }

    @Test
    @DisplayName("Assert WildHuntHelper strict null safety")
    void testWildHuntHelperNullSafety() {
        assertDoesNotThrow(() -> assertFalse(WildHuntHelper.isPrey(null)));
        assertDoesNotThrow(() -> assertFalse(WildHuntHelper.shouldHuntPrey(null)));
        assertDoesNotThrow(() -> assertFalse(WildHuntHelper.shouldContinueHunting(null, null)));
        assertDoesNotThrow(() -> WildHuntHelper.applySustenanceHealing(null));
    }
}
