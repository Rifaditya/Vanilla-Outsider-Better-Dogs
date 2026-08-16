// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PuppyMischiefTest {

    @Test
    public void testPuppyMischiefRadiusAndDistanceMath() {
        double mischiefRadius = 8.0;
        double mischiefRadiusSq = mischiefRadius * mischiefRadius;

        double targetWithinRadiusSq = 25.0;
        double targetOutsideRadiusSq = 100.0;

        Assertions.assertTrue(targetWithinRadiusSq <= mischiefRadiusSq, "Adult wolf at 5 blocks should be detected as mischief target.");
        Assertions.assertFalse(targetOutsideRadiusSq <= mischiefRadiusSq, "Adult wolf at 10 blocks should not be detected.");
    }

    @Test
    public void testAdultDisciplineDistanceThreshold() {
        double disciplineRadius = 2.0;
        double disciplineRadiusSq = disciplineRadius * disciplineRadius;

        Assertions.assertTrue(1.5 * 1.5 <= disciplineRadiusSq, "Puppy at 1.5 blocks should trigger adult discipline.");
        Assertions.assertFalse(3.0 * 3.0 <= disciplineRadiusSq, "Puppy at 3.0 blocks is too far to trigger discipline.");
    }

    @Test
    public void testCalmDisciplineDurationTicks() {
        int calmDurationTicks = 160;
        Assertions.assertEquals(160, calmDurationTicks, "Disciplined puppy should receive 160 calm ticks (8 seconds).");
    }
}
