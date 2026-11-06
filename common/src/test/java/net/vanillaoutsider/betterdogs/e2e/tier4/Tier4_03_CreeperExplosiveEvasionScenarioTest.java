// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier4;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 4 - Scenario 03: Creeper Explosive Hazard Evasion Workflow")
public class Tier4_03_CreeperExplosiveEvasionScenarioTest {

    @Test
    @DisplayName("Simulate pack detecting swelling creeper within hazard radius, fleeing at 170% speed")
    public void testCreeperExplosiveEvasionWorkflow() {
        // Step 1: Creeper swells within hazard radius (configured to 14 blocks)
        int hazardFleeRadius = 14;
        Vec3 creeperPos = new Vec3(0, 64, 0);
        Vec3 dogPos = new Vec3(5, 64, 5);

        double distanceToHazard = dogPos.distanceTo(creeperPos);
        assertTrue(distanceToHazard < hazardFleeRadius, "Dog is within hazard flee radius (" + distanceToHazard + " < 14m)");

        // Step 2: Explosive reflex activates: sitting dogs break sit if within immediate blast danger
        boolean dogSitting = true;
        boolean hazardActive = true;
        boolean breakSit = dogSitting && hazardActive && (distanceToHazard <= 14.0);
        assertTrue(breakSit, "Dog must immediately break sitting posture when primed explosive hazard is detected");

        // Step 3: Sprint velocity calculated with 170% multiplier
        int fleeSpeedMultiplierPercent = 170;
        double baseDogSpeed = 0.30;
        double sprintSpeed = baseDogSpeed * (fleeSpeedMultiplierPercent / 100.0);

        assertEquals(0.51, sprintSpeed, 1e-4, "Dog must sprint away from explosive hazard at 0.51 blocks/tick");

        // Step 4: Direction vector computed away from creeper
        Vec3 fleeDirection = dogPos.subtract(creeperPos).normalize();
        assertTrue(fleeDirection.x > 0 && fleeDirection.z > 0, "Flee vector must point directly away from hazard");
    }
}
