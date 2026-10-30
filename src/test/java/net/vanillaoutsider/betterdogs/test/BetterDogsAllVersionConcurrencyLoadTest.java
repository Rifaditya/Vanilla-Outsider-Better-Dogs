// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.test;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.WolfProjectileInterceptHelper;
import net.vanillaoutsider.betterdogs.command.CommandSuggestionsHelper;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.personality.CaninePersonalityHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pillar 2: Concurrency & Multi-Thread Stress Testing (50 Fixed Threads x 1,000 Tasks)")
public class BetterDogsAllVersionConcurrencyLoadTest {

    private static final int THREAD_COUNT = 50;
    private static final int TASKS_PER_THREAD = 1_000;
    private static final int TOTAL_TASKS = THREAD_COUNT * TASKS_PER_THREAD;

    @BeforeAll
    static void initMinecraft() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

    @Test
    @DisplayName("Pillar 2: 50,000 concurrent operations across personality, ballistic intercept, commands, genetics & config")
    void testConcurrencyLoadFixedThreads() throws InterruptedException {
        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger completedTasks = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(TOTAL_TASKS);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        try {
            for (int i = 0; i < TOTAL_TASKS; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    try {
                        // 1. CaninePersonalityHelper.rollFromUUID
                        UUID uuid = new UUID(taskId * 31L + 1L, taskId * 17L + 7L);
                        CanineArchetype archetype = CaninePersonalityHelper.rollFromUUID(uuid, null);
                        if (archetype == null) {
                            throw new IllegalStateException("CanineArchetype returned null for UUID: " + uuid);
                        }

                        // 2. WolfProjectileInterceptHelper.computeOwnerIntercept
                        Vec3 ownerPos = new Vec3(10.0, 64.0, 10.0);
                        Vec3 projPos = new Vec3(10.0 + (taskId % 15) * 0.1, 64.0, 25.0);
                        Vec3 projVel = new Vec3(0.0, 0.0, -1.5);
                        WolfProjectileInterceptHelper.InterceptPrediction pred =
                                WolfProjectileInterceptHelper.computeOwnerIntercept(ownerPos, projPos, projVel, 1.0, 20.0, 1.2);
                        if (pred == null || pred.interceptPosition() == null) {
                            throw new IllegalStateException("WolfProjectileInterceptHelper returned null prediction at task " + taskId);
                        }
                        if (Double.isNaN(pred.interceptPosition().x) || Double.isNaN(pred.interceptPosition().y) || Double.isNaN(pred.interceptPosition().z)) {
                            throw new IllegalStateException("WolfProjectileInterceptHelper returned NaN coords at task " + taskId);
                        }

                        // 3. CommandSuggestionsHelper normalization & type queries
                        String normalized = CommandSuggestionsHelper.normalizeRuleName("cliff_safety");
                        if (!"bd_cliff_safety".equals(normalized)) {
                            throw new IllegalStateException("CommandSuggestionsHelper normalize error: " + normalized);
                        }
                        if (!CommandSuggestionsHelper.isBooleanRule("bd_cliff_safety")) {
                            throw new IllegalStateException("Expected bd_cliff_safety to be boolean rule");
                        }
                        if (!CommandSuggestionsHelper.isIntegerRule("bd_horn_command_range")) {
                            throw new IllegalStateException("Expected bd_horn_command_range to be integer rule");
                        }

                        // 4. CanineGeneticsHelper calculation
                        float childHealth = CanineGeneticsHelper.calculateOffspringStat(
                                20.0f, 30.0f, 2.0f,
                                CanineGeneticsHelper.MIN_HEALTH,
                                CanineGeneticsHelper.MAX_HEALTH,
                                null
                        );
                        if (Float.isNaN(childHealth) || childHealth < CanineGeneticsHelper.MIN_HEALTH || childHealth > CanineGeneticsHelper.MAX_HEALTH) {
                            throw new IllegalStateException("CanineGeneticsHelper child health out of bounds: " + childHealth);
                        }
                        float voicePitch = CanineGeneticsHelper.calculateVoicePitch(1.0f, 1.2f);
                        if (Float.isNaN(voicePitch) || voicePitch < CanineGeneticsHelper.MIN_PITCH || voicePitch > CanineGeneticsHelper.MAX_PITCH) {
                            throw new IllegalStateException("CanineGeneticsHelper voice pitch out of bounds: " + voicePitch);
                        }

                        // 5. BetterDogsConfig validation
                        BetterDogsConfig cfg = new BetterDogsConfig();
                        cfg.wolfMinScale = 0.5 + (taskId % 10) * 0.05;
                        cfg.wolfMaxScale = 1.5 + (taskId % 10) * 0.05;
                        cfg.validate();
                        if (cfg.getWolfMinScale() > cfg.getWolfMaxScale()) {
                            throw new IllegalStateException("BetterDogsConfig scale min > max after validate");
                        }

                        completedTasks.incrementAndGet();
                    } catch (Throwable t) {
                        errors.add(t);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            boolean finished = latch.await(60, TimeUnit.SECONDS);
            assertTrue(finished, "Concurrency load test timed out waiting for 50,000 tasks");
            assertTrue(errors.isEmpty(), "Concurrency load test encountered errors: " + errors);
            assertEquals(TOTAL_TASKS, completedTasks.get(), "All 50,000 tasks must complete successfully");
        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
