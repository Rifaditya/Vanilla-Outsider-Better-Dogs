// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Event-driven manager handling player commands, dog selection, and vehicle/seat interactions.
 */
public class DogCommandManager {

    private static final Map<UUID, UUID> SELECTED_DOGS = new ConcurrentHashMap<>();
    private static final Map<UUID, Entity> VEHICLE_TARGETS = new ConcurrentHashMap<>();

    public static void selectDog(UUID playerUuid, UUID dogUuid) {
        if (playerUuid != null && dogUuid != null) {
            SELECTED_DOGS.put(playerUuid, dogUuid);
        }
    }

    public static UUID getSelectedDog(UUID playerUuid) {
        if (playerUuid == null) {
            return null;
        }
        return SELECTED_DOGS.get(playerUuid);
    }

    public static void clearSelection(UUID playerUuid) {
        if (playerUuid != null) {
            SELECTED_DOGS.remove(playerUuid);
        }
    }

    public static void setVehicleTarget(UUID dogUuid, Entity vehicle) {
        if (dogUuid != null && vehicle != null) {
            VEHICLE_TARGETS.put(dogUuid, vehicle);
        }
    }

    public static Entity getVehicleTarget(UUID dogUuid) {
        if (dogUuid == null) {
            return null;
        }
        return VEHICLE_TARGETS.get(dogUuid);
    }

    public static void clearVehicleTarget(UUID dogUuid) {
        if (dogUuid != null) {
            VEHICLE_TARGETS.remove(dogUuid);
        }
    }

    public static boolean isSitTarget(Entity entity) {
        return DogSeatHelper.isSitTarget(entity);
    }

    public static boolean hasPassengerSpace(Entity vehicle) {
        return DogSeatHelper.hasPassengerSpace(vehicle);
    }

    public static boolean isCommandItem(ItemStack stack) {
        return DogSeatHelper.isCommandItem(stack);
    }

    /**
     * Registers Fabric interaction events to handle Stick & Command Item commands.
     */
    public static void registerEvents() {
        // 1. Intercepting player clicking on an ENTITY (vehicle/seat or direct vehicle dismount)
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (hand != InteractionHand.MAIN_HAND || level.isClientSide()) {
                return InteractionResult.PASS;
            }
            if (!player.isSecondaryUseActive()) {
                return InteractionResult.PASS;
            }
            ItemStack held = player.getItemInHand(hand);
            if (!isCommandItem(held) && !held.isEmpty()) {
                return InteractionResult.PASS;
            }

            UUID dogUuid = getSelectedDog(player.getUUID());
            if (dogUuid == null) {
                // Direct click on vehicle with passenger dogs owned by the player
                if (entity instanceof AbstractMinecart || entity instanceof Boat || isSitTarget(entity) || !entity.getPassengers().isEmpty()) {
                    if (level instanceof ServerLevel serverLevel) {
                        for (Entity passenger : entity.getPassengers()) {
                            if (passenger instanceof Wolf wolf && wolf.isTame() && wolf.isOwnedBy(player) && wolf.isAlive()) {
                                wolf.stopRiding();
                                clearVehicleTarget(wolf.getUUID());
                                clearSelection(player.getUUID());

                                // Safely offset position outside vehicle/minecart collision envelope
                                Vec3 offset = DogSeatHelper.calculateDismountOffset(player.getLookAngle(), DogSeatHelper.DEFAULT_DISMOUNT_DISTANCE);
                                wolf.setPos(wolf.getX() + offset.x, wolf.getY(), wolf.getZ() + offset.z);

                                if (entity.entityTags().contains("betterdogs:seat")) {
                                    entity.discard();
                                }

                                player.sendOverlayMessage(Component.translatable("text.betterdogs.dog_dismounted", wolf.getName()));
                                net.minecraft.sounds.SoundEvent ambientSound = ((WolfAccessor) wolf).betterdogs$invokeGetAmbientSound();
                                serverLevel.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(),
                                        ambientSound, wolf.getSoundSource(), 1.0f, 1.0f);
                                serverLevel.sendParticles(ParticleTypes.CLOUD, wolf.getX(), wolf.getY() + 0.3, wolf.getZ(), 8, 0.2, 0.2, 0.2, 0.05);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
                return InteractionResult.PASS;
            }

            if (level instanceof ServerLevel serverLevel) {
                Entity selectedEntity = serverLevel.getEntity(dogUuid);
                if (selectedEntity instanceof Wolf wolf && wolf.isTame() && wolf.isOwnedBy(player) && wolf.isAlive()) {
                    // Range check (12 blocks = 144.0D squared)
                    if (wolf.distanceToSqr(entity) > DogSeatHelper.MAX_COMMAND_DISTANCE_SQ) {
                        player.sendOverlayMessage(Component.translatable("text.betterdogs.dog_too_far", wolf.getName()));
                        return InteractionResult.SUCCESS;
                    }

                    boolean unrestricted = DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_ALLOW_UNRESTRICTED_RIDING);
                    if (unrestricted || isSitTarget(entity)) {
                        if (hasPassengerSpace(entity)) {
                            setVehicleTarget(wolf.getUUID(), entity);
                            clearSelection(player.getUUID());
                            wolf.setOrderedToSit(false);
                            player.sendOverlayMessage(Component.translatable("text.betterdogs.dog_commanded_to_board", wolf.getName(), entity.getName()));
                            net.minecraft.sounds.SoundEvent whineSound = ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().whineSound().value();
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

        // 2. Intercepting player clicking on a BLOCK (chair/stair)
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (hand != InteractionHand.MAIN_HAND || level.isClientSide()) {
                return InteractionResult.PASS;
            }
            if (!player.isSecondaryUseActive() || !isCommandItem(player.getItemInHand(hand))) {
                return InteractionResult.PASS;
            }
            UUID dogUuid = getSelectedDog(player.getUUID());
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

                        if (hasPassengerSpace(seatEntity)) {
                            setVehicleTarget(wolf.getUUID(), seatEntity);
                            clearSelection(player.getUUID());
                            player.sendOverlayMessage(Component.translatable("text.betterdogs.dog_commanded_to_board", wolf.getName(), state.getBlock().getName()));
                            net.minecraft.sounds.SoundEvent whineSound = ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().whineSound().value();
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
