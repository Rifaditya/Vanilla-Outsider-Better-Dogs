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

@DisplayName("Pillar 5: Sidedness, Manifest & License Hygiene Contract Test Suite")
public class BetterDogsAllVersionSidednessContractTest {

    private static final String EXPECTED_LICENSE = "// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3";

    private static Path findCommonRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        if (Files.exists(dir.resolve("src/main/resources/vanilla-outsider-better-dogs.mixins.json"))) {
            return dir;
        }
        if (Files.exists(dir.resolve("common/src/main/resources/vanilla-outsider-better-dogs.mixins.json"))) {
            return dir.resolve("common");
        }
        Path parent = dir.getParent();
        if (parent != null && Files.exists(parent.resolve("common/src/main/resources/vanilla-outsider-better-dogs.mixins.json"))) {
            return parent.resolve("common");
        }
        return dir;
    }

    private static Path findProjectRoot() {
        Path common = findCommonRoot();
        if (Files.exists(common.resolve("src/main/resources/vanilla-outsider-better-dogs.mixins.json"))) {
            return common.getParent();
        }
        return common;
    }

    @Test
    @DisplayName("Pillar 5: Exact single-line GPLv3 license header present on line 1 across all sources")
    void testLicenseHeaderIntegrity() throws IOException {
        Path commonRoot = findCommonRoot();
        Path[] sourceRoots = new Path[] {
            commonRoot.resolve("src/main/java"),
            commonRoot.resolve("src/test/java")
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
                    String firstLine = lines.get(0).trim();
                    assertEquals(EXPECTED_LICENSE, firstLine,
                            "Missing or corrupted GPLv3 license header on line 1 of " + file.getFileName());
                }
            }
        }
    }

    @Test
    @DisplayName("Pillar 5: Non-client sources contain zero unguarded client package imports")
    void testSourceSidedness() throws IOException {
        Path commonRoot = findCommonRoot();
        Path srcMain = commonRoot.resolve("src/main/java");
        assertTrue(Files.exists(srcMain), "src/main/java must exist");

        try (Stream<Path> stream = Files.walk(srcMain)) {
            List<Path> javaFiles = stream.filter(p -> p.toString().endsWith(".java")).toList();
            assertFalse(javaFiles.isEmpty(), "Java source files must exist in src/main/java");

            for (Path file : javaFiles) {
                String normalized = file.toString().replace('\\', '/');
                if (normalized.contains("/client/") || normalized.contains("WolfRendererMixin") || normalized.contains("BetterDogsClient") || normalized.contains("YaclScreenHelper") || normalized.contains("/compat/jade/") || normalized.contains("/compat/yacl/")) {
                    continue; // Skip legitimate client-only subpackages / files
                }

                List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    assertFalse(line.startsWith("import net.minecraft.client."),
                            "Non-client file " + file.getFileName() + " imports net.minecraft.client.* at line " + (i + 1));
                    assertFalse(line.startsWith("import com.mojang.blaze3d."),
                            "Non-client file " + file.getFileName() + " imports com.mojang.blaze3d.* at line " + (i + 1));
                }
            }
        }
    }

    @Test
    @DisplayName("Pillar 5: fabric.mod.json and mixin manifest hygiene")
    void testManifestAndMixinHygiene() throws IOException {
        Path root = findProjectRoot();
        Path fabricModPath = root.resolve("fabric/src/main/resources/fabric.mod.json");
        if (!Files.exists(fabricModPath)) {
            fabricModPath = root.resolve("src/main/resources/fabric.mod.json");
        }
        assertTrue(Files.exists(fabricModPath), "fabric.mod.json must exist at " + fabricModPath);

        String content = Files.readString(fabricModPath, StandardCharsets.UTF_8);
        JsonObject json = JsonParser.parseString(content).getAsJsonObject();

        assertEquals("*", json.get("environment").getAsString(), "environment must be universal '*'");

        JsonObject entrypoints = json.getAsJsonObject("entrypoints");
        assertNotNull(entrypoints, "fabric.mod.json must declare entrypoints object");
        assertTrue(entrypoints.has("main"), "entrypoints must contain 'main'");
        assertTrue(entrypoints.has("modmenu"), "entrypoints must contain 'modmenu'");

        Path commonRoot = findCommonRoot();
        Path mixinConfigPath = commonRoot.resolve("src/main/resources/vanilla-outsider-better-dogs.mixins.json");
        assertTrue(Files.exists(mixinConfigPath), "vanilla-outsider-better-dogs.mixins.json must exist at " + mixinConfigPath);

        String mixinContent = Files.readString(mixinConfigPath, StandardCharsets.UTF_8);
        JsonObject mixinJson = JsonParser.parseString(mixinContent).getAsJsonObject();

        String pkg = mixinJson.get("package").getAsString().replace('.', '/');
        JsonArray mixinsArray = mixinJson.getAsJsonArray("mixins");

        List<String> mixinNames = new ArrayList<>();
        for (JsonElement el : mixinsArray) {
            mixinNames.add(el.getAsString());
        }
        if (mixinJson.has("client")) {
            for (JsonElement el : mixinJson.getAsJsonArray("client")) {
                mixinNames.add(el.getAsString());
            }
        }

        assertFalse(mixinNames.isEmpty(), "vanilla-outsider-better-dogs.mixins.json must declare mixin classes");

        Path srcMain = commonRoot.resolve("src/main/java");
        for (String mixinName : mixinNames) {
            Path classPath = srcMain.resolve(pkg).resolve(mixinName + ".java");
            assertTrue(Files.exists(classPath), "Mixin class file must exist in source: " + classPath);
        }
    }
}
