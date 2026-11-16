// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.vanillaoutsider.betterdogs.command.BetterDogsCommand;
import net.vanillaoutsider.betterdogs.network.BetterDogsClientCapPayload;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;
import net.vanillaoutsider.betterdogs.util.WolfHowlAudioHelper;

public class BetterDogsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BetterDogs.init();
        BetterDogs.registerCriteriaTriggers();
        net.vanillaoutsider.betterdogs.world.BetterDogsSpawningFabric.registerSpawns();
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> DogCommandManager.handleEntityUse(player, level, hand, entity));
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> DogCommandManager.handleBlockUse(player, level, hand, hitResult));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> BetterDogsCommand.register(dispatcher));

        // Optional client capability payload for vanilla client compatibility (BL-BD-064)
        PayloadTypeRegistry.playS2C().register(BetterDogsClientCapPayload.TYPE, BetterDogsClientCapPayload.CODEC);
        WolfHowlAudioHelper.setClientCapabilityChecker(player ->
                (player.connection.getRemoteAddress() instanceof io.netty.channel.local.LocalAddress)
                        || ServerPlayNetworking.canSend(player, BetterDogsClientCapPayload.TYPE));
    }
}
