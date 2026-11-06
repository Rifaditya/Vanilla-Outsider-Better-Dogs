// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 1 - Feature 01: Multi-Loader Build & Isolation Tests")
public class Tier1_01_MultiLoaderIsolationTest {

    @Test
    @DisplayName("Assert zero runtime dependency on Architectury API in build scripts")
    public void testZeroArchitecturyRuntimeDependency() throws IOException {
        Path buildFile = Paths.get("build.gradle");
        if (!Files.exists(buildFile)) {
            // Check parent or root if running in nested directory
            buildFile = Paths.get("Vanilla Outsider Collections/Better Dogs all version/Better Dogs v26.1/Better Dogs 26.1/build.gradle");
        }
        if (Files.exists(buildFile)) {
            List<String> lines = Files.readAllLines(buildFile);
            for (String line : lines) {
                assertFalse(line.contains("implementation") && line.contains("dev.architectury:architectury"),
                        "Must not declare runtime implementation dependency on architectury-api: " + line);
            }
        }
    }

    @Test
    @DisplayName("Assert archives base name complies with studio conventions")
    public void testArchivesBaseNameConvention() throws IOException {
        Path propFile = Paths.get("gradle.properties");
        if (!Files.exists(propFile)) {
            propFile = Paths.get("Vanilla Outsider Collections/Better Dogs all version/Better Dogs v26.1/Better Dogs 26.1/gradle.properties");
        }
        if (Files.exists(propFile)) {
            List<String> lines = Files.readAllLines(propFile);
            boolean foundBaseName = false;
            for (String line : lines) {
                if (line.startsWith("archives_base_name")) {
                    assertTrue(line.contains("vanilla-outsider-better-dogs"),
                            "archives_base_name must be vanilla-outsider-better-dogs: " + line);
                    foundBaseName = true;
                }
            }
            assertTrue(foundBaseName, "gradle.properties must define archives_base_name");
        }
    }

    @Test
    @DisplayName("Assert common domain packages contain zero loader-specific imports")
    public void testCommonDomainZeroLoaderImports() throws IOException {
        Path aiDir = Paths.get("src/main/java/net/vanillaoutsider/betterdogs/ai");
        if (Files.exists(aiDir)) {
            try (Stream<Path> stream = Files.walk(aiDir)) {
                List<Path> javaFiles = stream.filter(p -> p.toString().endsWith(".java")).toList();
                for (Path javaFile : javaFiles) {
                    List<String> lines = Files.readAllLines(javaFile);
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i).trim();
                        assertFalse(line.startsWith("import net.fabricmc."),
                                "Common AI class " + javaFile.getFileName() + " must not import net.fabricmc.* (line " + (i + 1) + ")");
                        assertFalse(line.startsWith("import net.neoforged."),
                                "Common AI class " + javaFile.getFileName() + " must not import net.neoforged.* (line " + (i + 1) + ")");
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("Assert genetics domain package contains zero loader-specific imports")
    public void testGeneticsDomainZeroLoaderImports() throws IOException {
        Path geneticsDir = Paths.get("src/main/java/net/vanillaoutsider/betterdogs/genetics");
        if (Files.exists(geneticsDir)) {
            try (Stream<Path> stream = Files.walk(geneticsDir)) {
                List<Path> javaFiles = stream.filter(p -> p.toString().endsWith(".java")).toList();
                for (Path javaFile : javaFiles) {
                    List<String> lines = Files.readAllLines(javaFile);
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i).trim();
                        assertFalse(line.startsWith("import net.fabricmc."),
                                "Genetics class " + javaFile.getFileName() + " must not import net.fabricmc.* (line " + (i + 1) + ")");
                        assertFalse(line.startsWith("import net.neoforged."),
                                "Genetics class " + javaFile.getFileName() + " must not import net.neoforged.* (line " + (i + 1) + ")");
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("Assert GPLv3 single-line license headers across common domain files")
    public void testLicenseHeaderIntegrity() throws IOException {
        Path personalityDir = Paths.get("src/main/java/net/vanillaoutsider/betterdogs/personality");
        if (Files.exists(personalityDir)) {
            try (Stream<Path> stream = Files.walk(personalityDir)) {
                List<Path> javaFiles = stream.filter(p -> p.toString().endsWith(".java")).toList();
                for (Path javaFile : javaFiles) {
                    List<String> lines = Files.readAllLines(javaFile);
                    assertFalse(lines.isEmpty(), javaFile.getFileName() + " must not be empty");
                    assertTrue(lines.get(0).startsWith("// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3"),
                            "Missing required GPLv3 header in " + javaFile.getFileName());
                }
            }
        }
    }
}
