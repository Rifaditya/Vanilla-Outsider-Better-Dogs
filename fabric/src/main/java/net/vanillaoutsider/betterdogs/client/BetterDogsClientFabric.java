// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;

/**
 * Fabric client-side initialization for Better Dogs.
 * Registers receiver for optional client capability channel so servers can detect
 * presence of the mod on the client for custom howl audio.
 */
@Environment(EnvType.CLIENT)
public class BetterDogsClientFabric implements ClientModInitializer {

    public static final ResourceLocation CLIENT_AUDIO_CAP = new ResourceLocation("betterdogs", "client_audio_cap");

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(CLIENT_AUDIO_CAP, (client, handler, buf, responseSender) -> {
            // No-op receiver: mere presence registers channel in client handshake for ServerPlayNetworking.canSend query
        });
    }
}
