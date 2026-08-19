// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.BabyCuriosityHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 31: Puppy Playful Exploration AI Tests")
class BabyCuriosityTest {

    @Test
    @DisplayName("Assert distance constants match design specifications")
    void testDistanceConstants() {
        assertEquals(100.0D, BabyCuriosityHelper.MAX_TARGET_DISTANCE_SQ, 0.001D, "Max search range must be 10 blocks (100 distance squared)");
        assertEquals(6.25D, BabyCuriosityHelper.CLOSE_INSPECT_DISTANCE_SQ, 0.001D, "Inspection range must be 2.5 blocks (6.25 distance squared)");
    }

    @Test
    @DisplayName("Assert personality curiosity trigger intervals")
    void testPersonalityCuriosityDelays() {
        assertEquals(40, BabyCuriosityHelper.calculateCuriosityDelay(WolfPersonality.PACIFIST), "Pacifist puppies should have high curiosity (40 ticks)");
        assertEquals(80, BabyCuriosityHelper.calculateCuriosityDelay(WolfPersonality.NORMAL), "Normal puppies should have standard curiosity (80 ticks)");
        assertEquals(-1, BabyCuriosityHelper.calculateCuriosityDelay(WolfPersonality.AGGRESSIVE), "Aggressive puppies should not exhibit harmless curiosity (-1 ticks)");
        assertEquals(80, BabyCuriosityHelper.calculateCuriosityDelay(null), "Null personality should default to normal delay");
    }

    @Test
    @DisplayName("Assert strict null and invalid input safety across curiosity helper methods")
    void testNullSafety() {
        assertFalse(BabyCuriosityHelper.canExhibitCuriosity(null));
        assertFalse(BabyCuriosityHelper.isCuriousEntity(null, null));
        assertFalse(BabyCuriosityHelper.isInterestingBlock(null));
        assertDoesNotThrow(() -> BabyCuriosityHelper.playCuriosityFeedback(null));
    }
}
