// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.play.PlayChoreographyHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 2: Pure Play Choreography Helper and Hazard Evaluation Tests")
class PlayChoreographyHelperTest {

    @Test
    @DisplayName("Assert strict null safety across all helper methods")
    void testStrictNullSafety() {
        assertDoesNotThrow(() -> assertFalse(PlayChoreographyHelper.canInitiatePlay(null)));
        assertDoesNotThrow(() -> assertNull(PlayChoreographyHelper.findPlayPartner(null, 10.0)));
        assertDoesNotThrow(() -> assertFalse(PlayChoreographyHelper.isValidPlayPartner(null, null)));
        assertDoesNotThrow(() -> assertFalse(PlayChoreographyHelper.isSafePath(null, null)));
        assertDoesNotThrow(() -> assertEquals(Vec3.ZERO, PlayChoreographyHelper.calculateOrbitVector((Vec3) null, (Vec3) null, 0.0, 3.0)));
        assertDoesNotThrow(() -> assertEquals(Vec3.ZERO, PlayChoreographyHelper.calculateOrbitVector((net.minecraft.world.entity.animal.wolf.Wolf) null, null, 0.0, 3.0)));

        // Coordinate and parameter sanitizers null / NaN safety
        assertEquals(0.0, PlayChoreographyHelper.sanitizeCoordinate(Double.NaN));
        assertEquals(0.0, PlayChoreographyHelper.sanitizeCoordinate(Double.POSITIVE_INFINITY));
        assertEquals(0.0, PlayChoreographyHelper.sanitizeCoordinate(Double.NEGATIVE_INFINITY));

        assertEquals(PlayChoreographyHelper.DEFAULT_ORBIT_RADIUS, PlayChoreographyHelper.sanitizeRadius(Double.NaN));
        assertEquals(PlayChoreographyHelper.DEFAULT_ORBIT_RADIUS, PlayChoreographyHelper.sanitizeRadius(Double.POSITIVE_INFINITY));

        assertEquals(0.0, PlayChoreographyHelper.sanitizeAngle(Double.NaN));
        assertEquals(0.0, PlayChoreographyHelper.sanitizeAngle(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("Assert circular orbit vector trigonometry in cardinal 90-degree steps")
    void testOrbitVectorTrigonometry() {
        Vec3 center = new Vec3(10.0, 64.0, 10.0);
        double radius = 5.0;

        // Start at angle 0: (15.0, 64.0, 10.0)
        Vec3 startPos = new Vec3(15.0, 64.0, 10.0);

        // Step 1: +90 degrees (PI / 2) -> (10.0, 64.0, 15.0)
        Vec3 step1 = PlayChoreographyHelper.calculateOrbitVector(center, startPos, Math.PI / 2.0, radius);
        assertEquals(10.0, step1.x, 1e-5, "Step 1 X should be 10.0");
        assertEquals(64.0, step1.y, 1e-5, "Step 1 Y should remain 64.0");
        assertEquals(15.0, step1.z, 1e-5, "Step 1 Z should be 15.0");

        // Step 2: +90 degrees again from step1 -> (5.0, 64.0, 10.0)
        Vec3 step2 = PlayChoreographyHelper.calculateOrbitVector(center, step1, Math.PI / 2.0, radius);
        assertEquals(5.0, step2.x, 1e-5, "Step 2 X should be 5.0");
        assertEquals(64.0, step2.y, 1e-5, "Step 2 Y should remain 64.0");
        assertEquals(10.0, step2.z, 1e-5, "Step 2 Z should be 10.0");

        // Step 3: +90 degrees again from step2 -> (10.0, 64.0, 5.0)
        Vec3 step3 = PlayChoreographyHelper.calculateOrbitVector(center, step2, Math.PI / 2.0, radius);
        assertEquals(10.0, step3.x, 1e-5, "Step 3 X should be 10.0");
        assertEquals(64.0, step3.y, 1e-5, "Step 3 Y should remain 64.0");
        assertEquals(5.0, step3.z, 1e-5, "Step 3 Z should be 5.0");

        // Step 4: +90 degrees completes circle back to start -> (15.0, 64.0, 10.0)
        Vec3 step4 = PlayChoreographyHelper.calculateOrbitVector(center, step3, Math.PI / 2.0, radius);
        assertEquals(15.0, step4.x, 1e-5, "Step 4 X should return to 15.0");
        assertEquals(64.0, step4.y, 1e-5, "Step 4 Y should remain 64.0");
        assertEquals(10.0, step4.z, 1e-5, "Step 4 Z should return to 10.0");
    }

    @Test
    @DisplayName("Assert orbit vector clockwise steps and center origin handling")
    void testOrbitVectorClockwiseAndCenterOrigin() {
        Vec3 center = new Vec3(0.0, 70.0, 0.0);
        double radius = 4.0;
        Vec3 currentPos = new Vec3(4.0, 70.0, 0.0);

        // Clockwise step (-PI / 2) -> (0.0, 70.0, -4.0)
        Vec3 cwStep = PlayChoreographyHelper.calculateOrbitVector(center, currentPos, -Math.PI / 2.0, radius);
        assertEquals(0.0, cwStep.x, 1e-5);
        assertEquals(70.0, cwStep.y, 1e-5);
        assertEquals(-4.0, cwStep.z, 1e-5);

        // If current position is directly on center (distance 0), defaults to angle 0 + step
        Vec3 atCenter = new Vec3(0.0, 70.0, 0.0);
        Vec3 fromCenter = PlayChoreographyHelper.calculateOrbitVector(center, atCenter, 0.0, radius);
        assertEquals(4.0, fromCenter.x, 1e-5, "From center with 0 step should place along X at radius");
        assertEquals(70.0, fromCenter.y, 1e-5);
        assertEquals(0.0, fromCenter.z, 1e-5);
    }

    @Test
    @DisplayName("Assert saturated bounds on radius, angles, and world coordinates")
    void testSaturatedBounds() {
        Vec3 center = new Vec3(0.0, 64.0, 0.0);
        Vec3 pos = new Vec3(1.0, 64.0, 0.0);

        // Negative radius converts to positive
        assertEquals(5.0, PlayChoreographyHelper.sanitizeRadius(-5.0));

        // Excess radius clamps to MAX_ORBIT_RADIUS
        assertEquals(PlayChoreographyHelper.MAX_ORBIT_RADIUS, PlayChoreographyHelper.sanitizeRadius(5000.0));

        // World coordinates beyond +/- 3.0E7 clamp safely
        assertEquals(3.0E7, PlayChoreographyHelper.sanitizeCoordinate(5.0E8));
        assertEquals(-3.0E7, PlayChoreographyHelper.sanitizeCoordinate(-5.0E8));

        // Orbit calculation with extreme coordinates does not produce NaN or Infinity
        Vec3 extremeCenter = new Vec3(4.0E8, 64.0, 4.0E8);
        Vec3 extremePos = new Vec3(4.0E8 + 10.0, 64.0, 4.0E8);
        Vec3 result = PlayChoreographyHelper.calculateOrbitVector(extremeCenter, extremePos, Math.PI / 4.0, 10.0);
        assertFalse(Double.isNaN(result.x));
        assertFalse(Double.isNaN(result.y));
        assertFalse(Double.isNaN(result.z));
        assertTrue(result.x <= PlayChoreographyHelper.MAX_COORDINATE_BOUND);
        assertTrue(result.z <= PlayChoreographyHelper.MAX_COORDINATE_BOUND);
    }

    @Test
    @DisplayName("Assert cliff drop evaluation and hazard boundaries")
    void testCliffDropEvaluation() {
        // Safe drops (<= 3 blocks)
        assertTrue(PlayChoreographyHelper.isSafeDrop(0), "0 block drop (flat) is safe");
        assertTrue(PlayChoreographyHelper.isSafeDrop(1), "1 block drop is safe");
        assertTrue(PlayChoreographyHelper.isSafeDrop(2), "2 block drop is safe");
        assertTrue(PlayChoreographyHelper.isSafeDrop(3), "3 block drop is safe");

        // Cliff hazards (> 3 blocks)
        assertFalse(PlayChoreographyHelper.isSafeDrop(4), "4 block drop is a cliff hazard");
        assertFalse(PlayChoreographyHelper.isSafeDrop(5), "5 block drop is a cliff hazard");
        assertFalse(PlayChoreographyHelper.isSafeDrop(10), "10 block drop is a cliff hazard");
        assertFalse(PlayChoreographyHelper.isSafeDrop(50), "50 block ravine drop is a lethal hazard");
    }

    @Test
    @DisplayName("Assert canInitiatePlay condition matrix and boundary conditions")
    void testCanInitiatePlayMatrix() {
        // Full positive case
        assertTrue(PlayChoreographyHelper.canInitiatePlay(
                true, true, false, false, false,
                20.0f, 20.0f, false, false, true,
                true, true, false, 25.0, 16.0
        ), "Fully healthy, tamed, following dog within owner range must be eligible for play");

        // Health thresholds (70% requirement)
        assertTrue(PlayChoreographyHelper.canInitiatePlay(
                true, true, false, false, false,
                14.0f, 20.0f, false, false, true,
                true, true, false, 25.0, 16.0
        ), "70% health (14/20) must be eligible for play");

        assertFalse(PlayChoreographyHelper.canInitiatePlay(
                true, true, false, false, false,
                13.9f, 20.0f, false, false, true,
                true, true, false, 25.0, 16.0
        ), "<70% health (13.9/20) must be rejected from play");

        // Disqualifications
        assertFalse(PlayChoreographyHelper.canInitiatePlay(false, true, false, false, false, 20.0f, 20.0f, false, false, true, true, true, false, 25.0, 16.0), "Dead wolf must be rejected");
        assertFalse(PlayChoreographyHelper.canInitiatePlay(true, false, false, false, false, 20.0f, 20.0f, false, false, true, true, true, false, 25.0, 16.0), "Untamed wolf must be rejected");
        assertFalse(PlayChoreographyHelper.canInitiatePlay(true, true, true, false, false, 20.0f, 20.0f, false, false, true, true, true, false, 25.0, 16.0), "Sitting wolf must be rejected");
        assertFalse(PlayChoreographyHelper.canInitiatePlay(true, true, false, true, false, 20.0f, 20.0f, false, false, true, true, true, false, 25.0, 16.0), "Wolf in sitting pose must be rejected");
        assertFalse(PlayChoreographyHelper.canInitiatePlay(true, true, false, false, true, 20.0f, 20.0f, false, false, true, true, true, false, 25.0, 16.0), "Wolf in combat must be rejected");
        assertFalse(PlayChoreographyHelper.canInitiatePlay(true, true, false, false, false, 20.0f, 20.0f, true, false, true, true, true, false, 25.0, 16.0), "Guard mode sentry must be rejected");
        assertFalse(PlayChoreographyHelper.canInitiatePlay(true, true, false, false, false, 20.0f, 20.0f, false, true, true, true, true, false, 25.0, 16.0), "Social mode active must be rejected");
        assertFalse(PlayChoreographyHelper.canInitiatePlay(true, true, false, false, false, 20.0f, 20.0f, false, false, false, true, true, false, 25.0, 16.0), "Disabled gamerule must be rejected");
        assertFalse(PlayChoreographyHelper.canInitiatePlay(true, true, false, false, false, 20.0f, 20.0f, false, false, true, false, true, false, 25.0, 16.0), "Missing owner must be rejected");
        assertFalse(PlayChoreographyHelper.canInitiatePlay(true, true, false, false, false, 20.0f, 20.0f, false, false, true, true, false, false, 25.0, 16.0), "Dead owner must be rejected");
        assertFalse(PlayChoreographyHelper.canInitiatePlay(true, true, false, false, false, 20.0f, 20.0f, false, false, true, true, true, true, 25.0, 16.0), "Spectator owner must be rejected");

        // Owner distance limit (Math.min(1024.0, maxPlayDistance * maxPlayDistance * 4.0))
        // With maxPlayDistance = 16: maxAllowedDistSqr = min(1024.0, 16 * 16 * 4) = min(1024.0, 1024.0) = 1024.0
        assertTrue(PlayChoreographyHelper.canInitiatePlay(
                true, true, false, false, false,
                20.0f, 20.0f, false, false, true,
                true, true, false, 1024.0, 16.0
        ), "Boundary distance squared 1024.0 must be allowed");

        assertFalse(PlayChoreographyHelper.canInitiatePlay(
                true, true, false, false, false,
                20.0f, 20.0f, false, false, true,
                true, true, false, 1024.01, 16.0
        ), "Distance squared 1024.01 must exceed max play range");
    }

    @Test
    @DisplayName("Assert isValidPlayPartner validation matrix")
    void testIsValidPlayPartnerMatrix() {
        // Positive eligible partner
        assertTrue(PlayChoreographyHelper.isValidPlayPartner(
                false, true, true, true, false, false, false, false
        ), "Alive, tamed packmate not sitting or in combat must be valid partner");

        // Disqualifications
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(true, true, true, true, false, false, false, false), "Self cannot be play partner");
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(false, false, true, true, false, false, false, false), "Dead wolf cannot be play partner");
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(false, true, false, true, false, false, false, false), "Untamed wolf cannot be play partner");
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(false, true, true, false, false, false, false, false), "Wolf with different owner cannot be play partner");
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(false, true, true, true, true, false, false, false), "Sitting wolf cannot be play partner");
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(false, true, true, true, false, true, false, false), "Wolf in sitting pose cannot be play partner");
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(false, true, true, true, false, false, true, false), "Wolf in combat cannot be play partner");
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(false, true, true, true, false, false, false, true), "Guard mode sentry cannot be play partner");
    }
}
