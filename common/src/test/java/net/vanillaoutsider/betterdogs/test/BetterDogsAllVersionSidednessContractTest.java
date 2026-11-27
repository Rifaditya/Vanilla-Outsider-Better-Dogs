// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
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
    @DisplayName("Pillar 5: Non-client sources contain zero unguarded client references")
    void testSourceSidedness() throws IOException {
        Path commonRoot = findCommonRoot();
        Path srcMain = commonRoot.resolve("src/main/java");
        assertTrue(Files.exists(srcMain), "src/main/java must exist");

        try (Stream<Path> stream = Files.walk(srcMain)) {
            List<Path> javaFiles = stream.filter(p -> p.toString().endsWith(".java")).toList();
            assertFalse(javaFiles.isEmpty(), "Java source files must exist in src/main/java");

            for (Path file : javaFiles) {
                String normalized = file.toString().replace('\\', '/');
                if (normalized.contains("/client/") || normalized.contains("BetterDogsClient")) {
                    continue; // Skip legitimate client-only subpackages / files
                }

                String content = Files.readString(file, StandardCharsets.UTF_8);
                if (content.contains("@Environment(EnvType.CLIENT)")) {
                    continue;
                }
                assertFalse(content.contains("net.minecraft.client."),
                        "Non-client file " + file.getFileName() + " contains net.minecraft.client.* reference");
                assertFalse(content.contains("com.mojang.blaze3d."),
                        "Non-client file " + file.getFileName() + " contains com.mojang.blaze3d.* reference");
            }
        }
    }

    @Test
    @DisplayName("Pillar 5: Compiled bytecode constant pool contains zero client references outside client/")
    void testDynamicBytecodeConstantPoolScanner() throws IOException {
        Path commonRoot = findCommonRoot();
        Path classesDir = commonRoot.resolve("build/classes/java/main");
        if (!Files.exists(classesDir)) {
            classesDir = findProjectRoot().resolve("common/build/classes/java/main");
        }
        if (!Files.exists(classesDir)) {
            classesDir = findProjectRoot().resolve("build/classes/java/main");
        }
        assertTrue(Files.exists(classesDir), "Compiled classes directory must exist at " + classesDir);

        List<Path> classFiles;
        try (Stream<Path> stream = Files.walk(classesDir)) {
            classFiles = stream.filter(p -> p.toString().endsWith(".class")).toList();
        }
        assertFalse(classFiles.isEmpty(), "Compiled .class files must exist in " + classesDir);

        for (Path classFile : classFiles) {
            String normalized = classFile.toString().replace('\\', '/');
            if (normalized.contains("/client/") || normalized.contains("BetterDogsClient")) {
                continue;
            }
            List<String> utf8Entries = readConstantPoolUtf8Entries(Files.readAllBytes(classFile));
            for (String entry : utf8Entries) {
                assertFalse(entry.contains("net/minecraft/client/"),
                        "Non-client bytecode " + normalized + " references net/minecraft/client/: " + entry);
                assertFalse(entry.contains("com/mojang/blaze3d/"),
                        "Non-client bytecode " + normalized + " references com/mojang/blaze3d/: " + entry);
            }
        }
    }

    private static List<String> readConstantPoolUtf8Entries(byte[] classBytes) throws IOException {
        List<String> entries = new ArrayList<>();
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(classBytes))) {
            int magic = in.readInt();
            assertEquals(0xCAFEBABE, magic, "Invalid classfile magic");
            in.readUnsignedShort(); // minor
            in.readUnsignedShort(); // major
            int cpCount = in.readUnsignedShort();
            for (int i = 1; i < cpCount; i++) {
                int tag = in.readUnsignedByte();
                switch (tag) {
                    case 1 -> entries.add(in.readUTF()); // CONSTANT_Utf8
                    case 3, 4, 9, 10, 11, 12, 17, 18 -> in.readInt(); // 4-byte entries
                    case 5, 6 -> {
                        in.readLong(); // 8-byte entries occupy two constant pool slots
                        i++;
                    }
                    case 7, 8, 16, 19, 20 -> in.readUnsignedShort(); // 2-byte entries
                    case 15 -> {
                        in.readUnsignedByte();
                        in.readUnsignedShort();
                    }
                    default -> throw new EOFException("Unknown constant pool tag " + tag);
                }
            }
        }
        return entries;
    }

    @Test
    @DisplayName("Pillar 5: fabric.mod.json, neoforge.mods.toml, mixin manifest, and client isolation hygiene")
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

        JsonObject depends = json.getAsJsonObject("depends");
        assertNotNull(depends, "fabric.mod.json must declare depends object");
        assertTrue(depends.has("dasik-library"), "fabric.mod.json must declare dasik-library dependency");
        assertEquals(">=1.4.1", depends.get("dasik-library").getAsString(),
                "fabric.mod.json must require dasik-library >=1.4.1");

        JsonObject entrypoints = json.getAsJsonObject("entrypoints");
        assertNotNull(entrypoints, "fabric.mod.json must declare entrypoints object");
        assertTrue(entrypoints.has("main"), "entrypoints must contain 'main'");
        assertTrue(entrypoints.has("client"), "entrypoints must contain 'client'");
        assertTrue(entrypoints.has("modmenu"), "entrypoints must contain 'modmenu'");
        JsonArray modmenuEntries = entrypoints.getAsJsonArray("modmenu");
        assertNotNull(modmenuEntries);
        assertEquals("net.vanillaoutsider.betterdogs.config.client.ModMenuIntegration",
                modmenuEntries.get(0).getAsString(),
                "modmenu entrypoint must point to config.client.ModMenuIntegration");

        Path neoforgeModsPath = root.resolve("neoforge/src/main/resources/META-INF/neoforge.mods.toml");
        assertTrue(Files.exists(neoforgeModsPath), "neoforge.mods.toml must exist at " + neoforgeModsPath);
        String neoforgeToml = Files.readString(neoforgeModsPath, StandardCharsets.UTF_8);
        assertTrue(neoforgeToml.contains("modId=\"dasik_library\""), "neoforge.mods.toml must declare dasik_library dependency");
        assertTrue(neoforgeToml.contains("versionRange=\"[1.4.1,)\""),
                "neoforge.mods.toml must require dasik_library versionRange=\"[1.4.1,)\"");

        Path commonRoot = findCommonRoot();
        Path modMenuPath = commonRoot.resolve("src/main/java/net/vanillaoutsider/betterdogs/config/client/ModMenuIntegration.java");
        assertTrue(Files.exists(modMenuPath), "ModMenuIntegration.java must exist in config/client/");
        String modMenuContent = Files.readString(modMenuPath, StandardCharsets.UTF_8);
        assertTrue(modMenuContent.contains("net.vanillaoutsider.betterdogs.config.client.YaclScreenHelper"),
                "ModMenuIntegration must reflectively target config.client.YaclScreenHelper");

        assertFalse(Files.exists(commonRoot.resolve("src/main/java/net/vanillaoutsider/betterdogs/compat/jade/BetterDogsHealthElement.java")),
                "Legacy un-isolated BetterDogsHealthElement.java must not exist");
        assertFalse(Files.exists(commonRoot.resolve("src/main/java/net/vanillaoutsider/betterdogs/compat/jade/WolfHealthProvider.java")),
                "Legacy un-isolated WolfHealthProvider.java must not exist");
        assertFalse(Files.exists(commonRoot.resolve("src/main/java/net/vanillaoutsider/betterdogs/config/YaclScreenHelper.java")),
                "Legacy un-isolated YaclScreenHelper.java must not exist");
        assertFalse(Files.exists(commonRoot.resolve("src/main/java/net/vanillaoutsider/betterdogs/config/ModMenuIntegration.java")),
                "Legacy un-isolated ModMenuIntegration.java must not exist");

        Path mixinConfigPath = commonRoot.resolve("src/main/resources/vanilla-outsider-better-dogs.mixins.json");
        assertTrue(Files.exists(mixinConfigPath), "vanilla-outsider-better-dogs.mixins.json must exist at " + mixinConfigPath);

        String mixinContent = Files.readString(mixinConfigPath, StandardCharsets.UTF_8);
        JsonObject mixinJson = JsonParser.parseString(mixinContent).getAsJsonObject();
        assertTrue(mixinJson.has("minVersion"), "vanilla-outsider-better-dogs.mixins.json must declare minVersion");
        assertEquals("0.8", mixinJson.get("minVersion").getAsString(), "Mixin minVersion must be 0.8");

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

    @Test
    @DisplayName("Pillar 5 / BL-BD-064: WolfRendererMixin removed from mixin configs and file tree")
    void testWolfRendererMixinAbsent() throws IOException {
        Path root = findProjectRoot();
        Path mixinConfigPath = root.resolve("common/src/main/resources/vanilla-outsider-better-dogs.mixins.json");
        assertTrue(Files.exists(mixinConfigPath), "Mixin config must exist");
        String content = Files.readString(mixinConfigPath, StandardCharsets.UTF_8);
        assertFalse(content.contains("WolfRendererMixin"), "Mixin config must not reference WolfRendererMixin");

        Path wolfRendererMixinPath = root.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/mixin/WolfRendererMixin.java");
        assertFalse(Files.exists(wolfRendererMixinPath), "WolfRendererMixin.java must be deleted to protect vanilla clients");
    }

    @Test
    @DisplayName("Pillar 5 / BL-BD-064: Zero EntityDataAccessor or defineSynchedData in WolfMixin")
    void testWolfMixinZeroEntityDataAccessor() throws IOException {
        Path root = findProjectRoot();
        Path wolfMixinPath = root.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/mixin/WolfMixin.java");
        assertTrue(Files.exists(wolfMixinPath), "WolfMixin.java must exist");
        String content = Files.readString(wolfMixinPath, StandardCharsets.UTF_8);
        assertFalse(content.contains("EntityDataAccessor"), "WolfMixin must contain zero EntityDataAccessor definitions");
        assertFalse(content.contains("defineId"), "WolfMixin must contain zero defineId calls");
        assertFalse(content.contains("defineSynchedData"), "WolfMixin must contain zero defineSynchedData injections");
    }

    @Test
    @DisplayName("Pillar 5 / BL-BD-064: Zero BuiltInRegistries.SOUND_EVENT or Registries.SOUND_EVENT registration")
    void testZeroSoundEventRegistration() throws IOException {
        Path root = findProjectRoot();

        Path soundEventsPath = root.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/registry/BetterDogsSoundEvents.java");
        assertTrue(Files.exists(soundEventsPath), "BetterDogsSoundEvents.java must exist");
        String soundEventsContent = Files.readString(soundEventsPath, StandardCharsets.UTF_8);
        assertFalse(soundEventsContent.contains("BuiltInRegistries.SOUND_EVENT"), "BetterDogsSoundEvents.java must not register into BuiltInRegistries.SOUND_EVENT");

        Path fabricInitPath = root.resolve("fabric/src/main/java/net/vanillaoutsider/betterdogs/BetterDogsFabric.java");
        assertTrue(Files.exists(fabricInitPath), "BetterDogsFabric.java must exist");
        String fabricContent = Files.readString(fabricInitPath, StandardCharsets.UTF_8);
        assertFalse(fabricContent.contains("registerSoundEvents"), "BetterDogsFabric.java must not call registerSoundEvents()");

        Path neoforgeInitPath = root.resolve("neoforge/src/main/java/net/vanillaoutsider/betterdogs/BetterDogsNeoForge.java");
        assertTrue(Files.exists(neoforgeInitPath), "BetterDogsNeoForge.java must exist");
        String neoforgeContent = Files.readString(neoforgeInitPath, StandardCharsets.UTF_8);
        assertFalse(neoforgeContent.contains("Registries.SOUND_EVENT"), "BetterDogsNeoForge.java must not register into Registries.SOUND_EVENT");
    }

    @Test
    @DisplayName("Pillar 5 / BL-BD-064: 100% advancement title and description fallback coverage")
    void testAdvancementFallbackCoverage() throws IOException {
        Path root = findProjectRoot();
        Path advDir = root.resolve("common/src/main/resources/data/betterdogs/advancement/husbandry");
        assertTrue(Files.exists(advDir), "advancement/husbandry directory must exist");
        try (Stream<Path> stream = Files.walk(advDir)) {
            List<Path> advFiles = stream.filter(p -> p.toString().endsWith(".json")).toList();
            assertTrue(advFiles.size() >= 15, "At least 15 advancement files must be audited for fallback coverage (found " + advFiles.size() + ")");
            for (Path advFile : advFiles) {
                String advContent = Files.readString(advFile, StandardCharsets.UTF_8);
                JsonObject advJson = JsonParser.parseString(advContent).getAsJsonObject();
                JsonObject display = advJson.getAsJsonObject("display");
                assertNotNull(display, "Advancement must have display object: " + advFile);

                JsonObject title = display.getAsJsonObject("title");
                assertNotNull(title, "Advancement must have title object: " + advFile);
                assertTrue(title.has("fallback") && !title.get("fallback").getAsString().isBlank(),
                        "Advancement title must have non-blank fallback: " + advFile);

                JsonObject description = display.getAsJsonObject("description");
                assertNotNull(description, "Advancement must have description object: " + advFile);
                assertTrue(description.has("fallback") && !description.get("fallback").getAsString().isBlank(),
                        "Advancement description must have non-blank fallback: " + advFile);
            }
        }
    }

    @Test
    @DisplayName("Pillar 5 / BL-BD-064: en_us.json contains wolf howl subtitle key")
    void testWolfHowlSubtitlePresent() throws IOException {
        Path root = findProjectRoot();
        Path langPath = root.resolve("common/src/main/resources/assets/betterdogs/lang/en_us.json");
        assertTrue(Files.exists(langPath), "en_us.json must exist at " + langPath);
        String content = Files.readString(langPath, StandardCharsets.UTF_8);
        JsonObject json = JsonParser.parseString(content).getAsJsonObject();
        assertTrue(json.has("subtitles.betterdogs.entity.wolf.howl"), "en_us.json must contain subtitles.betterdogs.entity.wolf.howl");
        assertEquals("Wolf howls", json.get("subtitles.betterdogs.entity.wolf.howl").getAsString());
    }
}
