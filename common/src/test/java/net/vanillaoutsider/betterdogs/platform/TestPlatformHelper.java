// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import java.nio.file.Path;
import net.vanillaoutsider.betterdogs.platform.services.IPlatformHelper;

public class TestPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "TestPlatform";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return false;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return true;
    }

    @Override
    public Path getConfigDirectory() {
        return Path.of("build/test-config");
    }

    @Override
    public String getModVersion(String modId) {
        return "5.7.11+26.1.2";
    }
}
