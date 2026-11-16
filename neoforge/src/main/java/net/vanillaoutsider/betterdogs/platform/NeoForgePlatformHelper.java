// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import io.netty.channel.local.LocalAddress;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.ConnectionData;
import net.minecraftforge.network.NetworkHooks;
import net.vanillaoutsider.betterdogs.platform.services.IPlatformHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        try {
            String forgeId = modId.replace('-', '_');
            return ModList.get() != null && (ModList.get().isLoaded(modId) || ModList.get().isLoaded(forgeId));
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        try {
            return !FMLLoader.isProduction();
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
            if (player.connection.getRemoteAddress() instanceof LocalAddress) {
                return true;
            }
            Connection conn = player.connection.connection;
            if (conn.isMemoryConnection()) {
                return true;
            }
            ConnectionData data = NetworkHooks.getConnectionData(conn);
            return data != null && data.getModList().contains("better_dogs");
        } catch (Throwable t) {
            return false;
        }
    }
}
