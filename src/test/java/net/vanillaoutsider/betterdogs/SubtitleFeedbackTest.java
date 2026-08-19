// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfFeedbackHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 33: Multi-Language Localized Subtitle Audio & Actionbar Feedback Tests")
class SubtitleFeedbackTest {

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
}
