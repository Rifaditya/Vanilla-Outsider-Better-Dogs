// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.FireballTrajectoryHelper;
import net.vanillaoutsider.betterdogs.ai.FireballTrajectoryHelper.TrajectoryInterception;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 1: Core Trajectory Math & Dynamic GameRules Tests [BL-BD-020]")
public class FireballTrajectoryHelperTest {

    private static final Pattern GAMERULE_KEY_PATTERN = Pattern.compile("^[a-z0-9_.-]+:[a-z0-9_.-]+$");

    @BeforeAll
    public static void setup() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
        try {
            BetterDogsGameRules.register();
        } catch (Throwable ignored) {
        }
    }

    // =========================================================================
    // Functional Interception Math Tests
    // =========================================================================

    @Test
    @DisplayName("Assert direct head-on collision trajectory detects interception")
    public void testDirectHeadOnCollision() {
        Vec3 dogPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 projPos = new Vec3(0.0, 64.0, 20.0);
        Vec3 projVel = new Vec3(0.0, 0.0, -1.0); // 1 block per tick towards dog

        TrajectoryInterception result = FireballTrajectoryHelper.computeInterception(
                dogPos, projPos, projVel, 25.0, 1.75, 85.0
        );

        assertTrue(result.intercepted(), "Direct incoming projectile must be intercepted");
        assertEquals(20.0, result.timeToImpactTicks(), 1e-5, "Time to closest approach should be 20 ticks");
        assertEquals(0.0, result.perpendicularDistance(), 1e-5, "Perpendicular distance on direct hit must be 0");
        assertEquals(0.28, result.dodgeImpulse().y, 1e-5, "Vertical hop impulse must be 0.28");
        assertEquals(0.85, Math.abs(result.dodgeImpulse().x), 1e-5, "Horizontal impulse magnitude must be 0.85 (85%)");
    }

    @Test
    @DisplayName("Assert near-miss trajectory within threat radius calculates outward lateral dodge")
    public void testNearMissTrajectoryWithinThreatRadius() {
        Vec3 dogPos = new Vec3(1.0, 64.0, 0.0); // Offset 1 block in +X
        Vec3 projPos = new Vec3(0.0, 64.0, 20.0);
        Vec3 projVel = new Vec3(0.0, 0.0, -2.0); // 2 blocks per tick towards Z=0

        TrajectoryInterception result = FireballTrajectoryHelper.computeInterception(
                dogPos, projPos, projVel, 20.0, 1.75, 85.0
        );

        assertTrue(result.intercepted(), "Near-miss within 1.75m threat radius must be intercepted");
        assertEquals(10.0, result.timeToImpactTicks(), 1e-5, "Time to closest approach should be 10 ticks");
        assertEquals(1.0, result.perpendicularDistance(), 1e-5, "Perpendicular distance must be 1.0 block");

        // Since dog is at +X relative to the trajectory path (Z axis), the dodge vector must push in +X
        assertTrue(result.dodgeImpulse().x > 0.0, "Dodge impulse must push outward in +X away from trajectory line");
        assertEquals(0.85, result.dodgeImpulse().x, 1e-5, "Dodge impulse magnitude in X must equal 0.85");
        assertEquals(0.0, result.dodgeImpulse().z, 1e-5, "Lateral vector should have 0 component along trajectory Z");
        assertEquals(0.28, result.dodgeImpulse().y, 1e-5, "Vertical hop impulse must be 0.28");
    }

    @Test
    @DisplayName("Assert diverging projectile moving away from dog produces no interception")
    public void testDivergingTrajectory() {
        Vec3 dogPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 projPos = new Vec3(0.0, 64.0, 10.0);
        Vec3 projVel = new Vec3(0.0, 0.0, 1.0); // Moving away (+Z)

        TrajectoryInterception result = FireballTrajectoryHelper.computeInterception(
                dogPos, projPos, projVel, 20.0, 1.75, 85.0
        );

        assertFalse(result.intercepted(), "Diverging projectile must not be intercepted");
        assertSame(TrajectoryInterception.NONE, result, "Diverging trajectory must return NONE singleton");
    }

    @Test
    @DisplayName("Assert trajectory exceeding reactionTicks window produces no interception")
    public void testExceedsReactionTicks() {
        Vec3 dogPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 projPos = new Vec3(0.0, 64.0, 50.0);
        Vec3 projVel = new Vec3(0.0, 0.0, -1.0); // Impact at 50 ticks, reaction window 20 ticks

        TrajectoryInterception result = FireballTrajectoryHelper.computeInterception(
                dogPos, projPos, projVel, 20.0, 1.75, 85.0
        );

        assertFalse(result.intercepted(), "Impact at 50 ticks with 20 tick window must not be intercepted");
        assertSame(TrajectoryInterception.NONE, result);
    }

    @Test
    @DisplayName("Assert trajectory passing outside threatRadius produces no interception")
    public void testExceedsThreatRadius() {
        Vec3 dogPos = new Vec3(5.0, 64.0, 0.0); // 5 blocks away
        Vec3 projPos = new Vec3(0.0, 64.0, 20.0);
        Vec3 projVel = new Vec3(0.0, 0.0, -2.0); // Line passes at distance 5.0m, threat radius is 1.75m

        TrajectoryInterception result = FireballTrajectoryHelper.computeInterception(
                dogPos, projPos, projVel, 20.0, 1.75, 85.0
        );

        assertFalse(result.intercepted(), "Pass-by outside threat radius must not be intercepted");
        assertSame(TrajectoryInterception.NONE, result);
    }

    @Test
    @DisplayName("Assert strictly vertical projectile trajectory gracefully handles cross product degeneracy")
    public void testStrictlyVerticalTrajectory() {
        Vec3 dogPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 projPos = new Vec3(0.0, 80.0, 0.0);
        Vec3 projVel = new Vec3(0.0, -1.0, 0.0); // Falling straight down

        TrajectoryInterception result = FireballTrajectoryHelper.computeInterception(
                dogPos, projPos, projVel, 30.0, 1.75, 85.0
        );

        assertTrue(result.intercepted(), "Vertical incoming projectile must be intercepted");
        assertEquals(16.0, result.timeToImpactTicks(), 1e-5);
        assertEquals(0.0, result.perpendicularDistance(), 1e-5);
        assertFalse(Double.isNaN(result.dodgeImpulse().x), "Dodge impulse X must not be NaN");
        assertFalse(Double.isNaN(result.dodgeImpulse().z), "Dodge impulse Z must not be NaN");
        assertEquals(0.28, result.dodgeImpulse().y, 1e-5);
    }

    @Test
    @DisplayName("Assert zero velocity projectile safely returns NONE without division by zero")
    public void testZeroVelocityProjectile() {
        Vec3 dogPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 projPos = new Vec3(0.0, 64.0, 5.0);
        Vec3 projVel = Vec3.ZERO;

        TrajectoryInterception result = FireballTrajectoryHelper.computeInterception(
                dogPos, projPos, projVel, 20.0, 1.75, 85.0
        );

        assertFalse(result.intercepted());
        assertSame(TrajectoryInterception.NONE, result);
    }

    @Test
    @DisplayName("Assert null, NaN, and Infinite inputs safely return NONE without throwing")
    public void testNullAndMalformedInputs() {
        Vec3 valid = new Vec3(1.0, 2.0, 3.0);
        Vec3 nanVec = new Vec3(Double.NaN, 0.0, 0.0);
        Vec3 infVec = new Vec3(Double.POSITIVE_INFINITY, 0.0, 0.0);

        assertSame(TrajectoryInterception.NONE, FireballTrajectoryHelper.computeInterception(null, valid, valid, 20.0));
        assertSame(TrajectoryInterception.NONE, FireballTrajectoryHelper.computeInterception(valid, null, valid, 20.0));
        assertSame(TrajectoryInterception.NONE, FireballTrajectoryHelper.computeInterception(valid, valid, null, 20.0));
        assertSame(TrajectoryInterception.NONE, FireballTrajectoryHelper.computeInterception(nanVec, valid, valid, 20.0));
        assertSame(TrajectoryInterception.NONE, FireballTrajectoryHelper.computeInterception(valid, nanVec, valid, 20.0));
        assertSame(TrajectoryInterception.NONE, FireballTrajectoryHelper.computeInterception(valid, valid, nanVec, 20.0));
        assertSame(TrajectoryInterception.NONE, FireballTrajectoryHelper.computeInterception(infVec, valid, valid, 20.0));
        assertSame(TrajectoryInterception.NONE, FireballTrajectoryHelper.computeInterception(valid, infVec, valid, 20.0));
        assertSame(TrajectoryInterception.NONE, FireballTrajectoryHelper.computeInterception(valid, valid, infVec, 20.0));
        assertSame(TrajectoryInterception.NONE, FireballTrajectoryHelper.computeInterception(valid, valid, valid, -5.0));
        assertSame(TrajectoryInterception.NONE, FireballTrajectoryHelper.computeInterception(valid, valid, valid, 20.0, -1.0, 85.0));
        assertSame(TrajectoryInterception.NONE, FireballTrajectoryHelper.computeInterception(valid, valid, valid, Double.NaN));
    }

    @Test
    @DisplayName("Assert isGroundSafe null-safety with null level or null targetPos")
    public void testGroundSafeNullSafety() {
        assertFalse(FireballTrajectoryHelper.isGroundSafe(null, null));
        assertFalse(FireballTrajectoryHelper.isGroundSafe(null, new BlockPos(0, 64, 0)));
    }

    // =========================================================================
    // Pillar 1: Chaos Property Fuzzing (30,000 Iterations)
    // =========================================================================

    @Test
    @DisplayName("Pillar 1: Chaos Property Fuzzing (30,000 iterations non-NaN, finite, zero-division immune)")
    public void testPillar1ChaosPropertyFuzzing() {
        Random rand = new Random(0xDA51C);
        int iterations = 30_000;

        for (int i = 0; i < iterations; i++) {
            double px = rand.nextDouble(-100.0, 100.0);
            double py = rand.nextDouble(-64.0, 320.0);
            double pz = rand.nextDouble(-100.0, 100.0);

            double qx = rand.nextDouble(-100.0, 100.0);
            double qy = rand.nextDouble(-64.0, 320.0);
            double qz = rand.nextDouble(-100.0, 100.0);

            // Mix standard velocities, near-zero velocities, subnormal, and negative values
            double vx = (i % 10 == 0) ? 0.0 : rand.nextDouble(-5.0, 5.0);
            double vy = (i % 11 == 0) ? 0.0 : rand.nextDouble(-5.0, 5.0);
            double vz = (i % 12 == 0) ? 0.0 : rand.nextDouble(-5.0, 5.0);

            double reactionTicks = rand.nextDouble(-5.0, 60.0);
            double threatRadius = rand.nextDouble(-1.0, 10.0);
            double lateralImpulse = rand.nextDouble(-100.0, 300.0);

            Vec3 dogPos = new Vec3(px, py, pz);
            Vec3 projPos = new Vec3(qx, qy, qz);
            Vec3 projVel = new Vec3(vx, vy, vz);

            TrajectoryInterception result = FireballTrajectoryHelper.computeInterception(
                    dogPos, projPos, projVel, reactionTicks, threatRadius, lateralImpulse
            );

            assertNotNull(result, "Result must never be null");
            assertFalse(Double.isNaN(result.timeToImpactTicks()), "tau must not be NaN at iteration " + i);
            assertFalse(Double.isNaN(result.perpendicularDistance()), "distPerp must not be NaN at iteration " + i);

            Vec3 impulse = result.dodgeImpulse();
            assertNotNull(impulse, "Impulse must not be null");
            assertFalse(Double.isNaN(impulse.x), "Impulse X must not be NaN at iteration " + i);
            assertFalse(Double.isNaN(impulse.y), "Impulse Y must not be NaN at iteration " + i);
            assertFalse(Double.isNaN(impulse.z), "Impulse Z must not be NaN at iteration " + i);

            assertTrue(Double.isFinite(impulse.x), "Impulse X must be finite at iteration " + i);
            assertTrue(Double.isFinite(impulse.y), "Impulse Y must be finite at iteration " + i);
            assertTrue(Double.isFinite(impulse.z), "Impulse Z must be finite at iteration " + i);

            if (result.intercepted()) {
                assertTrue(result.timeToImpactTicks() > 0.0, "Intercepted tau must be > 0");
                assertTrue(result.timeToImpactTicks() <= reactionTicks, "Intercepted tau must be <= reactionTicks");
                assertTrue(result.perpendicularDistance() >= 0.0, "distPerp must be non-negative");
                assertTrue(result.perpendicularDistance() <= threatRadius + 1e-5, "distPerp must be <= threatRadius");
                assertEquals(0.28, impulse.y, 1e-5, "Vertical impulse must be 0.28");
            }
        }
    }

    // =========================================================================
    // Pillar 2: Concurrency Load (50 Concurrent Threads - Java 17 Compatible)
    // =========================================================================

    @Test
    @DisplayName("Pillar 2: Concurrency Load (50 concurrent threads evaluating trajectory queries)")
    public void testPillar2ConcurrencyLoad() throws InterruptedException, ExecutionException {
        int threadCount = 50;
        int queriesPerThread = 1_000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        Future<?>[] futures = new Future<?>[threadCount];
        for (int t = 0; t < threadCount; t++) {
            final int seed = t * 1000 + 42;
            futures[t] = executor.submit(() -> {
                Random rand = new Random(seed);
                for (int q = 0; q < queriesPerThread; q++) {
                    Vec3 dogPos = new Vec3(rand.nextDouble(-50, 50), 64.0, rand.nextDouble(-50, 50));
                    Vec3 projPos = new Vec3(rand.nextDouble(-50, 50), 64.0, rand.nextDouble(-50, 50));
                    Vec3 projVel = new Vec3(rand.nextDouble(-2, 2), rand.nextDouble(-1, 1), rand.nextDouble(-2, 2));

                    TrajectoryInterception interception = FireballTrajectoryHelper.computeInterception(
                            dogPos, projPos, projVel, 20.0, 1.75, 85.0
                    );
                    assertNotNull(interception);
                    assertFalse(Double.isNaN(interception.timeToImpactTicks()));
                }
            });
        }

        for (Future<?> f : futures) {
            f.get(); // Propagate any assertion failure or exception
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS), "Executor must terminate cleanly");
    }

    // =========================================================================
    // Pillar 3: GameRule Registry Contract & Dynamic Alias Verification
    // =========================================================================

    @Test
    @DisplayName("Pillar 3: GameRule Contract (Registration, Defaults, Snake-Case, and Null-Safe Accessors)")
    public void testPillar3GameRuleContract() {
        // Assert static null-safe accessors return defaults
        assertTrue(BetterDogsGameRules.isBallisticEvasionEnabled(null),
                "isBallisticEvasionEnabled(null) must return true");
        assertEquals(20, BetterDogsGameRules.getEvasionReactionTicks(null),
                "getEvasionReactionTicks(null) must return 20");
        assertEquals(85, BetterDogsGameRules.getEvasionLateralImpulse(null),
                "getEvasionLateralImpulse(null) must return 85");

        if (BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED != null) {
            assertEquals("bd_ballistic_evasion_enabled", BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED.getId());
            assertEquals("bd_evasion_reaction_ticks", BetterDogsGameRules.BD_EVASION_REACTION_TICKS.getId());
            assertEquals("bd_evasion_lateral_impulse", BetterDogsGameRules.BD_EVASION_LATERAL_IMPULSE.getId());
        }

        // Assert snake_case pattern
        String enabledKey = "betterdogs:bd_ballistic_evasion_enabled";
        String ticksKey = "betterdogs:bd_evasion_reaction_ticks";
        String impulseKey = "betterdogs:bd_evasion_lateral_impulse";

        assertTrue(GAMERULE_KEY_PATTERN.matcher(enabledKey).matches(), "Enabled key must match pattern");
        assertTrue(GAMERULE_KEY_PATTERN.matcher(ticksKey).matches(), "Ticks key must match pattern");
        assertTrue(GAMERULE_KEY_PATTERN.matcher(impulseKey).matches(), "Impulse key must match pattern");

        assertNotNull(ResourceLocation.tryParse(enabledKey), "ResourceLocation.tryParse failed for enabledKey");
        assertNotNull(ResourceLocation.tryParse(ticksKey), "ResourceLocation.tryParse failed for ticksKey");
        assertNotNull(ResourceLocation.tryParse(impulseKey), "ResourceLocation.tryParse failed for impulseKey");
    }
}
