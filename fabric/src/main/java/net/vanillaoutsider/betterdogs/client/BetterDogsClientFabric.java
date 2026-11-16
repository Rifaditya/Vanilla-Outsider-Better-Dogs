// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.vanillaoutsider.betterdogs.network.BetterDogsClientCapPayload;

/**
 * Fabric client-side initialization for Better Dogs.
 * Registers receiver for optional client capability payload so servers can detect
 * presence of the mod on the client for custom howl audio.
 */
public class BetterDogsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(BetterDogsClientCapPayload.TYPE, (payload, ctx) -> {});
    }
}
