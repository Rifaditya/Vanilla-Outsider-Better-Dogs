package net.vanillaoutsider.betterdogs.client;

import net.fabricmc.api.ClientModInitializer;
import net.vanillaoutsider.betterdogs.BetterDogs;
import org.slf4j.LoggerFactory;

public class BetterDogsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BetterDogs.LOGGER.info("Better Dogs client initialized");
        // Client-side initialization (particles are handled in mixin)
    }
}
