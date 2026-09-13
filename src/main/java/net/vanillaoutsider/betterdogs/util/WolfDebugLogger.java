// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: WolfDebugLogger.java (26.3)
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.BetterDogs;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Utility for conditional debug logging based on GameRule state.
 */
public class WolfDebugLogger {

    /**
     * Logs a message if betterdogdebugging is enabled.
     */
    public static void log(Wolf wolf, String message) {
        if (DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_DEBUGGING)) {
            BetterDogs.LOGGER.info("[DEBUG][Wolf {}] {}", wolf.getUUID().toString().substring(0, 8), message);
        }
    }

    /**
     * Logs a message with a custom tag if betterdogdebugging is enabled.
     */
    public static void log(Wolf wolf, String tag, String message) {
        if (DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_DEBUGGING)) {
            BetterDogs.LOGGER.info("[DEBUG][{}][Wolf {}] {}", tag, wolf.getUUID().toString().substring(0, 8), message);
        }
    }
}
