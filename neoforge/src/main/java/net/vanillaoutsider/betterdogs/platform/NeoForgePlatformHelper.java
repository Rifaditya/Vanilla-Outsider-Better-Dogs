// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.vanillaoutsider.betterdogs.platform.services.IPlatformHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        try {
            String forgeId = modId.replace('-', '_');
            return ModList.get() != null && (ModList.get().isLoaded(modId) || ModList.get().isLoaded(forgeId));
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        try {
            return !FMLLoader.isProduction();
        } catch (Throwable t) {
            return false;
        }
    }
}
