// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier2;

import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.AvoidGhastFireballGoal;
import net.vanillaoutsider.betterdogs.ai.FireballTrajectoryHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 2 - Boundary 05: AI Reflex Boundary Conditions Tests")
public class Tier2_05_AIReflexBoundariesTest {

    @Test
    @DisplayName("Assert sitting scramble boundary condition at exact 1.500000m threshold")
    public void testExactScrambleDistanceBoundary() {
        // At exact 1.5m and tau = 10
        assertTrue(AvoidGhastFireballGoal.shouldScrambleFromSitting(1.500000, 10.0),
                "Exact 1.500000m must trigger scramble");

        // 1.500001m must NOT trigger
        assertFalse(AvoidGhastFireballGoal.shouldScrambleFromSitting(1.500001, 10.0),
                "1.500001m exceeds boundary and must not scramble");
    }

    @Test
    @DisplayName("Assert sitting scramble boundary condition at exact 20.000000 ticks threshold")
    public void testExactScrambleTauBoundary() {
        // At perp = 1.0m and exact tau = 20.0
        assertTrue(AvoidGhastFireballGoal.shouldScrambleFromSitting(1.0, 20.000000),
                "Exact 20.000000 ticks must trigger scramble");

        // 20.000001 ticks must NOT trigger
        assertFalse(AvoidGhastFireballGoal.shouldScrambleFromSitting(1.0, 20.000001),
                "20.000001 ticks exceeds boundary and must not scramble");
    }

    @Test
    @DisplayName("Assert standing dog reflex boundary condition at exact 1.750000m threat radius")
    public void testExactThreatRadiusBoundary() {
        double reaction = 20.0;
        assertTrue(AvoidGhastFireballGoal.canTriggerReflex(false, 1.750000, 10.0, reaction),
                "Exact 1.750000m threat radius must trigger reflex");

        assertFalse(AvoidGhastFireballGoal.canTriggerReflex(false, 1.750001, 10.0, reaction),
                "1.750001m exceeds threat radius and must not trigger reflex");
    }

    @Test
    @DisplayName("Assert zero-tau and negative-tau projectiles never trigger scramble or reflex")
    public void testPastProjectileNonTrigger() {
        assertFalse(AvoidGhastFireballGoal.shouldScrambleFromSitting(0.5, 0.0),
                "tau = 0.0 (impacted / simultaneous) must not scramble");
        assertFalse(AvoidGhastFireballGoal.shouldScrambleFromSitting(0.5, -0.001),
                "tau < 0 (passed projectile) must not scramble");
        assertFalse(AvoidGhastFireballGoal.canTriggerReflex(false, 0.5, 0.0, 20.0));
        assertFalse(AvoidGhastFireballGoal.canTriggerReflex(false, 0.5, -5.0, 20.0));
    }

    @Test
    @DisplayName("Assert stationary projectile (zero velocity) returns zero time-to-impact safely")
    public void testZeroVelocityProjectileSafety() {
        Vec3 dogPos = new Vec3(0, 64, 0);
        Vec3 projPos = new Vec3(5, 64, 5);
        Vec3 zeroVel = Vec3.ZERO;

        FireballTrajectoryHelper.TrajectoryInterception interception =
                FireballTrajectoryHelper.computeInterception(dogPos, projPos, zeroVel, 20.0, 1.75, 85.0);

        assertFalse(interception.intercepted(), "Stationary projectile must not trigger interception");
        assertTrue(interception.timeToImpactTicks() <= 0.0, "Stationary projectile must return non-positive tau");
    }
}
