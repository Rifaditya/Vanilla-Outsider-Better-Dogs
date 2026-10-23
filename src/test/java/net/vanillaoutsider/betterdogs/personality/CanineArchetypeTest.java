// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.personality;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 5-Pillar Headless Unit Test Suite for Extensible Canine Personality Archetypes.
 * Enforces chaos property fuzzing, concurrency resilience, and Knot side-safety.
 */
public class CanineArchetypeTest {

    @Test
    @DisplayName("Contract: Archetype Enum Definition, Fallbacks, and Stat Multipliers")
    public void testArchetypeEnumIntegrity() {
        Assertions.assertEquals(6, CanineArchetype.values().length, "There must be exactly 6 canine personality archetypes");

        // Verify IDs and Fallbacks
        Assertions.assertEquals(CanineArchetype.GUARDIAN, CanineArchetype.fromId(0));
        Assertions.assertEquals(CanineArchetype.PLAYFUL, CanineArchetype.fromId(1));
        Assertions.assertEquals(CanineArchetype.ADVENTURER, CanineArchetype.fromId(2));
        Assertions.assertEquals(CanineArchetype.GLUTTON, CanineArchetype.fromId(3));
        Assertions.assertEquals(CanineArchetype.LAZY, CanineArchetype.fromId(4));
        Assertions.assertEquals(CanineArchetype.FEARLESS, CanineArchetype.fromId(5));

        // Out-of-bounds fallbacks
        Assertions.assertEquals(CanineArchetype.GUARDIAN, CanineArchetype.fromId(-1));
        Assertions.assertEquals(CanineArchetype.GUARDIAN, CanineArchetype.fromId(99));
        Assertions.assertEquals(CanineArchetype.GUARDIAN, CanineArchetype.defaultFallback());

        // Serialized name lookup (case-insensitive & fallbacks)
        Assertions.assertEquals(CanineArchetype.GUARDIAN, CanineArchetype.fromSerializedName("guardian"));
        Assertions.assertEquals(CanineArchetype.PLAYFUL, CanineArchetype.fromSerializedName("PLAYFUL"));
        Assertions.assertEquals(CanineArchetype.ADVENTURER, CanineArchetype.fromSerializedName("adventurer"));
        Assertions.assertEquals(CanineArchetype.GLUTTON, CanineArchetype.fromSerializedName("glutton"));
        Assertions.assertEquals(CanineArchetype.LAZY, CanineArchetype.fromSerializedName("lazy"));
        Assertions.assertEquals(CanineArchetype.FEARLESS, CanineArchetype.fromSerializedName("fearless"));
        Assertions.assertEquals(CanineArchetype.GUARDIAN, CanineArchetype.fromSerializedName(null));
        Assertions.assertEquals(CanineArchetype.GUARDIAN, CanineArchetype.fromSerializedName(""));
        Assertions.assertEquals(CanineArchetype.GUARDIAN, CanineArchetype.fromSerializedName("unknown_archetype"));

        // Modifiers verification against specification
        Assertions.assertEquals(1.0, CanineArchetype.GUARDIAN.getHealthModifier(), 1e-4);
        Assertions.assertEquals(1.2, CanineArchetype.GUARDIAN.getDamageModifier(), 1e-4);
        Assertions.assertEquals(0.95, CanineArchetype.GUARDIAN.getSpeedModifier(), 1e-4);

        Assertions.assertEquals(0.95, CanineArchetype.PLAYFUL.getHealthModifier(), 1e-4);
        Assertions.assertEquals(0.9, CanineArchetype.PLAYFUL.getDamageModifier(), 1e-4);
        Assertions.assertEquals(1.15, CanineArchetype.PLAYFUL.getSpeedModifier(), 1e-4);

        Assertions.assertEquals(1.1, CanineArchetype.ADVENTURER.getHealthModifier(), 1e-4);
        Assertions.assertEquals(1.0, CanineArchetype.ADVENTURER.getDamageModifier(), 1e-4);
        Assertions.assertEquals(1.1, CanineArchetype.ADVENTURER.getSpeedModifier(), 1e-4);

        Assertions.assertEquals(1.15, CanineArchetype.GLUTTON.getHealthModifier(), 1e-4);
        Assertions.assertEquals(0.9, CanineArchetype.GLUTTON.getDamageModifier(), 1e-4);
        Assertions.assertEquals(0.95, CanineArchetype.GLUTTON.getSpeedModifier(), 1e-4);

        Assertions.assertEquals(1.25, CanineArchetype.LAZY.getHealthModifier(), 1e-4);
        Assertions.assertEquals(0.85, CanineArchetype.LAZY.getDamageModifier(), 1e-4);
        Assertions.assertEquals(0.85, CanineArchetype.LAZY.getSpeedModifier(), 1e-4);

        Assertions.assertEquals(1.0, CanineArchetype.FEARLESS.getHealthModifier(), 1e-4);
        Assertions.assertEquals(1.15, CanineArchetype.FEARLESS.getDamageModifier(), 1e-4);
        Assertions.assertEquals(1.05, CanineArchetype.FEARLESS.getSpeedModifier(), 1e-4);
    }

    @Test
    @DisplayName("Deterministic Invariant: Identical UUID Always Produces Identical Archetype")
    public void testDeterministicReplay() {
        for (int i = 0; i < 500; i++) {
            UUID testUuid = UUID.randomUUID();
            CanineArchetype first = CaninePersonalityHelper.rollFromUUID(testUuid, null);
            for (int replay = 0; replay < 20; replay++) {
                CanineArchetype subsequent = CaninePersonalityHelper.rollFromUUID(testUuid, null);
                Assertions.assertEquals(first, subsequent, "Replay failed: UUID must deterministically produce identical archetype");
            }
        }
    }

    @Test
    @DisplayName("Pillar 1 (Chaos Fuzzing): 50,000 Iterations Balanced Uniform Distribution")
    public void testChaosFuzzingUniformDistribution() {
        int totalIterations = 50_000;
        Map<CanineArchetype, Integer> counts = new EnumMap<>(CanineArchetype.class);
        for (CanineArchetype archetype : CanineArchetype.values()) {
            counts.put(archetype, 0);
        }

        for (int i = 0; i < totalIterations; i++) {
            UUID uuid = UUID.randomUUID();
            CanineArchetype archetype = CaninePersonalityHelper.rollFromUUID(uuid, null);
            Assertions.assertNotNull(archetype, "Archetype roll must never return null");
            counts.put(archetype, counts.get(archetype) + 1);
        }

        // Expected count per archetype is ~8,333 (16.67%)
        // Assert every archetype has at least 13% (6,500) and at most 20% (10,000)
        double minExpected = totalIterations * 0.13;
        double maxExpected = totalIterations * 0.20;

        for (CanineArchetype archetype : CanineArchetype.values()) {
            int count = counts.get(archetype);
            Assertions.assertTrue(count >= minExpected && count <= maxExpected,
                    "Distribution anomaly for " + archetype + ": count " + count + " outside bounds [" + minExpected + ", " + maxExpected + "]");
        }
    }

    @Test
    @DisplayName("Pillar 2 (Concurrency Load): 64 Concurrent Virtual Threads UUID Resolution")
    public void testConcurrentUUIDResolutionVirtualThreads() throws InterruptedException, ExecutionException {
        int threadCount = 64;
        int iterationsPerThread = 1_000;

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<Boolean>> tasks = new ArrayList<>();

            for (int t = 0; t < threadCount; t++) {
                tasks.add(() -> {
                    for (int i = 0; i < iterationsPerThread; i++) {
                        UUID uuid = UUID.randomUUID();
                        CanineArchetype archetype = CaninePersonalityHelper.rollFromUUID(uuid, null);
                        if (archetype == null) {
                            return false;
                        }
                    }
                    return true;
                });
            }

            List<Future<Boolean>> results = executor.invokeAll(tasks);
            for (Future<Boolean> future : results) {
                Assertions.assertTrue(future.get(), "Concurrent UUID resolution task encountered an unexpected null or error");
            }
        }
    }

    @Test
    @DisplayName("Pillar 5 (Sidedness Sweep): Zero Client Imports in net.vanillaoutsider.betterdogs.personality")
    public void testPillar5SidednessZeroClientImports() throws IOException {
        Path personalityDir = Paths.get("src/main/java/net/vanillaoutsider/betterdogs/personality");
        Assertions.assertTrue(Files.exists(personalityDir), "Personality package source directory must exist");

        try (Stream<Path> stream = Files.walk(personalityDir)) {
            List<Path> javaFiles = stream.filter(p -> p.toString().endsWith(".java")).toList();
            Assertions.assertFalse(javaFiles.isEmpty(), "Personality source files must be present");

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
