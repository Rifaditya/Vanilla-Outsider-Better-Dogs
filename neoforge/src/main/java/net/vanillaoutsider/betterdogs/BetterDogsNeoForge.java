// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.vanillaoutsider.betterdogs.command.BetterDogsCommand;
import net.vanillaoutsider.betterdogs.registry.BetterDogsSoundEvents;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;

@Mod("better_dogs")
public class BetterDogsNeoForge {

    public BetterDogsNeoForge(IEventBus modEventBus) {
        BetterDogs.init();
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::onRegister);
    }

    public void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.SOUND_EVENT)) {
            event.register(Registries.SOUND_EVENT,
                    BetterDogsSoundEvents.WOLF_HOWL_ID,
                    () -> BetterDogsSoundEvents.WOLF_HOWL);
        }
        if (event.getRegistryKey().equals(Registries.TRIGGER_TYPE)) {
            event.register(Registries.TRIGGER_TYPE, helper -> {
                helper.register(Identifier.fromNamespaceAndPath("betterdogs", "tame_wolf_personality"), BetterDogs.TAME_WOLF_PERSONALITY);
                helper.register(Identifier.fromNamespaceAndPath("betterdogs", "guard_wolf_personality"), BetterDogs.GUARD_WOLF_PERSONALITY);
                helper.register(Identifier.fromNamespaceAndPath("betterdogs", "inbred_wolf"), BetterDogs.INBRED_WOLF);
                helper.register(Identifier.fromNamespaceAndPath("betterdogs", "outcross_runt"), BetterDogs.OUTCROSS_RUNT);
                helper.register(Identifier.fromNamespaceAndPath("betterdogs", "cure_inbred"), BetterDogs.CURE_INBRED);
                helper.register(Identifier.fromNamespaceAndPath("betterdogs", "wolf_litter"), BetterDogs.WOLF_LITTER);
                helper.register(Identifier.fromNamespaceAndPath("betterdogs", "put_up_for_adoption"), BetterDogs.PUT_UP_FOR_ADOPTION);
                helper.register(Identifier.fromNamespaceAndPath("betterdogs", "on_patrol"), BetterDogs.ON_PATROL);
                helper.register(Identifier.fromNamespaceAndPath("betterdogs", "self_service"), BetterDogs.SELF_SERVICE);
            });
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
