// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import net.fabricmc.loader.api.FabricLoader;
import net.vanillaoutsider.betterdogs.platform.services.IPlatformHelper;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        try {
            return FabricLoader.getInstance().isModLoaded(modId);
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        try {
            return FabricLoader.getInstance().isDevelopmentEnvironment();
        } catch (Throwable t) {
            return false;
        }
    }
}
