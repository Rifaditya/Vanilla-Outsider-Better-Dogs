package net.vanillaoutsider.betterdogs.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return ClothConfigScreenHelper.createFactory();
        }
        return parent -> null;
    }
}
