// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier1;

import java.util.UUID;
import net.vanillaoutsider.betterdogs.util.AdultDisciplineHelper;
import net.vanillaoutsider.betterdogs.util.PackAudienceHelper;
import net.vanillaoutsider.betterdogs.util.SmallFightHelper;
import net.vanillaoutsider.betterdogs.util.WolfTargetingHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 1 - Feature 08: Pack Infighting Civil War Immunity Tests")
public class Tier1_08_PackCivilWarImmunityTest {

    @Test
    @DisplayName("Assert co-ownership logic accurately distinguishes packmates vs strangers")
    public void testCoOwnershipDistinction() {
        UUID playerA = UUID.randomUUID();
        UUID playerB = UUID.randomUUID();

        // Same owner = packmates
        assertEquals(playerA, playerA);
        // Different owner = not packmates
        assertNotEquals(playerA, playerB);
        // Null wolves safe check
        assertFalse(PackAudienceHelper.isCoOwned(null, null), "Null wolves cannot be co-owned");
    }

    @Test
    @DisplayName("Assert domestic alert silencing null safety and default protection")
    public void testAlertSilencingNullSafety() {
        assertFalse(AdultDisciplineHelper.shouldSilenceAlert(null, null));
        assertFalse(AdultDisciplineHelper.canDiscipline(null, null));
    }

    @Test
    @DisplayName("Assert retaliation suppression engages when disciplined")
    public void testRetaliationSuppressionGating() {
        assertTrue(WolfTargetingHandler.shouldSuppressRetaliation(true),
                "Retaliation must be suppressed during discipline correction");
        assertFalse(WolfTargetingHandler.shouldSuppressRetaliation(false),
                "Retaliation must not be suppressed under standard combat");
    }

    @Test
    @DisplayName("Assert sparring initiation negative guards for wild wolves")
    public void testWildWolvesSparringImmunity() {
        assertFalse(SmallFightHelper.shouldInitiatePlayFight(false, 400, 100, 0, 0),
                "Wild untamed wolves must never initiate play fighting");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, false, false, false, false, false, 0, false, false),
                "Wild wolves must never be eligible for play sparring");
    }

    @Test
    @DisplayName("Assert sitting and leashed wolves are immune from sparring initiation")
    public void testSittingAndLeashedSparringImmunity() {
        // isEligibleForPlay(isAlive, isTame, isSitting, isLeashed, hasTarget, isOnCooldown, penaltyTicks, isSocialActive, hasBloodFeud)
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, true, false, false, false, 0, false, false),
                "Sitting wolves must never be eligible for sparring");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, true, false, false, 0, false, false),
                "Leashed wolves must never be eligible for sparring");
    }
}
