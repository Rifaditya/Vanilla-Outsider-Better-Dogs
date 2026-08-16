// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.registry.BetterDogsTags;

/**
 * Dedicated manager for selecting dogs with command items and dispatching vehicle/seat boarding commands.
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
        return playerUuid != null ? SELECTED_DOGS.get(playerUuid) : null;
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
        return dogUuid != null ? VEHICLE_TARGETS.get(dogUuid) : null;
    }

    public static void clearVehicleTarget(UUID dogUuid) {
        if (dogUuid != null) {
            VEHICLE_TARGETS.remove(dogUuid);
        }
    }

    public static boolean isSitTarget(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof Boat || entity instanceof Minecart || entity instanceof AbstractHorse || entity instanceof Camel) {
            return true;
        }
        if (entity instanceof Pig pig && pig.isSaddled()) {
            return true;
        }
        if (entity instanceof Strider strider && strider.isSaddled()) {
            return true;
        }
        String className = entity.getClass().getSimpleName().toLowerCase();
        String typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString().toLowerCase();
        return className.contains("seat") || className.contains("chair") || className.contains("stool") || className.contains("bench") || className.contains("mount")
                || typeId.contains("seat") || typeId.contains("chair") || typeId.contains("stool") || typeId.contains("bench") || typeId.contains("mount");
    }

    public static boolean hasPassengerSpace(Entity vehicle) {
        if (vehicle == null) {
            return false;
        }
        if (vehicle instanceof Boat || vehicle instanceof Camel) {
            return vehicle.getPassengers().size() < 2;
        }
        return vehicle.getPassengers().isEmpty();
    }

    public static boolean isCommandItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.is(Items.BONE)) {
            return false;
        }
        return stack.is(BetterDogsTags.COMMAND_ITEMS)
                || stack.is(Items.STICK)
                || stack.is(Items.BLAZE_ROD);
    }

    public static void registerEvents() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
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

            if (level instanceof ServerLevel serverLevel) {
                Entity selectedEntity = serverLevel.getEntity(dogUuid);
                if (selectedEntity instanceof Wolf wolf && wolf.isTame() && wolf.isOwnedBy(player) && wolf.isAlive()) {
                    if (wolf.distanceToSqr(entity) > 144.0D) {
                        WolfFeedbackHelper.sendFeedback(player, level, Component.translatable("text.betterdogs.dog_too_far", wolf.getName()));
                        return InteractionResult.SUCCESS;
                    }

                    boolean unrestricted = BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_ALLOW_UNRESTRICTED_RIDING, false);
                    if (unrestricted || isSitTarget(entity)) {
                        if (hasPassengerSpace(entity)) {
                            setVehicleTarget(wolf.getUUID(), entity);
                            clearSelection(player.getUUID());
                            wolf.setOrderedToSit(false);
                            WolfFeedbackHelper.sendFeedback(player, level, Component.translatable("text.betterdogs.dog_commanded_to_board", wolf.getName(), entity.getName()));
                            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.WOLF_WHINE, wolf.getSoundSource(), 1.0f, 1.2f);
                            return InteractionResult.SUCCESS;
                        } else {
                            WolfFeedbackHelper.sendFeedback(player, level, Component.translatable("text.betterdogs.seat_occupied"));
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        });

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
            String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString().toLowerCase();

            boolean isChair = blockId.contains("chair") || blockId.contains("seat") || blockId.contains("stool") || blockId.contains("bench")
                    || state.is(BlockTags.STAIRS);

            if (isChair) {
                if (level instanceof ServerLevel serverLevel) {
                    Entity selectedEntity = serverLevel.getEntity(dogUuid);
                    if (selectedEntity instanceof Wolf wolf && wolf.isTame() && wolf.isOwnedBy(player) && wolf.isAlive()) {
                        if (wolf.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D) > 144.0D) {
                            WolfFeedbackHelper.sendFeedback(player, level, Component.translatable("text.betterdogs.dog_too_far", wolf.getName()));
                            return InteractionResult.SUCCESS;
                        }

                        AABB searchArea = new AABB(pos);
                        List<Entity> existingSeats = serverLevel.getEntities((Entity) null, searchArea,
                                e -> e.getTags().contains("betterdogs:seat"));

                        Entity seatEntity;
                        if (!existingSeats.isEmpty()) {
                            seatEntity = existingSeats.get(0);
                        } else {
                            Interaction seat = new Interaction(EntityType.INTERACTION, serverLevel);
                            seat.addTag("betterdogs:seat");
                            seat.setPos(pos.getX() + 0.5D, pos.getY() + 0.22D, pos.getZ() + 0.5D);
                            serverLevel.addFreshEntity(seat);
                            seatEntity = seat;
                        }

                        if (hasPassengerSpace(seatEntity)) {
                            setVehicleTarget(wolf.getUUID(), seatEntity);
                            clearSelection(player.getUUID());
                            wolf.setOrderedToSit(false);
                            WolfFeedbackHelper.sendFeedback(player, level, Component.translatable("text.betterdogs.dog_commanded_to_board", wolf.getName(), state.getBlock().getName()));
                            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.WOLF_WHINE, wolf.getSoundSource(), 1.0f, 1.2f);
                            return InteractionResult.SUCCESS;
                        } else {
                            WolfFeedbackHelper.sendFeedback(player, level, Component.translatable("text.betterdogs.seat_occupied"));
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }
}
