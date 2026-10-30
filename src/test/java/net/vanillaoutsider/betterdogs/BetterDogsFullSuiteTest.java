// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.ai.BabyCuriosityGoal;
import net.vanillaoutsider.betterdogs.registry.BetterDogsTags;
import net.vanillaoutsider.betterdogs.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Master Full-Suite Verification & Helper Contract Audits")
class BetterDogsFullSuiteTest {

    @Test
    @DisplayName("Assert full null safety and contract resilience across all modular helpers")
    void testAllHelpersNullSafety() {
        // WolfCliffSafetyHelper
        assertFalse(WolfCliffSafetyHelper.isDangerousPushDirection(null, 1.0, 0.0));

        // WolfHazardHelper
        assertFalse(WolfHazardHelper.isThermalHazard(null));

        // WolfStormHelper
        assertFalse(WolfStormHelper.isStormAnxietyActive(null));
        assertEquals(1.0f, WolfStormHelper.getPersonalityMultiplier(null));

        // WolfFetchHelper
        assertNull(WolfFetchHelper.findNearbyDroppedStick(null, 5.0));

        // WolfGuardHelper
        assertEquals(8, WolfGuardHelper.getPatrolRadius(null));
        assertEquals(12, WolfGuardHelper.getPatrolRadius(WolfPersonality.AGGRESSIVE));
        assertEquals(4, WolfGuardHelper.getPatrolRadius(WolfPersonality.PACIFIST));
        assertEquals(8, WolfGuardHelper.getPatrolRadius(WolfPersonality.NORMAL));

        // WolfInbreedingHelper
        assertFalse(WolfInbreedingHelper.isInbredPair(null, null));

        // WolfScaleGeneticsHelper
        assertEquals(1.0f, WolfScaleGeneticsHelper.calculateOffspringScale(null, 1.0f, 1.0f, null));

        // DogTreatHelper
        assertFalse(DogTreatHelper.isFavoriteTreat(null, null));
        assertEquals(1, DogTreatHelper.calculateSoothingReduction(10, 0.5f));
        assertEquals(0, DogTreatHelper.calculateSoothingReduction(0, 0.5f));

        // WolfGiftHelper
        assertFalse(WolfGiftHelper.canDeliverGift(null, null));

        // BabyCuriosityGoal
        assertFalse(BabyCuriosityGoal.isInterestingBlock(null));
    }

    @Test
    @DisplayName("Assert deterministic calculations and math bounds across helpers")
    void testDeterministicCalculations() {
        // Patrol radii
        assertEquals(12, WolfGuardHelper.getPatrolRadius(WolfPersonality.AGGRESSIVE));
        assertEquals(4, WolfGuardHelper.getPatrolRadius(WolfPersonality.PACIFIST));
        assertEquals(8, WolfGuardHelper.getPatrolRadius(WolfPersonality.NORMAL));

        int reduction = DogTreatHelper.calculateSoothingReduction(20, 0.5f);
        assertTrue(reduction > 0 && reduction <= 20);
    }

    @Test
    @DisplayName("Assert tags and personality enum mappings")
    void testTagsAndEnums() {
        for (WolfPersonality personality : WolfPersonality.values()) {
            assertEquals(personality, WolfPersonality.fromName(personality.getId()));
            assertNotNull(personality.next());
        }

        assertNotNull(BetterDogsTags.RAW_FOOD);
        assertNotNull(BetterDogsTags.COOKED_FOOD);
        assertNotNull(BetterDogsTags.COMMAND_ITEMS);
    }
}
