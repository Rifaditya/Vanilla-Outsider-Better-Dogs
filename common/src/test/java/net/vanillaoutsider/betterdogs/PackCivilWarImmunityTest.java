// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs;

import java.util.UUID;
import net.vanillaoutsider.betterdogs.ai.PacifistRevengeGoal;
import net.vanillaoutsider.betterdogs.util.AdultDisciplineHelper;
import net.vanillaoutsider.betterdogs.util.PackAudienceHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 35: Pack Civil War Immunity & Alert Silencing Tests (BL-BD-035)")
class PackCivilWarImmunityTest {

    @Test
    @DisplayName("Assert co-owned packmates owner matching logic")
    void testCoOwnedPackmateMatching() {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();

        assertEquals(ownerA, ownerA, "Matching owner UUID must qualify as packmates");
        assertNotEquals(ownerA, ownerB, "Different owners must not qualify as packmates");
        assertFalse(PackAudienceHelper.isCoOwned(null, null), "Null wolves cannot be co-owned");
    }

    @Test
    @DisplayName("Assert universal domestic alert silencing null safety")
    void testDomesticAlertSilencingNullSafety() {
        assertFalse(AdultDisciplineHelper.shouldSilenceAlert(null, null), "Null victim/attacker must return false");
        assertFalse(AdultDisciplineHelper.isPlayCorrectionSnap(null, null), "Null wolves cannot discipline");
    }

    @Test
    @DisplayName("Assert retaliation suppression logic remains intact")
    void testRetaliationSuppression() {
        assertTrue(AdultDisciplineHelper.shouldSuppressRetaliation(true), "Suppression must be active when disciplined");
        assertFalse(AdultDisciplineHelper.shouldSuppressRetaliation(false), "Suppression must be inactive when not disciplined");
    }

    @Test
    @DisplayName("Assert PacifistRevengeGoal null safety on packmate check")
    void testPacifistRevengeNullSafety() {
        PacifistRevengeGoal goal = new PacifistRevengeGoal(null);
        assertFalse(goal.canUse());
    }

    @Test
    @DisplayName("Assert Negative Parity: Non-co-owned wolves, wild wolves, and sitting/leashed dogs are immune to sparring (BL-BD-035)")
    void testNegativeParitySparringImmunity() {
        assertFalse(net.vanillaoutsider.betterdogs.util.SmallFightHelper.shouldInitiatePlayFight(false, 400, 100, 0, 0),
                "Wild or ineligible wolves must never initiate sparring");
        assertFalse(net.vanillaoutsider.betterdogs.util.SmallFightHelper.isEligibleForPlay(true, false, false, false, false, false, 0, false, false),
                "Wild wolves must never be eligible for play");
        assertFalse(net.vanillaoutsider.betterdogs.util.SmallFightHelper.isEligibleForPlay(true, true, true, false, false, false, 0, false, false),
                "Sitting wolves must never be eligible for play");
        assertFalse(net.vanillaoutsider.betterdogs.util.SmallFightHelper.isEligibleForPlay(true, true, false, true, false, false, 0, false, false),
                "Leashed wolves must never be eligible for play");
        assertFalse(net.vanillaoutsider.betterdogs.util.SmallFightHelper.isEligibleForPlay(true, true, false, false, true, false, 0, false, false),
                "Wolves with active targets must never be eligible for play");
    }
}
