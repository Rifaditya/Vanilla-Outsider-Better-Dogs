// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfHowlHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 23: Tamed Spontaneous Pack Howling AI Tests")
class PackHowlTest {

    @Test
    @DisplayName("Assert harmonic pitch variation math bounds (0.85F - 1.20F)")
    void testHarmonicPitchBounds() {
        assertEquals(0.85f, WolfHowlHelper.calculateHarmonicPitch(0.0f), 0.001f);
        assertEquals(1.20f, WolfHowlHelper.calculateHarmonicPitch(1.0f), 0.001f);
        assertEquals(1.025f, WolfHowlHelper.calculateHarmonicPitch(0.5f), 0.001f);

        // Clamp safety
        assertEquals(0.85f, WolfHowlHelper.calculateHarmonicPitch(-10.0f), 0.001f);
        assertEquals(1.20f, WolfHowlHelper.calculateHarmonicPitch(50.0f), 0.001f);
    }

    @Test
    @DisplayName("Assert chorus response delay calculation bounds (10 - 34 ticks)")
    void testChorusDelayBounds() {
        assertEquals(10, WolfHowlHelper.calculateChorusDelay(0));
        assertEquals(34, WolfHowlHelper.calculateChorusDelay(24));
        assertEquals(22, WolfHowlHelper.calculateChorusDelay(12));

        // Clamp safety
        assertEquals(10, WolfHowlHelper.calculateChorusDelay(-5));
        assertEquals(34, WolfHowlHelper.calculateChorusDelay(100));
    }

    @Test
    @DisplayName("Assert howling eligibility filter and null safety")
    void testHowlingFilterAndNullSafety() {
        assertFalse(WolfHowlHelper.canJoinHowl(null));
        assertDoesNotThrow(() -> WolfHowlHelper.initiateChorusHowl(null, 24.0));
        assertDoesNotThrow(() -> WolfHowlHelper.startHowl(null, 1.0f));
    }
}
