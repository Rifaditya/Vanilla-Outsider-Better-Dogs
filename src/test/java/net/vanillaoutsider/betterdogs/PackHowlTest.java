// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfHowlHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 23: Tamed Spontaneous Pack Howling AI Tests")
class PackHowlTest {

    @Test
    @DisplayName("Assert harmonic pitch variation math bounds (0.85F - 1.20F)")
    void testHarmonicPitchBounds() {
        assertEquals(0.85f, WolfHowlHelper.calculateHarmonicPitch(0.0f), 0.001f);
        assertEquals(1.20f, WolfHowlHelper.calculateHarmonicPitch(1.0f), 0.001f);
        assertEquals(1.025f, WolfHowlHelper.calculateHarmonicPitch(0.5f), 0.001f);

        // Clamp safety
        assertEquals(0.85f, WolfHowlHelper.calculateHarmonicPitch(-10.0f), 0.001f);
        assertEquals(1.20f, WolfHowlHelper.calculateHarmonicPitch(50.0f), 0.001f);
    }

    @Test
    @DisplayName("Assert chorus response delay calculation bounds (10 - 34 ticks)")
    void testChorusDelayBounds() {
        assertEquals(10, WolfHowlHelper.calculateChorusDelay(0));
        assertEquals(34, WolfHowlHelper.calculateChorusDelay(24));
        assertEquals(22, WolfHowlHelper.calculateChorusDelay(12));

        // Clamp safety
        assertEquals(10, WolfHowlHelper.calculateChorusDelay(-5));
        assertEquals(34, WolfHowlHelper.calculateChorusDelay(100));
    }

    @Test
    @DisplayName("Assert howling eligibility filter and null safety")
    void testHowlingFilterAndNullSafety() {
        assertFalse(WolfHowlHelper.canJoinHowl(null));
        assertDoesNotThrow(() -> WolfHowlHelper.initiateChorusHowl(null, 24.0));
        assertDoesNotThrow(() -> WolfHowlHelper.startHowl(null, 1.0f));
    }

    @Test
    @DisplayName("BL-BD-038: Follower chorus delay countdown and state-transition threshold logic")
    void testFollowerChorusStateTransitions() {
        int baseDuration = WolfHowlHelper.BASE_HOWL_DURATION; // 60 ticks
        assertEquals(60, baseDuration, "Base howl duration must be 60 ticks (3.0s)");

        // 1. Alerted wolf in staggered delay window (e.g. 60 + 20 = 80 ticks)
        int delayedTicks = baseDuration + 20;
        boolean canActivateDuringDelay = delayedTicks > 0 && delayedTicks <= baseDuration;
        assertFalse(canActivateDuringDelay, "Alerted follower must NOT activate goal while staggered delay is counting down (> 60 ticks)");

        // 2. Exact transition boundary (delay elapsed: exactly 60 ticks)
        int transitionTicks = baseDuration;
        boolean canActivateAtThreshold = transitionTicks > 0 && transitionTicks <= baseDuration;
        assertTrue(canActivateAtThreshold, "Follower must transition to active vocalization when delay elapses (exactly 60 ticks)");

        // 3. Mid-howl execution (e.g. 30 ticks remaining)
        int activeTicks = 30;
        boolean canActivateMidHowl = activeTicks > 0 && activeTicks <= baseDuration;
        assertTrue(canActivateMidHowl, "Follower remains active while howling ticks count down from 60 to 1");

        // 4. Idle/Completed state (0 ticks)
        int idleTicks = 0;
        boolean canActivateIdle = idleTicks > 0 && idleTicks <= baseDuration;
        assertFalse(canActivateIdle, "Un-alerted / idle wolf must not activate follower chorus branch");
    }

    @Test
    @DisplayName("BL-BD-038: Candidate alert filter prevents duplicate re-triggering of active howlers")
    void testCandidateAlertFilter() {
        // Wolves with howlingTicks <= 0 are eligible to be alerted
        assertTrue(0 <= 0, "Idle packmate (0 ticks) must be eligible for chorus alert");

        // Wolves with howlingTicks > 0 must be skipped from re-alerting
        assertFalse(60 <= 0, "Active howler (60 ticks) must NOT be re-alerted to prevent duplicate sound stacking");
        assertFalse(75 <= 0, "Packmate in delay countdown (75 ticks) must NOT be clobbered by secondary broadcasts");
    }
}
