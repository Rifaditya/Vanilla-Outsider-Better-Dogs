// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for wild wolf spawn personality determination,
 * climate-trait biasing, and cluster pack alpha hierarchy initialization.
 */
public final class WolfVariantHelper {

    private WolfVariantHelper() {}

    /**
     * Calculates spawn personality based on configured spawn percentages and local biome climate temperature.
     */
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

    /**
     * Initializes and synchronizes a newly spawned wild wolf pack cluster, electing the dominant alpha.
     */
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
