// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.platform.NeoForgeGameRuleHelper;
import net.vanillaoutsider.betterdogs.platform.NeoForgePlatformHelper;
import net.vanillaoutsider.betterdogs.platform.Services;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NeoForgePlatformTest {

    @Test
    @DisplayName("Pillar 1: NeoForge Loader SPI Resolution & Identity")
    public void testNeoForgeSpiResolution() {
        assertNotNull(Services.PLATFORM, "Platform service must not be null");
        assertEquals("NeoForge", Services.PLATFORM.getPlatformName(), "Platform helper must identify as NeoForge");
        assertTrue(Services.PLATFORM instanceof NeoForgePlatformHelper, "Platform helper must be NeoForgePlatformHelper");
    }

    @Test
    @DisplayName("Pillar 2: NeoForge Platform Query Crash Safety")
    public void testNeoForgeQueryCrashSafety() {
        assertDoesNotThrow(() -> Services.PLATFORM.isModLoaded("better_dogs"), "isModLoaded must not throw in test harness");
        assertDoesNotThrow(() -> Services.PLATFORM.isModLoaded("nonexistent_mod_xyz"), "isModLoaded must not throw for missing mods");
        assertFalse(Services.PLATFORM.isModLoaded("nonexistent_mod_xyz"), "Nonexistent mod must return false");
        assertDoesNotThrow(() -> Services.PLATFORM.isDevelopmentEnvironment(), "isDevelopmentEnvironment must not throw");
    }

    @Test
    @DisplayName("Pillar 3: NeoForge GameRules Helper SPI Resolution")
    public void testNeoForgeGameRulesResolution() {
        assertNotNull(Services.GAMERULES, "GameRules helper must not be null");
        assertTrue(Services.GAMERULES instanceof NeoForgeGameRuleHelper, "GameRules helper must be NeoForgeGameRuleHelper");
    }
}
