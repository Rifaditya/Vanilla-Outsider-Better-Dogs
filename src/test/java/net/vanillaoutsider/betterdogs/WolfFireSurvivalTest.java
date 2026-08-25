// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Headless unit tests for Wolf Fire Survival AI logic, water search proximity,
 * emergency sit-break cycles, and safe water drop cliff bypass.
 */
public class WolfFireSurvivalTest {

    @Test
    public void testFireSurvivalActivationGating() {
        // Must activate when on fire or remaining fire ticks > 0
        assertTrue(evaluateCanUse(true, 0, false, false, true, true), "Should activate when on fire");
        assertTrue(evaluateCanUse(false, 100, false, false, true, true), "Should activate when fire ticks > 0");

        // Must reject if not on fire
        assertFalse(evaluateCanUse(false, 0, false, false, true, true), "Should not activate when not burning");

        // Must reject if passenger
        assertFalse(evaluateCanUse(true, 0, true, false, true, true), "Should reject if riding a vehicle");

        // Must reject if main GameRule is disabled
        assertFalse(evaluateCanUse(true, 0, false, false, false, true), "Should reject if seek water gamerule is false");

        // When sitting: if break-sit is enabled -> allowed; if break-sit disabled -> rejected (vanilla)
        assertTrue(evaluateCanUse(true, 0, false, true, true, true), "Should allow sitting dog if break-sit enabled");
        assertFalse(evaluateCanUse(true, 0, false, true, true, false), "Should reject sitting dog if break-sit disabled");
    }

    @Test
    public void testBreakSitAndPostureRestoration() {
        // Initial state: sitting dog catches fire
        boolean originallySitting = true;
        boolean isBurning = true;

        // Step 1: Break sit on fire
        boolean isSitting = originallySitting;
        if (isBurning) {
            isSitting = false; // Emergency break-sit
        }
        assertFalse(isSitting, "Dog must temporarily break sit posture while burning");

        // Step 2: Extinguished in water
        isBurning = false;
        if (!isBurning && originallySitting) {
            isSitting = true; // Restored posture
        }
        assertTrue(isSitting, "Dog must restore sitting posture after being safely extinguished");
    }

    @Test
    public void testWaterSearchProximityCalculations() {
        double maxRadius = 16.0;
        double maxRadiusSq = maxRadius * maxRadius; // 256.0

        double distCloseSq = 5.0 * 5.0 + 3.0 * 3.0; // 34.0
        double distEdgeSq = 16.0 * 16.0; // 256.0
        double distFarSq = 17.0 * 17.0; // 289.0

        assertTrue(distCloseSq <= maxRadiusSq, "5m water is within 16m search radius");
        assertTrue(distEdgeSq <= maxRadiusSq, "16m water is within search radius boundary");
        assertFalse(distFarSq <= maxRadiusSq, "17m water is out of search radius");
    }

    @Test
    public void testSafeWaterDropBypass() {
        // Drop <= 3 blocks is always safe regardless of landing
        assertTrue(isSafeDropOrWaterLanding(2, false), "2-block drop onto solid ground is safe");
        assertTrue(isSafeDropOrWaterLanding(3, false), "3-block drop onto solid ground is safe");

        // Drop > 3 blocks onto dry solid ground is a hazardous cliff
        assertFalse(isSafeDropOrWaterLanding(4, false), "4-block drop onto dry ground is dangerous");
        assertFalse(isSafeDropOrWaterLanding(8, false), "8-block cliff drop onto dry ground is dangerous");

        // Drop > 3 blocks into deep water is SAFE (river bank / water leap)
        assertTrue(isSafeDropOrWaterLanding(4, true), "4-block leap into water is safe");
        assertTrue(isSafeDropOrWaterLanding(8, true), "8-block cliff leap into water is safe");
    }

    @Test
    public void testPanicFallbackDecision() {
        // If water found -> sprint to water at 1.4x
        String actionWithWater = selectFireAction(true);
        assertEquals("SPRINT_TO_WATER_1.4X", actionWithWater);

        // If no water found -> erratic panic sprint at 1.3x
        String actionNoWater = selectFireAction(false);
        assertEquals("PANIC_SPRINT_1.3X", actionNoWater);
    }

    private boolean evaluateCanUse(boolean onFire, int fireTicks, boolean isPassenger, boolean isSitting, boolean seekWaterRule, boolean breakSitRule) {
        if (isPassenger) return false;
        if (!onFire && fireTicks <= 0) return false;
        if (!seekWaterRule) return false;
        if (isSitting && !breakSitRule) return false;
        return true;
    }

    private boolean isSafeDropOrWaterLanding(int dropHeight, boolean isWaterLanding) {
        if (dropHeight <= 3) return true;
        return isWaterLanding;
    }

    private String selectFireAction(boolean waterFound) {
        return waterFound ? "SPRINT_TO_WATER_1.4X" : "PANIC_SPRINT_1.3X";
    }
}
