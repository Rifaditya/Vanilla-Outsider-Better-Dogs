// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.vanillaoutsider.betterdogs.command.BetterDogsCommand;

public class BetterDogsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BetterDogs.init();
        net.vanillaoutsider.betterdogs.util.DogCommandManager.registerEvents();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> BetterDogsCommand.register(dispatcher));
    }
}
