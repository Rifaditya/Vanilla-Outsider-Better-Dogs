// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
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
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BetterDogsGameRules.register();
    }

    @Test
    @DisplayName("Assert dog play choreography GameRules are registered and non-null")
    public void testGameRulesRegistered() {
        assertNotNull(BetterDogsGameRules.BD_DOG_PLAY_CHOREOGRAPHY_ENABLED,
                "BD_DOG_PLAY_CHOREOGRAPHY_ENABLED must be registered and non-null");
        assertNotNull(BetterDogsGameRules.BD_PLAY_CHOREOGRAPHY_FREQUENCY,
                "BD_PLAY_CHOREOGRAPHY_FREQUENCY must be registered and non-null");
        assertNotNull(BetterDogsGameRules.BD_MAX_PLAY_DISTANCE,
                "BD_MAX_PLAY_DISTANCE must be registered and non-null");
    }

    @Test
    @DisplayName("Assert GameRule default values are correct")
    public void testGameRuleDefaults() {
        assertTrue(BetterDogsGameRules.BD_DOG_PLAY_CHOREOGRAPHY_ENABLED.defaultValue(),
                "Default dogPlayChoreographyEnabled must be true");
        assertEquals(2400, BetterDogsGameRules.BD_PLAY_CHOREOGRAPHY_FREQUENCY.defaultValue(),
                "Default playChoreographyFrequency must be 2400 ticks (2 minutes)");
        assertEquals(16, BetterDogsGameRules.BD_MAX_PLAY_DISTANCE.defaultValue(),
                "Default maxPlayDistance must be 16 blocks");
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
    }

    @Test
    @DisplayName("Assert DynamicGameRuleManager resolves both bd_ and camelCase alias keys")
    public void testDynamicGameRuleAliases() {
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_dog_play_choreography_enabled"),
                "DynamicGameRuleManager must find bd_dog_play_choreography_enabled");
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_play_choreography_frequency"),
                "DynamicGameRuleManager must find bd_play_choreography_frequency");
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_max_play_distance"),
                "DynamicGameRuleManager must find bd_max_play_distance");

        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:dogPlayChoreographyEnabled"),
                "DynamicGameRuleManager must resolve alias dogPlayChoreographyEnabled");
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:playChoreographyFrequency"),
                "DynamicGameRuleManager must resolve alias playChoreographyFrequency");
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:maxPlayDistance"),
                "DynamicGameRuleManager must resolve alias maxPlayDistance");
    }

    @Test
    @DisplayName("Assert BetterDogsConfig provides matching default fields and getters")
    public void testConfigDefaults() {
        BetterDogsConfig config = BetterDogsConfig.get();
        assertNotNull(config, "BetterDogsConfig instance must be present");
        assertTrue(config.dogPlayChoreographyEnabled, "Config default dogPlayChoreographyEnabled must be true");
        assertEquals(2400, config.playChoreographyFrequency, "Config default playChoreographyFrequency must be 2400");
        assertEquals(16, config.maxPlayDistance, "Config default maxPlayDistance must be 16");

        assertTrue(config.getDogPlayChoreographyEnabled(), "Getter getDogPlayChoreographyEnabled() must be true");
        assertEquals(2400, config.getPlayChoreographyFrequency(), "Getter getPlayChoreographyFrequency() must be 2400");
        assertEquals(16, config.getMaxPlayDistance(), "Getter getMaxPlayDistance() must be 16");
    }
}
