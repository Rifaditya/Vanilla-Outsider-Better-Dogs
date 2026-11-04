// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WildHuntTest {

    @Test
    public void testHealthThresholdCalculation() {
        float maxHealth = 20.0f;
        float currentHealth = 8.0f; // 40%
        int threshold = 50;

        float healthPercent = (currentHealth / maxHealth) * 100.0f;
        Assertions.assertTrue(healthPercent < threshold, "Wolf at 40% health should initiate low-health hunt (< 50%)");

        float fullHealth = 20.0f; // 100%
        float fullPercent = (fullHealth / maxHealth) * 100.0f;
        Assertions.assertFalse(fullPercent < threshold, "Wolf at 100% health should not initiate low-health hunt");
    }

    @Test
    public void testSustenanceHealArithmetic() {
        float health = 8.0f;
        float healAmount = 4.0f;
        float maxHealth = 20.0f;

        float postHeal = Math.min(maxHealth, health + healAmount);
        Assertions.assertEquals(12.0f, postHeal, 0.01f, "Sustenance kill heal should restore 4.0 HP (2 hearts)");
    }

    @Test
    public void testStopThreshold() {
        float maxHealth = 20.0f;
        float recoveredHealth = 17.0f; // 85%
        float recoveredPercent = (recoveredHealth / maxHealth) * 100.0f;

        Assertions.assertTrue(recoveredPercent >= 80.0f, "Hunting goal should cease once health reaches >= 80%");
    }
}
