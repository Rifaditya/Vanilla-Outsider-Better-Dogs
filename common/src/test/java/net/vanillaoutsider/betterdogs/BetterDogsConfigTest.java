// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class BetterDogsConfigTest {

    @Test
    @DisplayName("BetterDogsConfig.load() copies default JSON from JAR resources and loads values")
    public void testLoadFreshConfig(@TempDir Path tempDir) {
        Path configJson = tempDir.resolve("vanilla-outsider-better-dogs.json");
        assertFalse(Files.exists(configJson), "Config file must not exist prior to loading");

        BetterDogsConfig.load(tempDir);

        assertTrue(Files.exists(configJson), "Config JSON must be created on disk from resource template");
        BetterDogsConfig config = BetterDogsConfig.get();
        assertNotNull(config, "Config instance must be initialized");
        assertEquals(3470, config.configVersion, "Config version must match default VERSION");
        assertEquals(0.20, config.globalSpeedBuff, 0.001);
        assertEquals(0.70, config.wolfMinScale, 0.001);
        assertEquals(1.45, config.wolfMaxScale, 0.001);
        assertTrue(config.enableStormAnxiety);
        assertTrue(config.enableCliffSafety);
    }

    @Test
    @DisplayName("BetterDogsConfig.load() preserves user custom values from disk")
    public void testLoadExistingCustomConfig(@TempDir Path tempDir) throws IOException {
        Path configJson = tempDir.resolve("vanilla-outsider-better-dogs.json");
        String customJson = "{\n" +
                "  \"configVersion\": 3470,\n" +
                "  \"globalSpeedBuff\": 0.45,\n" +
                "  \"wolfMinScale\": 0.85,\n" +
                "  \"wolfMaxScale\": 1.60,\n" +
                "  \"enableCliffSafety\": false\n" +
                "}";
        Files.writeString(configJson, customJson, StandardCharsets.UTF_8);

        BetterDogsConfig.load(tempDir);

        BetterDogsConfig config = BetterDogsConfig.get();
        assertNotNull(config);
        assertEquals(0.45, config.globalSpeedBuff, 0.001, "Custom globalSpeedBuff must be preserved");
        assertEquals(0.85, config.wolfMinScale, 0.001, "Custom wolfMinScale must be preserved");
        assertEquals(1.60, config.wolfMaxScale, 0.001, "Custom wolfMaxScale must be preserved");
        assertFalse(config.enableCliffSafety, "Custom enableCliffSafety must be preserved");
    }

    @Test
    @DisplayName("BetterDogsConfig.save() atomically persists modified fields to disk")
    public void testConfigSave(@TempDir Path tempDir) throws IOException {
        BetterDogsConfig.load(tempDir);
        BetterDogsConfig config = BetterDogsConfig.get();
        config.wolfMinScale = 0.95;

        BetterDogsConfig.save();

        Path configJson = tempDir.resolve("vanilla-outsider-better-dogs.json");
        String content = Files.readString(configJson, StandardCharsets.UTF_8);
        assertTrue(content.contains("0.95"), "Saved JSON file must reflect updated wolfMinScale value");
    }

    @Test
    @DisplayName("BetterDogsConfig.load() upgrades old config version and creates backup")
    public void testVersionMigration(@TempDir Path tempDir) throws IOException {
        Path configJson = tempDir.resolve("vanilla-outsider-better-dogs.json");
        Path backupJson = tempDir.resolve("vanilla-outsider-better-dogs.json.bak");

        String oldJson = "{\n" +
                "  \"configVersion\": 1000,\n" +
                "  \"globalSpeedBuff\": 0.33,\n" +
                "  \"wolfMinScale\": 0.75\n" +
                "}";
        Files.writeString(configJson, oldJson, StandardCharsets.UTF_8);

        BetterDogsConfig.load(tempDir);

        assertTrue(Files.exists(backupJson), "Backup (.bak) must be created on version upgrade");
        BetterDogsConfig config = BetterDogsConfig.get();
        assertEquals(3470, config.configVersion, "Config version must be upgraded to current");
        assertEquals(0.33, config.globalSpeedBuff, 0.001, "User settings must be preserved across upgrade");
    }
}
