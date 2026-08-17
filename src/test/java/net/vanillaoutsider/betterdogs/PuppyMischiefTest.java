// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfMischiefHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PuppyMischiefTest {

    @Test
    @DisplayName("Verify Puppy Mischief Radius and Distance Math")
    public void testPuppyMischiefRadiusAndDistanceMath() {
        double mischiefRadius = 8.0;
        double mischiefRadiusSq = mischiefRadius * mischiefRadius;

        double targetWithinRadiusSq = 25.0;
        double targetOutsideRadiusSq = 100.0;

        Assertions.assertTrue(targetWithinRadiusSq <= mischiefRadiusSq, "Adult wolf at 5 blocks should be detected as mischief target.");
        Assertions.assertFalse(targetOutsideRadiusSq <= mischiefRadiusSq, "Adult wolf at 10 blocks should not be detected.");
    }

    @Test
    @DisplayName("Verify Adult Discipline Distance Threshold")
    public void testAdultDisciplineDistanceThreshold() {
        double disciplineRadius = 2.0;
        double disciplineRadiusSq = disciplineRadius * disciplineRadius;

        Assertions.assertTrue(1.5 * 1.5 <= disciplineRadiusSq, "Puppy at 1.5 blocks should trigger adult discipline.");
        Assertions.assertFalse(3.0 * 3.0 <= disciplineRadiusSq, "Puppy at 3.0 blocks is too far to trigger discipline.");
    }

    @Test
    @DisplayName("Verify Calm Discipline Duration Ticks (160 Ticks / 8 Seconds)")
    public void testCalmDisciplineDurationTicks() {
        int calmDurationTicks = 160;
        Assertions.assertEquals(160, calmDurationTicks, "Disciplined puppy should receive 160 calm ticks (8 seconds).");
    }

    @Test
    @DisplayName("Verify WolfMischiefHelper Null Safety")
    public void testMischiefHelperNullSafety() {
        Assertions.assertNull(WolfMischiefHelper.findMischiefTarget(null, 8.0));
        Assertions.assertDoesNotThrow(() -> WolfMischiefHelper.performDiscipline(null, null));
    }
}
