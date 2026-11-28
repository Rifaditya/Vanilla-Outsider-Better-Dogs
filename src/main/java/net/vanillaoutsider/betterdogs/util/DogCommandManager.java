// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.vanillaoutsider.betterdogs.listener.DogBlockCommandListener;
import net.vanillaoutsider.betterdogs.listener.DogEntityCommandListener;

/**
 * Thread-safe state repository for active player dog selections and vehicle command targets.
 */
public class DogCommandManager {

    public static final int MAX_TRACKED_VEHICLE_TARGETS = 1024;

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
     * Registers command event listeners.
     */
    public static void registerEvents() {
        DogEntityCommandListener.register();
        DogBlockCommandListener.register();
    }
}
