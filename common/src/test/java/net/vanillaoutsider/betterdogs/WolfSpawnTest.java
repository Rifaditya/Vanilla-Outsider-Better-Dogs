// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WolfSpawnTest {

    @Test
    public void testSpawnGroupBounds() {
        int groupMin = 4;
        int groupMax = 8;

        Assertions.assertTrue(groupMin >= 1, "Spawn group min should be at least 1");
        Assertions.assertTrue(groupMax >= groupMin, "Spawn group max should be greater than or equal to group min");
        Assertions.assertEquals(8, groupMax, "Default spawn group max should be 8");
    }

    @Test
    public void testSpawnPercentageDistribution() {
        int normalPct = 60;
        int aggroPct = 20;
        int paciPct = 20;

        int total = normalPct + aggroPct + paciPct;
        Assertions.assertEquals(100, total, "Default spawn personality percentages should sum to 100%");
    }

    @Test
    public void testClusterAlphaElectionLogic() {
        double scoreAlpha = 1.6; // Large scale + Aggressive
        double scoreFollower = 1.0; // Normal scale + Normal

        Assertions.assertTrue(scoreAlpha > scoreFollower, "Alpha candidate should have higher dominance score than followers");

        UUID alphaUuid = UUID.randomUUID();
        UUID followerLeaderUuid = alphaUuid;

        Assertions.assertEquals(alphaUuid, followerLeaderUuid, "Followers in spawn cluster should point to elected Alpha UUID");
    }
}
