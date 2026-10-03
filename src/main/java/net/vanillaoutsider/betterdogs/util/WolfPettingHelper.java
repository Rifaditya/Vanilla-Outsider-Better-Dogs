// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose helper for Shift + Right-Click petting, anger clearing, and storm anxiety soothing.
 */
public class WolfPettingHelper {

    public static final long SOOTHE_DURATION_TICKS = 12000L; // 10 minutes

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
        if (hand != InteractionHand.MAIN_HAND || !itemInHand.isEmpty()) {
            return false;
        }
        if (!player.isSecondaryUseActive() && !player.isCrouching() && !player.isShiftKeyDown()) {
            return false;
        }
        if (wolf instanceof WolfExtensions ext) {
            Level level = wolf.level();
            if (level != null && Math.abs(level.getGameTime() - ext.betterdogs$getSoothedTime()) < 20L) {
                return false;
            }
        }
        return true;
    }

    public static InteractionResult petWolf(Wolf wolf, Player player) {
        if (wolf == null || player == null) {
            return InteractionResult.PASS;
        }

        Level level = wolf.level();
        if (level == null) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            boolean wasAnxious = WolfStormHelper.isStormAnxietyActive(wolf);
            boolean isSubdued = false;
            if (wolf instanceof WolfExtensions ext) {
                isSubdued = ext.betterdogs$isSubdued();
                ext.betterdogs$setSoothedTime(level.getGameTime());
            }

            wolf.stopBeingAngry();
            wolf.setTarget(null);

            float whinePitch = isSubdued ? 0.9F : 1.2F;
            SoundEvent petSound = SmallFightHelper.getWolfWhineSound(wolf);
            wolf.playSound(petSound, 1.0F, whinePitch);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HEART, wolf.getRandomX(1.0), wolf.getRandomY() + 0.5, wolf.getRandomZ(1.0), 3, 0.2, 0.1, 0.2, 0.02);
                serverLevel.sendParticles(ParticleTypes.NOTE, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 4, 0.2, 0.2, 0.2, 0.05);
            }
            WolfAdvancementHelper.grantAdvancement(player, "pet_dog");
            if (wasAnxious) {
                WolfAdvancementHelper.grantAdvancement(player, "soothe_dog");
            }
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Emits soft whine audio for dogs in the subdued mood / penalty state.
     */
    public static void emitSubduedWhine(Wolf wolf) {
        if (wolf == null || wolf.level() == null) {
            return;
        }
        try {
            float pitch = 0.85F + (wolf.getRandom() != null ? wolf.getRandom().nextFloat() * 0.15F : 0.05F);
            SoundEvent whineSound = SmallFightHelper.getWolfWhineSound(wolf);
            wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(),
                    whineSound, wolf.getSoundSource(), 0.9F, pitch);
        } catch (Throwable ignored) {
        }
    }
}
