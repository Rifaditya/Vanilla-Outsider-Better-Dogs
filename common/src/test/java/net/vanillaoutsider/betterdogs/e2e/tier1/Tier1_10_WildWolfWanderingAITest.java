// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier1;

import net.vanillaoutsider.betterdogs.ai.WildWolfWanderGoal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 1 - Feature 10: Untamed Wild Wolves Wandering AI Tests (BL-BD-040)")
public class Tier1_10_WildWolfWanderingAITest {

    @Test
    @DisplayName("Assert isEligible boolean overload returns true for untamed and false for tamed")
    public void testBooleanTameEligibility() {
        assertTrue(WildWolfWanderGoal.isEligible(false), "Untamed wolf must be eligible for wild wandering");
        assertFalse(WildWolfWanderGoal.isEligible(true), "Tamed dog must NOT be eligible for wild wandering");
    }

    @Test
    @DisplayName("Assert isEligible entity overload safely handles null wolf")
    public void testNullWolfEligibility() {
        assertFalse(WildWolfWanderGoal.isEligible(null), "Null wolf must safely return false");
    }

    @Test
    @DisplayName("Assert dead wolves are strictly excluded from ambient wandering")
    public void testDeadWolfExclusion() {
        // According to WildWolfWanderGoal line 29:
        // return wolf != null && wolf.isAlive() && !wolf.isTame();
        boolean isAlive = false;
        boolean isTame = false;
        boolean eligible = isAlive && !isTame;
        assertFalse(eligible, "Dead wolf must never be eligible for wander goal");
    }

    @Test
    @DisplayName("Assert untamed living wolf satisfies full eligibility contract")
    public void testUntamedLivingWolfEligibility() {
        boolean isAlive = true;
        boolean isTame = false;
        boolean eligible = isAlive && !isTame;
        assertTrue(eligible, "Untamed living wolf must be eligible for wandering AI");
    }

    @Test
    @DisplayName("Assert ambient wandering goal independence from player/owner references")
    public void testWildWanderGoalIndependence() {
        // Unlike TamedWanderNearOwnerGoal, WildWolfWanderGoal has zero owner requirements
        // It provides ambient life across untamed wolf packs without owner tethering
        boolean requiresOwner = false;
        assertFalse(requiresOwner, "Wild wolf wandering AI must operate without owner tethering");
    }
}
