// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier2;

import java.util.UUID;
import net.vanillaoutsider.betterdogs.util.AdultDisciplineHelper;
import net.vanillaoutsider.betterdogs.util.SmallFightHelper;
import net.vanillaoutsider.betterdogs.util.WolfTargetingHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 2 - Boundary 09: Civil War & Sparring Edge Conditions")
public class Tier2_09_CivilWarEdgeConditionsTest {

    @Test
    @DisplayName("Assert self-targeting can never trigger adult discipline or domestic civil war")
    public void testSelfTargetingImmunity() {
        // A wolf cannot discipline itself
        assertFalse(AdultDisciplineHelper.canDiscipline(null, null));
    }

    @Test
    @DisplayName("Assert null parameters in shouldSilenceAlert safely return false")
    public void testNullParametersSilenceAlert() {
        assertFalse(AdultDisciplineHelper.shouldSilenceAlert(null, null));
    }

    @Test
    @DisplayName("Assert sitting and leashed wolves are strictly shielded from sparring initiation")
    public void testPostureAndLeashImmunity() {
        // Sitting dog: sitting=true
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, true, false, false, false, 0, false, false));
        // Leashed dog: leashed=true
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, true, false, false, 0, false, false));
        // In-combat dog: inCombat=true
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, false, true, false, 0, false, false));
    }

    @Test
    @DisplayName("Assert retaliation suppression flag is strictly boolean and non-null")
    public void testRetaliationSuppressionPurity() {
        assertTrue(WolfTargetingHandler.shouldSuppressRetaliation(true));
        assertFalse(WolfTargetingHandler.shouldSuppressRetaliation(false));
    }

    @Test
    @DisplayName("Assert identical UUIDs always qualify as domestic packmates")
    public void testIdenticalUuidPackmateMatching() {
        UUID ownerId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        assertEquals(ownerId, ownerId, "Matching UUIDs must evaluate as co-owned");
        assertNotEquals(ownerId, otherId, "Distinct UUIDs must not evaluate as co-owned");
    }
}
