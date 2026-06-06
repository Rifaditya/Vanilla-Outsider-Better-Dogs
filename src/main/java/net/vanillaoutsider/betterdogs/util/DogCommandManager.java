/*
 * Vanilla Outsider: Better Dogs
 * Selection and command manager for dog mounting features.
 */
package net.vanillaoutsider.betterdogs.util;

import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;

public class DogCommandManager {
    // Map from Player UUID to Selected Wolf UUID
    private static final Map<UUID, UUID> SELECTED_DOGS = new ConcurrentHashMap<>();
    // Map from Wolf UUID to Target Vehicle Entity
    private static final Map<UUID, Entity> VEHICLE_TARGETS = new ConcurrentHashMap<>();

    public static void selectDog(UUID playerUuid, UUID dogUuid) {
        SELECTED_DOGS.put(playerUuid, dogUuid);
    }

    public static UUID getSelectedDog(UUID playerUuid) {
        return SELECTED_DOGS.get(playerUuid);
    }

    public static void clearSelection(UUID playerUuid) {
        SELECTED_DOGS.remove(playerUuid);
    }

    public static void setVehicleTarget(UUID dogUuid, Entity vehicle) {
        VEHICLE_TARGETS.put(dogUuid, vehicle);
    }

    public static Entity getVehicleTarget(UUID dogUuid) {
        return VEHICLE_TARGETS.get(dogUuid);
    }

    public static void clearVehicleTarget(UUID dogUuid) {
        VEHICLE_TARGETS.remove(dogUuid);
    }

    /**
     * Checks if the entity is a valid player seat (or standard vehicle/mount).
     */
    public static boolean isSitTarget(Entity entity) {
        if (entity instanceof Boat || entity instanceof AbstractMinecart || entity instanceof AbstractHorse || entity instanceof Camel) {
            return true;
        }
        if (entity instanceof Pig pig && pig.isSaddled()) {
            return true;
        }
        if (entity instanceof Strider strider && strider.isSaddled()) {
            return true;
        }
        
        // Dynamic check for modded chairs, benches, stools, and seats
        String className = entity.getClass().getSimpleName().toLowerCase();
        String typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString().toLowerCase();
        return className.contains("seat") || className.contains("chair") || className.contains("stool") || className.contains("bench") || className.contains("mount")
            || typeId.contains("seat") || typeId.contains("chair") || typeId.contains("stool") || typeId.contains("bench") || typeId.contains("mount");
    }

    /**
     * Checks if the vehicle has empty passenger slots available.
     */
    public static boolean hasPassengerSpace(Entity vehicle) {
        if (vehicle instanceof Boat) {
            return vehicle.getPassengers().size() < 2;
        }
        if (vehicle instanceof Camel) {
            return vehicle.getPassengers().size() < 2;
        }
        return vehicle.getPassengers().isEmpty();
    }

    /**
     * Registers Fabric interaction events to handle Stick commands.
     */
    public static void registerEvents() {
        // 1. Intercepting player clicking on an ENTITY (vehicle/seat)
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (hand != InteractionHand.MAIN_HAND || level.isClientSide()) {
                return InteractionResult.PASS;
            }
            if (!player.isSecondaryUseActive() || !player.getItemInHand(hand).is(Items.STICK)) {
                return InteractionResult.PASS;
            }
            UUID dogUuid = getSelectedDog(player.getUUID());
            if (dogUuid == null) {
                return InteractionResult.PASS;
            }

            if (level instanceof ServerLevel serverLevel) {
                Entity selectedEntity = serverLevel.getEntity(dogUuid);
                if (selectedEntity instanceof Wolf wolf && wolf.isTame() && wolf.isOwnedBy(player) && wolf.isAlive()) {
                    // Range check (12 blocks = 144.0D squared)
                    if (wolf.distanceToSqr(entity) > 144.0D) {
                        player.sendOverlayMessage(Component.translatable("text.betterdogs.dog_too_far", wolf.getName()));
                        return InteractionResult.SUCCESS;
                    }

                    boolean unrestricted = DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_ALLOW_UNRESTRICTED_RIDING);
                    if (unrestricted || isSitTarget(entity)) {
                        if (hasPassengerSpace(entity)) {
                            setVehicleTarget(wolf.getUUID(), entity);
                            clearSelection(player.getUUID());
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
            if (!player.isSecondaryUseActive() || !player.getItemInHand(hand).is(Items.STICK)) {
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
                            Interaction seat = new Interaction(EntityType.INTERACTION, serverLevel);
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
