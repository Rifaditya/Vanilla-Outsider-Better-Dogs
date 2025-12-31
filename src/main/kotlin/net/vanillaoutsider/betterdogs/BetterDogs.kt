package net.vanillaoutsider.betterdogs

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

import net.vanillaoutsider.betterdogs.config.BetterDogsConfig
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer

object BetterDogs : ModInitializer {
    const val MOD_ID = "vanilla-outsider-better-dogs"
    private val logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        logger.info("Vanilla Outsider: Better Dogs initializing...")

        // Register Config
        AutoConfig.register(BetterDogsConfig::class.java, ::GsonConfigSerializer)
        
        // Initialize wolf data persistence
        WolfDataAttachments.init()
        
        logger.info("Better Dogs initialized! Wolves have been enhanced.")
    }
}
