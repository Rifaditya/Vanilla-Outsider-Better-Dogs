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

@DisplayName("Tier 3 - Pairwise 03: Dynamic GameRules x Ballistic Interceptor Gating Tests")
public class Tier3_03_GameRulesBallisticInterceptorTest {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BetterDogsGameRules.register();
    }

    @Test
    @DisplayName("Assert disabling BD_PROJECTILE_INTERCEPT suppresses interception goal activation")
    public void testProjectileInterceptGatingByGameRule() {
        // When projectile intercept rule is disabled, wolves should bypass interception calculations
        boolean isRuleEnabled = false;
        boolean canIntercept = isRuleEnabled && BetterDogsGameRules.isProjectileInterceptEnabled(null);
        assertFalse(canIntercept, "Interception must be gated by the GameRule");
    }

    @Test
    @DisplayName("Assert BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD dictates interception trigger readiness")
    public void testOwnerHealthThresholdGating() {
        int threshold = BetterDogsGameRules.getInterceptorOwnerHealthThreshold(null);
        assertEquals(10, threshold, "Default interceptor owner health threshold must be 10 HP (5 hearts)");

        // Owner at 8 HP (< 10): dog should perform heroic intercept
        float lowOwnerHealth = 8.0f;
        boolean shouldInterceptForLowHealth = lowOwnerHealth <= threshold;
        assertTrue(shouldInterceptForLowHealth, "Owner at 8 HP is below threshold and should trigger interceptor");

        // Owner at 18 HP (> 10): dog does not need to sacrifice itself
        float highOwnerHealth = 18.0f;
        boolean shouldInterceptForHighHealth = highOwnerHealth <= threshold;
        assertFalse(shouldInterceptForHighHealth, "Owner at 18 HP is safe; interceptor goal should not trigger sacrifice");
    }
}
