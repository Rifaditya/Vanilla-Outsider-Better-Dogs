// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.FleeExplodingHazardGoal;
import net.vanillaoutsider.betterdogs.ai.HazardDetectionHelper;
import net.vanillaoutsider.betterdogs.ai.HazardDetectionHelper.DetonationHazard;
import net.vanillaoutsider.betterdogs.ai.HazardDetectionHelper.HazardType;
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

@DisplayName("Step 2: Canine Explosive Reflex AI Goal & Emergency Blast Scramble Tests [BL-BD-006]")
public class CanineExplosiveReflexTest {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BetterDogsGameRules.register();
    }

    @Test
    @DisplayName("Assert hazard within 4.0 blocks triggers emergency scramble predicate")
    public void testEmergencyScrambleWithinFourBlocks() {
        assertEquals(4.0, FleeExplodingHazardGoal.EMERGENCY_SCRAMBLE_DISTANCE, 1e-6);
        assertEquals(16.0, FleeExplodingHazardGoal.EMERGENCY_SCRAMBLE_DISTANCE_SQ, 1e-6);

        // Immediate vicinity (0 to 4 blocks)
        assertTrue(FleeExplodingHazardGoal.shouldScrambleFromSitting(0.0),
                "Sitting dog must scramble when hazard is at distance 0 (direct contact)");
        assertTrue(FleeExplodingHazardGoal.shouldScrambleFromSitting(1.0),
                "Sitting dog must scramble when hazard is at distance 1.0 block");
        assertTrue(FleeExplodingHazardGoal.shouldScrambleFromSitting(4.0),
                "Sitting dog must scramble when hazard is at distance 2.0 blocks (distSq 4.0)");
        assertTrue(FleeExplodingHazardGoal.shouldScrambleFromSitting(9.0),
                "Sitting dog must scramble when hazard is at distance 3.0 blocks (distSq 9.0)");
        assertTrue(FleeExplodingHazardGoal.shouldScrambleFromSitting(15.99),
                "Sitting dog must scramble when hazard is at distance ~3.99 blocks (distSq 15.99)");
        assertTrue(FleeExplodingHazardGoal.shouldScrambleFromSitting(16.0),
                "Sitting dog must scramble when hazard is exactly at boundary 4.0 blocks (distSq 16.0)");

        // canTriggerReflex evaluates full gating for sitting dogs
        double fleeRadiusSq = 14.0 * 14.0; // 196.0
        assertTrue(FleeExplodingHazardGoal.canTriggerReflex(true, 0.0, fleeRadiusSq),
                "Sitting dog canTriggerReflex must return true at distance 0");
        assertTrue(FleeExplodingHazardGoal.canTriggerReflex(true, 16.0, fleeRadiusSq),
                "Sitting dog canTriggerReflex must return true at distance 4.0 blocks (distSq 16.0)");

        // Standing dogs trigger reflex freely across entire flee radius
        assertTrue(FleeExplodingHazardGoal.canTriggerReflex(false, 25.0, fleeRadiusSq),
                "Standing dog canTriggerReflex must return true at distance 5.0 blocks (distSq 25.0)");
        assertTrue(FleeExplodingHazardGoal.canTriggerReflex(false, 100.0, fleeRadiusSq),
                "Standing dog canTriggerReflex must return true at distance 10.0 blocks (distSq 100.0)");
        assertTrue(FleeExplodingHazardGoal.canTriggerReflex(false, 196.0, fleeRadiusSq),
                "Standing dog canTriggerReflex must return true at distance 14.0 blocks (distSq 196.0)");
    }

    @Test
    @DisplayName("Assert hazard > 4.0 blocks does NOT disrupt sitting posture")
    public void testHazardExceedingFourBlocksDoesNotDisruptSittingPosture() {
        double fleeRadiusSq = 14.0 * 14.0; // 196.0

        // Just past boundary (4.001 blocks)
        assertFalse(FleeExplodingHazardGoal.shouldScrambleFromSitting(16.01),
                "Sitting dog must NOT scramble when hazard is > 4.0 blocks (distSq 16.01)");
        assertFalse(FleeExplodingHazardGoal.canTriggerReflex(true, 16.01, fleeRadiusSq),
                "canTriggerReflex must be false for sitting dog when hazard is > 4.0 blocks (distSq 16.01)");

        // Intermediate ranges within 14-block radius
        assertFalse(FleeExplodingHazardGoal.shouldScrambleFromSitting(25.0),
                "Sitting dog must NOT scramble at distance 5.0 blocks (distSq 25.0)");
        assertFalse(FleeExplodingHazardGoal.canTriggerReflex(true, 25.0, fleeRadiusSq),
                "Sitting posture must be preserved at distance 5.0 blocks");

        assertFalse(FleeExplodingHazardGoal.shouldScrambleFromSitting(49.0),
                "Sitting dog must NOT scramble at distance 7.0 blocks (distSq 49.0)");
        assertFalse(FleeExplodingHazardGoal.canTriggerReflex(true, 49.0, fleeRadiusSq),
                "Sitting posture must be preserved at distance 7.0 blocks");

        assertFalse(FleeExplodingHazardGoal.shouldScrambleFromSitting(100.0),
                "Sitting dog must NOT scramble at distance 10.0 blocks (distSq 100.0)");
        assertFalse(FleeExplodingHazardGoal.canTriggerReflex(true, 100.0, fleeRadiusSq),
                "Sitting posture must be preserved at distance 10.0 blocks");

        assertFalse(FleeExplodingHazardGoal.shouldScrambleFromSitting(196.0),
                "Sitting dog must NOT scramble at distance 14.0 blocks (distSq 196.0)");
        assertFalse(FleeExplodingHazardGoal.canTriggerReflex(true, 196.0, fleeRadiusSq),
                "Sitting posture must be preserved at distance 14.0 blocks");

        // Beyond flee radius (> 14 blocks)
        assertFalse(FleeExplodingHazardGoal.canTriggerReflex(false, 196.1, fleeRadiusSq),
                "Reflex must not trigger beyond flee radius even for standing dogs");
        assertFalse(FleeExplodingHazardGoal.canTriggerReflex(true, 196.1, fleeRadiusSq),
                "Reflex must not trigger beyond flee radius for sitting dogs");
    }

    @Test
    @DisplayName("Pillar 4 (Side-Safety): Assert zero client classes in FleeExplodingHazardGoal")
    public void testZeroClientClassesInFleeExplodingHazardGoal() throws IOException {
        Path goalPath = Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "ai", "FleeExplodingHazardGoal.java");
        assertTrue(Files.exists(goalPath), "FleeExplodingHazardGoal.java must exist on disk");

        List<String> lines = Files.readAllLines(goalPath);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("import net.minecraft.client.")
                    || line.startsWith("import com.mojang.blaze3d.")) {
                fail("Side-Safety Violation: Client import detected in FleeExplodingHazardGoal at line "
                        + (i + 1) + ": " + line);
            }
        }

        // Also verify HazardDetectionHelper for side-safety completeness
        Path helperPath = Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "ai", "HazardDetectionHelper.java");
        assertTrue(Files.exists(helperPath), "HazardDetectionHelper.java must exist on disk");
        List<String> helperLines = Files.readAllLines(helperPath);
        for (int i = 0; i < helperLines.size(); i++) {
            String line = helperLines.get(i).trim();
            if (line.startsWith("import net.minecraft.client.")
                    || line.startsWith("import com.mojang.blaze3d.")) {
                fail("Side-Safety Violation: Client import detected in HazardDetectionHelper at line "
                        + (i + 1) + ": " + line);
            }
        }
    }

    @Test
    @DisplayName("Assert flee speed multiplier calculation scales to 1.7x sprint speed")
    public void testFleeSpeedMultiplierCalculation() {
        int fleeSpeedInt = BetterDogsGameRules.getFleeSpeedMultiplier(null);
        assertEquals(170, fleeSpeedInt, "Default flee speed GameRule integer should be 170");

        double speedMultiplier = fleeSpeedInt / 100.0;
        assertEquals(1.7, speedMultiplier, 1e-6, "Speed multiplier must be 1.7x for sprint evasion");
    }
}
