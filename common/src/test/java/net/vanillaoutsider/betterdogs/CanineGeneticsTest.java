// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.11
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import net.minecraft.util.RandomSource;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CanineGeneticsTest {

    @Test
    @DisplayName("Pillar 1 (Chaos Fuzzing): 50,000 Generations Saturated Breeding Bounds")
    public void testChaosFuzzingGenerations() {
        RandomSource rand = RandomSource.create(42L);

        float p1Health = 20.0f;
        float p2Health = 30.0f;
        float p1Speed = 0.30f;
        float p2Speed = 0.35f;
        float p1Scale = 1.0f;
        float p2Scale = 1.1f;

        for (int gen = 0; gen < 50_000; gen++) {
            float childHealth = CanineGeneticsHelper.calculateOffspringStat(
                    p1Health, p2Health,
                    CanineGeneticsHelper.HEALTH_STD_DEV,
                    CanineGeneticsHelper.MIN_HEALTH,
                    CanineGeneticsHelper.MAX_HEALTH,
                    rand
            );

            float childSpeed = CanineGeneticsHelper.calculateOffspringStat(
                    p1Speed, p2Speed,
                    CanineGeneticsHelper.SPEED_STD_DEV,
                    CanineGeneticsHelper.MIN_SPEED,
                    CanineGeneticsHelper.MAX_SPEED,
                    rand
            );

            float childScale = CanineGeneticsHelper.calculateOffspringStat(
                    p1Scale, p2Scale,
                    CanineGeneticsHelper.SCALE_STD_DEV,
                    CanineGeneticsHelper.MIN_SCALE,
                    CanineGeneticsHelper.MAX_SCALE,
                    rand
            );

            Assertions.assertTrue(childHealth >= CanineGeneticsHelper.MIN_HEALTH && childHealth <= CanineGeneticsHelper.MAX_HEALTH,
                    "Generation " + gen + ": Health out of bounds: " + childHealth);
            Assertions.assertTrue(childSpeed >= CanineGeneticsHelper.MIN_SPEED && childSpeed <= CanineGeneticsHelper.MAX_SPEED,
                    "Generation " + gen + ": Speed out of bounds: " + childSpeed);
            Assertions.assertTrue(childScale >= CanineGeneticsHelper.MIN_SCALE && childScale <= CanineGeneticsHelper.MAX_SCALE,
                    "Generation " + gen + ": Scale out of bounds: " + childScale);

            // Shift parents for continuous generational drift
            p1Health = p2Health;
            p2Health = childHealth;
            p1Speed = p2Speed;
            p2Speed = childSpeed;
            p1Scale = p2Scale;
            p2Scale = childScale;
        }

        // Extreme edge cases / overflow / underflow input fuzzing
        float[] extremeInputs = {-1000.0f, 0.0f, 10000.0f, Float.MAX_VALUE / 2};
        for (float ext1 : extremeInputs) {
            for (float ext2 : extremeInputs) {
                float h = CanineGeneticsHelper.calculateOffspringStat(ext1, ext2, CanineGeneticsHelper.HEALTH_STD_DEV, CanineGeneticsHelper.MIN_HEALTH, CanineGeneticsHelper.MAX_HEALTH, rand);
                float s = CanineGeneticsHelper.calculateOffspringStat(ext1, ext2, CanineGeneticsHelper.SPEED_STD_DEV, CanineGeneticsHelper.MIN_SPEED, CanineGeneticsHelper.MAX_SPEED, rand);
                float sc = CanineGeneticsHelper.calculateOffspringStat(ext1, ext2, CanineGeneticsHelper.SCALE_STD_DEV, CanineGeneticsHelper.MIN_SCALE, CanineGeneticsHelper.MAX_SCALE, rand);

                Assertions.assertTrue(h >= CanineGeneticsHelper.MIN_HEALTH && h <= CanineGeneticsHelper.MAX_HEALTH);
                Assertions.assertTrue(s >= CanineGeneticsHelper.MIN_SPEED && s <= CanineGeneticsHelper.MAX_SPEED);
                Assertions.assertTrue(sc >= CanineGeneticsHelper.MIN_SCALE && sc <= CanineGeneticsHelper.MAX_SCALE);
            }
        }
    }

    @Test
    @DisplayName("Pillar 2 (Concurrency): 50 Threads Concurrent Breeding")
    public void testConcurrentBreedingVirtualThreads() throws InterruptedException, ExecutionException {
        int threadCount = 50;
        int iterationsPerThread = 1_000;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        try {
            List<Callable<Boolean>> tasks = new ArrayList<>();

            for (int t = 0; t < threadCount; t++) {
                final long seed = 1000L + t;
                tasks.add(() -> {
                    RandomSource rand = RandomSource.create(seed);
                    for (int i = 0; i < iterationsPerThread; i++) {
                        float health = CanineGeneticsHelper.calculateOffspringStat(15.0f, 45.0f, CanineGeneticsHelper.HEALTH_STD_DEV, CanineGeneticsHelper.MIN_HEALTH, CanineGeneticsHelper.MAX_HEALTH, rand);
                        float speed = CanineGeneticsHelper.calculateOffspringStat(0.20f, 0.40f, CanineGeneticsHelper.SPEED_STD_DEV, CanineGeneticsHelper.MIN_SPEED, CanineGeneticsHelper.MAX_SPEED, rand);
                        float scale = CanineGeneticsHelper.calculateOffspringStat(0.80f, 1.20f, CanineGeneticsHelper.SCALE_STD_DEV, CanineGeneticsHelper.MIN_SCALE, CanineGeneticsHelper.MAX_SCALE, rand);
                        float pitch = CanineGeneticsHelper.calculateVoicePitch(1.0f, scale);

                        if (health < CanineGeneticsHelper.MIN_HEALTH || health > CanineGeneticsHelper.MAX_HEALTH) return false;
                        if (speed < CanineGeneticsHelper.MIN_SPEED || speed > CanineGeneticsHelper.MAX_SPEED) return false;
                        if (scale < CanineGeneticsHelper.MIN_SCALE || scale > CanineGeneticsHelper.MAX_SCALE) return false;
                        if (pitch < CanineGeneticsHelper.MIN_PITCH || pitch > CanineGeneticsHelper.MAX_PITCH) return false;
                    }
                    return true;
                });
            }

            List<Future<Boolean>> results = executor.invokeAll(tasks);
            for (Future<Boolean> future : results) {
                Assertions.assertTrue(future.get(), "Concurrent breeding task failed bounds assertion");
            }
        } finally {
            executor.shutdown();
        }
    }

    @Test
    @DisplayName("Acoustic Invariant: Voice Pitch Modulates Inversely to Scale")
    public void testAcousticScaleInvariants() {
        float basePitch = 1.0f;

        float pitchSmall = CanineGeneticsHelper.calculateVoicePitch(basePitch, 0.70f);
        float pitchLarge = CanineGeneticsHelper.calculateVoicePitch(basePitch, 1.30f);
        float pitchNormal = CanineGeneticsHelper.calculateVoicePitch(basePitch, 1.00f);

        // Scale 1.30 -> lower pitch (< 1.0)
        Assertions.assertTrue(pitchLarge < 1.0f, "Scale 1.30 must produce pitch < 1.0, got: " + pitchLarge);
        // Scale 0.70 -> higher pitch (> 1.0)
        Assertions.assertTrue(pitchSmall > 1.0f, "Scale 0.70 must produce pitch > 1.0, got: " + pitchSmall);
        // Scale 1.00 -> base pitch (== 1.0)
        Assertions.assertEquals(1.0f, pitchNormal, 1e-4f, "Scale 1.00 must preserve base pitch");

        // Extreme bounds clamping check
        float pitchExtremeLarge = CanineGeneticsHelper.calculateVoicePitch(basePitch, 50.0f);
        float pitchExtremeSmall = CanineGeneticsHelper.calculateVoicePitch(basePitch, 0.001f);
        Assertions.assertEquals(CanineGeneticsHelper.MIN_PITCH, pitchExtremeLarge, 1e-4f, "Extreme scale must clamp to MIN_PITCH");
        Assertions.assertEquals(CanineGeneticsHelper.MAX_PITCH, pitchExtremeSmall, 1e-4f, "Extreme small scale must clamp to MAX_PITCH");

        // Null wolf safety test
        float nullPitch = CanineGeneticsHelper.getModulatedVoicePitch(null, basePitch);
        Assertions.assertEquals(basePitch, nullPitch, 1e-4f, "Null wolf must fallback safely to base pitch");
    }

    @Test
    @DisplayName("Pillar 5 (Sidedness Sweep): 0 Client Imports in net.vanillaoutsider.betterdogs.genetics")
    public void testSidednessZeroClientImports() throws IOException {
        Path geneticsDir = Paths.get("src/main/java/net/vanillaoutsider/betterdogs/genetics");
        Assertions.assertTrue(Files.exists(geneticsDir), "Genetics package source directory must exist");

        try (Stream<Path> stream = Files.walk(geneticsDir)) {
            List<Path> javaFiles = stream.filter(p -> p.toString().endsWith(".java")).toList();
            Assertions.assertFalse(javaFiles.isEmpty(), "Genetics source files must be present");

            for (Path javaFile : javaFiles) {
                List<String> lines = Files.readAllLines(javaFile);
                for (int lineNum = 0; lineNum < lines.size(); lineNum++) {
                    String line = lines.get(lineNum);
                    Assertions.assertFalse(line.contains("net.minecraft.client"),
                            "Side-Safety Violation: Client import detected in " + javaFile.getFileName() + " at line " + (lineNum + 1) + ": " + line);
                }
            }
        }
    }
}
