// Verified against: WolfSpawnMixin.java (26.2-pre-1)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.mixin;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.ServerLevelAccessor;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfDebugLogger;
import net.vanillaoutsider.betterdogs.util.WolfStatManager;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to handle wolf spawning behavior, cluster limits, and density boosts.
 */
@Mixin(Wolf.class)
public abstract class WolfSpawnMixin extends TamableAnimal {

    protected WolfSpawnMixin() {
        super(null, null);
    }

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void betterdogs$onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData,
            CallbackInfoReturnable<SpawnGroupData> cir) {
        if (!level.getLevel().isClientSide()) {
            Wolf wolf = (Wolf) (Object) this;
            if (wolf instanceof WolfExtensions ext) {
                WolfPersonality personality = WolfPersonality.random(level.getLevel());
                ext.betterdogs$setPersonality(personality);
                WolfDebugLogger.log(wolf, "Spawn", "Personality assigned: " + personality.name());

                long dna = this.getRandom().nextLong();
                ext.betterdogs$setDNA(dna);

                // Apply personality stats and scale immediately at spawn
                WolfStatManager.applyPersonalityStats(wolf, personality);

                net.dasik.social.core.SocialRegistry.register((net.dasik.social.api.SocialEntity) wolf);
                WolfDebugLogger.log(wolf, "Spawn", "Initialized with personality: " + personality.name());

                // Density Boost Logic (v3.3.0)
                if (spawnReason == EntitySpawnReason.NATURAL && groupData == null) {
                    int boostChance = DynamicGameRuleManager.getInt(level.getLevel(), BetterDogsGameRules.BD_WOLF_SPAWN_DENSITY_BOOST);
                    int roll = this.getRandom().nextInt(100);
                    WolfDebugLogger.log(wolf, "Spawn", "Density Boost Evaluation - Roll: " + roll + " / Target: " + boostChance);
                    if (roll < boostChance) {
                        this.betterdogs$triggerReinforcementSpawn(level);
                    }
                }
            }
        }
    }

    @Unique
    private void betterdogs$triggerReinforcementSpawn(ServerLevelAccessor level) {
        Wolf wolf = (Wolf) (Object) this;
        ServerLevel serverLevel = level.getLevel();
        BlockPos pos = wolf.blockPosition();

        // Find a spot nearby
        BlockPos spawnPos = pos.offset(
            this.getRandom().nextInt(16) - 8,
            0,
            this.getRandom().nextInt(16) - 8
        );

        // Spawn a reinforcement cluster
        int clusterSize = DynamicGameRuleManager.getInt(serverLevel, BetterDogsGameRules.BD_WOLF_PACK_CLUSTER_SIZE);
        int count = clusterSize / 2 + 1;
        for (int i = 0; i < count; i++) {
             Wolf reinforcement = net.minecraft.world.entity.EntityTypes.WOLF.create(serverLevel, EntitySpawnReason.REINFORCEMENT);
             if (reinforcement != null) {
                 reinforcement.snapTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, this.getRandom().nextFloat() * 360.0F, 0.0F);
                 reinforcement.finalizeSpawn(level, serverLevel.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.REINFORCEMENT, null);
                 serverLevel.addFreshEntityWithPassengers(reinforcement);
             }
        }
        WolfDebugLogger.log(wolf, "Spawn", "Density Boost: Reinforcement pack (" + count + ") spawned at " + spawnPos);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return DynamicGameRuleManager.getInt(this.level(), BetterDogsGameRules.BD_WOLF_PACK_CLUSTER_SIZE);
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide()) {
            Wolf wolf = (Wolf) (Object) this;
            net.dasik.social.core.SocialRegistry.unregister((net.dasik.social.api.SocialEntity) wolf);
        }
        super.remove(reason);
    }
}
