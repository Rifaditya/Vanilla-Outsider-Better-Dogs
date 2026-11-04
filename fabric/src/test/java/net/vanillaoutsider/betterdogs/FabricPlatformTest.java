// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.platform.FabricGameRuleHelper;
import net.vanillaoutsider.betterdogs.platform.FabricPlatformHelper;
import net.vanillaoutsider.betterdogs.platform.Services;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FabricPlatformTest {

    @Test
    @DisplayName("Pillar 1: Fabric Loader SPI Resolution & Identity")
    public void testFabricSpiResolution() {
        assertNotNull(Services.PLATFORM, "Platform service must not be null");
        assertEquals("Fabric", Services.PLATFORM.getPlatformName(), "Platform helper must identify as Fabric");
        assertTrue(Services.PLATFORM instanceof FabricPlatformHelper, "Platform helper must be FabricPlatformHelper");
    }

    @Test
    @DisplayName("Pillar 2: Fabric Platform Query Crash Safety")
    public void testFabricQueryCrashSafety() {
        assertDoesNotThrow(() -> Services.PLATFORM.isModLoaded("better_dogs"), "isModLoaded must not throw in test harness");
        assertDoesNotThrow(() -> Services.PLATFORM.isModLoaded("nonexistent_mod_xyz"), "isModLoaded must not throw for missing mods");
        assertFalse(Services.PLATFORM.isModLoaded("nonexistent_mod_xyz"), "Nonexistent mod must return false");
        assertDoesNotThrow(() -> Services.PLATFORM.isDevelopmentEnvironment(), "isDevelopmentEnvironment must not throw");
    }

    @Test
    @DisplayName("Pillar 3: Fabric GameRules Helper SPI Resolution")
    public void testFabricGameRulesResolution() {
        assertNotNull(Services.GAMERULES, "GameRules helper must not be null");
        assertTrue(Services.GAMERULES instanceof FabricGameRuleHelper, "GameRules helper must be FabricGameRuleHelper");
    }
}
