// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.vanillaoutsider.betterdogs.platform.services.IPlatformHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        try {
            String forgeId = modId.replace("-", "_");
            return ModList.get() != null && (ModList.get().isLoaded(modId) || ModList.get().isLoaded(forgeId));
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        try {
            return !FMLEnvironment.production;
        } catch (Throwable t) {
            return false;
        }
    }
}
