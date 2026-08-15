// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfPersonalityStatHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PersonalityStatScalingTest {

    @Test
    @DisplayName("Verify Base Wolf Speed Constant")
    public void testBaseSpeedConstant() {
        Assertions.assertEquals(0.30D, WolfPersonalityStatHelper.BASE_WOLF_SPEED);
    }

    @Test
    @DisplayName("Verify Personality Stat Helper Null Safety")
    public void testPersonalityStatNullSafety() {
        Assertions.assertDoesNotThrow(() -> WolfPersonalityStatHelper.applyPersonalityStats(null, null));
        Assertions.assertDoesNotThrow(() -> WolfPersonalityStatHelper.applyPersonalityStats(null, WolfPersonality.AGGRESSIVE));
        Assertions.assertDoesNotThrow(() -> WolfPersonalityStatHelper.applyPersonalityStats(null, WolfPersonality.PACIFIST));
        Assertions.assertDoesNotThrow(() -> WolfPersonalityStatHelper.applyPersonalityStats(null, WolfPersonality.NORMAL));
    }
}
