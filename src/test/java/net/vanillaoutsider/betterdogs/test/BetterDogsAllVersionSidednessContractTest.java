// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BetterDogsAllVersionSidednessContractTest: Pillar 5 Sidedness, Manifest & License Hygiene Contract Test Suite.
 *
 * Verifies across all version anchors:
 * - Line 1 of all .java files has the exact GPLv3 license header.
 * - Non-client sources contain zero unguarded client package imports or ClientModInitializer references.
 * - Non-client bytecode constant pool contains zero dynamic client/blaze3d references.
 * - fabric.mod.json declares universal environment ("*") and complete entrypoints ("main", "modmenu").
 * - vanilla-outsider-better-dogs.mixins.json exists and all registered mixin classes exist in source.
 */
public class BetterDogsAllVersionSidednessContractTest {

    private static final String EXPECTED_LICENSE = "// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3";

    private static Path findProjectRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        if (Files.exists(dir.resolve("src/main/resources/fabric.mod.json"))) {
            return dir;
        }
        Path parent = dir;
        while (parent != null) {
            if (Files.exists(parent.resolve("src/main/resources/fabric.mod.json"))) {
                return parent;
            }
            parent = parent.getParent();
        }
        String[] candidates = {
            "Better Dogs v26.3/Better Dogs 26.3",
            "Better Dogs v26.2/Better Dogs 26.2",
            "Better Dogs v26.1/Better Dogs 26.1",
            "Better Dogs v1.21.11/Better Dogs 1.21.11",
            "Better Dogs v1.21.1/Better Dogs 1.21.1",
            "Better Dogs v1.20.1/Better Dogs 1.20.1",
            "Vanilla Outsider Collections/Better Dogs all version/Better Dogs v26.3/Better Dogs 26.3",
            "Vanilla Outsider Collections/Better Dogs all version/Better Dogs v26.2/Better Dogs 26.2",
            "Vanilla Outsider Collections/Better Dogs all version/Better Dogs v26.1/Better Dogs 26.1",
            "Vanilla Outsider Collections/Better Dogs all version/Better Dogs v1.21.11/Better Dogs 1.21.11",
            "Vanilla Outsider Collections/Better Dogs all version/Better Dogs v1.21.1/Better Dogs 1.21.1",
            "Vanilla Outsider Collections/Better Dogs all version/Better Dogs v1.20.1/Better Dogs 1.20.1"
        };
        for (String cand : candidates) {
            Path p = dir.resolve(cand);
            if (Files.exists(p.resolve("src/main/resources/fabric.mod.json"))) {
                return p;
            }
        }
        return dir;
    }

    @Test
    @DisplayName("Pillar 5: Exact single-line GPLv3 license header present on line 1 across all sources")
    void testLicenseHeaderIntegrity() throws IOException {
        Path root = findProjectRoot();
        Path[] sourceRoots = new Path[] {
            root.resolve("src/main/java"),
            root.resolve("src/test/java")
        };

        for (Path srcRoot : sourceRoots) {
            if (!Files.exists(srcRoot)) {
                continue;
            }
            try (Stream<Path> stream = Files.walk(srcRoot)) {
                List<Path> javaFiles = stream.filter(p -> p.toString().endsWith(".java")).toList();
                assertFalse(javaFiles.isEmpty(), "Java source files must exist in " + srcRoot);
                for (Path file : javaFiles) {
                    List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
                    assertFalse(lines.isEmpty(), "Java file is empty: " + file);
                    String line1 = lines.get(0).trim();
                    if (line1.startsWith("\uFEFF")) {
                        line1 = line1.substring(1);
                    }
                    assertEquals(EXPECTED_LICENSE, line1,
                        "Missing or incorrect license header on line 1 in: " + file.toString().replace('\\', '/'));
                }
            }
        }
    }

    @Test
    @DisplayName("Pillar 5: Non-client sources contain zero unguarded client package imports")
    void testSourceSidedness() throws IOException {
        Path root = findProjectRoot();
        Path srcMain = root.resolve("src/main/java");
        assertTrue(Files.exists(srcMain), "src/main/java must exist");

        try (Stream<Path> stream = Files.walk(srcMain)) {
            List<Path> javaFiles = stream.filter(p -> p.toString().endsWith(".java")).toList();
            assertFalse(javaFiles.isEmpty(), "Java source files must exist in src/main/java");

            for (Path file : javaFiles) {
                String normalized = file.toString().replace('\\', '/');
                if (normalized.contains("/client/")
                        || normalized.endsWith("Client.java")
                        || normalized.contains("ModMenuIntegration")
                        || normalized.contains("YaclScreenHelper")
                        || normalized.contains("ClothConfigScreenHelper")
                        || normalized.contains("/compat/jade/")
                        || normalized.contains("WolfRendererMixin")) {
                    continue;
                }
                String content = Files.readString(file, StandardCharsets.UTF_8);
                if (content.contains("@Environment(EnvType.CLIENT)")) {
                    continue;
                }

                assertFalse(content.contains("net.minecraft.client."),
                    "Forbidden client package reference in common source file: " + normalized);
                assertFalse(content.contains("ClientModInitializer"),
                    "Forbidden ClientModInitializer reference in common source file: " + normalized);
                assertFalse(content.contains("com.mojang.blaze3d."),
                    "Forbidden Blaze3D reference in common source file: " + normalized);
            }
        }
    }

    @Test
    @DisplayName("Pillar 5: Non-client bytecode contains zero unguarded client package constant pool references")
    void testDynamicBytecodeConstantPoolScanner() throws IOException {
        Path root = findProjectRoot();
        Path classesRoot = root.resolve("build/classes/java/main");
        if (!Files.exists(classesRoot)) {
            return;
        }

        byte[] CLIENT_BYTES = new byte[]{110, 101, 116, 47, 109, 105, 110, 101, 99, 114, 97, 102, 116, 47, 99, 108, 105, 101, 110, 116, 47};
        byte[] BLAZE_BYTES = new byte[]{99, 111, 109, 47, 109, 111, 106, 97, 110, 103, 47, 98, 108, 97, 122, 101, 51, 100, 47};
        String clientToken = new String(CLIENT_BYTES, StandardCharsets.ISO_8859_1);
        String blazeToken = new String(BLAZE_BYTES, StandardCharsets.ISO_8859_1);

        try (Stream<Path> stream = Files.walk(classesRoot)) {
            List<Path> classFiles = stream.filter(p -> p.toString().endsWith(".class")).toList();
            for (Path classFile : classFiles) {
                String normalized = classFile.toString().replace('\\', '/');
                if (normalized.contains("/client/")
                        || normalized.contains("ModMenuIntegration")
                        || normalized.contains("YaclScreenHelper")
                        || normalized.contains("ClothConfigScreenHelper")
                        || normalized.contains("/compat/jade/")
                        || normalized.contains("WolfRendererMixin")) {
                    continue;
                }
                String rawBytes = new String(Files.readAllBytes(classFile), StandardCharsets.ISO_8859_1);
                assertFalse(rawBytes.contains(clientToken),
                    "Forbidden client constant pool reference in common bytecode: " + normalized);
                assertFalse(rawBytes.contains(blazeToken),
                    "Forbidden Blaze3D constant pool reference in common bytecode: " + normalized);
            }
        }
    }

    @Test
    @DisplayName("Pillar 5: fabric.mod.json and mixin manifest hygiene")
    void testManifestAndMixinHygiene() throws IOException {
        Path root = findProjectRoot();
        Path fabricModPath = root.resolve("src/main/resources/fabric.mod.json");
        assertTrue(Files.exists(fabricModPath), "fabric.mod.json must exist at " + fabricModPath);

        String content = Files.readString(fabricModPath, StandardCharsets.UTF_8);
        JsonObject json = JsonParser.parseString(content).getAsJsonObject();

        assertTrue(json.has("environment"), "fabric.mod.json must declare 'environment'");
        assertEquals("*", json.get("environment").getAsString(), "fabric.mod.json 'environment' must be '*'");

        assertTrue(json.has("entrypoints"), "fabric.mod.json must declare 'entrypoints'");
        JsonObject entrypoints = json.getAsJsonObject("entrypoints");
        assertTrue(entrypoints.has("main"), "entrypoints must contain 'main'");
        assertTrue(entrypoints.has("modmenu"), "entrypoints must contain 'modmenu'");

        Path mixinConfigPath = root.resolve("src/main/resources/vanilla-outsider-better-dogs.mixins.json");
        assertTrue(Files.exists(mixinConfigPath), "vanilla-outsider-better-dogs.mixins.json must exist at " + mixinConfigPath);

        String mixinContent = Files.readString(mixinConfigPath, StandardCharsets.UTF_8);
        JsonObject mixinJson = JsonParser.parseString(mixinContent).getAsJsonObject();

        assertTrue(mixinJson.has("package"), "Mixin config must declare 'package'");
        String pkg = mixinJson.get("package").getAsString().replace('.', '/');

        List<String> mixinNames = new ArrayList<>();
        if (mixinJson.has("mixins") && mixinJson.get("mixins").isJsonArray()) {
            JsonArray mixinsArray = mixinJson.getAsJsonArray("mixins");
            for (JsonElement elem : mixinsArray) {
                mixinNames.add(elem.getAsString());
            }
        }
        if (mixinJson.has("client") && mixinJson.get("client").isJsonArray()) {
            JsonArray clientArray = mixinJson.getAsJsonArray("client");
            for (JsonElement elem : clientArray) {
                mixinNames.add(elem.getAsString());
            }
        }
        if (mixinJson.has("server") && mixinJson.get("server").isJsonArray()) {
            JsonArray serverArray = mixinJson.getAsJsonArray("server");
            for (JsonElement elem : serverArray) {
                mixinNames.add(elem.getAsString());
            }
        }

        assertFalse(mixinNames.isEmpty(), "vanilla-outsider-better-dogs.mixins.json must declare mixin classes");

        Path srcMain = root.resolve("src/main/java");
        for (String mixinName : mixinNames) {
            Path classPath = srcMain.resolve(pkg).resolve(mixinName + ".java");
            assertTrue(Files.exists(classPath), "Mixin class file must exist in source: " + classPath);
        }
    }
}
