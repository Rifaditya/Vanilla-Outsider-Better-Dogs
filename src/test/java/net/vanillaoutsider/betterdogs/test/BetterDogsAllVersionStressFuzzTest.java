// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.test;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.WolfProjectileInterceptHelper;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.personality.CaninePersonalityHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BetterDogsAllVersionStressFuzzTest: Pillar 1 Stress & Chaos Fuzz Testing across all 6 version anchors.
 *
 * Runs 25,000 deterministic seeded iterations (0xDA51CBD18L) subjecting:
 * - BetterDogsConfig to adversarial min/max scale values
 * - CanineGeneticsHelper to extreme stat calculation and voice pitch modulation
 * - CaninePersonalityHelper to arbitrary UUID archetype distribution
 * - WolfProjectileInterceptHelper to 3D trajectory intercept predictions
 */
public class BetterDogsAllVersionStressFuzzTest {

    private static final long SEED = 0xDA51CBD18L;
    private static final int ITERATIONS = 25_000;

    @BeforeAll
    static void setup() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

    private static double generateAdversarialDouble(Random rand) {
        int choice = rand.nextInt(12);
        return switch (choice) {
            case 0 -> Double.NaN;
            case 1 -> Double.POSITIVE_INFINITY;
            case 2 -> Double.NEGATIVE_INFINITY;
            case 3 -> 0.0;
            case 4 -> -0.0;
            case 5 -> Double.MAX_VALUE;
            case 6 -> -Double.MAX_VALUE;
            case 7 -> Double.MIN_VALUE;
            case 8 -> -1.0;
            case 9 -> rand.nextDouble() * 100.0 - 50.0;
            case 10 -> rand.nextDouble() * 2.0;
            default -> rand.nextGaussian() * 10.0;
        };
    }

    private static float generateAdversarialFloat(Random rand) {
        int choice = rand.nextInt(12);
        return switch (choice) {
            case 0 -> Float.NaN;
            case 1 -> Float.POSITIVE_INFINITY;
            case 2 -> Float.NEGATIVE_INFINITY;
            case 3 -> 0.0f;
            case 4 -> -0.0f;
            case 5 -> Float.MAX_VALUE;
            case 6 -> -Float.MAX_VALUE;
            case 7 -> Float.MIN_VALUE;
            case 8 -> -1.0f;
            case 9 -> (float) (rand.nextDouble() * 100.0 - 50.0);
            case 10 -> (float) (rand.nextDouble() * 2.0);
            default -> (float) (rand.nextGaussian() * 10.0);
        };
    }

    private static Vec3 generateAdversarialVec3(Random rand, int step) {
        if (step % 20 == 0) {
            return null;
        }
        double x = generateAdversarialDouble(rand);
        double y = generateAdversarialDouble(rand);
        double z = generateAdversarialDouble(rand);
        return new Vec3(x, y, z);
    }

    @Test
    @DisplayName("Pillar 1: 25,000-iteration deterministic seeded chaos fuzzing across all submodules")
    void testChaosStressFuzzSuite() {
        Random rand = new Random(SEED);
        RandomSource mcRand = RandomSource.create(SEED);
        BetterDogsConfig config = new BetterDogsConfig();

        for (int i = 0; i < ITERATIONS; i++) {
            // 1. Config adversarial min/max scale fuzzing
            config.wolfMinScale = generateAdversarialDouble(rand);
            config.wolfMaxScale = generateAdversarialDouble(rand);
            config.validate();

            assertFalse(Double.isNaN(config.wolfMinScale), "wolfMinScale must not be NaN at iteration " + i);
            assertFalse(Double.isNaN(config.wolfMaxScale), "wolfMaxScale must not be NaN at iteration " + i);
            assertTrue(config.wolfMinScale >= 0.01, "wolfMinScale must be >= 0.01 at iteration " + i);
            assertTrue(config.wolfMaxScale >= 0.01, "wolfMaxScale must be >= 0.01 at iteration " + i);
            assertTrue(config.wolfMinScale <= config.wolfMaxScale, "wolfMinScale must be <= wolfMaxScale at iteration " + i);

            // 2. CanineGeneticsHelper.calculateOffspringStat
            float p1 = generateAdversarialFloat(rand);
            float p2 = generateAdversarialFloat(rand);
            float stdDev = generateAdversarialFloat(rand);
            float childStat = CanineGeneticsHelper.calculateOffspringStat(
                    p1, p2, stdDev,
                    CanineGeneticsHelper.MIN_HEALTH,
                    CanineGeneticsHelper.MAX_HEALTH,
                    (i % 10 == 0) ? null : mcRand
            );
            assertFalse(Float.isNaN(childStat), "childStat must not be NaN at iteration " + i);
            assertTrue(childStat >= CanineGeneticsHelper.MIN_HEALTH && childStat <= CanineGeneticsHelper.MAX_HEALTH,
                    "childStat out of bounds at iteration " + i + ": " + childStat);

            // 3. CanineGeneticsHelper.calculateVoicePitch
            float basePitch = generateAdversarialFloat(rand);
            float scale = generateAdversarialFloat(rand);
            float pitch = CanineGeneticsHelper.calculateVoicePitch(basePitch, scale);
            assertFalse(Float.isNaN(pitch), "voicePitch must not be NaN at iteration " + i);
            assertTrue(pitch >= CanineGeneticsHelper.MIN_PITCH && pitch <= CanineGeneticsHelper.MAX_PITCH,
                    "voicePitch out of bounds at iteration " + i + ": " + pitch);

            // 4. CaninePersonalityHelper.rollFromUUID
            UUID uuid = (i % 50 == 0) ? null : new UUID(rand.nextLong(), rand.nextLong());
            CanineArchetype archetype = CaninePersonalityHelper.rollFromUUID(uuid, null);
            assertNotNull(archetype, "CanineArchetype must not be null at iteration " + i);

            // 5. WolfProjectileInterceptHelper.computeOwnerIntercept
            Vec3 ownerPos = generateAdversarialVec3(rand, i);
            Vec3 projPos = generateAdversarialVec3(rand, i + 1);
            Vec3 projVel = generateAdversarialVec3(rand, i + 2);
            double minLook = generateAdversarialDouble(rand);
            double maxLook = generateAdversarialDouble(rand);
            double colRadius = generateAdversarialDouble(rand);

            WolfProjectileInterceptHelper.InterceptPrediction pred = WolfProjectileInterceptHelper.computeOwnerIntercept(
                    ownerPos, projPos, projVel, minLook, maxLook, colRadius
            );
            assertNotNull(pred, "InterceptPrediction must not be null at iteration " + i);
            assertNotNull(pred.interceptPosition(), "interceptPosition must not be null at iteration " + i);
            assertFalse(Double.isNaN(pred.interceptPosition().x), "interceptPosition.x is NaN at iteration " + i);
            assertFalse(Double.isNaN(pred.interceptPosition().y), "interceptPosition.y is NaN at iteration " + i);
            assertFalse(Double.isNaN(pred.interceptPosition().z), "interceptPosition.z is NaN at iteration " + i);
            assertFalse(Double.isNaN(pred.timeToImpactTicks()), "timeToImpactTicks is NaN at iteration " + i);
            assertFalse(Double.isNaN(pred.perpendicularDistance()), "perpendicularDistance is NaN at iteration " + i);
        }
    }
}
