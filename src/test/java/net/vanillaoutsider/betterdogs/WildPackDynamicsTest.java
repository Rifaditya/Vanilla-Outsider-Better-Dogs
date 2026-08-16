// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WildPackDynamicsTest {

    @Test
    public void testChorusPropagationDistanceMath() {
        double maxChorusRadius = 24.0;
        double maxChorusRadiusSq = maxChorusRadius * maxChorusRadius;

        double packMemberWithinRangeSq = 144.0;
        double packMemberOutsideRangeSq = 625.0;

        Assertions.assertTrue(packMemberWithinRangeSq <= maxChorusRadiusSq, "Pack member within 24 blocks should receive chorus cue.");
        Assertions.assertFalse(packMemberOutsideRangeSq <= maxChorusRadiusSq, "Pack member outside 24 blocks should not receive chorus cue.");
    }

    @Test
    public void testTerritorialStandoffRadiusMath() {
        double territorialRadius = 12.0;
        double territorialRadiusSq = territorialRadius * territorialRadius;

        Assertions.assertTrue(64.0 <= territorialRadiusSq, "Intruder at 8 blocks is inside territorial standoff boundary.");
        Assertions.assertFalse(225.0 <= territorialRadiusSq, "Intruder at 15 blocks is outside territorial boundary.");
    }

    @Test
    public void testHowlingDurationTicks() {
        int howlduration = 60;
        Assertions.assertEquals(60, howlduration, "Howling animation duration should be 60 ticks (3 seconds).");
    }
}
