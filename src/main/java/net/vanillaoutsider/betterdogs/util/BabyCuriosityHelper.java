// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.WolfPersonality;

/**
 * Single-purpose helper managing puppy curiosity eligibility, target filtering, foliage block validation, and feedback cues.
 */
public final class BabyCuriosityHelper {

    public static final double MAX_TARGET_DISTANCE_SQ = 100.0D; // 10 blocks squared
    public static final double CLOSE_INSPECT_DISTANCE_SQ = 6.25D; // 2.5 blocks squared

    private BabyCuriosityHelper() {
    }

    /**
     * Checks if a puppy is eligible to exhibit exploratory curiosity.
     */
    public static boolean canExhibitCuriosity(Wolf wolf) {
        if (wolf == null || !wolf.isAlive() || !wolf.isBaby()) {
            return false;
        }

        WolfPersonality personality = WolfPersistentData.getPersistedPersonality(wolf);
        if (personality == WolfPersonality.AGGRESSIVE) {
            return false;
        }

        if (wolf.getTarget() != null || wolf.isOrderedToSit() || wolf.isInSittingPose() || wolf.isLeashed()) {
            return false;
        }

        return !WolfPersistentData.isPersistedGuardMode(wolf);
    }

    /**
     * Calculates the curiosity trigger interval in ticks based on personality.
     */
    public static int calculateCuriosityDelay(WolfPersonality personality) {
        if (personality == null) {
            return 80;
        }
        return switch (personality) {
            case PACIFIST -> 40; // High curiosity (2 seconds)
            case NORMAL -> 80;   // Normal curiosity (4 seconds)
            case AGGRESSIVE -> -1; // Disinterested
        };
    }

    /**
     * Checks if an entity is a valid harmless target for puppy curiosity.
     */
    public static boolean isCuriousEntity(LivingEntity entity, Wolf wolf) {
        if (entity == null || !entity.isAlive() || entity == wolf) {
            return false;
        }
        if (entity instanceof Monster) {
            return false;
        }
        return entity instanceof Animal || entity instanceof Player || entity instanceof Wolf;
    }

    /**
     * Checks if a block state represents an interesting nature or foliage block.
     */
    public static boolean isInterestingBlock(BlockState state) {
        if (state == null) {
            return false;
        }
        try {
            if (state.is(net.vanillaoutsider.betterdogs.registry.BetterDogsTags.CURIOSITY_BLOCKS)) {
                return true;
            }
        } catch (Throwable ignored) {
            // Fallback for uninitialized tag registries
        }
        return state.is(Blocks.TALL_GRASS) || state.is(Blocks.SHORT_GRASS)
                || state.is(Blocks.FERN) || state.is(Blocks.LARGE_FERN)
                || state.is(Blocks.DANDELION) || state.is(Blocks.POPPY)
                || state.is(Blocks.PUMPKIN) || state.is(Blocks.MELON)
                || state.is(Blocks.SWEET_BERRY_BUSH);
    }

    /**
     * Dispatches curiosity feedback (soft baby ambient audio and happy villager particles).
     */
    public static void playCuriosityFeedback(Wolf wolf) {
        if (wolf == null || wolf.level() == null || wolf.level().isClientSide()) {
            return;
        }

        if (wolf.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(
                    null,
                    wolf.getX(),
                    wolf.getY(),
                    wolf.getZ(),
                    SoundEvents.WOLF_AMBIENT_BABY.value(),
                    SoundSource.NEUTRAL,
                    1.0F,
                    1.2F
            );
            WolfParticleHelper.spawnParticles(
                    wolf,
                    ParticleTypes.HAPPY_VILLAGER,
                    0.4D,
                    0.2D,
                    0.2D,
                    0.2D,
                    0.05D
            );
        }
    }
}
