// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.client;

import net.fabricmc.api.ClientModInitializer;
import net.vanillaoutsider.betterdogs.BetterDogs;

/**
 * Fabric client-side initialization.
 */
public class BetterDogsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BetterDogs.LOGGER.info("Better Dogs client initialized (Fabric)");
        // Client-side initialization (particles are handled in mixin)
    }
}
