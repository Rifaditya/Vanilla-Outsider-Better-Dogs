// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.util.WolfFlankingHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 16: Tactical Pack Flanking Coordination Tests")
class PackFlankingTest {

    @Test
    @DisplayName("Assert approach time calculation math")
    void testApproachTimeMath() {
        // Standard calculation: 10 blocks at 0.3 speed = 33.333...
        double t1 = WolfFlankingHelper.calculateApproachTimeMath(10.0, 0.3);
        assertEquals(33.333, t1, 0.01, "Approach time should equal distance / speed");

        // Faster wolf: 10 blocks at 0.5 speed = 20.0
        double t2 = WolfFlankingHelper.calculateApproachTimeMath(10.0, 0.5);
        assertEquals(20.0, t2, 0.01, "Faster wolf should have lower approach time");
        assertTrue(t2 < t1, "Faster speed should yield faster arrival");

        // Boundary cases
        assertEquals(0.0, WolfFlankingHelper.calculateApproachTimeMath(0.0, 0.3), 0.001);
        assertEquals(0.0, WolfFlankingHelper.calculateApproachTimeMath(-5.0, 0.3), 0.001);
        assertEquals(0.0, WolfFlankingHelper.calculateApproachTimeMath(10.0, 0.0), 0.001);
    }

    @Test
    @DisplayName("Assert pack split: closest 50% direct charge, slower 50% flank")
    void testSlowerHalfDetermination() {
        // Pack of 1: Solo dog never flanks (charges directly)
        assertFalse(WolfFlankingHelper.isSlowerHalf(0, 1));

        // Pack of 2: Dog 0 charges directly, Dog 1 flanks
        assertFalse(WolfFlankingHelper.isSlowerHalf(0, 2), "Closest dog in pair should charge directly");
        assertTrue(WolfFlankingHelper.isSlowerHalf(1, 2), "Second dog in pair should flank");

        // Pack of 3: Dog 0 charges directly, Dogs 1 and 2 flank (flankCount = 3/2 = 1 -> indices >= 2? wait: 3 - 1 = 2)
        assertFalse(WolfFlankingHelper.isSlowerHalf(0, 3));
        assertFalse(WolfFlankingHelper.isSlowerHalf(1, 3));
        assertTrue(WolfFlankingHelper.isSlowerHalf(2, 3));

        // Pack of 4: Dogs 0,1 charge directly, Dogs 2,3 flank
        assertFalse(WolfFlankingHelper.isSlowerHalf(0, 4));
        assertFalse(WolfFlankingHelper.isSlowerHalf(1, 4));
        assertTrue(WolfFlankingHelper.isSlowerHalf(2, 4));
        assertTrue(WolfFlankingHelper.isSlowerHalf(3, 4));

        // Out of bounds
        assertFalse(WolfFlankingHelper.isSlowerHalf(-1, 4));
        assertFalse(WolfFlankingHelper.isSlowerHalf(4, 4));
    }

    @Test
    @DisplayName("Assert flanking vector geometry and bounding box clearance math")
    void testFlankOffsetMath() {
        Vec3 forward = new Vec3(0.0, 0.0, 1.0); // Facing South
        double standardTargetWidth = 0.6; // Standard zombie/player width

        // Flank radius = max(3.0, 0.6 * 2.5 = 1.5) = 3.0
        // Rear shift = max(1.0, 0.6 * 1.1 = 0.66) = 1.0
        Vec3 rightOffset = WolfFlankingHelper.calculateFlankOffset(forward, true, standardTargetWidth);
        // Right flank: side = (-z, 0, x) = (-1.0, 0, 0) * 3.0 = (-3.0, 0, 0)
        // Shift back by forward (0, 0, 1.0) * 1.0 = (0, 0, -1.0)
        // Offset = (-3.0, 0.0, -1.0)
        assertEquals(-3.0, rightOffset.x, 0.01, "Right flank should project to target's right (-X when facing +Z)");
        assertEquals(0.0, rightOffset.y, 0.01);
        assertEquals(-1.0, rightOffset.z, 0.01, "Should shift rearward relative to target look angle");

        Vec3 leftOffset = WolfFlankingHelper.calculateFlankOffset(forward, false, standardTargetWidth);
        // Left flank: side = (z, 0, -x) = (1.0, 0, 0) * 3.0 = (3.0, 0, 0)
        // Offset = (3.0, 0.0, -1.0)
        assertEquals(3.0, leftOffset.x, 0.01, "Left flank should project to target's left (+X when facing +Z)");
        assertEquals(-1.0, leftOffset.z, 0.01);

        // Large target (e.g. Iron Golem: width 1.4)
        // Flank radius = max(3.0, 1.4 * 2.5 = 3.5) = 3.5
        // Rear shift = max(1.0, 1.4 * 1.1 = 1.54) = 1.54
        Vec3 golemRightOffset = WolfFlankingHelper.calculateFlankOffset(forward, true, 1.4);
        assertEquals(-3.5, golemRightOffset.x, 0.01, "Large mob flank radius should scale to 3.5m");
        assertEquals(-1.54, golemRightOffset.z, 0.01, "Large mob rear shift should scale to 1.54m");
    }

    @Test
    @DisplayName("Assert strict null safety across helper methods")
    void testNullSafety() {
        assertEquals(Double.MAX_VALUE, WolfFlankingHelper.calculateApproachTime(null, null));
        assertFalse(WolfFlankingHelper.isFlanker(null, null));
        assertNull(WolfFlankingHelper.calculateFlankDestination(null, null, false));
        assertNotNull(WolfFlankingHelper.calculateFlankOffset(null, true, 0.6));
    }
}
