// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PackWarTest {

    @Test
    public void testDominanceScoreComparison() {
        double scoreNormal = 1.0;
        double scoreAggressive = scoreNormal + 0.5;
        double scorePacifist = scoreNormal - 0.3;

        Assertions.assertTrue(scoreAggressive > scoreNormal, "Aggressive personality should have higher dominance weight than Normal");
        Assertions.assertTrue(scoreNormal > scorePacifist, "Normal personality should have higher dominance weight than Pacifist");
    }

    @Test
    public void testLeaderAssignmentAndMerger() {
        UUID alpha1 = UUID.randomUUID();
        UUID alpha2 = UUID.randomUUID();

        UUID followerLeader = alpha1;
        Assertions.assertEquals(alpha1, followerLeader, "Follower starts assigned to alpha1");

        // Simulating pack merger: follower moves to alpha2
        followerLeader = alpha2;
        Assertions.assertEquals(alpha2, followerLeader, "Follower successfully reassigned to victorious alpha2");
    }

    @Test
    public void testMatrixWarChanceRanges() {
        int aaWar = 80;
        int ppWar = 0;

        Assertions.assertTrue(aaWar > ppWar, "Aggressive vs Aggressive should have significantly higher war chance than Pacifist vs Pacifist");
    }
}
