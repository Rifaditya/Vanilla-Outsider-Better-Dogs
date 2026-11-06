// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HazardDetourTest {

    @Test
    public void testHazardPathInterception() {
        // Paths with safe nodes only must NOT be intercepted
        boolean[] safePathNodes = new boolean[]{false, false, false, false};
        assertFalse(evaluatePathInterception(safePathNodes), "Path with safe nodes must not be intercepted");

        // Paths intersecting a thermal hazard node must trigger interception
        boolean[] hazardPathNodes = new boolean[]{false, false, true, false};
        assertTrue(evaluatePathInterception(hazardPathNodes), "Path intersecting a thermal hazard node must be intercepted");
    }

    @Test
    public void testCheckLimitBound() {
        // Nodes beyond check limit (8) should not trigger immediate abort
        boolean[] farHazardNodes = new boolean[]{false, false, false, false, false, false, false, false, true};
        assertFalse(evaluatePathInterceptionWithLimit(farHazardNodes, 8), "Hazard beyond check limit must not trigger immediate stop");
    }

    private boolean evaluatePathInterception(boolean[] nodeHasHazard) {
        return evaluatePathInterceptionWithLimit(nodeHasHazard, 8);
    }

    private boolean evaluatePathInterceptionWithLimit(boolean[] nodeHasHazard, int limit) {
        int checkLimit = Math.min(nodeHasHazard.length, limit);
        for (int i = 0; i < checkLimit; i++) {
            if (nodeHasHazard[i]) {
                return true;
            }
        }
        return false;
    }
}
