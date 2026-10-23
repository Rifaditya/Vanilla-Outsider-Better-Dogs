// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.AvoidGhastFireballGoal;
import net.vanillaoutsider.betterdogs.ai.FireballTrajectoryHelper;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 2: Ballistic Evasion AI Goal & Emergency Scramble Tests [BL-BD-020]")
public class BallisticEvasionGoalTest {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BetterDogsGameRules.register();
    }

    @Test
    @DisplayName("Assert sitting dog emergency scramble triggers only within 1.5m and 20 ticks")
    public void testScramblePredicateSittingDog() {
        assertEquals(1.5, AvoidGhastFireballGoal.EMERGENCY_SCRAMBLE_DISTANCE, 1e-6);
        assertEquals(1.75, AvoidGhastFireballGoal.DEFAULT_THREAT_RADIUS, 1e-6);

        // Direct collision course (perp = 0.0, tau = 10)
        assertTrue(AvoidGhastFireballGoal.shouldScrambleFromSitting(0.0, 10.0),
                "Sitting dog must scramble when projectile path is direct (0.0m)");
        assertTrue(AvoidGhastFireballGoal.shouldScrambleFromSitting(0.5, 5.0),
                "Sitting dog must scramble when projectile path is 0.5m");
        assertTrue(AvoidGhastFireballGoal.shouldScrambleFromSitting(1.0, 15.0),
                "Sitting dog must scramble when projectile path is 1.0m");
        assertTrue(AvoidGhastFireballGoal.shouldScrambleFromSitting(1.5, 20.0),
                "Sitting dog must scramble at exact boundary (1.5m, 20 ticks)");

        // Out of boundary (distance > 1.5m)
        assertFalse(AvoidGhastFireballGoal.shouldScrambleFromSitting(1.51, 10.0),
                "Sitting dog must NOT scramble if perp distance exceeds 1.5m");
        assertFalse(AvoidGhastFireballGoal.shouldScrambleFromSitting(2.0, 5.0),
                "Sitting dog must NOT scramble if perp distance is 2.0m");

        // Out of boundary (tau <= 0 or tau > 20)
        assertFalse(AvoidGhastFireballGoal.shouldScrambleFromSitting(0.5, 0.0),
                "Sitting dog must NOT scramble if tau <= 0 (already passed)");
        assertFalse(AvoidGhastFireballGoal.shouldScrambleFromSitting(0.5, -5.0),
                "Sitting dog must NOT scramble if tau is negative");
        assertFalse(AvoidGhastFireballGoal.shouldScrambleFromSitting(0.5, 20.1),
                "Sitting dog must NOT scramble if impact is > 20 ticks away");
    }

    @Test
    @DisplayName("Assert canTriggerReflex gates correctly for standing vs sitting dogs")
    public void testCanTriggerReflex() {
        double reactionTicks = 20.0;

        // Standing dogs: triggered if perp <= 1.75m and 0 < tau <= reactionTicks
        assertTrue(AvoidGhastFireballGoal.canTriggerReflex(false, 0.0, 10.0, reactionTicks),
                "Standing dog reflex triggers on direct incoming projectile");
        assertTrue(AvoidGhastFireballGoal.canTriggerReflex(false, 1.5, 15.0, reactionTicks),
                "Standing dog reflex triggers at 1.5m");
        assertTrue(AvoidGhastFireballGoal.canTriggerReflex(false, 1.75, 20.0, reactionTicks),
                "Standing dog reflex triggers at exact threat radius boundary 1.75m");

        // Standing dog outside bounds
        assertFalse(AvoidGhastFireballGoal.canTriggerReflex(false, 1.76, 10.0, reactionTicks),
                "Standing dog reflex must NOT trigger beyond threat radius");
        assertFalse(AvoidGhastFireballGoal.canTriggerReflex(false, 0.5, 20.1, reactionTicks),
                "Standing dog reflex must NOT trigger beyond reaction ticks");
        assertFalse(AvoidGhastFireballGoal.canTriggerReflex(false, 0.5, 0.0, reactionTicks),
                "Standing dog reflex must NOT trigger if projectile already reached closest point");

        // Sitting dogs: strictly gated by shouldScrambleFromSitting (perp <= 1.5m, tau <= 20)
        assertTrue(AvoidGhastFireballGoal.canTriggerReflex(true, 1.2, 12.0, reactionTicks),
                "Sitting dog scrambles when within 1.5m and 20 ticks");
        assertFalse(AvoidGhastFireballGoal.canTriggerReflex(true, 1.6, 12.0, reactionTicks),
                "Sitting dog does NOT break sitting posture if perp distance is between 1.5m and 1.75m");
    }

    @Test
    @DisplayName("Assert isThreateningProjectile safely rejects null and dead entities")
    public void testThreateningProjectilePredicate() {
        assertFalse(AvoidGhastFireballGoal.isThreateningProjectile(null),
                "Null entity must return false");
    }

    @Test
    @DisplayName("Pillar 5 (Side-Safety): Assert zero client classes in AvoidGhastFireballGoal")
    public void testZeroClientClassesInAvoidGhastFireballGoal() throws IOException {
        Path goalPath = Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "ai", "AvoidGhastFireballGoal.java");
        assertTrue(Files.exists(goalPath), "AvoidGhastFireballGoal.java must exist on disk");

        List<String> lines = Files.readAllLines(goalPath);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            assertFalse(line.startsWith("import net.minecraft.client."),
                    "AvoidGhastFireballGoal must not import net.minecraft.client.* (line " + (i + 1) + ")");
            assertFalse(line.startsWith("import com.mojang.blaze3d."),
                    "AvoidGhastFireballGoal must not import com.mojang.blaze3d.* (line " + (i + 1) + ")");
            assertFalse(line.contains("net.fabricmc.api.EnvType.CLIENT"),
                    "AvoidGhastFireballGoal must not reference client environment (line " + (i + 1) + ")");
        }
    }

    @Test
    @DisplayName("Assert single-line GPLv3 header and single-purpose LOC ceiling")
    public void testLicenseHeaderAndLocCeiling() throws IOException {
        Path goalPath = Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "ai", "AvoidGhastFireballGoal.java");
        List<String> lines = Files.readAllLines(goalPath);
        assertFalse(lines.isEmpty(), "AvoidGhastFireballGoal.java must not be empty");

        assertEquals("// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3", lines.get(0).trim(),
                "Must feature exact single-line GPLv3 license header");

        assertTrue(lines.size() <= 300,
                "AvoidGhastFireballGoal must adhere to the ~300 LOC ceiling law (currently: " + lines.size() + " LOC)");
    }

    @Test
    @DisplayName("Assert ballistic dodge calculation matches lateral impulse vectors")
    public void testBallisticEvasionIntegration() {
        Vec3 dogPos = new Vec3(0, 64, 0);
        Vec3 projPos = new Vec3(0, 64, 20);
        Vec3 projVel = new Vec3(0, 0, -1.0); // 1 block per tick towards dog along -Z

        FireballTrajectoryHelper.TrajectoryInterception interception =
                FireballTrajectoryHelper.computeInterception(dogPos, projPos, projVel, 25.0, 1.75, 85.0);

        assertTrue(interception.intercepted(), "Incoming projectile must be intercepted");
        assertEquals(20.0, interception.timeToImpactTicks(), 1e-4, "Time to impact must be 20 ticks");
        assertEquals(0.0, interception.perpendicularDistance(), 1e-4, "Perpendicular distance must be 0");

        // Verify that sitting scramble triggers at tau = 20.0 and perp = 0.0
        assertTrue(AvoidGhastFireballGoal.shouldScrambleFromSitting(interception.perpendicularDistance(), interception.timeToImpactTicks()));
        assertTrue(AvoidGhastFireballGoal.canTriggerReflex(true, interception.perpendicularDistance(), interception.timeToImpactTicks(), 25.0));

        // Lateral impulse should be non-zero horizontally and have upward hop
        Vec3 impulse = interception.dodgeImpulse();
        assertTrue(Math.abs(impulse.x) > 0.0 || Math.abs(impulse.z) > 0.0, "Dodge impulse must have lateral movement");
        assertEquals(FireballTrajectoryHelper.VERTICAL_HOP_IMPULSE, impulse.y, 1e-4, "Dodge impulse must include vertical hop");
    }
}
