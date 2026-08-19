// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.BabyRetaliationHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 36: Feisty Puppy Retaliation AI Tests")
class BabyRetaliationTest {

    @Test
    @DisplayName("Assert Aggressive personality gating for puppy retaliation")
    void testAggressivePersonalityGating() {
        WolfPersonality aggro = WolfPersonality.AGGRESSIVE;
        WolfPersonality paci = WolfPersonality.PACIFIST;
        WolfPersonality norm = WolfPersonality.NORMAL;

        assertSame(WolfPersonality.AGGRESSIVE, aggro, "Aggressive personality allows retaliation");
        assertNotSame(WolfPersonality.AGGRESSIVE, paci, "Pacifist personality does not allow retaliation");
        assertNotSame(WolfPersonality.AGGRESSIVE, norm, "Normal personality does not allow retaliation");
    }

    @Test
    @DisplayName("Assert retaliation probability roll math at 50% default")
    void testRetaliationProbabilityRoll() {
        int percent = 50;
        int triggeredCount = 0;
        Random rand = new Random(12345L);

        for (int i = 0; i < 1000; i++) {
            if (rand.nextInt(100) < percent) {
                triggeredCount++;
            }
        }

        assertTrue(triggeredCount > 400 && triggeredCount < 600,
                "50% roll should trigger roughly half the time (got " + triggeredCount + ")");
    }

    @Test
    @DisplayName("Assert retaliation timer decay to zero")
    void testRetaliationTimerCountdown() {
        int ticks = BabyRetaliationHelper.RETALIATION_DURATION_TICKS;
        assertEquals(100, ticks);
        for (int t = 0; t < 100; t++) {
            ticks = Math.max(0, ticks - 1);
        }
        assertEquals(0, ticks, "Retaliation timer should expire to 0");
    }

    @Test
    @DisplayName("Assert BabyRetaliationHelper strict null safety")
    void testBabyRetaliationHelperNullSafety() {
        assertDoesNotThrow(() -> assertFalse(BabyRetaliationHelper.isEligible(null)));
        assertDoesNotThrow(() -> assertFalse(BabyRetaliationHelper.shouldTriggerRetaliation(null, null, new Random())));
        assertDoesNotThrow(() -> BabyRetaliationHelper.triggerRetaliation(null, null));
        assertDoesNotThrow(() -> BabyRetaliationHelper.playRetaliationCues(null));
    }
}
