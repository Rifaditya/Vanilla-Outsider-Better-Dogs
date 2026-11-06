// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier1;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 1 - Feature 04: Dynamic GameRules Parity Tests")
public class Tier1_04_GameRulesParityTest {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BetterDogsGameRules.register();
    }

    @Test
    @DisplayName("Assert GameRule category namespace integrity")
    public void testGameRuleCategoryNamespace() {
        assertNotNull(BetterDogsGameRules.BETTER_DOGS, "BETTER_DOGS category must not be null");
        // Verify identifier path contains 'better_dogs'
        assertTrue(BetterDogsGameRules.BETTER_DOGS.toString().contains("better_dogs")
                || BetterDogsGameRules.BETTER_DOGS.toString().contains("vanilla-outsider-better-dogs"),
                "Category must identify with mod namespace");
    }

    @Test
    @DisplayName("Assert all critical GameRules are successfully registered")
    public void testCriticalGameRulesRegistered() {
        assertNotNull(BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED, "BD_BALLISTIC_EVASION_ENABLED must be registered");
        assertNotNull(BetterDogsGameRules.BD_EVASION_REACTION_TICKS, "BD_EVASION_REACTION_TICKS must be registered");
        assertNotNull(BetterDogsGameRules.BD_EVASION_LATERAL_IMPULSE, "BD_EVASION_LATERAL_IMPULSE must be registered");
        assertNotNull(BetterDogsGameRules.BD_PROJECTILE_INTERCEPT, "BD_PROJECTILE_INTERCEPT must be registered");
        assertNotNull(BetterDogsGameRules.BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD, "BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD must be registered");
        assertNotNull(BetterDogsGameRules.BD_ARCHETYPES_ENABLED, "BD_ARCHETYPES_ENABLED must be registered");
        assertNotNull(BetterDogsGameRules.BD_DOG_GENETICS_ENABLED, "BD_DOG_GENETICS_ENABLED must be registered");
        assertNotNull(BetterDogsGameRules.BD_CANINE_EXPLOSIVE_REFLEX_ENABLED, "BD_CANINE_EXPLOSIVE_REFLEX_ENABLED must be registered");
    }

    @Test
    @DisplayName("Assert DynamicGameRuleManager registry discovery contains registered rules")
    public void testDynamicGameRuleManagerDiscovery() {
        assertNotNull(DynamicGameRuleManager.getDynamicRules(), "Dynamic rules map must not be null");
        assertFalse(DynamicGameRuleManager.getDynamicRules().isEmpty(), "Dynamic rules map must not be empty");
        assertTrue(DynamicGameRuleManager.getDynamicRules().containsKey("betterdogs:ballisticEvasionEnabled"),
                "Dynamic rules must include ballisticEvasionEnabled");
        assertTrue(DynamicGameRuleManager.getDynamicRules().containsKey("betterdogs:evasionReactionTicks"),
                "Dynamic rules must include evasionReactionTicks");
    }

    @Test
    @DisplayName("Assert bounded integer rules enforce valid default values")
    public void testIntegerRuleDefaults() {
        // Test null level safely returns configured default
        int reactionTicks = BetterDogsGameRules.getEvasionReactionTicks(null);
        assertEquals(20, reactionTicks, "Default evasion reaction ticks must be 20");

        int lateralImpulse = BetterDogsGameRules.getEvasionLateralImpulse(null);
        assertEquals(85, lateralImpulse, "Default evasion lateral impulse must be 85");

        int hazardRadius = BetterDogsGameRules.getHazardFleeRadius(null);
        assertEquals(14, hazardRadius, "Default hazard flee radius must be 14");

        int fleeSpeed = BetterDogsGameRules.getFleeSpeedMultiplier(null);
        assertEquals(170, fleeSpeed, "Default flee speed multiplier must be 170%");
    }

    @Test
    @DisplayName("Assert boolean rule default states under null level fallback")
    public void testBooleanRuleDefaultFallbacks() {
        assertTrue(BetterDogsGameRules.isBallisticEvasionEnabled(null), "Ballistic evasion must default to true");
        assertTrue(BetterDogsGameRules.isCanineExplosiveReflexEnabled(null), "Explosive reflex must default to true");
        assertTrue(BetterDogsGameRules.isProjectileInterceptEnabled(null), "Projectile intercept must default to true");
        assertTrue(BetterDogsGameRules.isDogGeneticsEnabled(null), "Dog genetics must default to true");
        assertTrue(BetterDogsGameRules.isArchetypesEnabled(null), "Archetypes must default to true");
    }
}
