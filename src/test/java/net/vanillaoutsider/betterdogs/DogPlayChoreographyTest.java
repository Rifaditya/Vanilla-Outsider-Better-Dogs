// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.play.OrbitSparringGoal;
import net.vanillaoutsider.betterdogs.ai.play.PlayChaseGoal;
import net.vanillaoutsider.betterdogs.ai.play.PlayChoreographyHelper;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 4: Dog Play Choreography 5-Pillar Test Suite [BL-BD-010]")
public class DogPlayChoreographyTest {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BetterDogsGameRules.register();
    }

    @Test
    @DisplayName("Pillar 1: Chaos Fuzzing - 20,000 iterations of extreme/random vector math, radius bounds, and target positions")
    void testPillar1ChaosFuzzing() {
        Random rand = new Random(42L);
        double[] testValues = new double[] {
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            0.0,
            -0.0,
            1.0,
            -1.0,
            1e-7,
            -1e-7,
            1e12,
            -1e12,
            Double.MAX_VALUE,
            -Double.MAX_VALUE,
            Double.MIN_VALUE,
            -Double.MIN_VALUE
        };

        for (int i = 0; i < 20_000; i++) {
            double cx = (i < testValues.length) ? testValues[i] : (rand.nextDouble() * 2e7 - 1e7);
            double cz = (i < testValues.length) ? testValues[testValues.length - 1 - i] : (rand.nextDouble() * 2e7 - 1e7);
            double px = rand.nextDouble() * 2e7 - 1e7;
            double py = rand.nextDouble() * 512.0 - 128.0;
            double pz = rand.nextDouble() * 2e7 - 1e7;
            double angle = (rand.nextInt(10) == 0) ? testValues[rand.nextInt(testValues.length)] : (rand.nextDouble() * 200.0 - 100.0);
            double radius = (rand.nextInt(10) == 0) ? testValues[rand.nextInt(testValues.length)] : (rand.nextDouble() * 2000.0 - 500.0);

            Vec3 center = new Vec3(cx, 64.0, cz);
            Vec3 current = new Vec3(px, py, pz);

            Vec3 result = PlayChoreographyHelper.calculateOrbitVector(center, current, angle, radius);

            assertNotNull(result, "Resulting orbit vector must never be null");
            assertFalse(Double.isNaN(result.x), "Resulting orbit X must not be NaN at iteration " + i);
            assertFalse(Double.isNaN(result.y), "Resulting orbit Y must not be NaN at iteration " + i);
            assertFalse(Double.isNaN(result.z), "Resulting orbit Z must not be NaN at iteration " + i);
            assertFalse(Double.isInfinite(result.x), "Resulting orbit X must not be infinite at iteration " + i);
            assertFalse(Double.isInfinite(result.y), "Resulting orbit Y must not be infinite at iteration " + i);
            assertFalse(Double.isInfinite(result.z), "Resulting orbit Z must not be infinite at iteration " + i);

            assertTrue(Math.abs(result.x) <= PlayChoreographyHelper.MAX_COORDINATE_BOUND,
                    "Resulting X must be bounded within world coordinates");
            assertTrue(Math.abs(result.z) <= PlayChoreographyHelper.MAX_COORDINATE_BOUND,
                    "Resulting Z must be bounded within world coordinates");

            // Sanitize fuzzer check
            double sanitizedCoord = PlayChoreographyHelper.sanitizeCoordinate(cx);
            assertFalse(Double.isNaN(sanitizedCoord));
            assertFalse(Double.isInfinite(sanitizedCoord));

            double sanitizedRad = PlayChoreographyHelper.sanitizeRadius(radius);
            assertFalse(Double.isNaN(sanitizedRad));
            assertFalse(Double.isInfinite(sanitizedRad));
            assertTrue(sanitizedRad >= 0.0 && sanitizedRad <= PlayChoreographyHelper.MAX_ORBIT_RADIUS);

            double sanitizedAng = PlayChoreographyHelper.sanitizeAngle(angle);
            assertFalse(Double.isNaN(sanitizedAng));
            assertFalse(Double.isInfinite(sanitizedAng));

            // Drop safety evaluation check
            int dropHeight = rand.nextInt();
            boolean safeDrop = PlayChoreographyHelper.isSafeDrop(dropHeight);
            assertEquals(dropHeight <= PlayChoreographyHelper.MAX_SAFE_DROP_BLOCKS, safeDrop);
        }
    }

    @Test
    @DisplayName("Pillar 2: Concurrency Load - Multi-threaded execution across 50 simulated dogs/sessions checking state consistency")
    void testPillar2ConcurrencyLoad() throws Exception {
        int simulatedDogs = 50;
        int operationsPerDog = 500;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(simulatedDogs);
        AtomicInteger successCount = new AtomicInteger(0);
        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        for (int dogIndex = 0; dogIndex < simulatedDogs; dogIndex++) {
            final int id = dogIndex;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    Vec3 center = new Vec3(100.0 + id * 10.0, 64.0, 100.0 + id * 10.0);
                    Vec3 current = new Vec3(center.x + 3.0, 64.0, center.z);

                    for (int op = 0; op < operationsPerDog; op++) {
                        double angle = 0.25;
                        double radius = 3.0;
                        current = PlayChoreographyHelper.calculateOrbitVector(center, current, angle, radius);

                        assertNotNull(current);
                        assertFalse(Double.isNaN(current.x));
                        assertFalse(Double.isNaN(current.z));

                        // Simulate evaluation of chase abort matrix
                        boolean abort = PlayChaseGoal.shouldAbortChase(
                                false, true, false, false, false, false,
                                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
                        );
                        assertFalse(abort);

                        // Simulate evaluation of orbit abort matrix
                        boolean orbitAbort = OrbitSparringGoal.shouldAbortOrbit(
                                false, true, false, false, false, false,
                                20.0f, 20.0f, 20.0f, 20.0f, 9.0, 256.0
                        );
                        assertFalse(orbitAbort);

                        // Simulate eligibility matrix
                        boolean eligible = PlayChoreographyHelper.canInitiatePlay(
                                true, true, false, false, false,
                                20.0f, 20.0f, false, false, true,
                                true, true, false, 25.0, 16.0
                        );
                        assertTrue(eligible);
                    }
                    successCount.incrementAndGet();
                } catch (Throwable t) {
                    errors.add(t);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
        assertTrue(completed, "All 50 simulated dog choreography sessions must complete within 30 seconds");
        assertTrue(errors.isEmpty(), "No concurrency errors or state corruption occurred: " + errors);
        assertEquals(simulatedDogs, successCount.get(), "All 50 sessions must succeed");
    }

    @Test
    @DisplayName("Pillar 3: Brigadier AST - Verify DynamicGameRuleManager registers play choreography GameRules and aliases")
    void testPillar3BrigadierAst() {
        // Direct GameRule registrations
        assertNotNull(BetterDogsGameRules.BD_DOG_PLAY_CHOREOGRAPHY_ENABLED,
                "BD_DOG_PLAY_CHOREOGRAPHY_ENABLED must be non-null");
        assertNotNull(BetterDogsGameRules.BD_PLAY_CHOREOGRAPHY_FREQUENCY,
                "BD_PLAY_CHOREOGRAPHY_FREQUENCY must be non-null");
        assertNotNull(BetterDogsGameRules.BD_MAX_PLAY_DISTANCE,
                "BD_MAX_PLAY_DISTANCE must be non-null");

        // DynamicGameRuleManager internal map registrations
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_dog_play_choreography_enabled"),
                "DynamicGameRuleManager must contain betterdogs:bd_dog_play_choreography_enabled");
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_play_choreography_frequency"),
                "DynamicGameRuleManager must contain betterdogs:bd_play_choreography_frequency");
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_max_play_distance"),
                "DynamicGameRuleManager must contain betterdogs:bd_max_play_distance");

        // CamelCase Aliases
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:dogPlayChoreographyEnabled"),
                "DynamicGameRuleManager must contain alias betterdogs:dogPlayChoreographyEnabled");
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:playChoreographyFrequency"),
                "DynamicGameRuleManager must contain alias betterdogs:playChoreographyFrequency");
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:maxPlayDistance"),
                "DynamicGameRuleManager must contain alias betterdogs:maxPlayDistance");

        // Default values and bounds
        assertTrue(BetterDogsGameRules.isDogPlayChoreographyEnabled(null), "Default dogPlayChoreographyEnabled must be true");
        assertEquals(2400, BetterDogsGameRules.getPlayChoreographyFrequency(null), "Default playChoreographyFrequency must be 2400");
        assertEquals(16, BetterDogsGameRules.getMaxPlayDistance(null), "Default maxPlayDistance must be 16");
    }

    @Test
    @DisplayName("Pillar 4: GameTest / Headless Contract - Verify goal lifecycle, flags, and interrupt contracts")
    void testPillar4LifecycleAndInterruptContracts() {
        // Goal Flags contract
        PlayChaseGoal chaseGoal = new PlayChaseGoal(null);
        EnumSet<Goal.Flag> chaseFlags = chaseGoal.getFlags();
        assertTrue(chaseFlags.contains(Goal.Flag.MOVE), "PlayChaseGoal must set Goal.Flag.MOVE");
        assertTrue(chaseFlags.contains(Goal.Flag.LOOK), "PlayChaseGoal must set Goal.Flag.LOOK");

        OrbitSparringGoal orbitGoal = new OrbitSparringGoal(null);
        EnumSet<Goal.Flag> orbitFlags = orbitGoal.getFlags();
        assertTrue(orbitFlags.contains(Goal.Flag.MOVE), "OrbitSparringGoal must set Goal.Flag.MOVE");
        assertTrue(orbitFlags.contains(Goal.Flag.LOOK), "OrbitSparringGoal must set Goal.Flag.LOOK");

        // Null-safety & headless contracts
        assertFalse(chaseGoal.canUse());
        assertFalse(chaseGoal.canContinueToUse());
        assertDoesNotThrow(chaseGoal::stop);

        assertFalse(orbitGoal.canUse());
        assertFalse(orbitGoal.canContinueToUse());
        assertDoesNotThrow(orbitGoal::stop);

        // Chase abort contract: combat interrupt (wolf target or partner target)
        assertTrue(PlayChaseGoal.shouldAbortChase(false, true, false, false, true, false, 20f, 20f, 20f, 20f, 9.0, 256.0),
                "Chaser acquiring a combat target must immediately abort play chase");
        assertTrue(PlayChaseGoal.shouldAbortChase(false, true, false, false, false, true, 20f, 20f, 20f, 20f, 9.0, 256.0),
                "Partner acquiring a combat target must immediately abort play chase");

        // Chase abort contract: low-health interrupt (< 70% max health)
        assertTrue(PlayChaseGoal.shouldAbortChase(false, true, false, false, false, false, 13.9f, 20.0f, 20.0f, 20.0f, 9.0, 256.0),
                "Chaser taking damage below 70% HP must immediately abort play chase");
        assertTrue(PlayChaseGoal.shouldAbortChase(false, true, false, false, false, false, 20.0f, 20.0f, 13.9f, 20.0f, 9.0, 256.0),
                "Partner taking damage below 70% HP must immediately abort play chase");

        // Chase abort contract: sitting interrupt
        assertTrue(PlayChaseGoal.shouldAbortChase(false, true, false, true, false, false, 20f, 20f, 20f, 20f, 9.0, 256.0),
                "Chaser sitting must immediately abort play chase");
        assertTrue(PlayChaseGoal.shouldAbortChase(false, true, true, false, false, false, 20f, 20f, 20f, 20f, 9.0, 256.0),
                "Partner sitting must immediately abort play chase");

        // Chase abort contract: distance exceedance
        assertTrue(PlayChaseGoal.shouldAbortChase(false, true, false, false, false, false, 20f, 20f, 20f, 20f, 257.0, 256.0),
                "Exceeding max play distance must immediately abort play chase");

        // Orbit abort contract: combat, health, sitting, distance
        assertTrue(OrbitSparringGoal.shouldAbortOrbit(false, true, false, false, true, false, 20f, 20f, 20f, 20f, 9.0, 256.0),
                "Combat target must immediately abort sparring orbit");
        assertTrue(OrbitSparringGoal.shouldAbortOrbit(false, true, false, false, false, false, 13.9f, 20.0f, 20.0f, 20.0f, 9.0, 256.0),
                "Health drop below 70% must immediately abort sparring orbit");
        assertTrue(OrbitSparringGoal.shouldAbortOrbit(false, true, true, false, false, false, 20f, 20f, 20f, 20f, 9.0, 256.0),
                "Sitting partner must immediately abort sparring orbit");
        assertTrue(OrbitSparringGoal.shouldAbortOrbit(false, true, false, false, false, false, 20f, 20f, 20f, 20f, 257.0, 256.0),
                "Distance exceedance must immediately abort sparring orbit");

        // Timeout contracts
        assertEquals(240, PlayChaseGoal.MAX_CHASE_TICKS);
        assertEquals(200, OrbitSparringGoal.MAX_ORBIT_TICKS);
    }

    @Test
    @DisplayName("Pillar 5: Sidedness & Hygiene - Verify 0 client imports in play package and single-line GPLv3 headers")
    void testPillar5SidednessAndHygiene() throws IOException {
        Path baseDir = Path.of(System.getProperty("user.dir", "."));
        Path playPackagePath = baseDir.resolve("src/main/java/net/vanillaoutsider/betterdogs/ai/play");
        assertTrue(Files.exists(playPackagePath), "Play package path must exist: " + playPackagePath);

        String expectedHeader = "// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3";

        try (Stream<Path> paths = Files.walk(playPackagePath)) {
            List<Path> javaFiles = paths.filter(p -> p.toString().endsWith(".java")).toList();
            assertFalse(javaFiles.isEmpty(), "Play package must contain java source files");

            for (Path javaFile : javaFiles) {
                List<String> lines = Files.readAllLines(javaFile);
                assertFalse(lines.isEmpty(), javaFile.getFileName() + " must not be empty");

                // Check single-line GPLv3 header on line 1
                assertEquals(expectedHeader, lines.get(0).trim(),
                        "File " + javaFile.getFileName() + " must start with exact license header");

                // Check 0 client imports
                for (int lineNum = 0; lineNum < lines.size(); lineNum++) {
                    String line = lines.get(lineNum);
                    assertFalse(line.contains("import net.minecraft.client."),
                            "Forbidden client import in common play package at " + javaFile.getFileName() + ":" + (lineNum + 1));
                    assertFalse(line.contains("net/minecraft/client/"),
                            "Forbidden client bytecode descriptor reference in common play package at " + javaFile.getFileName() + ":" + (lineNum + 1));
                }
            }
        }
    }
}
