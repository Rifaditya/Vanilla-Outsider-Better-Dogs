// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) {
            try {
                Class<?> helperClass = Class.forName("net.vanillaoutsider.betterdogs.config.YaclScreenHelper");
                java.lang.reflect.Method method = helperClass.getMethod("createFactory");
                return (ConfigScreenFactory<?>) method.invoke(null);
            } catch (Exception e) {
                // Fail gracefully
            }
        }
        return parent -> null;
    }
}
