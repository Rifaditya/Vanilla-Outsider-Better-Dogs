// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import io.netty.channel.local.LocalAddress;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.vanillaoutsider.betterdogs.network.BetterDogsClientCapPayload;
import net.vanillaoutsider.betterdogs.platform.services.IPlatformHelper;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        try {
            return FabricLoader.getInstance().isModLoaded(modId);
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        try {
            return FabricLoader.getInstance().isDevelopmentEnvironment();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean hasBetterDogsClient(ServerPlayer player) {
        if (player == null || player.connection == null) {
            return false;
        }
        try {
            return (player.connection.getRemoteAddress() instanceof LocalAddress)
                    || ServerPlayNetworking.canSend(player, BetterDogsClientCapPayload.TYPE);
        } catch (Throwable t) {
            return false;
        }
    }
}
