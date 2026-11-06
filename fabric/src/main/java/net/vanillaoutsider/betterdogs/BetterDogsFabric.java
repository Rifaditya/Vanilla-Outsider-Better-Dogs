// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.resources.Identifier;
import net.vanillaoutsider.betterdogs.command.BetterDogsCommand;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;
import net.vanillaoutsider.betterdogs.world.BetterDogsSpawningFabric;

public class BetterDogsFabric implements ModInitializer {

    public static final AttachmentType<WolfPersistentData> WOLF_DATA = AttachmentRegistry.createPersistent(
            Identifier.parse("betterdogs:wolf_data"),
            WolfPersistentData.CODEC);

    @Override
    public void onInitialize() {
        // Initialize common logic
        BetterDogs.init();

        // Register commands on Fabric
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            BetterDogsCommand.register(dispatcher);
        });

        // Register entity & block interaction callbacks
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            return DogCommandManager.handleEntityInteract(player, level, hand, entity);
        });

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            return DogCommandManager.handleBlockInteract(player, level, hand, hitResult);
        });

        // Register Fabric biome modifications
        BetterDogsSpawningFabric.registerSpawns();
    }
}
