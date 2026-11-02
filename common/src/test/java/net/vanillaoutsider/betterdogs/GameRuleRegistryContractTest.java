// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pillar 1/5: GameRule Registry Contract & Identifier Safety Tests")
public class GameRuleRegistryContractTest {

    private static final Pattern GAMERULE_KEY_PATTERN = Pattern.compile("^(?:[a-z0-9_.-]+:)?[a-z0-9/._-]+$");

    @BeforeAll
    public static void initMinecraft() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

    @Test
    @DisplayName("Assert all registered GameRules conform to strict ResourceLocation format")
    public void testGameRuleKeysFormat() throws Exception {
        List<String> ruleKeys = extractDeclaredGameRuleKeys();
        assertFalse(ruleKeys.isEmpty(), "At least one GameRule must be declared");

        for (String key : ruleKeys) {
            assertTrue(GAMERULE_KEY_PATTERN.matcher(key).matches(),
                    "GameRule key '" + key + "' violates naming pattern ^(?:[a-z0-9_.-]+:)?[a-z0-9/._-]+$");

            assertEquals(key.toLowerCase(Locale.ROOT), key,
                    "GameRule key '" + key + "' must not contain any uppercase characters");

            ResourceLocation parsed = ResourceLocation.tryParse(key.contains(":") ? key : "betterdogs:" + key);
            assertNotNull(parsed, "ResourceLocation.tryParse failed for GameRule key: " + key);
        }
    }

    @Test
    @DisplayName("Assert all declared GameRules have matching translations in en_us.json")
    public void testGameRulesHaveMatchingTranslations() throws Exception {
        List<String> ruleKeys = extractDeclaredGameRuleKeys();

        InputStream stream = getClass().getResourceAsStream("/assets/betterdogs/lang/en_us.json");
        if (stream == null) {
            stream = getClass().getResourceAsStream("/assets/vanilla-outsider-better-dogs/lang/en_us.json");
        }
        assertNotNull(stream, "en_us.json must exist on classpath");

        JsonObject lang;
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            lang = JsonParser.parseReader(reader).getAsJsonObject();
        }

        for (String key : ruleKeys) {
            String colonKey = "gamerule." + key;
            String dotKey = "gamerule." + key.replace(':', '.');
            String bdColonKey = "gamerule.betterdogs:" + key;
            String bdDotKey = "gamerule.betterdogs." + key;
            String matColonKey = key.startsWith("bd_terr_") ? "gamerule.bd_territorial_matrix_" + key.substring("bd_terr_".length()) : "";
            String matDotKey = key.startsWith("bd_terr_") ? "gamerule.betterdogs.bd_territorial_matrix_" + key.substring("bd_terr_".length()) : "";
            String shortKey = key.contains(":") ? "gamerule." + key.substring(key.indexOf(':') + 1) : "";
            String betterdogsKey = key.contains(":") ? "gamerule.betterdogs." + key.substring(key.indexOf(':') + 1) : "";

            boolean hasTranslation = lang.has(colonKey) || lang.has(dotKey)
                    || lang.has(colonKey + ".description") || lang.has(dotKey + ".description")
                    || lang.has(bdColonKey) || lang.has(bdDotKey)
                    || lang.has(bdColonKey + ".description") || lang.has(bdDotKey + ".description")
                    || (!matColonKey.isEmpty() && (lang.has(matColonKey) || lang.has(matColonKey + ".description")))
                    || (!matDotKey.isEmpty() && (lang.has(matDotKey) || lang.has(matDotKey + ".description")))
                    || (!shortKey.isEmpty() && (lang.has(shortKey) || lang.has(shortKey + ".description")))
                    || (!betterdogsKey.isEmpty() && (lang.has(betterdogsKey) || lang.has(betterdogsKey + ".description")));

            assertTrue(hasTranslation,
                    "Missing translation in en_us.json for GameRule key '" + key + "'");
        }
    }

    private List<String> extractDeclaredGameRuleKeys() throws Exception {
        Path sourcePath = Path.of("src/main/java/net/vanillaoutsider/betterdogs/registry/BetterDogsGameRules.java");
        String content;
        if (Files.exists(sourcePath)) {
            content = Files.readString(sourcePath, StandardCharsets.UTF_8);
        } else {
            Path altPath = Path.of("common/src/main/java/net/vanillaoutsider/betterdogs/registry/BetterDogsGameRules.java");
            content = Files.readString(altPath, StandardCharsets.UTF_8);
        }

        List<String> keys = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?:registerBooleanGameRule|registerIntGameRule|registerBoolean|registerInteger|integerRule|booleanRule|GameRuleRegistry\\.register)\\s*\\(\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            keys.add(matcher.group(1));
        }
        return keys;
    }
}
