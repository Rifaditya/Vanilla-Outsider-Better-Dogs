// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.resources.Identifier;
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

    private static final Pattern GAMERULE_KEY_PATTERN = Pattern.compile("^[a-z0-9_.-]+:[a-z0-9/._-]+$");

    @BeforeAll
    public static void initMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    @DisplayName("Assert BetterDogsGameRules.register() completes without IdentifierException")
    public void testRegisterDoesNotThrow() {
        assertDoesNotThrow(BetterDogsGameRules::register,
                "BetterDogsGameRules.register() must complete cleanly without throwing IdentifierException");

        assertNotNull(BetterDogsGameRules.BD_DOG_PLAY_FIGHTING, "BD_DOG_PLAY_FIGHTING must be registered and non-null");
        assertNotNull(BetterDogsGameRules.BD_PLAY_PENALTY_MINUTES, "BD_PLAY_PENALTY_MINUTES must be registered and non-null");
        assertTrue(BetterDogsGameRules.isDogPlayFightingEnabled(null), "Default dogPlayFighting must be true");
        assertEquals(10, BetterDogsGameRules.getPlayPenaltyMinutes(null), "Default playPenaltyMinutes must be 10");

        assertNotNull(BetterDogsGameRules.BD_PROJECTILE_INTERCEPT, "BD_PROJECTILE_INTERCEPT must be registered and non-null");
        assertNotNull(BetterDogsGameRules.BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD, "BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD must be registered and non-null");
        assertTrue(BetterDogsGameRules.isProjectileInterceptEnabled(null), "Default projectileIntercept must be true");
        assertEquals(10, BetterDogsGameRules.getInterceptorOwnerHealthThreshold(null), "Default interceptorOwnerHealthThreshold must be 10");
    }

    @Test
    @DisplayName("Assert all GameRule keys declared in BetterDogsGameRules are strictly lowercase and valid Identifiers")
    public void testGameRuleIdentifiersAreValidAndStrictlyLowercase() throws Exception {
        List<String> ruleKeys = extractDeclaredGameRuleKeys();
        assertTrue(ruleKeys.size() >= 25, "Expected at least 25 GameRules in BetterDogsGameRules, found: " + ruleKeys.size());

        for (String key : ruleKeys) {
            assertTrue(GAMERULE_KEY_PATTERN.matcher(key).matches(),
                    "GameRule key '" + key + "' violates Mojang Identifier pattern [a-z0-9_.-]+:[a-z0-9/._-]+");

            assertEquals(key.toLowerCase(Locale.ROOT), key,
                    "GameRule key '" + key + "' must not contain any uppercase characters");

            Identifier parsed = Identifier.tryParse(key);
            assertNotNull(parsed, "Identifier.tryParse failed for GameRule key: " + key);

            assertDoesNotThrow(() -> Identifier.parse(key),
                    "Identifier.parse threw an exception for GameRule key: " + key);
        }
    }

    @Test
    @DisplayName("Assert all declared GameRules have matching translations in en_us.json")
    public void testGameRulesHaveMatchingTranslations() throws Exception {
        List<String> ruleKeys = extractDeclaredGameRuleKeys();

        InputStream stream = getClass().getResourceAsStream("/assets/vanilla-outsider-better-dogs/lang/en_us.json");
        assertNotNull(stream, "en_us.json must exist on classpath");

        JsonObject lang;
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            lang = JsonParser.parseReader(reader).getAsJsonObject();
        }

        for (String key : ruleKeys) {
            String colonKey = "gamerule." + key;
            String dotKey = "gamerule." + key.replace(':', '.');

            boolean hasTranslation = lang.has(colonKey) || lang.has(dotKey)
                    || lang.has(colonKey + ".description") || lang.has(dotKey + ".description");

            assertTrue(hasTranslation,
                    "Missing translation in en_us.json for GameRule key '" + key + "' (checked: " + colonKey + " and " + dotKey + ")");
        }
    }

    @Test
    @DisplayName("Assert DynamicGameRuleManager safely handles camelCase registration defensively")
    public void testDynamicGameRuleManagerDefensiveCaseInsensitivity() {
        String testCamelKey = "betterdogs:testCamelCaseRuleContract";
        assertDoesNotThrow(() -> {
            var rule = DynamicGameRuleManager.booleanRule(testCamelKey, BetterDogsGameRules.BETTER_DOGS, true).register();
            assertNotNull(rule, "Defensive registration of camelCase rule should succeed via lowercase normalization");
        });

        // Cleanup
        DynamicGameRuleManager.unregister(testCamelKey);
    }

    private List<String> extractDeclaredGameRuleKeys() throws Exception {
        Path sourcePath = Path.of("src/main/java/net/vanillaoutsider/betterdogs/registry/BetterDogsGameRules.java");
        String content;
        if (Files.exists(sourcePath)) {
            content = Files.readString(sourcePath, StandardCharsets.UTF_8);
        } else {
            Path altPath = Path.of("E:/Minecraft Project/Vanilla Outsider Collections/Better Dogs all version/Better Dogs v26.2/Better Dogs 26.2/src/main/java/net/vanillaoutsider/betterdogs/registry/BetterDogsGameRules.java");
            content = Files.readString(altPath, StandardCharsets.UTF_8);
        }

        List<String> keys = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?:registerBoolean|registerInteger|integerRule|booleanRule)\\s*\\(\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            keys.add(matcher.group(1));
        }
        return keys;
    }
}
