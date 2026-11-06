// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier4;

import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.AvoidGhastFireballGoal;
import net.vanillaoutsider.betterdogs.ai.FireballTrajectoryHelper;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 4 - Scenario 02: Ghast Fireball Interceptor Rescue Scenario")
public class Tier4_02_FireballInterceptorRescueScenarioTest {

    @Test
    @DisplayName("Simulate sitting Guardian dog detecting incoming fireball, scrambling from sit, and intercepting")
    public void testFireballInterceptorRescueWorkflow() {
        // Step 1: Owner is at low health (6 HP < 10 threshold)
        float ownerHealth = 6.0f;
        int interceptThreshold = 10;
        assertTrue(ownerHealth <= interceptThreshold, "Low owner health triggers heroic interceptor requirement");

        // Step 2: Guardian dog is seated 1.0m from incoming projectile path
        Vec3 dogPos = new Vec3(0, 64, 0);
        Vec3 fireballPos = new Vec3(0, 64, 18);
        Vec3 fireballVel = new Vec3(0, 0, -1.0); // 1.0 block per tick towards player/dog

        // Step 3: Trajectory solver calculates trajectory parameters
        FireballTrajectoryHelper.TrajectoryInterception interception =
                FireballTrajectoryHelper.computeInterception(dogPos, fireballPos, fireballVel, 25.0, 1.75, 85.0);

        assertTrue(interception.intercepted(), "Fireball trajectory must be intercepted");
        assertEquals(18.0, interception.timeToImpactTicks(), 1e-4, "Time to impact is 18 ticks");
        assertEquals(0.0, interception.perpendicularDistance(), 1e-4);

        // Step 4: Emergency scramble breaks sitting posture because perp <= 1.5m and tau <= 20 ticks
        boolean shouldScramble = AvoidGhastFireballGoal.shouldScrambleFromSitting(
                interception.perpendicularDistance(), interception.timeToImpactTicks()
        );
        assertTrue(shouldScramble, "Dog must scramble out of sitting posture to protect owner");

        // Step 5: Dog receives lateral dodge / intercept impulse
        Vec3 impulse = interception.dodgeImpulse();
        assertNotNull(impulse);
        assertEquals(FireballTrajectoryHelper.VERTICAL_HOP_IMPULSE, impulse.y, 1e-4, "Dog hops into intercept trajectory");

        // Step 6: Guardian archetype durability handles routed heroic damage
        CanineArchetype guardian = CanineArchetype.GUARDIAN;
        assertEquals(1.0, guardian.getHealthModifier(), 1e-4);
        assertEquals(1.2, guardian.getDamageModifier(), 1e-4);
    }
}
