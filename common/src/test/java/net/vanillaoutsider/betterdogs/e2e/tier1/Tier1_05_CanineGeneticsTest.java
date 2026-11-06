// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier1;

import net.minecraft.util.RandomSource;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 1 - Feature 05: Canine Physical Genetics & Timbre Tests")
public class Tier1_05_CanineGeneticsTest {

    @Test
    @DisplayName("Assert Gaussian stat inheritance stays bounded over repeated generations")
    public void testStatInheritanceBounds() {
        RandomSource rand = RandomSource.create(12345L);
        float p1 = 20.0f;
        float p2 = 30.0f;

        for (int i = 0; i < 500; i++) {
            float childHealth = CanineGeneticsHelper.calculateOffspringStat(
                    p1, p2, CanineGeneticsHelper.HEALTH_STD_DEV,
                    CanineGeneticsHelper.MIN_HEALTH, CanineGeneticsHelper.MAX_HEALTH, rand
            );
            assertTrue(childHealth >= CanineGeneticsHelper.MIN_HEALTH && childHealth <= CanineGeneticsHelper.MAX_HEALTH,
                    "Health must be clamped within bounds: " + childHealth);
        }
    }

    @Test
    @DisplayName("Assert health bounds clamping invariants [10.0, 60.0]")
    public void testHealthBoundsClamping() {
        assertEquals(10.0f, CanineGeneticsHelper.MIN_HEALTH, 1e-4f);
        assertEquals(60.0f, CanineGeneticsHelper.MAX_HEALTH, 1e-4f);

        float clampedMin = CanineGeneticsHelper.clamp(5.0f, CanineGeneticsHelper.MIN_HEALTH, CanineGeneticsHelper.MAX_HEALTH);
        assertEquals(10.0f, clampedMin, 1e-4f, "Values below MIN_HEALTH must clamp to 10.0f");

        float clampedMax = CanineGeneticsHelper.clamp(75.0f, CanineGeneticsHelper.MIN_HEALTH, CanineGeneticsHelper.MAX_HEALTH);
        assertEquals(60.0f, clampedMax, 1e-4f, "Values above MAX_HEALTH must clamp to 60.0f");
    }

    @Test
    @DisplayName("Assert speed bounds clamping invariants [0.15, 0.45]")
    public void testSpeedBoundsClamping() {
        assertEquals(0.15f, CanineGeneticsHelper.MIN_SPEED, 1e-4f);
        assertEquals(0.45f, CanineGeneticsHelper.MAX_SPEED, 1e-4f);

        float clampedLow = CanineGeneticsHelper.clamp(0.05f, CanineGeneticsHelper.MIN_SPEED, CanineGeneticsHelper.MAX_SPEED);
        assertEquals(0.15f, clampedLow, 1e-4f);

        float clampedHigh = CanineGeneticsHelper.clamp(0.85f, CanineGeneticsHelper.MIN_SPEED, CanineGeneticsHelper.MAX_SPEED);
        assertEquals(0.45f, clampedHigh, 1e-4f);
    }

    @Test
    @DisplayName("Assert scale bounds clamping invariants [0.70, 1.30]")
    public void testScaleBoundsClamping() {
        assertEquals(0.70f, CanineGeneticsHelper.MIN_SCALE, 1e-4f);
        assertEquals(1.30f, CanineGeneticsHelper.MAX_SCALE, 1e-4f);

        float clampedLow = CanineGeneticsHelper.clamp(0.20f, CanineGeneticsHelper.MIN_SCALE, CanineGeneticsHelper.MAX_SCALE);
        assertEquals(0.70f, clampedLow, 1e-4f);

        float clampedHigh = CanineGeneticsHelper.clamp(2.50f, CanineGeneticsHelper.MIN_SCALE, CanineGeneticsHelper.MAX_SCALE);
        assertEquals(1.30f, clampedHigh, 1e-4f);
    }

    @Test
    @DisplayName("Assert acoustic timbre invariant: pitch modulates inversely to scale")
    public void testAcousticTimbreModulation() {
        float basePitch = 1.0f;
        float pitchSmall = CanineGeneticsHelper.calculateVoicePitch(basePitch, 0.70f);
        float pitchNormal = CanineGeneticsHelper.calculateVoicePitch(basePitch, 1.00f);
        float pitchLarge = CanineGeneticsHelper.calculateVoicePitch(basePitch, 1.30f);

        assertTrue(pitchSmall > pitchNormal, "Smaller dog must produce higher pitch (>1.0), got: " + pitchSmall);
        assertEquals(1.0f, pitchNormal, 1e-4f, "Normal scale 1.0 must preserve 1.0 base pitch");
        assertTrue(pitchLarge < pitchNormal, "Larger dog must produce deeper pitch (<1.0), got: " + pitchLarge);
        assertTrue(pitchSmall <= CanineGeneticsHelper.MAX_PITCH);
        assertTrue(pitchLarge >= CanineGeneticsHelper.MIN_PITCH);
    }
}
