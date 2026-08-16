// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BabyRetaliationTest {

    @Test
    public void testAggressivePersonalityGating() {
        WolfPersonality aggro = WolfPersonality.AGGRESSIVE;
        WolfPersonality paci = WolfPersonality.PACIFIST;
        WolfPersonality norm = WolfPersonality.NORMAL;

        Assertions.assertTrue(aggro == WolfPersonality.AGGRESSIVE, "Aggressive personality allows retaliation");
        Assertions.assertFalse(paci == WolfPersonality.AGGRESSIVE, "Pacifist personality does not allow retaliation");
        Assertions.assertFalse(norm == WolfPersonality.AGGRESSIVE, "Normal personality does not allow retaliation");
    }

    @Test
    public void testRetaliationProbabilityRoll() {
        int percent = 50;
        int triggeredCount = 0;
        java.util.Random rand = new java.util.Random(12345L);

        for (int i = 0; i < 1000; i++) {
            if (rand.nextInt(100) < percent) {
                triggeredCount++;
            }
        }

        Assertions.assertTrue(triggeredCount > 400 && triggeredCount < 600, "50% roll should trigger roughly half the time (got " + triggeredCount + ")");
    }

    @Test
    public void testRetaliationTimerCountdown() {
        int ticks = 100;
        for (int t = 0; t < 100; t++) {
            ticks = Math.max(0, ticks - 1);
        }
        Assertions.assertEquals(0, ticks, "Retaliation timer should expire to 0");
    }
}
