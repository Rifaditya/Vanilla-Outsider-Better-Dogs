// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import io.netty.channel.local.LocalAddress;
import java.nio.file.Path;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.vanillaoutsider.betterdogs.BetterDogs;
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
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public String getModVersion(String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("");
    }

    @Override
    public void registerContent() {
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "tame_wolf_personality"), BetterDogs.TAME_WOLF_PERSONALITY);
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "guard_wolf_personality"), BetterDogs.GUARD_WOLF_PERSONALITY);
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "inbred_wolf"), BetterDogs.INBRED_WOLF);
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "outcross_runt"), BetterDogs.OUTCROSS_RUNT);
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "cure_inbred"), BetterDogs.CURE_INBRED);
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "wolf_litter"), BetterDogs.WOLF_LITTER);
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "put_up_for_adoption"), BetterDogs.PUT_UP_FOR_ADOPTION);
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "on_patrol"), BetterDogs.ON_PATROL);
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "self_service"), BetterDogs.SELF_SERVICE);
    }

    @Override
    public boolean hasBetterDogsClient(ServerPlayer player) {
        if (player == null || player.connection == null) {
            return false;
        }
        return (player.connection.getRemoteAddress() instanceof LocalAddress)
                || ServerPlayNetworking.canSend(player, BetterDogsClientCapPayload.TYPE);
    }
}
