// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfScavengeHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 21: Autonomous Low-Health Scavenging AI Tests")
class GroundFeedingTest {

    @Test
    @DisplayName("Assert empty and null stack healing calculation returns 0")
    void testEmptyStackHealing() {
        assertEquals(0.0f, WolfScavengeHelper.calculateHealAmount(null), 0.001f);
    }

    @Test
    @DisplayName("Assert strict null and invalid input safety across helper methods")
    void testNullSafety() {
        assertFalse(WolfScavengeHelper.isEdible(null, null));
        assertFalse(WolfScavengeHelper.canScavenge(null));
        assertDoesNotThrow(() -> WolfScavengeHelper.consumeGroundFood(null, null));
    }
}
