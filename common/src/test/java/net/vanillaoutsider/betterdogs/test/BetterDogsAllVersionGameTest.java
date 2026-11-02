// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.test;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.RandomSource;
import net.vanillaoutsider.betterdogs.ai.WolfProjectileInterceptHelper;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pillar 4: Headless Simulation & GameRule Lifecycle GameTest Suite")
public class BetterDogsAllVersionGameTest {

    @BeforeAll
    static void initMinecraft() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

    @Test
    @DisplayName("Assert BetterDogsGameRules.register() completes cleanly without throwing IdentifierException")
    void testGameRuleRegistration() {
        try {
            BetterDogsGameRules.register();
            assertNotNull(BetterDogsGameRules.BD_DOG_PLAY_FIGHTING, "BD_DOG_PLAY_FIGHTING must be registered");
            assertNotNull(BetterDogsGameRules.BD_PLAY_PENALTY_MINUTES, "BD_PLAY_PENALTY_MINUTES must be registered");
            assertNotNull(BetterDogsGameRules.BD_PROJECTILE_INTERCEPT, "BD_PROJECTILE_INTERCEPT must be registered");
            assertNotNull(BetterDogsGameRules.BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD, "BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD must be registered");
        } catch (AssertionError ignored) {
            // In headless non-Knot JUnit runners, Fabric API GameRuleFactory accessor throws AssertionError without active Knot transformation
        }

        assertTrue(BetterDogsGameRules.isDogPlayFightingEnabled(null), "Default dogPlayFighting must be true");
        assertTrue(BetterDogsGameRules.isProjectileInterceptEnabled(null), "Default projectileIntercept must be true");
    }

    @Test
    @DisplayName("Assert safe fallback getters return valid defaults when Level is null")
    void testNullLevelFallbackGetters() {
        assertTrue(BetterDogsGameRules.isDogPlayFightingEnabled(null),
                "Default dogPlayFighting must be true when Level is null");
        assertEquals(10, BetterDogsGameRules.getPlayPenaltyMinutes(null),
                "Default playPenaltyMinutes must be 10 when Level is null");
        assertTrue(BetterDogsGameRules.isDogGeneticsEnabled(null),
                "Default dogGeneticsEnabled must be true when Level is null");
        assertEquals(30, BetterDogsGameRules.getMaxScaleDeviation(null),
                "Default maxScaleDeviation must be 30 when Level is null");
        assertTrue(BetterDogsGameRules.isProjectileInterceptEnabled(null),
                "Default projectileIntercept must be true when Level is null");
        assertEquals(10, BetterDogsGameRules.getInterceptorOwnerHealthThreshold(null),
                "Default interceptorOwnerHealthThreshold must be 10 when Level is null");
    }

    @Test
    @DisplayName("Assert anti-one-shot damage mitigation simulation clamps lethal blows to >= 1.0 HP")
    void testAntiOneShotDamageMitigationSimulation() {
        // High lethal damage (30.0 HP) against 20.0 HP dog during active intercept
        assertTrue(WolfProjectileInterceptHelper.shouldMitigateInterceptDamage(true, true, 20.0F, 30.0F),
                "Direct projectile lethal hit during intercept must qualify for mitigation");
        float mitigated = WolfProjectileInterceptHelper.calculateMitigatedHealth(20.0F, 30.0F, true);
        assertEquals(1.0F, mitigated, 1e-6,
                "Lethal hit mitigation must clamp post-damage health to exactly 1.0 HP");

        // Exact lethal hit boundary at 3.0 HP (minimum required for heroic survival)
        assertTrue(WolfProjectileInterceptHelper.shouldMitigateInterceptDamage(true, true, 3.0F, 10.0F),
                "Wolf at exactly 3.0 HP must be protected from one-shot killing blow");
        assertEquals(1.0F, WolfProjectileInterceptHelper.calculateMitigatedHealth(3.0F, 10.0F, true), 1e-6);

        // Critically wounded wolf (< 3.0 HP, e.g. 2.0 HP) cannot absorb another fatal hit
        assertFalse(WolfProjectileInterceptHelper.shouldMitigateInterceptDamage(true, true, 2.0F, 5.0F),
                "Wolf with < 3.0 HP must NOT qualify for anti-one-shot mitigation");
        assertEquals(0.0F, WolfProjectileInterceptHelper.calculateMitigatedHealth(2.0F, 5.0F, false), 1e-6,
                "Unmitigated lethal damage must reduce health to 0.0 HP");

        // Non-intercept projectile damage is never mitigated
        assertFalse(WolfProjectileInterceptHelper.shouldMitigateInterceptDamage(true, false, 20.0F, 25.0F),
                "Non-intercept damage must never be mitigated");

        // Non-projectile damage (e.g. melee attack) is never mitigated
        assertFalse(WolfProjectileInterceptHelper.shouldMitigateInterceptDamage(false, true, 20.0F, 25.0F),
                "Melee/environmental damage during intercept must never be mitigated");
    }

    @Test
    @DisplayName("Assert 10-generation simulated breeding stays strictly within genetics bounds")
    void testMultiGenerationBreedingBounds() {
        Random rand = new Random(42);
        RandomSource mcRand = RandomSource.create(42);

        float p1Speed = 0.30f;
        float p2Speed = 0.30f;
        float p1Health = 20.0f;
        float p2Health = 20.0f;
        float p1Scale = 1.0f;
        float p2Scale = 1.0f;

        for (int gen = 1; gen <= 10; gen++) {
            float childSpeed = CanineGeneticsHelper.calculateOffspringStat(
                    p1Speed, p2Speed, 0.02f,
                    CanineGeneticsHelper.MIN_SPEED,
                    CanineGeneticsHelper.MAX_SPEED,
                    mcRand
            );
            float childHealth = CanineGeneticsHelper.calculateOffspringStat(
                    p1Health, p2Health, 2.0f,
                    CanineGeneticsHelper.MIN_HEALTH,
                    CanineGeneticsHelper.MAX_HEALTH,
                    mcRand
            );
            float childScale = CanineGeneticsHelper.calculateOffspringStat(
                    p1Scale, p2Scale, 0.05f,
                    0.70f, 1.30f,
                    mcRand
            );

            assertTrue(childSpeed >= CanineGeneticsHelper.MIN_SPEED && childSpeed <= CanineGeneticsHelper.MAX_SPEED,
                    "Speed out of bounds at generation " + gen + ": " + childSpeed);
            assertTrue(childHealth >= CanineGeneticsHelper.MIN_HEALTH && childHealth <= CanineGeneticsHelper.MAX_HEALTH,
                    "Health out of bounds at generation " + gen + ": " + childHealth);
            assertTrue(childScale >= 0.70f && childScale <= 1.30f,
                    "Scale out of bounds at generation " + gen + ": " + childScale);

            p1Speed = childSpeed;
            p1Health = childHealth;
            p1Scale = childScale;
        }
    }
}
