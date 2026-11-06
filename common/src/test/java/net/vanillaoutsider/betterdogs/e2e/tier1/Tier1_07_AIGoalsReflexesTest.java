// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier1;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.AvoidGhastFireballGoal;
import net.vanillaoutsider.betterdogs.ai.FireballTrajectoryHelper;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 1 - Feature 07: AI Goals & Reflexes Tests")
public class Tier1_07_AIGoalsReflexesTest {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BetterDogsGameRules.register();
    }

    @Test
    @DisplayName("Assert sitting dog emergency scramble reflex triggers strictly at <= 1.5m and <= 20 ticks")
    public void testEmergencyScrambleSittingDog() {
        assertEquals(1.5, AvoidGhastFireballGoal.EMERGENCY_SCRAMBLE_DISTANCE, 1e-6);

        // Within bounds
        assertTrue(AvoidGhastFireballGoal.shouldScrambleFromSitting(0.0, 10.0));
        assertTrue(AvoidGhastFireballGoal.shouldScrambleFromSitting(1.5, 20.0));

        // Outside bounds: distance > 1.5m
        assertFalse(AvoidGhastFireballGoal.shouldScrambleFromSitting(1.51, 10.0));

        // Outside bounds: tau > 20 ticks
        assertFalse(AvoidGhastFireballGoal.shouldScrambleFromSitting(1.0, 20.1));

        // Outside bounds: tau <= 0
        assertFalse(AvoidGhastFireballGoal.shouldScrambleFromSitting(1.0, 0.0));
        assertFalse(AvoidGhastFireballGoal.shouldScrambleFromSitting(1.0, -2.0));
    }

    @Test
    @DisplayName("Assert standing dog reflex triggers within threat radius (1.75m)")
    public void testStandingDogReflexTrigger() {
        double reactionTicks = 20.0;

        // Inside threat radius
        assertTrue(AvoidGhastFireballGoal.canTriggerReflex(false, 0.5, 12.0, reactionTicks));
        assertTrue(AvoidGhastFireballGoal.canTriggerReflex(false, 1.75, 20.0, reactionTicks));

        // Outside threat radius (> 1.75m)
        assertFalse(AvoidGhastFireballGoal.canTriggerReflex(false, 1.76, 12.0, reactionTicks));
        assertFalse(AvoidGhastFireballGoal.canTriggerReflex(false, 2.0, 5.0, reactionTicks));
    }

    @Test
    @DisplayName("Assert FireballTrajectoryHelper calculates exact impact parameters")
    public void testTrajectorySolverDirectCourse() {
        Vec3 dogPos = new Vec3(0, 64, 0);
        Vec3 projPos = new Vec3(0, 64, 30);
        Vec3 projVel = new Vec3(0, 0, -1.0); // 1.0 block per tick towards dog

        FireballTrajectoryHelper.TrajectoryInterception interception =
                FireballTrajectoryHelper.computeInterception(dogPos, projPos, projVel, 40.0, 1.75, 85.0);

        assertTrue(interception.intercepted(), "Projectile moving towards dog must be intercepted");
        assertEquals(30.0, interception.timeToImpactTicks(), 1e-4);
        assertEquals(0.0, interception.perpendicularDistance(), 1e-4);
    }

    @Test
    @DisplayName("Assert dodge impulse vector contains lateral movement and vertical hop")
    public void testDodgeImpulseVector() {
        Vec3 dogPos = new Vec3(0, 64, 0);
        Vec3 projPos = new Vec3(0, 64, 15);
        Vec3 projVel = new Vec3(0, 0, -1.0);

        FireballTrajectoryHelper.TrajectoryInterception interception =
                FireballTrajectoryHelper.computeInterception(dogPos, projPos, projVel, 25.0, 1.75, 85.0);

        Vec3 impulse = interception.dodgeImpulse();
        assertNotNull(impulse);
        assertTrue(Math.abs(impulse.x) > 0.0 || Math.abs(impulse.z) > 0.0, "Impulse must move laterally");
        assertEquals(FireballTrajectoryHelper.VERTICAL_HOP_IMPULSE, impulse.y, 1e-4, "Impulse must provide vertical hop");
    }

    @Test
    @DisplayName("Assert projectile moving away from dog produces non-intercepted state")
    public void testDivergentProjectileNotIntercepted() {
        Vec3 dogPos = new Vec3(0, 64, 0);
        Vec3 projPos = new Vec3(0, 64, 15);
        Vec3 projVel = new Vec3(0, 0, 1.0); // Moving away (+Z)

        FireballTrajectoryHelper.TrajectoryInterception interception =
                FireballTrajectoryHelper.computeInterception(dogPos, projPos, projVel, 25.0, 1.75, 85.0);

        assertFalse(interception.intercepted(), "Divergent projectile must NOT trigger evasion");
    }
}
