// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.vanillaoutsider.betterdogs.command.BetterDogsCommand;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;

@Mod("better_dogs")
public class BetterDogsNeoForge {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "better_dogs");

    public static final Supplier<AttachmentType<WolfPersistentData>> WOLF_DATA =
            ATTACHMENT_TYPES.register("wolf_data", () -> AttachmentType.builder(() -> WolfPersistentData.DEFAULT)
                    .serialize(WolfPersistentData.MAP_CODEC)
                    .build());

    public BetterDogsNeoForge(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
        modEventBus.addListener(this::onRegister);
        BetterDogs.init();
        NeoForge.EVENT_BUS.register(this);
    }

    private void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.TRIGGER_TYPE)) {
            event.register(Registries.TRIGGER_TYPE, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "tame_wolf_personality"), () -> BetterDogs.TAME_WOLF_PERSONALITY);
            event.register(Registries.TRIGGER_TYPE, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "guard_wolf_personality"), () -> BetterDogs.GUARD_WOLF_PERSONALITY);
            event.register(Registries.TRIGGER_TYPE, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "inbred_wolf"), () -> BetterDogs.INBRED_WOLF);
            event.register(Registries.TRIGGER_TYPE, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "outcross_runt"), () -> BetterDogs.OUTCROSS_RUNT);
            event.register(Registries.TRIGGER_TYPE, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "cure_inbred"), () -> BetterDogs.CURE_INBRED);
            event.register(Registries.TRIGGER_TYPE, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "wolf_litter"), () -> BetterDogs.WOLF_LITTER);
            event.register(Registries.TRIGGER_TYPE, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "put_up_for_adoption"), () -> BetterDogs.PUT_UP_FOR_ADOPTION);
            event.register(Registries.TRIGGER_TYPE, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "on_patrol"), () -> BetterDogs.ON_PATROL);
            event.register(Registries.TRIGGER_TYPE, Identifier.fromNamespaceAndPath(BetterDogs.MOD_ID, "self_service"), () -> BetterDogs.SELF_SERVICE);
        }
        if (event.getRegistryKey().equals(Registries.SOUND_EVENT)) {
            event.register(Registries.SOUND_EVENT, Identifier.fromNamespaceAndPath("betterdogs", "entity.wolf.howl"), () -> BetterDogs.WOLF_HOWL);
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        BetterDogsCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = DogCommandManager.handleEntityInteract(
                event.getEntity(), event.getLevel(), event.getHand(), event.getTarget()
        );
        if (result.consumesAction()) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = DogCommandManager.handleBlockInteract(
                event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec()
        );
        if (result.consumesAction()) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }
}
