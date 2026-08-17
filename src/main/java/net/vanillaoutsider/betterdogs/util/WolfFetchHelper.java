// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import net.vanillaoutsider.betterdogs.registry.BetterDogsTags;

/**
 * Dedicated single-purpose helper for stick & bone fetching detection, pickup, and return delivery.
 */
public final class WolfFetchHelper {

    private WolfFetchHelper() {
    }

    public static boolean isFetchItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.is(Items.STICK) || stack.is(Items.BONE) || stack.is(BetterDogsTags.FETCH_ITEMS);
    }

    public static ItemEntity findNearbyDroppedFetchItem(Wolf wolf, double radius) {
        if (wolf == null) {
            return null;
        }
        Level level = wolf.level();
        if (level == null) {
            return null;
        }

        List<ItemEntity> items = level.getEntitiesOfClass(
                ItemEntity.class,
                wolf.getBoundingBox().inflate(radius),
                item -> item != null && item.isAlive() && isFetchItem(item.getItem())
        );

        ItemEntity closest = null;
        double closestDistSq = Double.MAX_VALUE;
        for (ItemEntity item : items) {
            double distSq = wolf.distanceToSqr(item);
            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                closest = item;
            }
        }
        return closest;
    }

    public static void pickupItem(Wolf wolf, ItemEntity itemEntity) {
        if (wolf == null || itemEntity == null || !itemEntity.isAlive()) {
            return;
        }
        ItemStack stack = itemEntity.getItem().copy();
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setHasFetchedItem(true);
            ext.betterdogs$setFetchedItemStack(stack);
        }
        wolf.setItemInHand(InteractionHand.MAIN_HAND, stack);
        itemEntity.discard();
    }

    public static void dropItemToOwner(Wolf wolf, Player owner) {
        if (wolf == null || owner == null) {
            return;
        }
        Level level = wolf.level();
        if (level == null || level.isClientSide()) {
            return;
        }

        ItemStack deliveredStack = null;
        if (wolf instanceof WolfExtensions ext) {
            deliveredStack = ext.betterdogs$getFetchedItemStack();
            ext.betterdogs$setHasFetchedItem(false);
            ext.betterdogs$setFetchedItemStack(null);
        }

        if (deliveredStack == null || deliveredStack.isEmpty()) {
            deliveredStack = wolf.getMainHandItem();
            if (deliveredStack.isEmpty()) {
                deliveredStack = new ItemStack(Items.STICK);
            }
        }

        wolf.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

        ItemEntity dropped = new ItemEntity(level, owner.getX(), owner.getY() + 0.2, owner.getZ(), deliveredStack);
        dropped.setDefaultPickUpDelay();
        level.addFreshEntity(dropped);

        try {
            if (wolf instanceof WolfAccessor accessor) {
                var sounds = accessor.betterdogs$invokeGetSoundSet();
                if (sounds != null && sounds.ambientSound() != null) {
                    wolf.playSound(sounds.ambientSound().value(), 1.0F, 1.2F);
                }
            }
        } catch (Exception ignored) {
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    wolf.getX(), wolf.getY() + 0.5, wolf.getZ(),
                    6, 0.2, 0.2, 0.2, 0.05
            );
        }

        String dogName = wolf.hasCustomName() ? wolf.getCustomName().getString() : "Dog";
        owner.sendOverlayMessage(Component.literal("§6Fetch: §a" + dogName + " brought back your " + deliveredStack.getHoverName().getString() + "!"));
    }
}
