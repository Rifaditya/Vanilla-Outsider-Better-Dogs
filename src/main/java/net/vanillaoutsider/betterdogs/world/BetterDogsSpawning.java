// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biomes;

/**
 * Dedicated single-purpose world generator integration for registering expanded wolf biomes.
 */
public final class BetterDogsSpawning {

    public static final int DEFAULT_MIN_GROUP = 4;
    public static final int DEFAULT_MAX_GROUP = 8;
    public static final int DEFAULT_SPAWN_WEIGHT = 8;

    private BetterDogsSpawning() {
    }

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
                EntityTypes.WOLF,
                DEFAULT_SPAWN_WEIGHT,
                DEFAULT_MIN_GROUP,
                DEFAULT_MAX_GROUP
        );
    }
}
