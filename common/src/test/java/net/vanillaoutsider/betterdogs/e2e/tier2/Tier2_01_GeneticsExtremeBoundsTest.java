// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier2;

import net.minecraft.util.RandomSource;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 2 - Boundary 01: Genetics Extreme Bounds & Floating-Point Tests")
public class Tier2_01_GeneticsExtremeBoundsTest {

    @Test
    @DisplayName("Assert Float.NaN inputs in clamp utility fallback safely to min bound")
    public void testFloatNanHandlingInClamp() {
        float result = CanineGeneticsHelper.clamp(Float.NaN, 10.0f, 60.0f);
        assertEquals(10.0f, result, 1e-4f, "Float.NaN must fallback safely to min value");
    }

    @Test
    @DisplayName("Assert Float.POSITIVE_INFINITY and Float.MAX_VALUE clamp to MAX_HEALTH")
    public void testExtremeFloatClamping() {
        float posInf = CanineGeneticsHelper.clamp(Float.POSITIVE_INFINITY, CanineGeneticsHelper.MIN_HEALTH, CanineGeneticsHelper.MAX_HEALTH);
        assertEquals(CanineGeneticsHelper.MAX_HEALTH, posInf, 1e-4f);

        float maxVal = CanineGeneticsHelper.clamp(Float.MAX_VALUE, CanineGeneticsHelper.MIN_HEALTH, CanineGeneticsHelper.MAX_HEALTH);
        assertEquals(CanineGeneticsHelper.MAX_HEALTH, maxVal, 1e-4f);

        float negInf = CanineGeneticsHelper.clamp(Float.NEGATIVE_INFINITY, CanineGeneticsHelper.MIN_HEALTH, CanineGeneticsHelper.MAX_HEALTH);
        assertEquals(CanineGeneticsHelper.MIN_HEALTH, negInf, 1e-4f);
    }

    @Test
    @DisplayName("Assert negative stat inputs clamp to MIN_SPEED and MIN_SCALE")
    public void testNegativeStatsClamping() {
        float speed = CanineGeneticsHelper.clamp(-999.0f, CanineGeneticsHelper.MIN_SPEED, CanineGeneticsHelper.MAX_SPEED);
        assertEquals(CanineGeneticsHelper.MIN_SPEED, speed, 1e-4f);

        float scale = CanineGeneticsHelper.clamp(-50.0f, CanineGeneticsHelper.MIN_SCALE, CanineGeneticsHelper.MAX_SCALE);
        assertEquals(CanineGeneticsHelper.MIN_SCALE, scale, 1e-4f);
    }

    @Test
    @DisplayName("Assert acoustic pitch calculation bounds under extreme scale values")
    public void testExtremeScalePitchClamping() {
        float basePitch = 1.0f;

        // Sub-zero / microscopic scale
        float pitchMicro = CanineGeneticsHelper.calculateVoicePitch(basePitch, 0.00001f);
        assertEquals(CanineGeneticsHelper.MAX_PITCH, pitchMicro, 1e-4f, "Microscopic scale must clamp to MAX_PITCH");

        // Gigantic scale
        float pitchGigantic = CanineGeneticsHelper.calculateVoicePitch(basePitch, 99999.0f);
        assertEquals(CanineGeneticsHelper.MIN_PITCH, pitchGigantic, 1e-4f, "Gigantic scale must clamp to MIN_PITCH");

        // NaN scale input
        float pitchNan = CanineGeneticsHelper.calculateVoicePitch(basePitch, Float.NaN);
        assertTrue(pitchNan >= CanineGeneticsHelper.MIN_PITCH && pitchNan <= CanineGeneticsHelper.MAX_PITCH,
                "NaN scale must safely clamp within pitch bounds");
    }

    @Test
    @DisplayName("Assert null RandomSource handles Gaussian drift safely without throwing NPE")
    public void testNullRandomSourceHandling() {
        // When rand is null, CanineGeneticsHelper line 40: float gaussian = (rand != null) ? (float) rand.nextGaussian() : 0.0f;
        assertDoesNotThrow(() -> {
            float childStat = CanineGeneticsHelper.calculateOffspringStat(20.0f, 30.0f, 2.0f, 10.0f, 60.0f, null);
            assertEquals(25.0f, childStat, 1e-4f, "Null random must yield exact midpoint (p1+p2)/2");
        });
    }
}
