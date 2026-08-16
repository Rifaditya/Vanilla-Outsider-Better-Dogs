// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.animal.WolfVariants;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for dynamic climate temperature coat variant selection,
 * breeding inheritance, and cluster pack alpha leadership initialization.
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

    public static WolfPersonality calculateSpawnPersonality(Level level, BlockPos pos, RandomSource random) {
        int normalPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_SPAWN_NORMAL_PERCENT, 60);
        int aggroPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_SPAWN_AGGRO_PERCENT, 20);
        int paciPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_SPAWN_PACI_PERCENT, 20);

        if (level != null && pos != null) {
            Holder<Biome> biomeHolder = level.getBiome(pos);
            if (biomeHolder != null && biomeHolder.isBound()) {
                float temp = biomeHolder.value().getBaseTemperature();
                if (temp > 1.0f) { // Arid / Savanna / Badlands
                    aggroPct += 20;
                } else if (temp < 0.2f) { // Snowy / Cold
                    paciPct += 20;
                }
            }
        }

        int total = Math.max(1, normalPct + aggroPct + paciPct);
        int roll = random.nextInt(total);

        if (roll < aggroPct) {
            return WolfPersonality.AGGRESSIVE;
        } else if (roll < aggroPct + paciPct) {
            return WolfPersonality.PACIFIST;
        } else {
            return WolfPersonality.NORMAL;
        }
    }

    public static void applyClimateVariant(Wolf wolf, Level level) {
        if (wolf == null || level == null || level.isClientSide()) {
            return;
        }
        if (!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_DYNAMIC_CLIMATE_VARIANTS, true)) {
            return;
        }

        Registry<WolfVariant> registry = level.registryAccess().registry(Registries.WOLF_VARIANT).orElse(null);
        if (registry == null) {
            return;
        }

        RandomSource random = wolf.getRandom();
        float temp = level.getBiome(wolf.blockPosition()).value().getBaseTemperature();
        ResourceKey<WolfVariant> key = getVariantKeyForTemperature(temp, random);
        registry.getHolder(key).ifPresent(wolf::setVariant);
    }

    public static void assignPuppyVariant(Wolf puppy, Wolf parent1, Wolf parent2) {
        if (puppy == null || parent1 == null || parent2 == null) {
            return;
        }
        Level level = puppy.level();
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

    public static void initializeWildPackCluster(Level level, Wolf newlySpawned) {
        if (level == null || level.isClientSide() || newlySpawned == null || newlySpawned.isTame()) {
            return;
        }

        List<Wolf> nearbyWolves = level.getEntitiesOfClass(
            Wolf.class,
            newlySpawned.getBoundingBox().inflate(12.0),
            w -> w.isAlive() && !w.isTame()
        );

        if (nearbyWolves.size() > 1) {
            Wolf alpha = nearbyWolves.get(0);
            double bestScore = WolfTerritorialRivalryHelper.calculateDominanceScore(alpha);

            for (Wolf w : nearbyWolves) {
                double score = WolfTerritorialRivalryHelper.calculateDominanceScore(w);
                if (score > bestScore) {
                    bestScore = score;
                    alpha = w;
                }
            }

            for (Wolf w : nearbyWolves) {
                if (w instanceof WolfExtensions ext) {
                    if (w == alpha) {
                        ext.betterdogs$setPackLeader(true);
                        ext.betterdogs$setLeaderUUID(null);
                    } else {
                        ext.betterdogs$setPackLeader(false);
                        ext.betterdogs$setLeaderUUID(alpha.getUUID());
                    }
                }
            }
        } else if (newlySpawned instanceof WolfExtensions ext) {
            ext.betterdogs$setPackLeader(true);
            ext.betterdogs$setLeaderUUID(null);
        }
    }
}
