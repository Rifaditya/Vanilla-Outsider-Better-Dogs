// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
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
        return playerUuid != null ? SELECTED_DOGS.get(playerUuid) : null;
    }

    public static void clearSelection(UUID playerUuid) {
        if (playerUuid != null) {
            SELECTED_DOGS.remove(playerUuid);
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

    public static int getTrackedVehicleTargetCount() {
        return VEHICLE_TARGETS.size();
    }

    private static void pruneStaleAndEnforceCap(UUID incomingDogUuid) {
        Iterator<Map.Entry<UUID, WeakReference<Entity>>> iterator = VEHICLE_TARGETS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, WeakReference<Entity>> entry = iterator.next();
            WeakReference<Entity> ref = entry.getValue();
            Entity target = ref != null ? ref.get() : null;
            if (target == null || target.isRemoved()) {
                iterator.remove();
            }
        }
        if (VEHICLE_TARGETS.containsKey(incomingDogUuid) || VEHICLE_TARGETS.size() < MAX_TRACKED_VEHICLE_TARGETS) {
            return;
        }
        Iterator<UUID> keyIterator = VEHICLE_TARGETS.keySet().iterator();
        while (VEHICLE_TARGETS.size() >= MAX_TRACKED_VEHICLE_TARGETS && keyIterator.hasNext()) {
            keyIterator.next();
            keyIterator.remove();
        }
    }

    public static boolean isSitTarget(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof Boat || entity instanceof Minecart || entity instanceof AbstractHorse
                || entity instanceof Pig || entity instanceof Strider || entity instanceof Camel) {
            return true;
        }
        if (isBetterDogsSeat(entity)) {
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
                || stack.is(Items.BLAZE_ROD)
                || stack.is(Items.BREEZE_ROD);
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
        return entity != null && entity.getTags().contains(SEAT_ENTITY_TAG);
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

    public static InteractionResult handleEntityUse(Player player, Level level, InteractionHand hand, Entity entity) {
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
                    WolfFeedbackHelper.sendFeedback(player, level, Component.translatableWithFallback("text.betterdogs.dog_too_far", "%s is too far away to board.", wolf.getName()));
                    return InteractionResult.SUCCESS;
                }

                boolean unrestricted = BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_ALLOW_UNRESTRICTED_RIDING, false);
                if (unrestricted || isSitTarget(entity)) {
                    if (hasPassengerSpace(entity)) {
                        setVehicleTarget(wolf.getUUID(), entity);
                        clearSelection(player.getUUID());
                        prepareWolfForBoarding(wolf);
                        WolfFeedbackHelper.sendFeedback(player, level, Component.translatableWithFallback("text.betterdogs.dog_commanded_to_board", "Commanded %s to board %s.", wolf.getName(), entity.getName()));
                        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.WOLF_WHINE, wolf.getSoundSource(), 1.0f, 1.2f);
                        return InteractionResult.SUCCESS;
                    } else {
                        WolfFeedbackHelper.sendFeedback(player, level, Component.translatableWithFallback("text.betterdogs.seat_occupied", "That seat is already occupied."));
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult handleBlockUse(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
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
                        WolfFeedbackHelper.sendFeedback(player, level, Component.translatableWithFallback("text.betterdogs.dog_too_far", "%s is too far away to board.", wolf.getName()));
                        return InteractionResult.SUCCESS;
                    }

                    AABB searchArea = new AABB(pos);
                    List<Entity> existingSeats = serverLevel.getEntities((Entity) null, searchArea,
                            DogCommandManager::isBetterDogsSeat);

                    Entity seatEntity;
                    if (!existingSeats.isEmpty()) {
                        seatEntity = existingSeats.get(0);
                    } else {
                        Interaction seat = new Interaction(EntityType.INTERACTION, serverLevel);
                        seat.addTag(SEAT_ENTITY_TAG);
                        seat.setPos(pos.getX() + 0.5D, pos.getY() + 0.22D, pos.getZ() + 0.5D);
                        serverLevel.addFreshEntity(seat);
                        seatEntity = seat;
                    }

                    if (hasPassengerSpace(seatEntity)) {
                        setVehicleTarget(wolf.getUUID(), seatEntity);
                        clearSelection(player.getUUID());
                        prepareWolfForBoarding(wolf);
                        WolfFeedbackHelper.sendFeedback(player, level, Component.translatableWithFallback("text.betterdogs.dog_commanded_to_board", "Commanded %s to board %s.", wolf.getName(), state.getBlock().getName()));
                        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.WOLF_WHINE, wolf.getSoundSource(), 1.0f, 1.2f);
                        return InteractionResult.SUCCESS;
                    } else {
                        WolfFeedbackHelper.sendFeedback(player, level, Component.translatableWithFallback("text.betterdogs.seat_occupied", "That seat is already occupied."));
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
}
