// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.mixin;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.random.Weighted;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {

    @Inject(method = "mobsAt", at = @At("RETURN"), cancellable = true)
    private static void betterdogs$modifySpawnWeights(
            ServerLevel level, StructureManager structureManager, ChunkGenerator generator,
            MobCategory mobCategory, BlockPos pos, Holder<Biome> biome,
            CallbackInfoReturnable<WeightedList<MobSpawnSettings.SpawnerData>> cir) {
        
        WeightedList<MobSpawnSettings.SpawnerData> original = cir.getReturnValue();
        if (original == null) {
            return;
        }

        boolean hasWolf = false;
        for (Weighted<MobSpawnSettings.SpawnerData> item : original.unwrap()) {
            if (item.value().type() == EntityTypes.WOLF) {
                hasWolf = true;
                break;
            }
        }

        int multiplierPct = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_WOLF_SPAWN_MULTIPLIER_PCT);
        int minGroup = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_WOLF_SPAWN_GROUP_MIN);
        int maxGroup = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_WOLF_SPAWN_GROUP_MAX);
        boolean expandedBiomes = DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_WOLF_SPAWN_EXPANDED_BIOMES);

        minGroup = Math.max(1, minGroup);
        maxGroup = Math.max(minGroup, maxGroup);
        double multiplier = multiplierPct / 100.0;

        if (hasWolf) {
            WeightedList.Builder<MobSpawnSettings.SpawnerData> builder = WeightedList.builder();
            for (Weighted<MobSpawnSettings.SpawnerData> item : original.unwrap()) {
                if (item.value().type() == EntityTypes.WOLF) {
                    int newWeight = (int) Math.round(item.weight() * multiplier);
                    builder.add(new MobSpawnSettings.SpawnerData(EntityTypes.WOLF, net.minecraft.util.valueproviders.UniformInt.of(minGroup, maxGroup)), Math.max(1, newWeight));
                } else {
                    builder.add(item.value(), item.weight());
                }
            }
            cir.setReturnValue(builder.build());
        } else if (expandedBiomes && mobCategory == MobCategory.CREATURE) {
            String path = biome.unwrapKey().map(k -> k.identifier().getPath().toLowerCase()).orElse("");
            boolean isExpanded = path.contains("plains") || path.contains("meadow") || path.contains("forest") || path.contains("mountain") || path.contains("slope") || path.contains("grove") || path.contains("hill");
            if (isExpanded) {
                WeightedList.Builder<MobSpawnSettings.SpawnerData> builder = WeightedList.builder();
                for (Weighted<MobSpawnSettings.SpawnerData> item : original.unwrap()) {
                    builder.add(item.value(), item.weight());
                }
                int weight = (int) Math.round(8 * multiplier);
                builder.add(new MobSpawnSettings.SpawnerData(EntityTypes.WOLF, net.minecraft.util.valueproviders.UniformInt.of(minGroup, maxGroup)), Math.max(1, weight));
                cir.setReturnValue(builder.build());
            }
        }
    }
}
