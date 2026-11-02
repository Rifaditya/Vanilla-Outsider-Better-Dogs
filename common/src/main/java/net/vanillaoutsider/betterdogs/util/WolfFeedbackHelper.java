// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for sending optional player actionbar and subtitle feedback.
 */
public class WolfFeedbackHelper {

    public static void sendFeedback(Player player, Level level, Component message) {
        if (player == null || level == null || level.isClientSide() || message == null) {
            return;
        }
        if (BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_ACTIONBAR_FEEDBACK, false)) {
            player.displayClientMessage(message, true);
        }
    }
}
