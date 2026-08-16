// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.animal.WolfVariants;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for dynamic climate temperature coat variant selection and breeding inheritance.
 */
public class WolfCoatVariantHelper {

    public static ResourceKey<WolfVariant> getVariantKeyForTemperature(float temp, RandomSource random) {
        if (temp < 0.15f) {
            return random.nextBoolean() ? WolfVariants.SNOWY : WolfVariants.ASHEN;
        } else if (temp < 0.45f) {
            List<ResourceKey<WolfVariant>> cold = List.of(WolfVariants.PALE, WolfVariants.ASHEN, WolfVariants.BLACK, WolfVariants.CHESTNUT);
            return cold.get(random.nextInt(cold.size()));
        } else if (temp < 0.85f) {
            return random.nextBoolean() ? WolfVariants.WOODS : WolfVariants.PALE;
        } else if (temp <= 1.15f) {
            return WolfVariants.RUSTY;
        } else {
            return random.nextBoolean() ? WolfVariants.SPOTTED : WolfVariants.STRIPED;
        }
    }

    public static void assignPuppyVariant(Wolf puppy, Wolf parent1, Wolf parent2) {
        if (puppy == null || parent1 == null || parent2 == null) {
            return;
        }
        Level level = puppy.getCommandSenderWorld();
        if (level == null || level.isClientSide()) {
            return;
        }

        if (!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_DYNAMIC_CLIMATE_VARIANTS, true)) {
            return;
        }

        Registry<WolfVariant> registry = level.registryAccess().registry(Registries.WOLF_VARIANT).orElse(null);
        if (registry == null) {
            return;
        }

        RandomSource random = puppy.getRandom();
        if (random.nextBoolean()) {
            Holder<WolfVariant> parentVariant = random.nextBoolean() ? parent1.getVariant() : parent2.getVariant();
            if (parentVariant != null) {
                puppy.setVariant(parentVariant);
                return;
            }
        }

        float temp = level.getBiome(puppy.blockPosition()).value().getBaseTemperature();
        ResourceKey<WolfVariant> key = getVariantKeyForTemperature(temp, random);
        registry.getHolder(key).ifPresent(puppy::setVariant);
    }
}
