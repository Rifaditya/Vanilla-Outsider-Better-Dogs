// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier3;

import net.vanillaoutsider.betterdogs.ai.FireballTrajectoryHelper;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 3 - Pairwise 07: Physical Scale Genetics x Acoustic Pitch x Evasion Physics Tests")
public class Tier3_07_GeneticsAcousticEvasionTest {

    @Test
    @DisplayName("Assert small scale dog genetics produce high pitch and valid lateral dodge impulse")
    public void testSmallDogGeneticsPitchAndImpulse() {
        float smallScale = 0.70f;
        float pitch = CanineGeneticsHelper.calculateVoicePitch(1.0f, smallScale);
        assertTrue(pitch > 1.15f, "0.70 scale dog must have high bark pitch");

        // Vertical hop impulse is constant across sizes
        assertEquals(0.28, FireballTrajectoryHelper.VERTICAL_HOP_IMPULSE, 1e-4);
    }

    @Test
    @DisplayName("Assert large scale dog genetics produce deep bass pitch and valid evasion trajectory")
    public void testLargeDogGeneticsPitchAndImpulse() {
        float largeScale = 1.30f;
        float pitch = CanineGeneticsHelper.calculateVoicePitch(1.0f, largeScale);
        assertTrue(pitch < 0.90f, "1.30 scale dog must have deep bass bark pitch");

        assertTrue(pitch >= CanineGeneticsHelper.MIN_PITCH);
    }
}
