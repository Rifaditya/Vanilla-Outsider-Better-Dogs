// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.vanillaoutsider.betterdogs.command.BetterDogsCommand;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;

@Mod("better_dogs")
public class BetterDogsNeoForge {

    public BetterDogsNeoForge(IEventBus modEventBus) {
        BetterDogs.init();
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::onRegister);
    }

    public void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.SOUND_EVENT)) {
            event.register(net.minecraft.core.registries.Registries.SOUND_EVENT,
                    net.vanillaoutsider.betterdogs.registry.BetterDogsSoundEvents.WOLF_HOWL_ID,
                    () -> net.vanillaoutsider.betterdogs.registry.BetterDogsSoundEvents.WOLF_HOWL);
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        BetterDogsCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = DogCommandManager.handleEntityUse(
                event.getEntity(), event.getLevel(), event.getHand(), event.getTarget()
        );
        if (result.consumesAction()) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = DogCommandManager.handleBlockUse(
                event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec()
        );
        if (result.consumesAction()) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }
}
