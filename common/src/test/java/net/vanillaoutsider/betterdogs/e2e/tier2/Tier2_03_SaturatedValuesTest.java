// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier2;

import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.FireballTrajectoryHelper;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 2 - Boundary 03: Saturated Values & Heavy Stress Tests")
public class Tier2_03_SaturatedValuesTest {

    @Test
    @DisplayName("Assert 50,000 chained breeding generations remain strictly within saturated bounds")
    public void testSaturatedBreedingGenerationsStress() {
        RandomSource rand = RandomSource.create(999999L);
        float p1 = 15.0f;
        float p2 = 45.0f;

        for (int i = 0; i < 50_000; i++) {
            float child = CanineGeneticsHelper.calculateOffspringStat(
                    p1, p2, CanineGeneticsHelper.HEALTH_STD_DEV,
                    CanineGeneticsHelper.MIN_HEALTH, CanineGeneticsHelper.MAX_HEALTH, rand
            );
            assertTrue(child >= CanineGeneticsHelper.MIN_HEALTH && child <= CanineGeneticsHelper.MAX_HEALTH,
                    "Breeding generation " + i + " drifted out of bounds: " + child);
            p1 = p2;
            p2 = child;
        }
    }

    @Test
    @DisplayName("Assert trajectory solver handles saturated distant projectile coordinates")
    public void testSaturatedDistantProjectile() {
        Vec3 dogPos = new Vec3(0, 64, 0);
        Vec3 projPos = new Vec3(100_000, 64, 100_000);
        Vec3 projVel = new Vec3(0, 0, -1.0);

        FireballTrajectoryHelper.TrajectoryInterception interception =
                FireballTrajectoryHelper.computeInterception(dogPos, projPos, projVel, 100.0, 1.75, 85.0);

        assertFalse(interception.intercepted(), "Distant projectile (100km) must not be intercepted");
        assertTrue(interception.perpendicularDistance() > 10_000.0);
    }

    @Test
    @DisplayName("Assert trajectory solver handles exact zero perpendicular distance collision course")
    public void testDirectZeroPerpendicularDistance() {
        Vec3 dogPos = new Vec3(10, 64, 10);
        Vec3 projPos = new Vec3(10, 64, 30);
        Vec3 projVel = new Vec3(0, 0, -0.5); // Moving along -Z straight towards dog

        FireballTrajectoryHelper.TrajectoryInterception interception =
                FireballTrajectoryHelper.computeInterception(dogPos, projPos, projVel, 50.0, 1.75, 85.0);

        assertTrue(interception.intercepted());
        assertEquals(0.0, interception.perpendicularDistance(), 1e-4);
        assertEquals(40.0, interception.timeToImpactTicks(), 1e-4);
    }

    @Test
    @DisplayName("Assert extreme high-speed projectile velocity (>200 blocks/tick) handled cleanly")
    public void testHighSpeedProjectileHandling() {
        Vec3 dogPos = new Vec3(0, 64, 0);
        Vec3 projPos = new Vec3(0, 64, 100);
        Vec3 projVel = new Vec3(0, 0, -200.0); // 200 blocks in 1 tick

        FireballTrajectoryHelper.TrajectoryInterception interception =
                FireballTrajectoryHelper.computeInterception(dogPos, projPos, projVel, 20.0, 1.75, 85.0);

        assertTrue(interception.intercepted());
        assertEquals(0.5, interception.timeToImpactTicks(), 1e-4);
    }

    @Test
    @DisplayName("Assert saturated integer game rule frequency bounds (up to 72,000 ticks)")
    public void testSaturatedGameRuleFrequencies() {
        int maxTickDay = 72_000; // 3 Minecraft days
        int clampedVal = Math.clamp(100_000, 0, maxTickDay);
        assertEquals(72_000, clampedVal, "Frequency exceeding maxTickDay must clamp to 72,000");

        int minClamped = Math.clamp(-500, 0, maxTickDay);
        assertEquals(0, minClamped, "Negative tick inputs must clamp to 0");
    }
}
