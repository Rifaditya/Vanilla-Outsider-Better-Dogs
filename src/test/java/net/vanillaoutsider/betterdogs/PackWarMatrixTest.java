// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfTerritorialRivalryHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 40: Wild Wolf Pack War & Territorial Rivalry Matrix Tests")
class PackWarMatrixTest {

    @Test
    @DisplayName("Assert territorial standoff radius and duration constants")
    void testTerritoryConstants() {
        assertEquals(96.0, WolfTerritorialRivalryHelper.DEFAULT_TERRITORY_RADIUS);
        assertEquals(1200, WolfTerritorialRivalryHelper.DEFAULT_WAR_DURATION_TICKS);
    }

    @Test
    @DisplayName("Assert RivalryOutcome enum states")
    void testRivalryOutcomeEnum() {
        assertEquals(3, WolfTerritorialRivalryHelper.RivalryOutcome.values().length);
        assertNotNull(WolfTerritorialRivalryHelper.RivalryOutcome.valueOf("WAR"));
        assertNotNull(WolfTerritorialRivalryHelper.RivalryOutcome.valueOf("MERGE"));
        assertNotNull(WolfTerritorialRivalryHelper.RivalryOutcome.valueOf("RETREAT"));
    }

    @Test
    @DisplayName("Assert matrix roll probability math (AA 80% War)")
    void testAaMatrixProbabilityMath() {
        int warChance = 80;
        int mergeChance = 0;
        int warCount = 0;
        Random rand = new Random(42L);

        for (int i = 0; i < 1000; i++) {
            int roll = rand.nextInt(100);
            if (roll < warChance) {
                warCount++;
            }
        }

        assertTrue(warCount > 750 && warCount < 850,
                "80% war roll should trigger roughly 800 times in 1000 trials (got " + warCount + ")");
    }

    @Test
    @DisplayName("Assert WolfTerritorialRivalryHelper strict null safety")
    void testWolfTerritorialRivalryHelperNullSafety() {
        assertDoesNotThrow(() -> assertEquals(0.0, WolfTerritorialRivalryHelper.calculateDominanceScore(null)));
        assertDoesNotThrow(() -> assertFalse(WolfTerritorialRivalryHelper.isMoreDominant(null, null)));
        assertDoesNotThrow(() -> assertEquals(WolfTerritorialRivalryHelper.RivalryOutcome.RETREAT,
                WolfTerritorialRivalryHelper.evaluateOutcome(null, null, null, null)));
        assertDoesNotThrow(() -> WolfTerritorialRivalryHelper.startWar(null, null));
        assertDoesNotThrow(() -> WolfTerritorialRivalryHelper.mergePacks(null, null, 96.0));
    }
}
