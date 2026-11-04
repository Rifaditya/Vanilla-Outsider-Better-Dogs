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
        Vec3 projVel = new Vec3(0.0, 0.0, -1.0); // Will take 50 ticks, exceeds 20 ticks reaction

        TrajectoryInterception result = FireballTrajectoryHelper.computeInterception(
                dogPos, projPos, projVel, 20.0, 1.75, 85.0
        );

        assertFalse(result.intercepted(), "Trajectory beyond reactionTicks window must not be intercepted");
        assertSame(TrajectoryInterception.NONE, result);
    }

    @Test
    @DisplayName("Assert wide pass exceeding threat radius produces no interception")
    public void testWidePassExceedsThreatRadius() {
        Vec3 dogPos = new Vec3(5.0, 64.0, 0.0); // 5 blocks away laterally
        Vec3 projPos = new Vec3(0.0, 64.0, 20.0);
        Vec3 projVel = new Vec3(0.0, 0.0, -1.0);

        TrajectoryInterception result = FireballTrajectoryHelper.computeInterception(
                dogPos, projPos, projVel, 25.0, 1.75, 85.0
        );

        assertFalse(result.intercepted(), "Wide projectile passing beyond threatRadius must not be intercepted");
        assertSame(TrajectoryInterception.NONE, result);
    }

    // =========================================================================
    // Pillar 1: Robustness Math / Fuzz Invariants (10,000 Iterations)
    // =========================================================================

    @Test
    @DisplayName("Pillar 1: Robustness Math / Fuzz Invariants (10,000 random vectors, NaN/Infinity safety)")
    public void testPillar1RobustnessMathFuzz() {
        Random rand = new Random(1337);

        // Sub-test A: Extreme & Invalid Floats
        double[] invalidValues = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        for (double val : invalidValues) {
            assertSame(TrajectoryInterception.NONE,
                    FireballTrajectoryHelper.computeInterception(
                            new Vec3(val, 64, 0), new Vec3(0, 64, 10), new Vec3(0, 0, -1), 20.0, 1.75, 85.0));
            assertSame(TrajectoryInterception.NONE,
                    FireballTrajectoryHelper.computeInterception(
                            new Vec3(0, 64, 0), new Vec3(val, 64, 10), new Vec3(0, 0, -1), 20.0, 1.75, 85.0));
            assertSame(TrajectoryInterception.NONE,
                    FireballTrajectoryHelper.computeInterception(
                            new Vec3(0, 64, 0), new Vec3(0, 64, 10), new Vec3(0, val, -1), 20.0, 1.75, 85.0));
            assertSame(TrajectoryInterception.NONE,
                    FireballTrajectoryHelper.computeInterception(
                            new Vec3(0, 64, 0), new Vec3(0, 64, 10), new Vec3(0, 0, -1), val, 1.75, 85.0));
        }

        // Sub-test B: Null Safety
        assertSame(TrajectoryInterception.NONE,
                FireballTrajectoryHelper.computeInterception(null, new Vec3(0, 0, 0), new Vec3(0, 0, 0), 20.0, 1.75, 85.0));
        assertSame(TrajectoryInterception.NONE,
                FireballTrajectoryHelper.computeInterception(new Vec3(0, 0, 0), null, new Vec3(0, 0, 0), 20.0, 1.75, 85.0));
        assertSame(TrajectoryInterception.NONE,
                FireballTrajectoryHelper.computeInterception(new Vec3(0, 0, 0), new Vec3(0, 0, 0), null, 20.0, 1.75, 85.0));

        // Sub-test C: 10,000 Random Vector Fuzz Loop
        for (int i = 0; i < 10_000; i++) {
            Vec3 dogPos = new Vec3(rand.nextDouble(-100, 100), rand.nextDouble(-50, 150), rand.nextDouble(-100, 100));
            Vec3 projPos = new Vec3(rand.nextDouble(-100, 100), rand.nextDouble(-50, 150), rand.nextDouble(-100, 100));
            Vec3 projVel = new Vec3(rand.nextDouble(-5, 5), rand.nextDouble(-5, 5), rand.nextDouble(-5, 5));

            double reactionTicks = rand.nextDouble(1, 40);
            double threatRadius = rand.nextDouble(0.5, 5.0);
            double lateralImpulse = rand.nextDouble(10, 150);

            TrajectoryInterception result = FireballTrajectoryHelper.computeInterception(
                    dogPos, projPos, projVel, reactionTicks, threatRadius, lateralImpulse
            );

            assertNotNull(result, "TrajectoryInterception must never be null");
            assertFalse(Double.isNaN(result.timeToImpactTicks()), "timeToImpactTicks must not be NaN at iteration " + i);
            assertFalse(Double.isNaN(result.perpendicularDistance()), "perpendicularDistance must not be NaN at iteration " + i);

            Vec3 impulse = result.dodgeImpulse();
            assertNotNull(impulse, "Dodge impulse vector must never be null");
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
