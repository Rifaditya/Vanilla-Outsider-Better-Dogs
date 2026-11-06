// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.mock;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Mock implementation of IPlatformHelper for headless opaque-box testing.
 */
public class MockPlatformHelper implements IPlatformHelper {

    private String platformName = "Fabric";
    private boolean devEnvironment = true;
    private Path configDirectory = Paths.get("config");
    private String modVersion = "5.7.11+26.1.2";
    private final Set<String> loadedMods = new HashSet<>();

    public MockPlatformHelper() {
        this.loadedMods.add("vanilla-outsider-better-dogs");
        this.loadedMods.add("dasik-library");
        this.loadedMods.add("minecraft");
    }

    @Override
    public String getPlatformName() {
        return this.platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return modId != null && this.loadedMods.contains(modId);
    }

    public void setModLoaded(String modId, boolean loaded) {
        if (loaded) {
            this.loadedMods.add(modId);
        } else {
            this.loadedMods.remove(modId);
        }
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return this.devEnvironment;
    }

    public void setDevelopmentEnvironment(boolean devEnvironment) {
        this.devEnvironment = devEnvironment;
    }

    @Override
    public Path getConfigDirectory() {
        return this.configDirectory;
    }

    public void setConfigDirectory(Path configDirectory) {
        this.configDirectory = configDirectory;
    }

    @Override
    public String getModVersion(String modId) {
        if ("vanilla-outsider-better-dogs".equals(modId)) {
            return this.modVersion;
        }
        return "1.0.0";
    }

    public void setModVersion(String modVersion) {
        this.modVersion = modVersion;
    }
}
