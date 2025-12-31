package net.vanillaoutsider.betterdogs

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object BetterDogs : ModInitializer {
    const val MOD_ID = "vanilla-outsider-better-dogs"
    private val logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        logger.info("Vanilla Outsider: Better Dogs initializing...")
        
        // Initialize wolf data persistence
        WolfDataAttachments.init()
        
        logger.info("Better Dogs initialized! Wolves have been enhanced.")
    }
}
