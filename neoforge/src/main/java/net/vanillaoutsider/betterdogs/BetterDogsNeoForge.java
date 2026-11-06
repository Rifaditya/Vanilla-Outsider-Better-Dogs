// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.function.Supplier;
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
        BetterDogs.init();
        NeoForge.EVENT_BUS.register(this);
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
