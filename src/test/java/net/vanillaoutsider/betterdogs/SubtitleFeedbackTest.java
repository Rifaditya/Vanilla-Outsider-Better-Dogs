// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfFeedbackHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SubtitleFeedbackTest {

    @Test
    public void testDefaultActionbarFeedbackIsFalse() {
        boolean defaultFeedback = BetterDogsGameRules.getBoolean(null, BetterDogsGameRules.BD_ACTIONBAR_FEEDBACK, false);
        Assertions.assertFalse(defaultFeedback, "bd_actionbar_feedback must default to false (disabled) for organic immersion.");
    }

    @Test
    public void testWolfFeedbackHelperNullSafety() {
        Assertions.assertDoesNotThrow(() -> WolfFeedbackHelper.sendFeedback(null, null, null));
    }
}
