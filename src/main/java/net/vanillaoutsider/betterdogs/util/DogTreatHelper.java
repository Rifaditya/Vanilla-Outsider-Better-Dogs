// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose helper for favorite treat affinity rolls, refusal condition checks, and treat bonuses.
 */
public class DogTreatHelper {

    public static boolean isFavoriteTreat(Wolf wolf, ItemStack stack) {
        if (wolf == null || stack == null || stack.isEmpty()) {
            return false;
        }
        if (!(wolf instanceof WolfExtensions ext)) {
            return false;
        }
        String fav = ext.betterdogs$getFavoriteTreat();
        if (fav == null || fav.isEmpty()) {
            return false;
        }
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return fav.equals(itemId);
    }

    public static boolean isHoldingFavoriteTreat(Wolf wolf, Player player) {
        if (wolf == null || player == null) {
            return false;
        }
        return isFavoriteTreat(wolf, player.getMainHandItem()) || isFavoriteTreat(wolf, player.getOffhandItem());
    }

    public static void tryRollFavoriteTreat(Wolf wolf, ItemStack stack) {
        if (wolf == null || stack == null || stack.isEmpty()) {
            return;
        }
        if (!(wolf instanceof WolfExtensions ext)) {
            return;
        }
        if (!ext.betterdogs$getFavoriteTreat().isEmpty()) {
            return;
        }
        if (DogFoodHelper.isRawMeat(stack) || DogFoodHelper.isCookedMeat(stack)) {
            if (wolf.getRandom().nextFloat() < 0.35F) {
                String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                ext.betterdogs$setFavoriteTreat(itemId);
                ext.betterdogs$setZoomiesTicks(120);
                wolf.playSound(SoundEvents.WOLF_AMBIENT, 1.2F, 1.3F);
            }
        }
    }

    public static boolean shouldRefuseFood(Wolf wolf, ItemStack stack) {
        if (wolf == null || stack == null || stack.isEmpty()) {
            return false;
        }
        if (!wolf.isTame()) {
            return false;
        }
        Level level = wolf.getCommandSenderWorld();
        if (level == null) {
            return false;
        }
        if (wolf.getHealth() < wolf.getMaxHealth()) {
            return false;
        }
        if (wolf.isBaby()) {
            return false;
        }
        if (wolf.canFallInLove()) {
            return false;
        }
        return true;
    }

    public static void performRefusal(Wolf wolf) {
        if (wolf == null) {
            return;
        }
        wolf.playSound(SoundEvents.WOLF_WHINE, 1.0F, 0.8F);
        wolf.setIsInterested(true);
    }
}
