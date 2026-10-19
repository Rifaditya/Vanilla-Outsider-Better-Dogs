// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.vanillaoutsider.betterdogs.ai.play.OrbitSparringGoal;
import net.vanillaoutsider.betterdogs.ai.play.PlayChaseGoal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 3: PlayChaseGoal and OrbitSparringGoal AI Unit Tests")
class PlayGoalsTest {

    @Test
    @DisplayName("Assert Goal control flags contain MOVE and LOOK for both goals")
    void testGoalControlFlags() {
        PlayChaseGoal chaseGoal = new PlayChaseGoal(null);
        EnumSet<Goal.Flag> chaseFlags = chaseGoal.getFlags();
        assertTrue(chaseFlags.contains(Goal.Flag.MOVE), "PlayChaseGoal must set Goal.Flag.MOVE");
        assertTrue(chaseFlags.contains(Goal.Flag.LOOK), "PlayChaseGoal must set Goal.Flag.LOOK");
        assertEquals(2, chaseFlags.size(), "PlayChaseGoal should only have MOVE and LOOK flags");

        OrbitSparringGoal orbitGoal = new OrbitSparringGoal(null);
        EnumSet<Goal.Flag> orbitFlags = orbitGoal.getFlags();
        assertTrue(orbitFlags.contains(Goal.Flag.MOVE), "OrbitSparringGoal must set Goal.Flag.MOVE");
        assertTrue(orbitFlags.contains(Goal.Flag.LOOK), "OrbitSparringGoal must set Goal.Flag.LOOK");
        assertEquals(2, orbitFlags.size(), "OrbitSparringGoal should only have MOVE and LOOK flags");
    }

    @Test
    @DisplayName("Assert strict null-safety on canUse, canContinueToUse, and stop without exceptions")
    void testNullSafety() {
        PlayChaseGoal chaseGoal = new PlayChaseGoal(null);
        assertDoesNotThrow(() -> assertFalse(chaseGoal.canUse(), "canUse with null wolf must return false"));
        assertDoesNotThrow(() -> assertFalse(chaseGoal.canContinueToUse(), "canContinueToUse with null partner must return false"));
        assertDoesNotThrow(chaseGoal::stop, "stop() with null wolf must not throw");

        OrbitSparringGoal orbitGoal = new OrbitSparringGoal(null);
        assertDoesNotThrow(() -> assertFalse(orbitGoal.canUse(), "canUse with null wolf must return false"));
        assertDoesNotThrow(() -> assertFalse(orbitGoal.canContinueToUse(), "canContinueToUse with null partner must return false"));
        assertDoesNotThrow(orbitGoal::stop, "stop() with null wolf must not throw");
    }

    @Test
    @DisplayName("Assert PlayChaseGoal baseline constants and parameter configurations")
    void testChaseConstants() {
        assertEquals(16.0, PlayChaseGoal.DEFAULT_PARTNER_RADIUS, "Default partner search radius should be 16.0 blocks");
        assertEquals(1.25, PlayChaseGoal.DEFAULT_SPEED_MODIFIER, "Chase sprint speed modifier should be 1.25x");
        assertEquals(240, PlayChaseGoal.MAX_CHASE_TICKS, "Max chase duration should be 240 ticks");
        assertEquals(256.0, PlayChaseGoal.DEFAULT_MAX_PLAY_DISTANCE_SQR, "Default max play distance squared should be 256.0 (16^2)");
    }

    @Test
    @DisplayName("Assert OrbitSparringGoal baseline constants and parameter configurations")
    void testOrbitConstants() {
        assertEquals(16.0, OrbitSparringGoal.DEFAULT_PARTNER_RADIUS, "Default partner search radius should be 16.0 blocks");
        assertEquals(1.15, OrbitSparringGoal.DEFAULT_SPEED_MODIFIER, "Sparring orbit speed modifier should be 1.15x");
        assertEquals(3.0, OrbitSparringGoal.DEFAULT_ORBIT_RADIUS, "Default circular orbit radius should be 3.0 blocks");
        assertEquals(0.25, OrbitSparringGoal.DEFAULT_ANGULAR_STEP, "Default circular angular step should be 0.25 radians");
        assertEquals(200, OrbitSparringGoal.MAX_ORBIT_TICKS, "Max orbit duration should be 200 ticks");
        assertEquals(256.0, OrbitSparringGoal.DEFAULT_MAX_PLAY_DISTANCE_SQR, "Default max play distance squared should be 256.0");
    }

    @Test
    @DisplayName("Assert PlayChaseGoal abort condition matrix across health, combat, sitting, and distance")
    void testChaseAbortMatrix() {
        // Base normal state: partner alive, neither sitting, no targets, full health (20/20), distance 9.0 (3 blocks), max 256.0
        assertFalse(PlayChaseGoal.shouldAbortChase(
                false, true, false, false, false, false,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Normal playing conditions must not abort chase");

        // 1. Partner null or dead
        assertTrue(PlayChaseGoal.shouldAbortChase(
                true, true, false, false, false, false,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Null partner must trigger abort");
        assertTrue(PlayChaseGoal.shouldAbortChase(
                false, false, false, false, false, false,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Dead partner must trigger abort");

        // 2. Sitting state
        assertTrue(PlayChaseGoal.shouldAbortChase(
                false, true, true, false, false, false,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Sitting partner must trigger abort");
        assertTrue(PlayChaseGoal.shouldAbortChase(
                false, true, false, true, false, false,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Sitting chaser wolf must trigger abort");

        // 3. Combat targeting
        assertTrue(PlayChaseGoal.shouldAbortChase(
                false, true, false, false, true, false,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Active target on chaser must trigger combat abort");
        assertTrue(PlayChaseGoal.shouldAbortChase(
                false, true, false, false, false, true,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Active target on partner must trigger combat abort");

        // 4. Low health (< 70%)
        assertTrue(PlayChaseGoal.shouldAbortChase(
                false, true, false, false, false, false,
                13.9f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Wolf health < 70% (13.9 / 20.0) must trigger abort");
        assertTrue(PlayChaseGoal.shouldAbortChase(
                false, true, false, false, false, false,
                20.0f, 20.0f, 13.9f, 20.0f, 9.0, 256.0
        ), "Partner health < 70% must trigger abort");
        assertFalse(PlayChaseGoal.shouldAbortChase(
                false, true, false, false, false, false,
                14.0f, 20.0f, 14.0f, 20.0f, 9.0, 256.0
        ), "Health exactly at 70% (14.0 / 20.0) must not abort");

        // 5. Exceeded distance (> maxPlayDistance)
        assertTrue(PlayChaseGoal.shouldAbortChase(
                false, true, false, false, false, false,
                20.0f, 20.0f, 20.0f, 20.0f, 256.1, 256.0
        ), "Distance > maxPlayDistance must trigger abort");
        assertFalse(PlayChaseGoal.shouldAbortChase(
                false, true, false, false, false, false,
                20.0f, 20.0f, 20.0f, 20.0f, 256.0, 256.0
        ), "Distance exactly at maxPlayDistance must not abort");
    }

    @Test
    @DisplayName("Assert OrbitSparringGoal abort condition matrix across health, combat, sitting, and distance")
    void testOrbitAbortMatrix() {
        // Base normal state
        assertFalse(OrbitSparringGoal.shouldAbortOrbit(
                false, true, false, false, false, false,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Normal sparring conditions must not abort orbit");

        // 1. Partner null or dead
        assertTrue(OrbitSparringGoal.shouldAbortOrbit(
                true, true, false, false, false, false,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Null partner must abort");
        assertTrue(OrbitSparringGoal.shouldAbortOrbit(
                false, false, false, false, false, false,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Dead partner must abort");

        // 2. Sitting state
        assertTrue(OrbitSparringGoal.shouldAbortOrbit(
                false, true, true, false, false, false,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Sitting partner must abort");
        assertTrue(OrbitSparringGoal.shouldAbortOrbit(
                false, true, false, true, false, false,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Sitting orbiting wolf must abort");

        // 3. Combat targeting
        assertTrue(OrbitSparringGoal.shouldAbortOrbit(
                false, true, false, false, true, false,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Combat target on wolf must abort");
        assertTrue(OrbitSparringGoal.shouldAbortOrbit(
                false, true, false, false, false, true,
                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Combat target on partner must abort");

        // 4. Low health (< 70%)
        assertTrue(OrbitSparringGoal.shouldAbortOrbit(
                false, true, false, false, false, false,
                13.9f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
        ), "Wolf health < 70% must abort");
        assertTrue(OrbitSparringGoal.shouldAbortOrbit(
                false, true, false, false, false, false,
                20.0f, 20.0f, 13.9f, 20.0f, 9.0, 256.0
        ), "Partner health < 70% must abort");

        // 5. Exceeded distance
        assertTrue(OrbitSparringGoal.shouldAbortOrbit(
                false, true, false, false, false, false,
                20.0f, 20.0f, 20.0f, 20.0f, 256.1, 256.0
        ), "Distance exceeding max must abort");
    }

    @Test
    @DisplayName("Assert Goal state transitions, tick counters, and property mutators")
    void testGoalStateTransitions() {
        PlayChaseGoal chaseGoal = new PlayChaseGoal(null);
        chaseGoal.setChaseTicks(100);
        assertEquals(100, chaseGoal.getChaseTicks());
        chaseGoal.start();
        assertEquals(0, chaseGoal.getChaseTicks(), "start() must reset chaseTicks to 0");
        chaseGoal.stop();
        assertNull(chaseGoal.getPartner(), "stop() must clear partner reference");
        assertEquals(0, chaseGoal.getChaseTicks(), "stop() must reset chaseTicks");

        OrbitSparringGoal orbitGoal = new OrbitSparringGoal(null);
        orbitGoal.setOrbitTicks(50);
        assertEquals(50, orbitGoal.getOrbitTicks());
        orbitGoal.start();
        assertEquals(0, orbitGoal.getOrbitTicks(), "start() must reset orbitTicks to 0");
        orbitGoal.stop();
        assertNull(orbitGoal.getPartner(), "stop() must clear partner reference");
        assertEquals(0, orbitGoal.getOrbitTicks(), "stop() must reset orbitTicks");

        // Orbit parameter mutators
        orbitGoal.setOrbitRadius(2.5);
        assertEquals(2.5, orbitGoal.getOrbitRadius());
        orbitGoal.setAngularStep(-0.25);
        assertEquals(-0.25, orbitGoal.getAngularStep());
    }
}
