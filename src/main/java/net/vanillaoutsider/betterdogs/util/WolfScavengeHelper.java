// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.vanillaoutsider.betterdogs.BetterDogs;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.registry.BetterDogsTags;

/**
 * Single-purpose helper for autonomous ground food scavenging, nutrition scaling,
 * edibility filtering, and eating feedback.
 */
public final class WolfScavengeHelper {

    private WolfScavengeHelper() {
    }

    /**
     * Pure calculation of health points restored when consuming a food item.
     */
    public static float calculateHealAmount(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0.0f;
        }

        if (stack.is(Items.ROTTEN_FLESH)) {
            return 1.0f;
        }

        if (stack.getComponents().has(DataComponents.FOOD)) {
            var foodComp = stack.getComponents().get(DataComponents.FOOD);
            if (foodComp != null) {
                return (float) foodComp.nutrition() / 2.0f;
            }
        }

        return 2.0f;
    }

    /**
     * Checks if a food item is edible for the given wolf based on tags and gamerules.
     */
    public static boolean isEdible(Wolf wolf, ItemStack stack) {
        if (wolf == null || stack == null || stack.isEmpty()) {
            return false;
        }

        if (!wolf.isFood(stack)) {
            return false;
        }

        // Wild wolves eat anything edible
        if (!wolf.isTame()) {
            return true;
        }

        // Tamed dogs check gamerules
        if (stack.is(BetterDogsTags.RAW_FOOD)) {
            return DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_DOGS_EAT_RAW_FOOD);
        }

        if (stack.is(BetterDogsTags.COOKED_FOOD)) {
            return DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_DOGS_EAT_COOKED_FOOD);
        }

        // Fallback heuristic for modded food
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().toLowerCase();
        boolean isCooked = path.contains("cooked") || path.contains("roasted") || path.contains("grilled");

        if (isCooked) {
            return DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_DOGS_EAT_COOKED_FOOD);
        } else {
            return DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_DOGS_EAT_RAW_FOOD);
        }
    }

    /**
     * Checks if a wolf is eligible to scavenge ground food.
     */
    public static boolean canScavenge(Wolf wolf) {
        return canScavenge(wolf, false);
    }

    /**
     * Checks if a wolf is eligible to scavenge ground food, taking Hoover trait into account.
     */
    public static boolean canScavenge(Wolf wolf, boolean isHoover) {
        if (wolf == null || !wolf.isAlive()) {
            return false;
        }

        if (wolf.getHealth() >= wolf.getMaxHealth() && !isHoover) {
            return false;
        }

        if (wolf.isTame()) {
            if (wolf.isInSittingPose()) {
                return false;
            }

            if (WolfPersistentData.refusesGroundFood(wolf) &&
                    DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_ENABLE_REFUSE_GROUND_FOOD)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Executes food consumption, healing, eating audio/particles, item entity management,
     * and advancement dispatches.
     */
    public static void consumeGroundFood(Wolf wolf, ItemEntity foodEntity) {
        if (wolf == null || foodEntity == null || !foodEntity.isAlive()) {
            return;
        }

        ItemStack stack = foodEntity.getItem();
        if (stack.isEmpty()) {
            return;
        }

        float healAmount = calculateHealAmount(stack);

        // Check if food was dropped by the dog's owner to trigger Self-Service
        if (wolf.isTame()) {
            Entity thrower = foodEntity.getOwner();
            if (thrower != null && wolf.getOwnerReference() != null &&
                    thrower.getUUID().equals(wolf.getOwnerReference().getUUID())) {
                if (thrower instanceof ServerPlayer serverPlayer) {
                    BetterDogs.SELF_SERVICE.trigger(serverPlayer);
                }
            }
        }

        // Consume one item
        if (stack.getCount() > 1) {
            stack.shrink(1);
        } else {
            foodEntity.discard();
        }

        // Heal the wolf
        wolf.heal(healAmount);

        // Visual & Audio Feedback
        wolf.playSound(SoundEvents.GENERIC_EAT.value(), 1.0F, 1.0F);
        if (!wolf.level().isClientSide()) {
            wolf.level().broadcastEntityEvent(wolf, (byte) 45); // Eating particles
        }
    }
}
