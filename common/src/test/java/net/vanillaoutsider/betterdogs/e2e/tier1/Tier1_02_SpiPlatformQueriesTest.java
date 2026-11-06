// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier1;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.vanillaoutsider.betterdogs.e2e.mock.IPlatformHelper;
import net.vanillaoutsider.betterdogs.e2e.mock.MockPlatformHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 1 - Feature 02: Java SPI Platform Queries Tests")
public class Tier1_02_SpiPlatformQueriesTest {

    @Test
    @DisplayName("Assert platform name query resolves expected loader identifier")
    public void testPlatformNameResolution() {
        MockPlatformHelper helper = new MockPlatformHelper();
        assertEquals("Fabric", helper.getPlatformName(), "Default mock platform must be Fabric");

        helper.setPlatformName("NeoForge");
        assertEquals("NeoForge", helper.getPlatformName(), "Platform name must reflect NeoForge");
    }

    @Test
    @DisplayName("Assert isModLoaded returns correct status for installed and missing mods")
    public void testModLoadedQuery() {
        IPlatformHelper helper = new MockPlatformHelper();
        assertTrue(helper.isModLoaded("vanilla-outsider-better-dogs"), "Self mod ID must be reported as loaded");
        assertTrue(helper.isModLoaded("dasik-library"), "DasikLibrary must be reported as loaded");
        assertFalse(helper.isModLoaded("non_existent_addon_xyz"), "Uninstalled mod must return false");
        assertFalse(helper.isModLoaded(null), "Null mod ID must safely return false");
    }

    @Test
    @DisplayName("Assert development environment detection query contract")
    public void testDevelopmentEnvironmentQuery() {
        MockPlatformHelper helper = new MockPlatformHelper();
        assertTrue(helper.isDevelopmentEnvironment(), "Development environment should default to true in tests");

        helper.setDevelopmentEnvironment(false);
        assertFalse(helper.isDevelopmentEnvironment(), "Must reflect production environment when configured");
    }

    @Test
    @DisplayName("Assert config directory path query returns valid non-null path")
    public void testConfigDirectoryQuery() {
        MockPlatformHelper helper = new MockPlatformHelper();
        Path configDir = helper.getConfigDirectory();
        assertNotNull(configDir, "Config directory path must never be null");
        assertEquals(Paths.get("config"), configDir, "Default config directory should point to config");
    }

    @Test
    @DisplayName("Assert mod version query returns semantic version string matching Minecraft anchor")
    public void testModVersionQuery() {
        IPlatformHelper helper = new MockPlatformHelper();
        String version = helper.getModVersion("vanilla-outsider-better-dogs");
        assertNotNull(version, "Mod version string must not be null");
        assertTrue(version.contains("26.1.2"), "Version must target 26.1.2 anchor: " + version);
    }
}
