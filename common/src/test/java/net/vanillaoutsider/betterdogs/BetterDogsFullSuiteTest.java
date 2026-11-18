// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.11
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        assertFalse(WolfGiftHelper.hasDaylightCycle(null));
        assertEquals(0L, WolfGiftHelper.getCelestialDayTime(null));
        assertEquals(0L, WolfGiftHelper.getDayCount(null));
        assertFalse(WolfGiftHelper.isMorningOrWaking(null, null));

        // BabyCuriosityGoal
        assertFalse(BabyCuriosityGoal.isInterestingBlock(null));

        // SmallFightHelper (BL-BD-043)
        SmallFightHelper.clearPlayPenalties();
        SmallFightHelper.clearCooldowns();
        SmallFightHelper.clearCorrectionSnaps();
        SmallFightHelper.clearSessions();
        assertFalse(SmallFightHelper.isEligibleForPlay(null));
        assertFalse(SmallFightHelper.shouldInitiatePlayFight(null));
        assertDoesNotThrow(() -> SmallFightHelper.applyCooldown(null));
        assertEquals(0, SmallFightHelper.getTrackedPenaltyCount());
        assertEquals(0, SmallFightHelper.getTrackedCooldownCount());
        assertEquals(0, SmallFightHelper.getTrackedCorrectionSnapCount());
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
        assertNotNull(BetterDogsTags.TREATS);
        assertNotNull(BetterDogsTags.COMMAND_ITEMS);
    }

    @Test
    @DisplayName("BL-BD-042: Assert WolfGiftHelper celestial day math, morning window, and source contract")
    void testMorningGiftHelperContractsAndMath() throws IOException {
        assertEquals(0L, WolfGiftHelper.calculateDayCount(18000L));
        assertEquals(1L, WolfGiftHelper.calculateDayCount(24000L));
        assertEquals(-1L, WolfGiftHelper.calculateDayCount(-1L));
        assertEquals(-1L, WolfGiftHelper.calculateDayCount(-24000L));

        assertTrue(WolfGiftHelper.isMorningOrWakingTicks(0L, false));
        assertTrue(WolfGiftHelper.isMorningOrWakingTicks(2000L, false));
        assertFalse(WolfGiftHelper.isMorningOrWakingTicks(2001L, false));
        assertFalse(WolfGiftHelper.isMorningOrWakingTicks(6000L, false));
        assertFalse(WolfGiftHelper.isMorningOrWakingTicks(14000L, false));
        assertFalse(WolfGiftHelper.isMorningOrWakingTicks(-1000L, false));
        assertTrue(WolfGiftHelper.isMorningOrWakingTicks(14000L, true));
        assertTrue(WolfGiftHelper.isMorningOrWakingTicks(-24000L, false));

        String source = Files.readString(resolveWolfGiftHelperSource(), StandardCharsets.UTF_8);
        assertFalse(source.contains("getGameTime() / 24000L"));
        assertFalse(source.contains("getGameTime() % 24000L"));
        assertTrue(source.contains("getDayTime()"));
        assertTrue(source.contains("hasDaylightCycle"));
        assertTrue(source.contains("Math.floorDiv"));
        assertTrue(source.contains("Math.floorMod"));
    }

    private static Path resolveWolfGiftHelperSource() {
        Path cursor = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (cursor != null) {
            Path direct = cursor.resolve("src/main/java/net/vanillaoutsider/betterdogs/util/WolfGiftHelper.java");
            if (Files.exists(direct)) {
                return direct;
            }
            Path common = cursor.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfGiftHelper.java");
            if (Files.exists(common)) {
                return common;
            }
            cursor = cursor.getParent();
        }
        throw new AssertionError("Unable to locate WolfGiftHelper.java from " + System.getProperty("user.dir"));
    }
}
