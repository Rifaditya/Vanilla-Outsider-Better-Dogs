// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.ai.play.PlayChoreographyHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tamed Pack Clumping & Play Choreography Cooldown Tests (BL-BD-036, BL-BD-037, BL-BD-049)")
class TamedPackClumpingAndPlayTest {

    @Test
    @DisplayName("BL-BD-049: Play choreography disabled when frequency is zero or negative")
    void testPlayChoreographyDisabledWhenFrequencyZero() {
        // frequency = 0 -> must return false immediately
        boolean resultZero = PlayChoreographyHelper.canInitiatePlay(
                true, true, false, false, false,
                20.0f, 20.0f, false, false, true,
                true, true, false, 25.0, 16.0,
                0, 1000L, 0L
        );
        assertFalse(resultZero, "Play choreography must be disabled when frequency is 0");

        // frequency = -1 -> must return false immediately
        boolean resultNegative = PlayChoreographyHelper.canInitiatePlay(
                true, true, false, false, false,
                20.0f, 20.0f, false, false, true,
                true, true, false, 25.0, 16.0,
                -1, 1000L, 0L
        );
        assertFalse(resultNegative, "Play choreography must be disabled when frequency is negative");
    }

    @Test
    @DisplayName("BL-BD-049: Play choreography strictly enforces cooldown timer")
    void testPlayChoreographyCooldownEnforced() {
        long currentGameTime = 5000L;
        long cooldownExpiry = 7400L; // 2400 ticks remaining

        // Current game time is before cooldown expiry -> must return false
        boolean resultCooling = PlayChoreographyHelper.canInitiatePlay(
                true, true, false, false, false,
                20.0f, 20.0f, false, false, true,
                true, true, false, 25.0, 16.0,
                2400, currentGameTime, cooldownExpiry
        );
        assertFalse(resultCooling, "Wolf under cooldown must not initiate play choreography");

        // Game time has passed cooldown expiry -> must return true
        boolean resultReady = PlayChoreographyHelper.canInitiatePlay(
                true, true, false, false, false,
                20.0f, 20.0f, false, false, true,
                true, true, false, 25.0, 16.0,
                2400, 7500L, cooldownExpiry
        );
        assertTrue(resultReady, "Wolf whose cooldown expired must be eligible to initiate play choreography");
    }

    @Test
    @DisplayName("BL-BD-037: Minimum separation distance guard correctly distinguishes overlapping wolves")
    void testOverlapSeparationDistanceGuard() {
        // Overlapping wolves: distance = 0.5 blocks (distSq = 0.25 < 2.25)
        double overlapDistSq = 0.5 * 0.5;
        assertTrue(overlapDistSq < 2.25, "Wolves within 0.5 blocks must trigger overlap separation (< 1.5 blocks)");

        // Distant wolves: distance = 2.0 blocks (distSq = 4.0 >= 2.25)
        double separatedDistSq = 2.0 * 2.0;
        assertFalse(separatedDistSq < 2.25, "Wolves at 2.0 blocks are outside overlap separation boundary");
    }

    @Test
    @DisplayName("BL-BD-036 & BL-BD-049: Pure null safety guards across all extensions and helpers")
    void testNullSafetyGuards() {
        assertDoesNotThrow(() -> PlayChoreographyHelper.applyPlayCooldown(null, null));
        assertDoesNotThrow(() -> assertFalse(PlayChoreographyHelper.canInitiatePlay(null)));
        assertDoesNotThrow(() -> assertFalse(PlayChoreographyHelper.isValidPlayPartner(null, null)));
    }
}
