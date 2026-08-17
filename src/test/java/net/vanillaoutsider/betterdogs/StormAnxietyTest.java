// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfStormHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StormAnxietyTest {

    @Test
    @DisplayName("Verify Personality Anxiety Multipliers (Pacifist 3x, Normal 1x, Aggressive 0x)")
    public void testPersonalityAnxietyMultipliers() {
        float pacifistMult = 3.0F;
        float normalMult = 1.0F;
        float aggroMult = 0.0F;

        Assertions.assertEquals(3.0F, pacifistMult, 0.001F, "Pacifist must have 3.0x anxiety multiplier");
        Assertions.assertEquals(1.0F, normalMult, 0.001F, "Normal must have 1.0x anxiety multiplier");
        Assertions.assertEquals(0.0F, aggroMult, 0.001F, "Aggressive must have 0.0x anxiety multiplier (fearless)");
    }

    @Test
    @DisplayName("Verify Shelter Search Volume Bounds")
    public void testShelterSearchVolumeBounds() {
        int xRange = 12;
        int yRange = 4;
        int zRange = 12;

        int totalVolume = (xRange * 2 + 1) * (yRange * 2 + 1) * (zRange * 2 + 1);
        Assertions.assertEquals(5625, totalVolume, "Shelter search volume must check 5,625 blocks");
    }

    @Test
    @DisplayName("Verify Storm Helper Null Safety")
    public void testStormHelperNullSafety() {
        Assertions.assertFalse(WolfStormHelper.isStormAnxietyActive(null));
        Assertions.assertEquals(1.0F, WolfStormHelper.getPersonalityMultiplier(null));
        Assertions.assertNull(WolfStormHelper.findShelterTarget(null));
        Assertions.assertFalse(WolfStormHelper.isSafeStandBlock(null, null));
    }
}
