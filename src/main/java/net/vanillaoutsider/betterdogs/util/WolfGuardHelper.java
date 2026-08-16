// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

/**
 * Dedicated single-purpose helper for managing Guard Mode state, toggling, and territory patrol radii.
 */
public class WolfGuardHelper {

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

    public static boolean canToggleGuard(Wolf wolf, Player player, ItemStack held) {
        if (wolf == null || player == null || !wolf.isTame()) {
            return false;
        }
        if (!player.isCrouching() && !player.isShiftKeyDown()) {
            return false;
        }
        if (held != null && !held.isEmpty()) {
            return false;
        }
        return wolf.isOwnedBy(player);
    }

    public static InteractionResult toggleGuardMode(Wolf wolf, Player player) {
        if (wolf == null || player == null || !(wolf instanceof WolfExtensions ext)) {
            return InteractionResult.PASS;
        }

        Level level = wolf.getCommandSenderWorld();
        if (level == null) {
            return InteractionResult.PASS;
        }

        boolean currentGuarding = ext.betterdogs$isGuarding();
        boolean newGuarding = !currentGuarding;

        ext.betterdogs$setGuarding(newGuarding);

        if (newGuarding) {
            BlockPos guardPos = wolf.blockPosition();
            ext.betterdogs$setGuardPos(guardPos);
            WolfFeedbackHelper.sendFeedback(
                player,
                level,
                Component.literal("§6Guard Mode: §aActive §7(" + guardPos.getX() + ", " + guardPos.getY() + ", " + guardPos.getZ() + ")")
            );
        } else {
            ext.betterdogs$setGuardPos(null);
            WolfFeedbackHelper.sendFeedback(
                player,
                level,
                Component.literal("§6Guard Mode: §7Inactive")
            );
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ENCHANT, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 10, 0.3, 0.3, 0.3, 0.05);
            level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 0.8f, 1.2f);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
