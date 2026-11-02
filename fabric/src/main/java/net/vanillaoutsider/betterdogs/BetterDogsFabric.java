// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.vanillaoutsider.betterdogs.command.BetterDogsCommand;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;

public class BetterDogsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BetterDogs.init();
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> DogCommandManager.handleEntityUse(player, level, hand, entity));
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> DogCommandManager.handleBlockUse(player, level, hand, hitResult));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> BetterDogsCommand.register(dispatcher));
    }
}
