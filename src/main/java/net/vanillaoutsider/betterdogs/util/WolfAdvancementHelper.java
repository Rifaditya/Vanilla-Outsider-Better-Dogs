// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Dedicated single-purpose helper for awarding canine husbandry advancements safely.
 */
public class WolfAdvancementHelper {

    public static void grantAdvancement(Player player, String criterionName) {
        if (player instanceof ServerPlayer serverPlayer && criterionName != null && player.level() instanceof ServerLevel serverLevel) {
            Identifier id = Identifier.fromNamespaceAndPath("betterdogs", "husbandry/" + criterionName);
            AdvancementHolder adv = serverLevel.getServer().getAdvancements().get(id);
            if (adv != null) {
                serverPlayer.getAdvancements().award(adv, criterionName);
            }
        }
    }
}
