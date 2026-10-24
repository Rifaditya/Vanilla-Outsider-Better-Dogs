// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.ai;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.WolfProjectileInterceptHelper.InterceptPrediction;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.util.WolfDispositionHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 1: Trajectory Intercept Mathematical Solver & Disposition Affinity [BL-BD-021]")
public class WolfProjectileInterceptHelperTest {

    @BeforeAll
    public static void setup() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

    // =========================================================================
    // 1. Direct Collision Trajectory Prediction Accuracy
    // =========================================================================

    @Test
    @DisplayName("Assert direct head-on collision calculates exact tau and intercept coordinate")
    public void testDirectHeadOnCollision() {
        Vec3 ownerPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 projPos = new Vec3(0.0, 64.0, 20.0);
        Vec3 projVel = new Vec3(0.0, 0.0, -1.0); // 1 block/tick along -Z towards owner

        InterceptPrediction prediction = WolfProjectileInterceptHelper.computeOwnerIntercept(
                ownerPos, projPos, projVel, 24.0, 1.25
        );

        assertTrue(prediction.willHitOwner(), "Head-on projectile must register willHitOwner");
        assertEquals(20.0, prediction.timeToImpactTicks(), 1e-5, "Tau must be exactly 20 ticks");
        assertEquals(0.0, prediction.perpendicularDistance(), 1e-5, "Perpendicular distance must be 0");

        // Optimal intercept is 1 tick before impact (tau - 1 = 19 ticks from projPos)
        Vec3 expectedIntercept = new Vec3(0.0, 64.0, 1.0);
        assertEquals(expectedIntercept.x, prediction.interceptPosition().x, 1e-5);
        assertEquals(expectedIntercept.y, prediction.interceptPosition().y, 1e-5);
        assertEquals(expectedIntercept.z, prediction.interceptPosition().z, 1e-5);
    }

    @Test
    @DisplayName("Assert angled 3D trajectory calculates exact tau, perp distance, and waypoint")
    public void testAngledTrajectoryCollision() {
        Vec3 ownerPos = new Vec3(10.0, 70.0, 10.0);
        Vec3 projPos = new Vec3(0.0, 70.0, 0.0);
        Vec3 projVel = new Vec3(1.0, 0.0, 1.0); // Velocity (1, 0, 1), vSq = 2.0

        InterceptPrediction prediction = WolfProjectileInterceptHelper.computeOwnerIntercept(
                ownerPos, projPos, projVel, 24.0, 1.25
        );

        assertTrue(prediction.willHitOwner(), "Direct angled projectile must register willHitOwner");
        assertEquals(10.0, prediction.timeToImpactTicks(), 1e-5, "Tau must be exactly 10 ticks");
        assertEquals(0.0, prediction.perpendicularDistance(), 1e-5, "Direct line perp distance must be 0");

        // tau - 1 = 9 ticks -> 0 + 9*1 = 9.0
        assertEquals(9.0, prediction.interceptPosition().x, 1e-5);
        assertEquals(70.0, prediction.interceptPosition().y, 1e-5);
        assertEquals(9.0, prediction.interceptPosition().z, 1e-5);
    }

    @Test
    @DisplayName("Assert near-miss within owner collision cylinder passes cylinder check")
    public void testNearMissWithinOwnerCylinder() {
        Vec3 ownerPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 projPos = new Vec3(0.8, 64.5, 15.0); // Offset 0.8m horizontally, 0.5m vertically
        Vec3 projVel = new Vec3(0.0, 0.0, -1.0);

        InterceptPrediction prediction = WolfProjectileInterceptHelper.computeOwnerIntercept(
                ownerPos, projPos, projVel, 24.0, 1.25
        );

        assertTrue(prediction.willHitOwner(), "Near miss within 1.25m cylinder must hit owner");
        assertEquals(15.0, prediction.timeToImpactTicks(), 1e-5);
        assertEquals(Math.sqrt(0.8 * 0.8 + 0.5 * 0.5), prediction.perpendicularDistance(), 1e-5);
        assertEquals(0.8, prediction.interceptPosition().x, 1e-5);
        assertEquals(64.5, prediction.interceptPosition().y, 1e-5);
        assertEquals(1.0, prediction.interceptPosition().z, 1e-5);
    }

    // =========================================================================
    // 2. Missed Trajectory / Divergent Velocity (Negative Prediction)
    // =========================================================================

    @Test
    @DisplayName("Assert projectile passing outside horizontal radius produces negative prediction")
    public void testMissOutsideHorizontalRadius() {
        Vec3 ownerPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 projPos = new Vec3(2.5, 64.0, 20.0); // 2.5m > 1.25m
        Vec3 projVel = new Vec3(0.0, 0.0, -1.0);

        InterceptPrediction prediction = WolfProjectileInterceptHelper.computeOwnerIntercept(
                ownerPos, projPos, projVel, 24.0, 1.25
        );

        assertFalse(prediction.willHitOwner(), "Passing outside horizontal radius must not hit owner");
        assertEquals(20.0, prediction.timeToImpactTicks(), 1e-5);
        assertEquals(2.5, prediction.perpendicularDistance(), 1e-5);
    }

    @Test
    @DisplayName("Assert projectile passing above owner cylinder produces negative prediction")
    public void testMissAboveOwnerCylinder() {
        Vec3 ownerPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 projPos = new Vec3(0.0, 67.0, 20.0); // dy = 3.0m > 2.0m
        Vec3 projVel = new Vec3(0.0, 0.0, -1.0);

        InterceptPrediction prediction = WolfProjectileInterceptHelper.computeOwnerIntercept(
                ownerPos, projPos, projVel, 24.0, 1.25
        );

        assertFalse(prediction.willHitOwner(), "Passing above cylinder must not hit owner");
        assertEquals(20.0, prediction.timeToImpactTicks(), 1e-5);
        assertEquals(3.0, prediction.perpendicularDistance(), 1e-5);
    }

    @Test
    @DisplayName("Assert projectile passing below owner feet produces negative prediction")
    public void testMissBelowOwnerCylinder() {
        Vec3 ownerPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 projPos = new Vec3(0.0, 62.5, 20.0); // dy = -1.5m < -0.5m
        Vec3 projVel = new Vec3(0.0, 0.0, -1.0);

        InterceptPrediction prediction = WolfProjectileInterceptHelper.computeOwnerIntercept(
                ownerPos, projPos, projVel, 24.0, 1.25
        );

        assertFalse(prediction.willHitOwner(), "Passing below feet must not hit owner");
        assertEquals(20.0, prediction.timeToImpactTicks(), 1e-5);
        assertEquals(1.5, prediction.perpendicularDistance(), 1e-5);
    }

    @Test
    @DisplayName("Assert divergent projectile moving away from owner produces NONE")
    public void testDivergentProjectile() {
        Vec3 ownerPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 projPos = new Vec3(0.0, 64.0, 10.0);
        Vec3 projVel = new Vec3(0.0, 0.0, 1.0); // Moving away (+Z)

        InterceptPrediction prediction = WolfProjectileInterceptHelper.computeOwnerIntercept(
                ownerPos, projPos, projVel, 24.0, 1.25
        );

        assertFalse(prediction.willHitOwner(), "Divergent projectile must not hit owner");
        assertSame(InterceptPrediction.NONE, prediction);
    }

    @Test
    @DisplayName("Assert projectile outside lookahead window produces NONE")
    public void testLookaheadBounds() {
        Vec3 ownerPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 farPos = new Vec3(0.0, 64.0, 30.0); // 30 ticks away, max lookahead 24
        Vec3 nearPos = new Vec3(0.0, 64.0, 2.0); // 2 ticks away, min lookahead 3
        Vec3 projVel = new Vec3(0.0, 0.0, -1.0);

        InterceptPrediction farPrediction = WolfProjectileInterceptHelper.computeOwnerIntercept(
                ownerPos, farPos, projVel, 24.0, 1.25
        );
        InterceptPrediction nearPrediction = WolfProjectileInterceptHelper.computeOwnerIntercept(
                ownerPos, nearPos, projVel, 24.0, 1.25
        );

        assertFalse(farPrediction.willHitOwner());
        assertSame(InterceptPrediction.NONE, farPrediction);

        assertFalse(nearPrediction.willHitOwner());
        assertSame(InterceptPrediction.NONE, nearPrediction);
    }

    // =========================================================================
    // 3. Division by Zero / NaN / Infinity Safety
    // =========================================================================

    @Test
    @DisplayName("Assert division by zero and subnormal velocities safely return NONE")
    public void testZeroAndSubnormalVelocity() {
        Vec3 ownerPos = new Vec3(0.0, 64.0, 0.0);
        Vec3 projPos = new Vec3(0.0, 64.0, 10.0);

        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(
                ownerPos, projPos, Vec3.ZERO, 24.0, 1.25
        ));

        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(
                ownerPos, projPos, new Vec3(1e-7, 0.0, 0.0), 24.0, 1.25
        ));
    }

    @Test
    @DisplayName("Assert null, NaN, and Infinite inputs safely return NONE without throwing")
    public void testNullNaNAndInfinitySafety() {
        Vec3 valid = new Vec3(0.0, 64.0, 0.0);
        Vec3 nanVec = new Vec3(Double.NaN, 64.0, 0.0);
        Vec3 infVec = new Vec3(Double.POSITIVE_INFINITY, 64.0, 0.0);

        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(null, valid, valid));
        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(valid, null, valid));
        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(valid, valid, null));
        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(nanVec, valid, valid));
        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(valid, nanVec, valid));
        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(valid, valid, nanVec));
        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(infVec, valid, valid));
        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(valid, infVec, valid));
        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(valid, valid, infVec));
        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(valid, valid, valid, Double.NaN, 1.25));
        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(valid, valid, valid, 24.0, Double.NaN));
        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(valid, valid, valid, -5.0, 1.25));
        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.computeOwnerIntercept(valid, valid, valid, 24.0, -1.0));

        assertFalse(WolfDispositionHelper.isBallisticInterceptor((Wolf) null));
        assertFalse(WolfDispositionHelper.isBallisticInterceptor((UUID) null, CanineArchetype.GUARDIAN));
        assertSame(InterceptPrediction.NONE, WolfProjectileInterceptHelper.predictOwnerThreat(null, valid, valid, 24.0));
    }

    // =========================================================================
    // 4. Trait Distribution & Deterministic Repeatability
    // =========================================================================

    @Test
    @DisplayName("Assert baseline archetype affinities match Studio specifications")
    public void testArchetypeAffinities() {
        assertEquals(85, WolfDispositionHelper.getBallisticInterceptorAffinity(CanineArchetype.GUARDIAN));
        assertEquals(75, WolfDispositionHelper.getBallisticInterceptorAffinity(CanineArchetype.FEARLESS));
        assertEquals(40, WolfDispositionHelper.getBallisticInterceptorAffinity(CanineArchetype.ADVENTURER));
        assertEquals(20, WolfDispositionHelper.getBallisticInterceptorAffinity(CanineArchetype.PLAYFUL));
        assertEquals(15, WolfDispositionHelper.getBallisticInterceptorAffinity(CanineArchetype.GLUTTON));
        assertEquals(5, WolfDispositionHelper.getBallisticInterceptorAffinity(CanineArchetype.LAZY));
        assertEquals(30, WolfDispositionHelper.getBallisticInterceptorAffinity(null));
    }

    @Test
    @DisplayName("Assert deterministic trait evaluation repeatability for identical UUID")
    public void testTraitRepeatability() {
        UUID testUUID = UUID.fromString("12345678-1234-1234-1234-123456789abc");
        boolean firstRoll = WolfDispositionHelper.isBallisticInterceptor(testUUID, CanineArchetype.GUARDIAN);
        for (int i = 0; i < 100; i++) {
            assertEquals(firstRoll, WolfDispositionHelper.isBallisticInterceptor(testUUID, CanineArchetype.GUARDIAN),
                    "Repeated trait roll must be 100% deterministic");
        }
    }

    @Test
    @DisplayName("Assert statistical distribution across 10,000 deterministic UUID rolls matches affinities")
    public void testStatisticalDistribution() {
        Random rand = new Random(0x494E5452L);
        int sampleSize = 10_000;
        int guardianHits = 0;
        int lazyHits = 0;
        int fearlessHits = 0;
        int playfulHits = 0;

        for (int i = 0; i < sampleSize; i++) {
            UUID uuid = new UUID(rand.nextLong(), rand.nextLong());
            if (WolfDispositionHelper.isBallisticInterceptor(uuid, CanineArchetype.GUARDIAN)) guardianHits++;
            if (WolfDispositionHelper.isBallisticInterceptor(uuid, CanineArchetype.LAZY)) lazyHits++;
            if (WolfDispositionHelper.isBallisticInterceptor(uuid, CanineArchetype.FEARLESS)) fearlessHits++;
            if (WolfDispositionHelper.isBallisticInterceptor(uuid, CanineArchetype.PLAYFUL)) playfulHits++;
        }

        double guardianRate = (double) guardianHits / sampleSize;
        double lazyRate = (double) lazyHits / sampleSize;
        double fearlessRate = (double) fearlessHits / sampleSize;
        double playfulRate = (double) playfulHits / sampleSize;

        assertEquals(0.85, guardianRate, 0.02, "Guardian rate should be ~85%");
        assertEquals(0.05, lazyRate, 0.015, "Lazy rate should be ~5%");
        assertEquals(0.75, fearlessRate, 0.02, "Fearless rate should be ~75%");
        assertEquals(0.20, playfulRate, 0.02, "Playful rate should be ~20%");
    }

    // =========================================================================
    // 5. Concurrency Load Test (16 Concurrent Threads)
    // =========================================================================

    @Test
    @DisplayName("Assert concurrent trajectory queries across 16 threads execute safely")
    public void testConcurrencyLoad() throws InterruptedException, ExecutionException {
        int threadCount = 16;
        int iterationsPerThread = 1_000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        Future<?>[] futures = new Future<?>[threadCount];
        for (int t = 0; t < threadCount; t++) {
            final int seed = t * 100 + 7;
            futures[t] = executor.submit(() -> {
                Random rand = new Random(seed);
                for (int i = 0; i < iterationsPerThread; i++) {
                    Vec3 owner = new Vec3(rand.nextDouble(-50, 50), 64.0, rand.nextDouble(-50, 50));
                    Vec3 proj = new Vec3(rand.nextDouble(-50, 50), 64.0, rand.nextDouble(-50, 50));
                    Vec3 vel = new Vec3(rand.nextDouble(-3, 3), rand.nextDouble(-1, 1), rand.nextDouble(-3, 3));

                    InterceptPrediction p = WolfProjectileInterceptHelper.computeOwnerIntercept(owner, proj, vel);
                    assertNotNull(p);
                    assertFalse(Double.isNaN(p.timeToImpactTicks()));

                    UUID uuid = new UUID(rand.nextLong(), rand.nextLong());
                    WolfDispositionHelper.isBallisticInterceptor(uuid, CanineArchetype.GUARDIAN);
                }
            });
        }

        for (Future<?> f : futures) {
            f.get();
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
}
