// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.registry.BetterDogsTags;
import net.vanillaoutsider.betterdogs.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Master Full-Suite Verification & Helper Contract Audits")
class BetterDogsFullSuiteTest {

    @Test
    @DisplayName("Assert full null safety and contract resilience across all modular helpers")
    void testAllHelpersNullSafety() {
        // WolfCliffSafetyHelper
        assertFalse(WolfCliffSafetyHelper.isDangerousPushDirection(null, 1.0, 0.0));
        assertFalse(WolfCliffSafetyHelper.isSafeWaterLanding(null, 0, 64, 0, 4));

        // WolfHazardHelper
        assertFalse(WolfHazardHelper.isThermalHazard(null));
        assertFalse(WolfHazardHelper.isHazardNearby(null, 5.0));

        // WolfStormHelper
        assertFalse(WolfStormHelper.isStormAnxietyActive(null));
        assertEquals(1.0f, WolfStormHelper.getPersonalityMultiplier(null));

        // WolfFetchHelper
        assertFalse(WolfFetchHelper.isFetchItem(null));
        assertNull(WolfFetchHelper.findNearbyDroppedFetchItem(null, 5.0));

        // WolfGuardHelper
        assertEquals(8, WolfGuardHelper.getPatrolRadius(null));
        assertEquals(12, WolfGuardHelper.getPatrolRadius(WolfPersonality.AGGRESSIVE));
        assertEquals(4, WolfGuardHelper.getPatrolRadius(WolfPersonality.PACIFIST));
        assertEquals(8, WolfGuardHelper.getPatrolRadius(WolfPersonality.NORMAL));

        // WolfInbreedingHelper
        assertDoesNotThrow(() -> WolfInbreedingHelper.processBreedingLineage(null, null, null, null));

        // WolfScaleGeneticsHelper
        assertEquals(1.0f, WolfScaleGeneticsHelper.calculateOffspringScale(null, 1.0f, 1.0f, null));

        // DogTreatHelper
        assertEquals(0, DogTreatHelper.calculateTreatIndex(null, 9));
        assertFalse(DogTreatHelper.isFavoriteTreat(null, null));

        // WolfGiftHelper
        assertFalse(WolfGiftHelper.canDeliverGift(null, null));
        assertEquals(0L, WolfGiftHelper.getDayCount(null));

        // BabyCuriosityHelper
        assertFalse(BabyCuriosityHelper.canExhibitCuriosity(null));
        assertFalse(BabyCuriosityHelper.isCuriousEntity(null, null));
        assertFalse(BabyCuriosityHelper.isInterestingBlock(null));
    }

    @Test
    @DisplayName("Assert deterministic calculations and math bounds across helpers")
    void testDeterministicCalculations() {
        UUID testUuid = UUID.fromString("11111111-2222-3333-4444-555555555555");
        int indexA = DogTreatHelper.calculateTreatIndex(testUuid, 9);
        int indexB = DogTreatHelper.calculateTreatIndex(testUuid, 9);
        assertEquals(indexA, indexB);
        assertTrue(indexA >= 0 && indexA < 9);

        // Patrol radii
        assertEquals(12, WolfGuardHelper.getPatrolRadius(WolfPersonality.AGGRESSIVE));
        assertEquals(4, WolfGuardHelper.getPatrolRadius(WolfPersonality.PACIFIST));
        assertEquals(8, WolfGuardHelper.getPatrolRadius(WolfPersonality.NORMAL));
    }

    @Test
    @DisplayName("Assert tags and personality enum mappings")
    void testTagsAndEnums() {
        for (WolfPersonality personality : WolfPersonality.values()) {
            assertEquals(personality, WolfPersonality.fromId(personality.getId()));
            assertNotNull(personality.next());
        }

        assertNotNull(BetterDogsTags.TREATS);
        assertNotNull(BetterDogsTags.CURIOSITY_BLOCKS);
        assertNotNull(BetterDogsTags.SEATS);
        assertNotNull(BetterDogsTags.COMMON_CHAIRS);
    }
}
