// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Dedicated single-purpose helper for awarding canine husbandry advancements safely.
 */
public class WolfAdvancementHelper {

    public static void grantAdvancement(Player player, String criterionName) {
        if (player instanceof ServerPlayer serverPlayer && criterionName != null && serverPlayer.getServer() != null) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("betterdogs", "husbandry/" + criterionName);
            AdvancementHolder adv = serverPlayer.getServer().getAdvancements().get(id);
            if (adv != null) {
                serverPlayer.getAdvancements().award(adv, criterionName);
            }
        }
    }
}
