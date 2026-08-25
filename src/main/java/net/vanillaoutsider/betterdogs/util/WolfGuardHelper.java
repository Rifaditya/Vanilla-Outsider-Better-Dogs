// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import java.util.List;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.BetterDogs;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;

/**
 * Dedicated single-purpose helper for managing Guard Mode state, toggling, and territory patrol radii.
 */
public final class WolfGuardHelper {

    private WolfGuardHelper() {
    }

    public static int getPatrolRadius(WolfPersonality personality) {
        if (personality == null) {
            return 8;
        }
        return switch (personality) {
            case AGGRESSIVE -> 12;
            case PACIFIST -> 4;
            case NORMAL -> 8;
        };
    }

    public static boolean canToggleGuard(Wolf wolf, Player player, InteractionHand hand, ItemStack held) {
        if (wolf == null || player == null || !wolf.isTame() || hand != InteractionHand.MAIN_HAND) {
            return false;
        }
        if (held == null || !held.is(Items.BONE)) {
            return false;
        }
        if (!player.isSecondaryUseActive()) {
            return false;
        }
        return wolf.isOwnedBy(player);
    }

    /**
     * Backward-compatible 3-arg overload for testing/fallbacks.
     */
    public static boolean canToggleGuard(Wolf wolf, Player player, ItemStack held) {
        return canToggleGuard(wolf, player, InteractionHand.MAIN_HAND, held);
    }

    public static InteractionResult toggleGuardMode(Wolf wolf, Player player, InteractionHand hand, ItemStack held) {
        if (!canToggleGuard(wolf, player, hand, held) || !(wolf instanceof WolfExtensions ext)) {
            return InteractionResult.PASS;
        }

        Level level = wolf.level();
        if (level == null) {
            return InteractionResult.PASS;
        }

        boolean currentGuarding = ext.betterdogs$isGuardMode();
        boolean newGuarding = !currentGuarding;

        if (!level.isClientSide()) {
            ext.betterdogs$setGuardMode(newGuarding);

            if (newGuarding) {
                BlockPos guardPos = wolf.blockPosition();
                ext.betterdogs$setGuardPos(guardPos);

                // Preserve posture: if sitting, maintain sitting as stationary sentry
                if (wolf.isInSittingPose()) {
                    ext.betterdogs$setSittingManually(true);
                    wolf.setOrderedToSit(true);
                }

                player.sendOverlayMessage(
                        Component.translatable("text.betterdogs.guard_activated", wolf.getName(), guardPos.getX(), guardPos.getY(), guardPos.getZ())
                );

                WolfPersonality personality = ext.betterdogs$getPersonality();
                if (player instanceof ServerPlayer serverPlayer) {
                    BetterDogs.GUARD_WOLF_PERSONALITY.trigger(serverPlayer, personality);
                }

                float pitch = switch (personality) {
                    case AGGRESSIVE -> 0.8f;
                    case NORMAL -> 1.2f;
                    case PACIFIST -> 1.5f;
                };

                SoundEvent sound = personality == WolfPersonality.PACIFIST ?
                        ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().whineSound().value() :
                        ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().ambientSound().value();
                level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), sound, wolf.getSoundSource(), 1.0f, pitch);

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.ENCHANT, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 10, 0.3, 0.3, 0.3, 0.05);
                }
            } else {
                ext.betterdogs$setGuardPos(null);
                ext.betterdogs$setSittingManually(false);
                player.sendOverlayMessage(
                        Component.translatable("text.betterdogs.guard_deactivated", wolf.getName())
                );

                SoundEvent sound = ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().ambientSound().value();
                level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), sound, wolf.getSoundSource(), 1.0f, 1.0f);
            }

            if (held != null) {
                held.consume(1, player);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private static final DustParticleOptions AGGRESSIVE_PARTICLE = new DustParticleOptions(0xFF3333, 0.6f);
    private static final DustParticleOptions PACIFIST_PARTICLE = new DustParticleOptions(0x00FF88, 0.6f);
    private static final DustParticleOptions GUARD_PARTICLE = new DustParticleOptions(0xFFD700, 0.6f);

    public static void tickGuardMode(Wolf wolf, WolfExtensions ext, ServerLevel serverLevel) {
        if (wolf == null || ext == null || serverLevel == null) {
            return;
        }
        WolfPersonality personality = ext.betterdogs$getPersonality();
        double px = wolf.getRandomX(0.5);
        double py = wolf.getRandomY() + 0.5;
        double pz = wolf.getRandomZ(0.5);

        if (personality == WolfPersonality.AGGRESSIVE) {
            serverLevel.sendParticles(AGGRESSIVE_PARTICLE, px, py, pz, 1, 0, 0.05, 0, 0.0);
        } else if (personality == WolfPersonality.PACIFIST) {
            serverLevel.sendParticles(PACIFIST_PARTICLE, px, py, pz, 1, 0, 0.05, 0, 0.0);

            // Watchdog Grace Buff (Regeneration and Resistance to owner/allies within 6 blocks of wolf OR guard post)
            if (wolf.tickCount % 40 == 0 && DynamicGameRuleManager.getBoolean(serverLevel, BetterDogsGameRules.BD_PACIFIST_GUARD_BUFFS)) {
                double buffRangeSqr = 36.0; // 6 blocks
                Player owner = wolf.getOwner() instanceof Player ? (Player) wolf.getOwner() : null;
                if (owner != null) {
                    if (owner.isAlive()) {
                        boolean isNearWolf = wolf.distanceToSqr(owner) <= buffRangeSqr;
                        BlockPos post = ext.betterdogs$getGuardPos();
                        boolean isNearPost = post != null && owner.distanceToSqr(post.getX() + 0.5, post.getY() + 0.5, post.getZ() + 0.5) <= buffRangeSqr;

                        if (isNearWolf || isNearPost) {
                            owner.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, true, true));
                            owner.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 80, 0, true, true));
                        }
                    }

                    // Buff allied wolves within 6 blocks of this wolf
                    List<Wolf> allies = serverLevel.getEntitiesOfClass(Wolf.class, wolf.getBoundingBox().inflate(6.0), w -> w.isTame());
                    for (Wolf ally : allies) {
                        if (ally.getOwner() == owner) {
                            ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, true, true));
                            ally.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 80, 0, true, true));
                        }
                    }
                }
            }
        } else {
            serverLevel.sendParticles(GUARD_PARTICLE, px, py, pz, 1, 0, 0.05, 0, 0.0);
        }
    }

    /**
     * Backward-compatible 2-arg overload for testing/fallbacks.
     */
    public static InteractionResult toggleGuardMode(Wolf wolf, Player player) {
        return toggleGuardMode(wolf, player, InteractionHand.MAIN_HAND, player != null ? player.getMainHandItem() : ItemStack.EMPTY);
    }
}
