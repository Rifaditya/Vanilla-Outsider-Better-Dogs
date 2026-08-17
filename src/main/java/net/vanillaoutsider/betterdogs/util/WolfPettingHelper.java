// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;

/**
 * Dedicated single-purpose helper for Shift + Right-Click petting, anger clearing, and storm anxiety soothing.
 */
public final class WolfPettingHelper {

    public static final long SOOTHE_DURATION_TICKS = 12000L; // 10 minutes
    public static final long PET_COOLDOWN_TICKS = 20L; // 1 second debounce

    private WolfPettingHelper() {
    }

    public static boolean isSoothed(Wolf wolf) {
        if (wolf instanceof WolfExtensions ext) {
            long soothedTime = ext.betterdogs$getSoothedTime();
            if (soothedTime <= 0L) {
                return false;
            }
            Level level = wolf.level();
            return level != null && (level.getGameTime() - soothedTime) < SOOTHE_DURATION_TICKS;
        }
        return false;
    }

    public static boolean canPet(Wolf wolf, Player player, InteractionHand hand, ItemStack itemInHand) {
        if (wolf == null || player == null) {
            return false;
        }
        if (!wolf.isTame() || !wolf.isOwnedBy(player)) {
            return false;
        }
        if (hand != InteractionHand.MAIN_HAND || (itemInHand != null && !itemInHand.isEmpty())) {
            return false;
        }
        return player.isSecondaryUseActive();
    }

    public static InteractionResult petWolf(Wolf wolf, Player player) {
        if (wolf == null || player == null) {
            return InteractionResult.PASS;
        }

        Level level = wolf.level();
        if (level == null) {
            return InteractionResult.PASS;
        }

        if (wolf instanceof WolfExtensions ext) {
            long lastPetTime = ext.betterdogs$getSoothedTime();
            // Debounce check to prevent spamming
            if (lastPetTime > 0L && (level.getGameTime() - lastPetTime) < PET_COOLDOWN_TICKS) {
                return InteractionResult.SUCCESS;
            }

            if (!level.isClientSide()) {
                ext.betterdogs$setSoothedTime(level.getGameTime());

                wolf.stopBeingAngry();
                wolf.setTarget(null);

                try {
                    var soundSet = ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet();
                    if (soundSet != null && soundSet.whineSound() != null) {
                        wolf.playSound(soundSet.whineSound().value(), 1.0F, 1.2F);
                    }
                } catch (Exception ignored) {
                }

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HEART, wolf.getRandomX(1.0), wolf.getRandomY() + 0.5, wolf.getRandomZ(1.0), 3, 0.2, 0.1, 0.2, 0.02);
                    serverLevel.sendParticles(ParticleTypes.NOTE, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 4, 0.2, 0.2, 0.2, 0.05);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
