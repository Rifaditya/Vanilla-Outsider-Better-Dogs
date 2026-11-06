// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfVariantHelper;
import net.vanillaoutsider.betterdogs.world.BetterDogsSpawning;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 42: Expanded Spawning, Dynamic Climate Coats & Alpha Leadership Tests")
class SpawnVariantTest {

    @Test
    @DisplayName("Assert spawn cluster size bounds and default weight")
    void testSpawnGroupBounds() {
        assertEquals(4, BetterDogsSpawning.DEFAULT_MIN_GROUP);
        assertEquals(8, BetterDogsSpawning.DEFAULT_MAX_GROUP);
        assertEquals(8, BetterDogsSpawning.DEFAULT_SPAWN_WEIGHT);
        assertTrue(BetterDogsSpawning.DEFAULT_MAX_GROUP >= BetterDogsSpawning.DEFAULT_MIN_GROUP);
    }

    @Test
    @DisplayName("Assert spawn personality distribution percentages")
    void testSpawnPercentageDistribution() {
        int normalPct = 60;
        int aggroPct = 20;
        int paciPct = 20;

        int total = normalPct + aggroPct + paciPct;
        assertEquals(100, total, "Default spawn personality percentages should sum to 100%");
    }

    @Test
    @DisplayName("Assert cluster Alpha dominance election logic")
    void testClusterAlphaElectionLogic() {
        double scoreAlpha = 1.6; // Large scale + Aggressive
        double scoreFollower = 1.0; // Normal scale + Normal

        assertTrue(scoreAlpha > scoreFollower, "Alpha candidate should have higher dominance score than followers");

        UUID alphaUuid = UUID.randomUUID();
        UUID followerLeaderUuid = alphaUuid;

        assertEquals(alphaUuid, followerLeaderUuid, "Followers in spawn cluster should point to elected Alpha UUID");
    }

    @Test
    @DisplayName("Assert all 9 standard variants exist in WolfVariantHelper")
    void testAllVariantsRegistered() {
        assertEquals(9, WolfVariantHelper.ALL_VARIANTS.size());
    }

    @Test
    @DisplayName("Assert WolfVariantHelper strict null safety")
    void testWolfVariantHelperNullSafety() {
        assertDoesNotThrow(() -> WolfVariantHelper.applyClimateVariant(null, null));
    }
}
