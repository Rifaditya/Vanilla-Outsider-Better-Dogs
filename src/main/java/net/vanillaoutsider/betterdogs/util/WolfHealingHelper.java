// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

/**
 * Dedicated single-purpose helper for passive out-of-combat wolf regeneration.
 */
public class WolfHealingHelper {

    public static int tickPassiveHealing(Wolf wolf, WolfExtensions ext, int healTimer) {
        if (wolf == null || ext == null) {
            return 0;
        }
        int lastDamageTime = ext.betterdogs$getLastDamageTime();
        if (wolf.tickCount - lastDamageTime > BetterDogsConfig.get().getCombatHealDelayTicks()
                && wolf.getHealth() < wolf.getMaxHealth()) {
            healTimer++;
            if (healTimer >= BetterDogsConfig.get().getPassiveHealIntervalTicks()) {
                wolf.heal((float) BetterDogsConfig.get().getPassiveHealAmount());
                return 0;
            }
            return healTimer;
        } else {
            return 0;
        }
    }
}
