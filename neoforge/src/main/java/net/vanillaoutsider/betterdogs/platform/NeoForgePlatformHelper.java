// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import java.nio.file.Path;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.vanillaoutsider.betterdogs.network.BetterDogsClientCapPayload;
import net.vanillaoutsider.betterdogs.platform.services.IPlatformHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        try {
            if (ModList.get() == null) {
                return false;
            }
            String altId = modId.replace("-", "_");
            return ModList.get().isLoaded(modId) || ModList.get().isLoaded(altId);
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        try {
            return !FMLEnvironment.isProduction();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public String getModVersion(String modId) {
        try {
            if (ModList.get() == null) {
                return "";
            }
            String altId = modId.replace("-", "_");
            java.util.Optional<? extends net.neoforged.fml.ModContainer> container = ModList.get().getModContainerById(modId);
            if (container.isEmpty()) {
                container = ModList.get().getModContainerById(altId);
            }
            return container.map(c -> c.getModInfo().getVersion().toString()).orElse("");
        } catch (Throwable t) {
            return "";
        }
    }

    @Override
    public void registerContent() {
        // Content registration is handled asynchronously via RegisterEvent on the NeoForge mod event bus
    }

    @Override
    public boolean hasBetterDogsClient(ServerPlayer player) {
        if (player == null || player.connection == null) {
            return false;
        }
        return player.connection.getConnection().isMemoryConnection()
                || NetworkRegistry.hasChannel(player.connection, BetterDogsClientCapPayload.TYPE.id());
    }
}
