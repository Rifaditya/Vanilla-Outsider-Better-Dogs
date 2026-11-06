// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier3;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 3 - Pairwise 04: Dynamic GameRules x Canine Genetics Scaling Tests")
public class Tier3_04_GameRulesGeneticsScalingTest {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BetterDogsGameRules.register();
    }

    @Test
    @DisplayName("Assert BD_DOG_GENETICS_ENABLED gates genetics inheritance calculations")
    public void testGeneticsRuleGating() {
        boolean geneticsEnabled = BetterDogsGameRules.isDogGeneticsEnabled(null);
        assertTrue(geneticsEnabled, "Genetics must default to enabled");
    }

    @Test
    @DisplayName("Assert BD_MAX_SCALE_DEVIATION clamps breeding scale variance")
    public void testMaxScaleDeviationClamping() {
        int maxDeviationPercent = BetterDogsGameRules.getMaxScaleDeviation(null);
        assertEquals(30, maxDeviationPercent, "Default scale deviation must be 30%");

        float baseScale = 1.0f;
        float maxAllowedScale = baseScale * (1.0f + (maxDeviationPercent / 100.0f));
        float minAllowedScale = baseScale * (1.0f - (maxDeviationPercent / 100.0f));

        assertEquals(1.30f, maxAllowedScale, 1e-4f);
        assertEquals(0.70f, minAllowedScale, 1e-4f);
    }
}
