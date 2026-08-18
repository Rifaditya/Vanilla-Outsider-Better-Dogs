// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.ai.PacifistRevengeGoal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 25: Pacifist Threat Fleeing AI Tests")
class PacifistRevengeTest {

    @Test
    @DisplayName("Assert defense alert radius math (d <= 16.0m, d^2 <= 256.0)")
    void testDefenseAlertRadius() {
        double radius = PacifistRevengeGoal.DEFENSE_ALERT_RADIUS;
        assertEquals(16.0D, radius, 0.001D);

        double radiusSq = radius * radius;
        assertEquals(256.0D, radiusSq, 0.001D);

        assertTrue(100.0D <= radiusSq); // 10 blocks away
        assertTrue(256.0D <= radiusSq); // Exactly 16 blocks away
        assertFalse(256.01D <= radiusSq); // Beyond 16 blocks
    }

    @Test
    @DisplayName("Assert flee speed multiplier (1.25x)")
    void testFleeSpeedMultiplier() {
        assertEquals(1.25D, PacifistRevengeGoal.DEFAULT_FLEE_SPEED, 0.001D);
    }

    @Test
    @DisplayName("Assert personality enum validity for Pacifist filtering")
    void testPersonalityFiltering() {
        assertEquals(WolfPersonality.PACIFIST, WolfPersonality.valueOf("PACIFIST"));
        assertNotEquals(WolfPersonality.PACIFIST, WolfPersonality.AGGRESSIVE);
        assertNotEquals(WolfPersonality.PACIFIST, WolfPersonality.NORMAL);
    }

    @Test
    @DisplayName("Assert null safety across goal methods")
    void testNullSafety() {
        PacifistRevengeGoal goal = new PacifistRevengeGoal(null);
        assertFalse(goal.canUse());
        assertDoesNotThrow(() -> goal.alertNearbyPackmates(null));
        assertDoesNotThrow(goal::stop);
    }
}
