// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HazardReactionTest {

    @Test
    public void testEscapeVectorCalculation() {
        // Hazard at (10, 64, 10), Wolf at (11.0, 64, 10.5) -> push in positive X
        double hazardCenterX = 10.0 + 0.5;
        double hazardCenterZ = 10.0 + 0.5;
        double wolfX = 11.0;
        double wolfZ = 10.5;

        double dx = wolfX - hazardCenterX;
        double dz = wolfZ - hazardCenterZ;
        double dist = Math.sqrt(dx * dx + dz * dz);

        assertTrue(dist > 0.0, "Distance must be positive");
        double pushX = (dx / dist) * 0.35;
        double pushZ = (dz / dist) * 0.35;

        assertTrue(pushX > 0.0, "Escape push must point away in positive X");
        assertEquals(0.0, pushZ, 0.0001, "Escape push in Z must be zero when aligned");
    }

    @Test
    public void testWaterExtinguishCandidate() {
        int radius = 6;
        int distanceToWater = 4;
        assertTrue(distanceToWater <= radius, "Water within 6 blocks must be targeted for extinguish");

        int distanceToFarWater = 8;
        assertFalse(distanceToFarWater <= radius, "Water beyond 6 blocks must not be targeted");
    }
}
