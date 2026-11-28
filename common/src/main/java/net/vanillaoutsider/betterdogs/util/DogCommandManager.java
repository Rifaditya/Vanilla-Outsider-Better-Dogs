// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Event-driven manager handling player commands, dog selection, and vehicle/seat interactions.
 */
public class DogCommandManager {

    public static final int MAX_TRACKED_VEHICLE_TARGETS = 1024;
    public static final String SEAT_ENTITY_TAG = "betterdogs:seat";
    public static final int ORPHAN_SEAT_GRACE_TICKS = 200;

    private static final Map<UUID, UUID> SELECTED_DOGS = new ConcurrentHashMap<>();
    private static final Map<UUID, WeakReference<Entity>> VEHICLE_TARGETS = new ConcurrentHashMap<>();

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

    public static int getTrackedVehicleTargetCount() {
        return VEHICLE_TARGETS.size();
    }

    private static void pruneStaleAndEnforceCap(UUID incomingDogUuid) {
        Iterator<Map.Entry<UUID, WeakReference<Entity>>> iterator = VEHICLE_TARGETS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, WeakReference<Entity>> entry = iterator.next();
            WeakReference<Entity> ref = entry.getValue();
            Entity entity = ref != null ? ref.get() : null;
            if (entity == null || entity.isRemoved()) {
                iterator.remove();
            }
        }
        if (incomingDogUuid == null || VEHICLE_TARGETS.containsKey(incomingDogUuid)
                || VEHICLE_TARGETS.size() < MAX_TRACKED_VEHICLE_TARGETS) {
            return;
        }
        Iterator<UUID> keyIterator = VEHICLE_TARGETS.keySet().iterator();
        while (VEHICLE_TARGETS.size() >= MAX_TRACKED_VEHICLE_TARGETS && keyIterator.hasNext()) {
            keyIterator.next();
            keyIterator.remove();
        }
    }

    public static void setVehicleTarget(UUID dogUuid, Entity vehicle) {
        if (dogUuid == null) {
            return;
        }
        if (vehicle == null || vehicle.isRemoved()) {
            VEHICLE_TARGETS.remove(dogUuid);
            return;
        }
        pruneStaleAndEnforceCap(dogUuid);
        VEHICLE_TARGETS.put(dogUuid, new WeakReference<>(vehicle));
    }

    public static Entity getVehicleTarget(UUID dogUuid) {
        if (dogUuid == null) {
            return null;
        }
        WeakReference<Entity> ref = VEHICLE_TARGETS.get(dogUuid);
        if (ref == null) {
            return null;
        }
        Entity target = ref.get();
        if (target == null || target.isRemoved()) {
            VEHICLE_TARGETS.remove(dogUuid, ref);
            return null;
        }
        return target;
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
     * Clears sitting state so a tamed dog stands up before navigating to a commanded seat or vehicle.
     */
    public static void prepareWolfForBoarding(Wolf wolf) {
        if (wolf == null) {
            return;
        }
        wolf.setOrderedToSit(false);
        wolf.setInSittingPose(false);
    }

    /**
     * Checks whether an entity carries the Better Dogs temporary seat marker tag.
     */
    public static boolean isBetterDogsSeat(Entity entity) {
        return entity != null && entity.entityTags().contains(SEAT_ENTITY_TAG);
    }

    /**
     * Checks whether a vehicle or seat is currently targeted by a live, standing, non-mounted wolf.
     * Prunes stale or invalid references along the way so orphaned seats cannot deadlock cleanup.
     */
    public static boolean isActivelyTargetedByLiveWolf(ServerLevel level, Entity vehicle) {
        if (vehicle == null || vehicle.isRemoved()) {
            return false;
        }
        Iterator<Map.Entry<UUID, WeakReference<Entity>>> iterator = VEHICLE_TARGETS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, WeakReference<Entity>> entry = iterator.next();
            WeakReference<Entity> ref = entry.getValue();
            Entity target = ref != null ? ref.get() : null;
            if (target == null || target.isRemoved()) {
                iterator.remove();
                continue;
            }
            if (target != vehicle) {
                continue;
            }
            if (level == null) {
                return true;
            }
            Entity dogEntity = level.getEntity(entry.getKey());
            if (dogEntity instanceof Wolf wolf
                    && !wolf.isRemoved()
                    && wolf.isAlive()
                    && !wolf.isPassenger()
                    && !wolf.isOrderedToSit()) {
                return true;
            }
            iterator.remove();
        }
        return false;
    }

    /**
     * Returns true when an untargeted, unoccupied Better Dogs seat entity is safe to discard immediately.
     */
    public static boolean shouldDiscardAbandonedSeat(ServerLevel level, Entity entity) {
        return entity != null
                && !entity.isRemoved()
                && isBetterDogsSeat(entity)
                && entity.getPassengers().isEmpty()
                && !isActivelyTargetedByLiveWolf(level, entity);
    }

    /**
     * Discards an entity if and only if it is an abandoned, unoccupied Better Dogs seat.
     */
    public static void discardIfAbandonedSeat(ServerLevel level, Entity entity) {
        if (shouldDiscardAbandonedSeat(level, entity)) {
            entity.discard();
        }
    }

    /**
     * Pure state predicate enforcing the orphan-seat grace period before periodic level sweeps discard a seat.
     */
    public static boolean shouldSweepOrphanedSeatState(
            boolean isRemoved,
            boolean isBetterDogsSeat,
            boolean hasPassengers,
            boolean isActivelyTargeted,
            int tickCount
    ) {
        return !isRemoved
                && isBetterDogsSeat
                && !hasPassengers
                && !isActivelyTargeted
                && tickCount >= ORPHAN_SEAT_GRACE_TICKS;
    }

    /**
     * Checks whether a world entity is an orphaned Better Dogs seat past the grace period.
     */
    public static boolean shouldSweepOrphanedSeat(ServerLevel level, Entity entity) {
        if (entity == null || entity.isRemoved() || !isBetterDogsSeat(entity)) {
            return false;
        }
        if (!entity.getPassengers().isEmpty() || entity.tickCount < ORPHAN_SEAT_GRACE_TICKS) {
            return false;
        }
        return shouldSweepOrphanedSeatState(
                false,
                true,
                false,
                isActivelyTargetedByLiveWolf(level, entity),
                entity.tickCount
        );
    }

    /**
     * Handles player interaction with an entity (vehicles, seats, or wolves).
     * Platform-agnostic handler called by Fabric/NeoForge event listeners.
     */
    public static InteractionResult handleEntityInteract(Player player, Level level, InteractionHand hand, Entity entity) {
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

                            if (isBetterDogsSeat(entity)) {
                                entity.discard();
                            }

                            player.sendOverlayMessage(Component.translatableWithFallback("text.betterdogs.dog_dismounted", "%s hopped out of the seat.", wolf.getName()));
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
                    player.sendOverlayMessage(Component.translatableWithFallback("text.betterdogs.dog_too_far", "%s is too far away to board.", wolf.getName()));
                    return InteractionResult.SUCCESS;
                }

                boolean unrestricted = DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_ALLOW_UNRESTRICTED_RIDING);
                if (unrestricted || isSitTarget(entity)) {
                    if (hasPassengerSpace(entity)) {
                        setVehicleTarget(wolf.getUUID(), entity);
                        clearSelection(player.getUUID());
                        prepareWolfForBoarding(wolf);
                        player.sendOverlayMessage(Component.translatableWithFallback("text.betterdogs.dog_commanded_to_board", "Commanded %s to board %s.", wolf.getName(), entity.getName()));
                        net.minecraft.sounds.SoundEvent whineSound = ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().whineSound().value();
                        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                                whineSound, wolf.getSoundSource(), 1.0f, 1.2f);
                        return InteractionResult.SUCCESS;
                    } else {
                        player.sendOverlayMessage(Component.translatableWithFallback("text.betterdogs.seat_occupied", "That seat is already occupied."));
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    /**
     * Handles player interaction with a block (chair/stair).
     * Platform-agnostic handler called by Fabric/NeoForge event listeners.
     */
    public static InteractionResult handleBlockInteract(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
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
                        player.sendOverlayMessage(Component.translatableWithFallback("text.betterdogs.dog_too_far", "%s is too far away to board.", wolf.getName()));
                        return InteractionResult.SUCCESS;
                    }

                    // Check if seat already exists to avoid duplication
                    AABB searchArea = new AABB(pos);
                    List<Entity> existingSeats = serverLevel.getEntities((Entity) null, searchArea,
                            DogCommandManager::isBetterDogsSeat);

                    Entity seatEntity;
                    if (!existingSeats.isEmpty()) {
                        seatEntity = existingSeats.get(0);
                    } else {
                        // Spawn invisible Interaction entity at seat height
                        Interaction seat = new Interaction(EntityType.INTERACTION, serverLevel);
                        seat.setWidth(0.5f);
                        seat.setHeight(0.5f);
                        seat.addTag(SEAT_ENTITY_TAG);
                        seat.setPos(pos.getX() + 0.5D, pos.getY() + 0.22D, pos.getZ() + 0.5D);
                        serverLevel.addFreshEntity(seat);
                        seatEntity = seat;
                    }

                    if (hasPassengerSpace(seatEntity)) {
                        setVehicleTarget(wolf.getUUID(), seatEntity);
                        clearSelection(player.getUUID());
                        prepareWolfForBoarding(wolf);
                        player.sendOverlayMessage(Component.translatableWithFallback("text.betterdogs.dog_commanded_to_board", "Commanded %s to board %s.", wolf.getName(), state.getBlock().getName()));
                        net.minecraft.sounds.SoundEvent whineSound = ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().whineSound().value();
                        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                                whineSound, wolf.getSoundSource(), 1.0f, 1.2f);
                        return InteractionResult.SUCCESS;
                    } else {
                        player.sendOverlayMessage(Component.translatableWithFallback("text.betterdogs.seat_occupied", "That seat is already occupied."));
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
}
