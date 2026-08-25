// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

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
     * Registers command event listeners.
     */
    public static void registerEvents() {
        DogEntityCommandListener.register();
        DogBlockCommandListener.register();
    }
}
