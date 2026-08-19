// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for sending optional player actionbar and subtitle feedback.
 */
public final class WolfFeedbackHelper {

    private WolfFeedbackHelper() {
    }

    /**
     * Sends an actionbar message to the player if the actionbar feedback gamerule is enabled.
     */
    public static void sendFeedback(Player player, Level level, Component message) {
        if (player == null || level == null || level.isClientSide() || message == null) {
            return;
        }
        if (DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_ACTIONBAR_FEEDBACK)) {
            player.sendOverlayMessage(message);
        }
    }
}
