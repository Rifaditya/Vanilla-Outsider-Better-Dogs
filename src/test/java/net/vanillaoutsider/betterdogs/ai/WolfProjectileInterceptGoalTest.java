// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 3: Canine Intercept AI Goal Implementation [BL-BD-021]")
public class WolfProjectileInterceptGoalTest {

    @Test
    @DisplayName("Assert isHostileShooter and null entity handling")
    public void testHostileShooterAndNullHandling() {
        assertFalse(WolfProjectileInterceptGoal.isHostileProjectile(null, null),
                "Null entity must never be considered a hostile projectile");
        assertFalse(WolfProjectileInterceptGoal.isDamageDealingProjectile(null),
                "Null projectile must not be damage dealing");

        // Unowned projectile (e.g. fired by dispenser or mob without explicit owner) is hostile
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(null, null),
                "Unowned projectile must be considered hostile");
    }

    @Test
    @DisplayName("Assert emergency unseat distance gating for sitting dogs")
    public void testSittingUnseatDistanceGating() {
        assertEquals(2.5, WolfProjectileInterceptGoal.EMERGENCY_UNSEAT_MAX_DISTANCE, 1e-6);

        // Standing dog is always allowed
        assertTrue(WolfProjectileInterceptGoal.isEmergencyUnseatAllowed(false, 0.0),
                "Standing dog is allowed to intercept at 0.0m");
        assertTrue(WolfProjectileInterceptGoal.isEmergencyUnseatAllowed(false, 10.0),
                "Standing dog is allowed to intercept at 10.0m");

        // Sitting dog within emergency scramble threshold (<= 2.5m)
        assertTrue(WolfProjectileInterceptGoal.isEmergencyUnseatAllowed(true, 0.0),
                "Sitting dog must scramble when right at owner (0.0m)");
        assertTrue(WolfProjectileInterceptGoal.isEmergencyUnseatAllowed(true, 1.5),
                "Sitting dog must scramble when 1.5m from owner");
        assertTrue(WolfProjectileInterceptGoal.isEmergencyUnseatAllowed(true, 2.5),
                "Sitting dog must scramble at exact 2.5m boundary");

        // Sitting dog beyond emergency threshold (> 2.5m)
        assertFalse(WolfProjectileInterceptGoal.isEmergencyUnseatAllowed(true, 2.51),
                "Sitting dog must NOT unseat when distance exceeds 2.5m");
        assertFalse(WolfProjectileInterceptGoal.isEmergencyUnseatAllowed(true, 5.0),
                "Sitting dog must NOT unseat when distance is 5.0m");
    }

    @Test
    @DisplayName("Assert canine reachability evaluation (canDogReach)")
    public void testCanDogReach() {
        // Close range (<= 2.0m) can always reach if tau > 0
        assertTrue(WolfProjectileInterceptGoal.canDogReach(1.0, 1.0),
                "Dog within 2.0m should always reach");
        assertTrue(WolfProjectileInterceptGoal.canDogReach(2.0, 0.5),
                "Dog at exact 2.0m boundary should reach");

        // Medium range: reachTime = distance / 0.5
        // At 5.0m, reachTime = 10 ticks
        assertTrue(WolfProjectileInterceptGoal.canDogReach(5.0, 15.0),
                "Dog should reach 5.0m target with 15 ticks lookahead");
        assertTrue(WolfProjectileInterceptGoal.canDogReach(5.0, 10.0),
                "Dog should reach 5.0m target at exact 10 ticks lookahead");
        assertFalse(WolfProjectileInterceptGoal.canDogReach(5.0, 9.9),
                "Dog should NOT reach 5.0m target when tau < reachTime");
        assertFalse(WolfProjectileInterceptGoal.canDogReach(5.0, 5.0),
                "Dog should NOT reach 5.0m target with insufficient tau");

        // Invalid / past tau
        assertFalse(WolfProjectileInterceptGoal.canDogReach(1.0, 0.0),
                "Dog cannot reach when tau is 0 (already impacted)");
        assertFalse(WolfProjectileInterceptGoal.canDogReach(1.0, -1.0),
                "Dog cannot reach when tau is negative");
    }

    @Test
    @DisplayName("Assert leap impulse math, horizontal normalization, and vertical hop")
    public void testCalculateLeapImpulse() {
        // Null checks
        assertEquals(Vec3.ZERO, WolfProjectileInterceptGoal.calculateLeapImpulse(null, new Vec3(1, 1, 1)));
        assertEquals(Vec3.ZERO, WolfProjectileInterceptGoal.calculateLeapImpulse(new Vec3(1, 1, 1), null));

        // Along +X axis
        Vec3 dogPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 targetX = new Vec3(10.0, 64.0, 0.0);
        Vec3 impulseX = WolfProjectileInterceptGoal.calculateLeapImpulse(dogPos, targetX);
        assertEquals(0.45, impulseX.x, 1e-5, "Impulse X must be 0.45");
        assertEquals(0.35, impulseY(impulseX), 1e-5, "Impulse Y must be 0.35");
        assertEquals(0.0, impulseX.z, 1e-5, "Impulse Z must be 0.0");

        // Along +Z axis
        Vec3 targetZ = new Vec3(0.0, 64.0, 10.0);
        Vec3 impulseZ = WolfProjectileInterceptGoal.calculateLeapImpulse(dogPos, targetZ);
        assertEquals(0.0, impulseZ.x, 1e-5, "Impulse X must be 0.0");
        assertEquals(0.35, impulseY(impulseZ), 1e-5, "Impulse Y must be 0.35");
        assertEquals(0.45, impulseZ.z, 1e-5, "Impulse Z must be 0.45");

        // Diagonal
        Vec3 targetDiag = new Vec3(10.0, 64.0, 10.0);
        Vec3 impulseDiag = WolfProjectileInterceptGoal.calculateLeapImpulse(dogPos, targetDiag);
        double horizLen = Math.sqrt(impulseDiag.x * impulseDiag.x + impulseDiag.z * impulseDiag.z);
        assertEquals(0.45, horizLen, 1e-5, "Diagonal horizontal impulse length must be exactly 0.45");
        assertEquals(0.35, impulseY(impulseDiag), 1e-5, "Diagonal impulse Y must be 0.35");

        // Coincident position
        Vec3 impulseSame = WolfProjectileInterceptGoal.calculateLeapImpulse(dogPos, dogPos);
        assertEquals(0.0, impulseSame.x, 1e-5);
        assertEquals(0.35, impulseY(impulseSame), 1e-5);
        assertEquals(0.0, impulseSame.z, 1e-5);
    }

    private static double impulseY(Vec3 v) {
        return v.y;
    }

    @Test
    @DisplayName("Pillar 5 (Side-Safety): Assert zero client classes in WolfProjectileInterceptGoal")
    public void testZeroClientClassesInWolfProjectileInterceptGoal() throws IOException {
        Path goalPath = Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "ai", "WolfProjectileInterceptGoal.java");
        assertTrue(Files.exists(goalPath), "WolfProjectileInterceptGoal.java must exist on disk");

        List<String> lines = Files.readAllLines(goalPath);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            assertFalse(line.startsWith("import net.minecraft.client."),
                    "WolfProjectileInterceptGoal must not import net.minecraft.client.* (line " + (i + 1) + ")");
            assertFalse(line.startsWith("import com.mojang.blaze3d."),
                    "WolfProjectileInterceptGoal must not import com.mojang.blaze3d.* (line " + (i + 1) + ")");
            assertFalse(line.contains("net.fabricmc.api.EnvType.CLIENT"),
                    "WolfProjectileInterceptGoal must not reference client environment (line " + (i + 1) + ")");
        }
    }

    @Test
    @DisplayName("Assert single-line GPLv3 header and single-purpose LOC ceiling")
    public void testLicenseHeaderAndLocCeiling() throws IOException {
        Path goalPath = Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "ai", "WolfProjectileInterceptGoal.java");
        List<String> lines = Files.readAllLines(goalPath);
        assertFalse(lines.isEmpty(), "WolfProjectileInterceptGoal.java must not be empty");

        assertEquals("// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3", lines.get(0).trim(),
                "Must feature exact single-line GPLv3 license header");

        assertTrue(lines.size() <= 300,
                "WolfProjectileInterceptGoal must adhere to the ~300 LOC ceiling law (currently: " + lines.size() + " LOC)");
    }
}
