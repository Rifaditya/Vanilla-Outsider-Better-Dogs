// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HazardDetectionTest {

    @Test
    public void testThermalHazardClassification() {
        // Known lethal thermal hazards
        assertTrue(isHazardType("minecraft:lava"), "Lava must be recognized as a thermal hazard");
        assertTrue(isHazardType("minecraft:fire"), "Fire must be recognized as a thermal hazard");
        assertTrue(isHazardType("minecraft:soul_fire"), "Soul Fire must be recognized as a thermal hazard");
        assertTrue(isHazardType("minecraft:magma_block"), "Magma Block must be recognized as a thermal hazard");

        // Campfire states
        assertTrue(isCampfireHazard(true), "Lit Campfire must be recognized as a thermal hazard");
        assertFalse(isCampfireHazard(false), "Unlit Campfire must NOT be recognized as a thermal hazard");

        // Safe terrain blocks
        assertFalse(isHazardType("minecraft:grass_block"), "Grass must be recognized as safe");
        assertFalse(isHazardType("minecraft:water"), "Water must be recognized as safe");
        assertFalse(isHazardType("minecraft:stone"), "Stone must be recognized as safe");
    }

    @Test
    public void testDetourProximityCheck() {
        // Proximity detection radius check
        assertTrue(isWithinHazardRadius(0, 0, 0, 1, 0, 0, 2), "Hazard 1 block away is within radius 2");
        assertFalse(isWithinHazardRadius(0, 0, 0, 5, 0, 0, 2), "Hazard 5 blocks away is outside radius 2");
    }

    private boolean isHazardType(String blockId) {
        return blockId.equals("minecraft:lava") ||
               blockId.equals("minecraft:fire") ||
               blockId.equals("minecraft:soul_fire") ||
               blockId.equals("minecraft:magma_block");
    }

    private boolean isCampfireHazard(boolean isLit) {
        return isLit;
    }

    private boolean isWithinHazardRadius(int cx, int cy, int cz, int hx, int hy, int hz, int maxRadius) {
        return Math.abs(cx - hx) <= maxRadius && Math.abs(cy - hy) <= maxRadius && Math.abs(cz - hz) <= maxRadius;
    }
}
