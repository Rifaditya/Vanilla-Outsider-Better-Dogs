// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfCureHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 19: Defect Curing via Golden Apple Tests")
class InbredCureTest {

    @Test
    @DisplayName("Assert scale restoration math for cured runts")
    void testCalculateCuredScale() {
        // Inbred runt scale 0.70f -> restored to 1.0f
        assertEquals(1.0f, WolfCureHelper.calculateCuredScale(0.70f), 0.001f);

        // Severely stunted runt scale 0.35f -> restored to 1.0f
        assertEquals(1.0f, WolfCureHelper.calculateCuredScale(0.35f), 0.001f);

        // Normal scale 1.0f -> remains 1.0f
        assertEquals(1.0f, WolfCureHelper.calculateCuredScale(1.0f), 0.001f);

        // Giant dog scale 1.25f -> preserved at 1.25f
        assertEquals(1.25f, WolfCureHelper.calculateCuredScale(1.25f), 0.001f);
    }

    @Test
    @DisplayName("Assert null and empty safety across curing helper methods")
    void testNullSafety() {
        assertFalse(WolfCureHelper.isCureItem(null));
        assertFalse(WolfCureHelper.canCure(null, null, null, null));
        assertDoesNotThrow(() -> WolfCureHelper.tryCureInbredWolf(null, null, null, null));
        assertDoesNotThrow(() -> WolfCureHelper.playCureFeedback(null, null));
    }
}
