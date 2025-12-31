package net.vanillaoutsider.betterdogs.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.autoconfig.AutoConfig

import net.minecraft.client.gui.screens.Screen

class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent: Screen -> 
            AutoConfig.getConfigScreen(BetterDogsConfig::class.java, parent).get()
        }
    }
}
