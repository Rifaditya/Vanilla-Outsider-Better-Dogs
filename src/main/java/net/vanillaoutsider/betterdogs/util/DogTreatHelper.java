// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.wolf.Wolf;
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

    public static net.minecraft.world.item.Item getFavoriteTreat(Wolf wolf) {
        if (wolf instanceof WolfExtensions ext) {
            String fav = ext.betterdogs$getFavoriteTreat();
            if (fav != null && !fav.isEmpty()) {
                net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.tryParse(fav);
                if (id != null) {
                    return BuiltInRegistries.ITEM.getValue(id);
                }
            }
        }
        return null;
    }

    public static boolean isHoldingFavoriteTreat(Wolf wolf, Player player) {
        if (wolf == null || player == null) {
            return false;
        }
        return isFavoriteTreat(wolf, player.getMainHandItem()) || isFavoriteTreat(wolf, player.getOffhandItem());
    }

    public static void tryRollFavoriteTreat(Wolf wolf, ItemStack stack) {
        tryRollFavoriteTreat(wolf, stack, null);
    }

    public static void tryRollFavoriteTreat(Wolf wolf, ItemStack stack, Player player) {
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
                wolf.playSound(SoundEvents.WOLF_SHAKE, 1.2F, 1.3F);
                if (player != null) {
                    WolfAdvancementHelper.grantAdvancement(player, "favorite_treat");
                    WolfAdvancementHelper.grantAdvancement(player, "zoomies");
                }
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
        Level level = wolf.level();
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
        wolf.playSound(SoundEvents.WOLF_SHAKE, 1.0F, 0.8F);
        wolf.setIsInterested(true);
    }

    /**
     * Checks if an item is a comforting treat (Bone, Cooked Meat).
     */
    public static boolean isComfortingTreat(net.minecraft.world.item.Item item) {
        if (item == null) {
            return false;
        }
        if (net.minecraft.world.item.Items.BONE != null && item == net.minecraft.world.item.Items.BONE) {
            return true;
        }
        return item == net.minecraft.world.item.Items.COOKED_BEEF || item == net.minecraft.world.item.Items.COOKED_PORKCHOP
                || item == net.minecraft.world.item.Items.COOKED_MUTTON || item == net.minecraft.world.item.Items.COOKED_CHICKEN
                || item == net.minecraft.world.item.Items.COOKED_RABBIT || item == net.minecraft.world.item.Items.COOKED_COD
                || item == net.minecraft.world.item.Items.COOKED_SALMON;
    }

    /**
     * Checks if an item stack is a comforting treat (Bone, Cooked Meat, or items in the treat pool).
     */
    public static boolean isComfortingTreat(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        try {
            if (net.minecraft.world.item.Items.BONE != null && stack.is(net.minecraft.world.item.Items.BONE)) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        try {
            if (stack.is(net.vanillaoutsider.betterdogs.registry.BetterDogsTags.COOKED_FOOD)
                    || stack.is(net.vanillaoutsider.betterdogs.registry.BetterDogsTags.TREATS)) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        try {
            net.minecraft.world.item.Item item = stack.getItem();
            if (item != null && isComfortingTreat(item)) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        try {
            return DogFoodHelper.isCookedMeat(stack);
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Calculates the soothing penalty tick reduction for a given penalty and random factor (bounds [0.0, 0.10]).
     */
    public static int calculateSoothingReduction(int currentPenalty, float randomFactor) {
        if (currentPenalty <= 0) {
            return 0;
        }
        float factor = Math.max(0.0f, Math.min(0.10f, randomFactor));
        return (int) (currentPenalty * factor);
    }

    /**
     * Applies the comforting soothing reduction to the dog's remaining play penalty ticks.
     */
    public static int applySoothingReduction(Wolf wolf, int currentPenalty) {
        if (currentPenalty <= 0) {
            return 0;
        }
        float randomFactor = (wolf != null && wolf.getRandom() != null)
                ? wolf.getRandom().nextFloat() * 0.10f
                : 0.05f;
        int reduction = calculateSoothingReduction(currentPenalty, randomFactor);
        int remaining = Math.max(0, currentPenalty - reduction);
        if (wolf != null) {
            SmallFightHelper.setPlayPenaltyTicks(wolf, remaining);
        }
        return reduction;
    }

    /**
     * Checks if a dog can be fed a comforting treat to soothe its active play penalty.
     */
    public static boolean canFeedComfortingTreat(Wolf wolf, Player player, net.minecraft.world.InteractionHand hand, ItemStack stack) {
        if (wolf == null || player == null || stack == null || stack.isEmpty()) {
            return false;
        }
        if (hand != net.minecraft.world.InteractionHand.MAIN_HAND) {
            return false;
        }
        if (!wolf.isTame() || !wolf.isOwnedBy(player)) {
            return false;
        }
        if (SmallFightHelper.getPlayPenaltyTicks(wolf) <= 0) {
            return false;
        }
        return isComfortingTreat(stack);
    }

    /**
     * Feeds a comforting treat to a dog in penalty state, reducing 0% to 10% of remaining penalty ticks,
     * restoring minor health, and emitting gentle heart particles with soft whine audio.
     */
    public static net.minecraft.world.InteractionResult tryFeedComfortingTreat(Wolf wolf, Player player, net.minecraft.world.InteractionHand hand, ItemStack stack) {
        if (!canFeedComfortingTreat(wolf, player, hand, stack)) {
            return net.minecraft.world.InteractionResult.PASS;
        }

        if (wolf.level() != null && wolf.level().isClientSide()) {
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        // 1. Consume treat (Creative Bypass)
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        // 2. Heal wolf slightly if damaged
        wolf.heal(2.0F);

        // 3. Soothing reduction: randomly reduce 0% to 10% of remaining penalty ticks
        int currentPenalty = SmallFightHelper.getPlayPenaltyTicks(wolf);
        applySoothingReduction(wolf, currentPenalty);

        // 4. Play soft whine audio
        try {
            net.minecraft.sounds.SoundEvent whineSound = SmallFightHelper.getWolfWhineSound(wolf);
            wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(),
                    whineSound, wolf.getSoundSource(), 0.8F, 1.3F);
        } catch (Throwable ignored) {
        }

        // 5. Spawn gentle heart particles
        if (wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART, wolf.getRandomX(0.8), wolf.getRandomY() + 0.4, wolf.getRandomZ(0.8),
                    2, 0.15, 0.1, 0.15, 0.02);
        }

        return net.minecraft.world.InteractionResult.SUCCESS;
    }
}
