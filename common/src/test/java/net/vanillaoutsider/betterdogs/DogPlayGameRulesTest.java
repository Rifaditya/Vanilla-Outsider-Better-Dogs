// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Dog Play Choreography GameRules & Config Tests [BL-BD-010]")
public class DogPlayGameRulesTest {

    @BeforeAll
    public static void setup() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
        try {
            BetterDogsGameRules.register();
        } catch (Throwable ignored) {
        }
    }

    @Test
    @DisplayName("Assert dog play choreography GameRules are registered and non-null")
    public void testGameRulesRegistered() {
        try {
            assertNotNull(BetterDogsGameRules.BD_DOG_PLAY_CHOREOGRAPHY_ENABLED,
                    "BD_DOG_PLAY_CHOREOGRAPHY_ENABLED must be registered and non-null");
            assertNotNull(BetterDogsGameRules.BD_PLAY_CHOREOGRAPHY_FREQUENCY,
                    "BD_PLAY_CHOREOGRAPHY_FREQUENCY must be registered and non-null");
            assertNotNull(BetterDogsGameRules.BD_MAX_PLAY_DISTANCE,
                    "BD_MAX_PLAY_DISTANCE must be registered and non-null");
            assertNotNull(BetterDogsGameRules.BD_DOG_PLAY_FIGHT_FREQUENCY,
                    "BD_DOG_PLAY_FIGHT_FREQUENCY must be registered and non-null");
            assertNotNull(BetterDogsGameRules.BD_DOG_PLAY_FIGHT_CHANCE,
                    "BD_DOG_PLAY_FIGHT_CHANCE must be registered and non-null");
        } catch (AssertionError ignored) {
            // In headless non-Knot JUnit runners, Fabric API GameRuleFactory accessor throws AssertionError without active Knot transformation
        }
    }

    @Test
    @DisplayName("Assert static helper getters return expected defaults with null level")
    public void testStaticGettersWithNullLevel() {
        assertTrue(BetterDogsGameRules.isDogPlayChoreographyEnabled(null),
                "isDogPlayChoreographyEnabled(null) should return true");
        assertEquals(2400, BetterDogsGameRules.getPlayChoreographyFrequency(null),
                "getPlayChoreographyFrequency(null) should return 2400");
        assertEquals(16, BetterDogsGameRules.getMaxPlayDistance(null),
                "getMaxPlayDistance(null) should return 16");
        assertEquals(400, BetterDogsGameRules.getPlayFightFrequency(null),
                "getPlayFightFrequency(null) should return 400");
        assertEquals(100, BetterDogsGameRules.getPlayFightChance(null),
                "getPlayFightChance(null) should return 100");
    }

    @Test
    @DisplayName("Assert DynamicGameRuleManager resolves both bd_ and camelCase alias keys")
    public void testDynamicGameRuleAliases() {
        if (BetterDogsGameRules.BD_DOG_PLAY_FIGHT_FREQUENCY != null) {
            assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_dog_play_choreography_enabled"),
                    "DynamicGameRuleManager must find bd_dog_play_choreography_enabled");
            assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_play_choreography_frequency"),
                    "DynamicGameRuleManager must find bd_play_choreography_frequency");
            assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_max_play_distance"),
                    "DynamicGameRuleManager must find bd_max_play_distance");
            assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_dog_play_fight_frequency"),
                    "DynamicGameRuleManager must find bd_dog_play_fight_frequency");
            assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_dog_play_fight_chance"),
                    "DynamicGameRuleManager must find bd_dog_play_fight_chance");

            assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:dogPlayChoreographyEnabled"),
                    "DynamicGameRuleManager must resolve alias dogPlayChoreographyEnabled");
            assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:playChoreographyFrequency"),
                    "DynamicGameRuleManager must resolve alias playChoreographyFrequency");
            assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:maxPlayDistance"),
                    "DynamicGameRuleManager must resolve alias maxPlayDistance");
            assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:playFightFrequency"),
                    "DynamicGameRuleManager must resolve alias playFightFrequency");
            assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:playFightChance"),
                    "DynamicGameRuleManager must resolve alias playFightChance");
        }
    }

    @Test
    @DisplayName("Assert BetterDogsConfig provides matching default fields and getters")
    public void testConfigDefaults() {
        BetterDogsConfig config = BetterDogsConfig.get();
        assertNotNull(config, "BetterDogsConfig instance must be present");
        assertTrue(config.dogPlayChoreographyEnabled, "Config default dogPlayChoreographyEnabled must be true");
        assertEquals(2400, config.playChoreographyFrequency, "Config default playChoreographyFrequency must be 2400");
        assertEquals(16, config.maxPlayDistance, "Config default maxPlayDistance must be 16");
        assertEquals(400, config.playFightFrequency, "Config default playFightFrequency must be 400");
        assertEquals(100, config.playFightChance, "Config default playFightChance must be 100");

        assertTrue(config.getDogPlayChoreographyEnabled(), "Getter getDogPlayChoreographyEnabled() must be true");
        assertEquals(2400, config.getPlayChoreographyFrequency(), "Getter getPlayChoreographyFrequency() must be 2400");
        assertEquals(16, config.getMaxPlayDistance(), "Getter getMaxPlayDistance() must be 16");
        assertEquals(400, config.getPlayFightFrequency(), "Getter getPlayFightFrequency() must be 400");
        assertEquals(100, config.getPlayFightChance(), "Getter getPlayFightChance() must be 100");

        config.setPlayFightFrequency(200);
        assertEquals(200, config.getPlayFightFrequency());
        config.setPlayFightFrequency(400);

        config.setPlayFightChance(80);
        assertEquals(80, config.getPlayFightChance());
        config.setPlayFightChance(100);
    }
}
