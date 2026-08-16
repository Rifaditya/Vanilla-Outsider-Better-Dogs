// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biomes;

/**
 * Dedicated single-purpose world generator integration for registering expanded wolf biomes.
 */
public final class BetterDogsSpawning {

    private BetterDogsSpawning() {}

    public static void registerSpawns() {
        BiomeModifications.addSpawn(
            BiomeSelectors.includeByKey(
                Biomes.PLAINS,
                Biomes.SAVANNA,
                Biomes.SAVANNA_PLATEAU,
                Biomes.WINDSWEPT_SAVANNA,
                Biomes.BADLANDS,
                Biomes.ERODED_BADLANDS,
                Biomes.WOODED_BADLANDS,
                Biomes.MEADOW
            ),
            MobCategory.CREATURE,
            EntityType.WOLF,
            8,
            4,
            8
        );
    }
}
