// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.registry.BetterDogsTags;

/**
 * Single-purpose helper managing vehicle/seat detection, capacity validation, command item checks, and dismount math.
 */
public final class DogSeatHelper {

    public static final double MAX_COMMAND_DISTANCE_SQ = 144.0D; // 12 blocks squared
    public static final double MOUNT_DISTANCE_SQ = 2.25D;       // 1.5 blocks squared
    public static final double DEFAULT_DISMOUNT_DISTANCE = 0.8D;

    private DogSeatHelper() {
    }

    /**
     * Checks if the entity is a valid player seat, mount, or standard vehicle.
     */
    public static boolean isSitTarget(Entity entity) {
        if (entity == null || !entity.isAlive()) {
            return false;
        }
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
        if (vehicle == null || !vehicle.isAlive()) {
            return false;
        }
        if (vehicle instanceof Boat || vehicle instanceof Camel) {
            return vehicle.getPassengers().size() < 2;
        }
        return vehicle.getPassengers().isEmpty();
    }

    /**
     * Checks if the stack is a valid command item for commanding dogs (Sticks, Blaze Rods, Breeze Rods, #betterdogs:command_items).
     * Strictly excludes Bones, which are reserved for Guard Mode.
     */
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
     * Checks if the block state represents a sittable chair, bench, stool, or stair block.
     */
    public static boolean isChairBlock(BlockState state) {
        if (state == null) {
            return false;
        }
        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString().toLowerCase();
        return blockId.contains("chair") || blockId.contains("seat") || blockId.contains("stool") || blockId.contains("bench")
                || state.is(BlockTags.STAIRS);
    }

    /**
     * Calculates the safe lateral dismount vector based on player look angle.
     */
    public static Vec3 calculateDismountOffset(Vec3 lookAngle, double distance) {
        if (lookAngle == null) {
            return Vec3.ZERO;
        }
        return lookAngle.scale(-distance).multiply(1.0D, 0.0D, 1.0D);
    }
}
