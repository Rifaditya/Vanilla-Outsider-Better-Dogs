// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.dasik.social.api.config.GuiHelper;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return GuiHelper.getOptionalFactory(
                "vanilla-outsider-better-dogs",
                "net.vanillaoutsider.betterdogs.config.ClothConfigScreenHelper",
                "createFactory"
        );
    }
}
