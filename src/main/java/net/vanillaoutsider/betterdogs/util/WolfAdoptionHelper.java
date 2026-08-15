// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose helper for listing dogs for adoption and transferring ownership.
 */
public class WolfAdoptionHelper {

    public static boolean canListForAdoption(Wolf wolf, Player player, ItemStack held) {
        if (wolf == null || player == null || held == null) {
            return false;
        }
        if (!wolf.isTame() || !wolf.isOwnedBy(player)) {
            return false;
        }
        if (!player.isCrouching() && !player.isShiftKeyDown()) {
            return false;
        }
        if (!(wolf instanceof WolfExtensions ext) || ext.betterdogs$isUpForAdoption()) {
            return false;
        }
        return held.is(Items.PAPER);
    }

    public static boolean canCancelAdoption(Wolf wolf, Player player, ItemStack held) {
        if (wolf == null || player == null) {
            return false;
        }
        if (!wolf.isTame() || !wolf.isOwnedBy(player)) {
            return false;
        }
        if (!player.isCrouching() && !player.isShiftKeyDown()) {
            return false;
        }
        if (!(wolf instanceof WolfExtensions ext) || !ext.betterdogs$isUpForAdoption()) {
            return false;
        }
        return held.isEmpty() || held.is(Items.PAPER);
    }

    public static boolean canAdopt(Wolf wolf, Player player, ItemStack held) {
        if (wolf == null || player == null) {
            return false;
        }
        if (!wolf.isTame() || wolf.isOwnedBy(player)) {
            return false;
        }
        if (!player.isCrouching() && !player.isShiftKeyDown()) {
            return false;
        }
        if (!(wolf instanceof WolfExtensions ext) || !ext.betterdogs$isUpForAdoption()) {
            return false;
        }
        return held.isEmpty();
    }

    public static InteractionResult tryHandleAdoption(Wolf wolf, Player player, ItemStack held) {
        if (wolf == null || player == null) {
            return InteractionResult.PASS;
        }

        Level level = wolf.level();
        if (level == null) {
            return InteractionResult.PASS;
        }

        // 1. Owner cancels adoption listing
        if (canCancelAdoption(wolf, player, held)) {
            if (wolf instanceof WolfExtensions ext) {
                ext.betterdogs$setUpForAdoption(false);
            }
            if (!level.isClientSide()) {
                player.displayClientMessage(Component.literal("§6Adoption: §cListing Cancelled"), true);
                level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 0.8f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }

        // 2. Owner lists dog for adoption
        if (canListForAdoption(wolf, player, held)) {
            if (wolf instanceof WolfExtensions ext) {
                ext.betterdogs$setUpForAdoption(true);
            }
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            if (!level.isClientSide()) {
                player.displayClientMessage(Component.literal("§6Adoption: §aDog is now listed for adoption!"), true);
                level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.0f);
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 10, 0.3, 0.3, 0.3, 0.05);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // 3. New owner adopts dog
        if (canAdopt(wolf, player, held)) {
            if (wolf instanceof WolfExtensions ext) {
                ext.betterdogs$setUpForAdoption(false);
            }
            wolf.tame(player);

            if (!level.isClientSide()) {
                String dogName = wolf.hasCustomName() ? wolf.getCustomName().getString() : "Dog";
                player.displayClientMessage(Component.literal("§6Adoption: §aYou have adopted " + dogName + "!"), true);
                level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8f, 1.2f);
                level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.WOLF_SHAKE, SoundSource.NEUTRAL, 1.0f, 1.2f);
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HEART, wolf.getX(), wolf.getY() + 0.6, wolf.getZ(), 12, 0.4, 0.4, 0.4, 0.1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public static void tickAdoptionAmbientParticles(Wolf wolf) {
        if (wolf == null) {
            return;
        }
        Level level = wolf.level();
        if (level == null || level.isClientSide()) {
            return;
        }
        if (wolf instanceof WolfExtensions ext && ext.betterdogs$isUpForAdoption()) {
            if (wolf.tickCount % 40 == 0 && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 2, 0.25, 0.25, 0.25, 0.02);
            }
        }
    }
}
