// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for dog food classification and healing math.
 */
public class DogFoodHelper {

    public static boolean isRawMeat(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.is(Items.BEEF) || stack.is(Items.PORKCHOP) || stack.is(Items.MUTTON)
                || stack.is(Items.CHICKEN) || stack.is(Items.RABBIT) || stack.is(Items.ROTTEN_FLESH);
    }

    public static boolean isCookedMeat(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.is(Items.COOKED_BEEF) || stack.is(Items.COOKED_PORKCHOP)
                || stack.is(Items.COOKED_MUTTON) || stack.is(Items.COOKED_CHICKEN) || stack.is(Items.COOKED_RABBIT);
    }

    public static boolean isEdibleDogFood(Level level, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        boolean allowRaw = BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_DOGS_EAT_RAW_FOOD, true);
        boolean allowCooked = BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_DOGS_EAT_COOKED_FOOD, true);

        if (allowRaw && isRawMeat(stack)) {
            return true;
        }
        if (allowCooked && isCookedMeat(stack)) {
            return true;
        }

        if (stack.getItem().isEdible()) {
            FoodProperties props = stack.getItem().getFoodProperties();
            if (props != null && props.isMeat()) {
                return true;
            }
        }
        return false;
    }

    public static float calculateHealAmount(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 2.0F;
        }
        if (stack.getItem().isEdible()) {
            FoodProperties props = stack.getItem().getFoodProperties();
            if (props != null) {
                return (float) props.getNutrition() * 2.0F;
            }
        }
        return 4.0F;
    }

    public static float calculateHealAmount(net.minecraft.world.entity.animal.Wolf wolf, ItemStack stack) {
        float baseHeal = calculateHealAmount(stack);
        if (wolf != null && DogTreatHelper.isFavoriteTreat(wolf, stack)) {
            return baseHeal * 2.0F;
        }
        return baseHeal;
    }
}
