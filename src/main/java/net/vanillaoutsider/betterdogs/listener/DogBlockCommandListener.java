// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.listener;

import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;
import net.vanillaoutsider.betterdogs.util.DogSeatHelper;

/**
 * Dedicated single-purpose listener for player interactions with blocks,
 * handling chair/stair seat interaction and spawning invisible seats for dogs.
 */
public class DogBlockCommandListener {

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (hand != InteractionHand.MAIN_HAND || level.isClientSide()) {
                return InteractionResult.PASS;
            }
            if (!player.isSecondaryUseActive() || !DogSeatHelper.isCommandItem(player.getItemInHand(hand))) {
                return InteractionResult.PASS;
            }
            UUID dogUuid = DogCommandManager.getSelectedDog(player.getUUID());
            if (dogUuid == null) {
                return InteractionResult.PASS;
            }

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = level.getBlockState(pos);

            if (DogSeatHelper.isChairBlock(state)) {
                if (level instanceof ServerLevel serverLevel) {
                    Entity selectedEntity = serverLevel.getEntity(dogUuid);
                    if (selectedEntity instanceof Wolf wolf && wolf.isTame() && wolf.isOwnedBy(player) && wolf.isAlive()) {
                        if (wolf.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D) > DogSeatHelper.MAX_COMMAND_DISTANCE_SQ) {
                            player.sendOverlayMessage(Component.translatable("text.betterdogs.dog_too_far", wolf.getName()));
                            return InteractionResult.SUCCESS;
                        }

                        // Check if seat already exists to avoid duplication
                        AABB searchArea = new AABB(pos);
                        List<Entity> existingSeats = serverLevel.getEntities((Entity) null, searchArea,
                                e -> e.entityTags().contains("betterdogs:seat"));

                        Entity seatEntity;
                        if (!existingSeats.isEmpty()) {
                            seatEntity = existingSeats.get(0);
                        } else {
                            // Spawn invisible Interaction entity at seat height
                            Interaction seat = new Interaction(EntityTypes.INTERACTION, serverLevel);
                            seat.setWidth(0.5f);
                            seat.setHeight(0.5f);
                            seat.addTag("betterdogs:seat");
                            seat.setPos(pos.getX() + 0.5D, pos.getY() + 0.22D, pos.getZ() + 0.5D);
                            serverLevel.addFreshEntity(seat);
                            seatEntity = seat;
                        }

                        if (DogSeatHelper.hasPassengerSpace(seatEntity)) {
                            DogCommandManager.setVehicleTarget(wolf.getUUID(), seatEntity);
                            DogCommandManager.clearSelection(player.getUUID());
                            player.sendOverlayMessage(Component.translatable("text.betterdogs.dog_commanded_to_board", wolf.getName(), state.getBlock().getName()));
                            SoundEvent whineSound = ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().whineSound().value();
                            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                                    whineSound, wolf.getSoundSource(), 1.0f, 1.2f);
                            return InteractionResult.SUCCESS;
                        } else {
                            player.sendOverlayMessage(Component.translatable("text.betterdogs.seat_occupied"));
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }
}
