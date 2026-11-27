// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.config.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import java.lang.reflect.Method;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        try {
            if (FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) {
                return wrapScreenMethod(Class.forName("net.vanillaoutsider.betterdogs.config.client.YaclScreenHelper"));
            }
            if (FabricLoader.getInstance().isModLoaded("cloth-config2") || FabricLoader.getInstance().isModLoaded("cloth_config")) {
                return wrapScreenMethod(Class.forName("net.vanillaoutsider.betterdogs.config.client.ClothConfigScreenHelper"));
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
        }
        return parent -> null;
    }

    private static ConfigScreenFactory<?> wrapScreenMethod(Class<?> helperClass) throws ReflectiveOperationException {
        Method createScreen = helperClass.getMethod("createScreen", Screen.class);
        return parent -> {
            try {
                return (Screen) createScreen.invoke(null, parent);
            } catch (ReflectiveOperationException | LinkageError ignored) {
                return null;
            }
        };
    }
}
