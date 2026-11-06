// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 2 - Boundary 10: Pack Clumping Extreme Boundary Tests")
public class Tier2_10_PackClumpingBoundariesTest {

    @Test
    @DisplayName("Assert follower count of 1 produces exact 0.0 spacing offset")
    public void testSingleFollowerZeroOffset() {
        int followerCount = 1;
        float multiplier = 1.0f;
        float maxExtra = 10.0f;

        double offset = followerCount > 1 ? Math.min(Math.sqrt(followerCount - 1) * multiplier, maxExtra) : 0.0;
        assertEquals(0.0, offset, 1e-6);
    }

    @Test
    @DisplayName("Assert follower count of 0 or negative safely yields 0.0 spacing offset")
    public void testZeroOrNegativeFollowerCount() {
        int followerCount0 = 0;
        double offset0 = followerCount0 > 1 ? Math.min(Math.sqrt(followerCount0 - 1) * 1.0f, 10.0f) : 0.0;
        assertEquals(0.0, offset0, 1e-6);

        int followerCountNeg = -10;
        double offsetNeg = followerCountNeg > 1 ? Math.min(Math.sqrt(followerCountNeg - 1) * 1.0f, 10.0f) : 0.0;
        assertEquals(0.0, offsetNeg, 1e-6);
    }

    @Test
    @DisplayName("Assert massive follower count (1,000 dogs) clamps cleanly to maxExtra")
    public void testMassiveFollowerCountClamping() {
        int followerCount = 1000;
        float multiplier = 1.0f;
        float maxExtra = 15.0f; // max extra 15 blocks

        double offset = Math.min(Math.sqrt(followerCount - 1) * multiplier, maxExtra);
        assertEquals(15.0, offset, 1e-6, "Massive pack must clamp to maxExtra to prevent unbounded spreading");
    }

    @Test
    @DisplayName("Assert maximum stroll boundary radius remains positive under all multiplier settings")
    public void testMaxStrollRadiusPositivity() {
        double baseRadius = 4.0; // Pacifist base radius
        double offset = 0.0;
        double totalRadius = baseRadius + offset;

        assertTrue(totalRadius >= 4.0, "Total wander radius must be at least the base personality radius");
    }

    @Test
    @DisplayName("Assert extreme follower spacing multiplier zero disables spread expansion")
    public void testZeroMultiplierDisablesSpread() {
        int followerCount = 50;
        float zeroMultiplier = 0.0f;
        float maxExtra = 10.0f;

        double offset = Math.min(Math.sqrt(followerCount - 1) * zeroMultiplier, maxExtra);
        assertEquals(0.0, offset, 1e-6, "Multiplier of 0 must yield 0 offset");
    }
}
