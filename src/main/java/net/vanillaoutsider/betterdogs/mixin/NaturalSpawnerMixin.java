// Verified against: NaturalSpawner.java (26.1.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
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
        if (original == null || original.isEmpty()) {
            return;
        }

        boolean hasWolf = false;
        for (Weighted<MobSpawnSettings.SpawnerData> item : original.unwrap()) {
            if (item.value().type() == net.minecraft.world.entity.EntityType.WOLF) {
                hasWolf = true;
                break;
            }
        }

        if (!hasWolf) {
            return;
        }

        int multiplierPct = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_WOLF_SPAWN_MULTIPLIER_PCT);
        if (multiplierPct == 100) {
            return; // No modification needed for 1.0x
        }

        double multiplier = multiplierPct / 100.0;
        WeightedList.Builder<MobSpawnSettings.SpawnerData> builder = WeightedList.builder();
        for (Weighted<MobSpawnSettings.SpawnerData> item : original.unwrap()) {
            if (item.value().type() == net.minecraft.world.entity.EntityType.WOLF) {
                int newWeight = (int) Math.round(item.weight() * multiplier);
                builder.add(item.value(), Math.max(1, newWeight));
            } else {
                builder.add(item.value(), item.weight());
            }
        }
        
        cir.setReturnValue(builder.build());
    }
}
