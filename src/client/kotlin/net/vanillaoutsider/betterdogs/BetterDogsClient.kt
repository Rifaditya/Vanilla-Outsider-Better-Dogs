package net.vanillaoutsider.betterdogs

import net.fabricmc.api.ClientModInitializer
import org.slf4j.LoggerFactory

object BetterDogsClient : ClientModInitializer {
    private val logger = LoggerFactory.getLogger(BetterDogs.MOD_ID)
    
    override fun onInitializeClient() {
        logger.info("Better Dogs client initialized")
        // Client-side initialization (particles are handled in mixin)
    }
}
