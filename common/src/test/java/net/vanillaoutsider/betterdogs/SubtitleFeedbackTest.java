// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfFeedbackHelper;
import net.vanillaoutsider.betterdogs.util.WolfHowlAudioHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 33: Multi-Language Localized Subtitle Audio & Actionbar Feedback Tests")
public class SubtitleFeedbackTest {

    @Test
    @DisplayName("Assert default actionbar feedback gamerule value is false for organic immersion")
    void testDefaultActionbarFeedbackIsFalse() {
        boolean defaultFeedback = BetterDogsGameRules.getBoolean(null, BetterDogsGameRules.BD_ACTIONBAR_FEEDBACK, false);
        assertFalse(defaultFeedback, "bd_actionbar_feedback must default to false (disabled) for organic immersion.");
    }

    @Test
    @DisplayName("Assert strict null safety across WolfFeedbackHelper methods")
    void testWolfFeedbackHelperNullSafety() {
        assertDoesNotThrow(() -> WolfFeedbackHelper.sendFeedback(null, null, null));
    }

    @Test
    @DisplayName("Assert WolfHowlAudioHelper null safety on missing level or entity")
    void testWolfHowlAudioHelperNullSafety() {
        assertDoesNotThrow(() -> WolfHowlAudioHelper.playHowlSound(null, null, 1.0f, 1.0f));
    }

    @Test
    @DisplayName("Assert computeVanillaFallbackPitch saturation clamping and NaN handling")
    void testComputeVanillaFallbackPitchBounds() {
        assertEquals(0.65f, WolfHowlAudioHelper.computeVanillaFallbackPitch(Float.NaN), 1e-4f);
        assertEquals(0.65f, WolfHowlAudioHelper.computeVanillaFallbackPitch(Float.POSITIVE_INFINITY), 1e-4f);
        assertEquals(0.65f, WolfHowlAudioHelper.computeVanillaFallbackPitch(Float.NEGATIVE_INFINITY), 1e-4f);
        assertEquals(0.50f, WolfHowlAudioHelper.computeVanillaFallbackPitch(0.10f), 1e-4f);
        assertEquals(0.80f, WolfHowlAudioHelper.computeVanillaFallbackPitch(1.50f), 1e-4f);
        assertEquals(0.70f, WolfHowlAudioHelper.computeVanillaFallbackPitch(0.70f), 1e-4f);
    }
}
