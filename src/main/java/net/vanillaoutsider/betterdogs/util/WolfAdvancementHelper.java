// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Dedicated single-purpose helper for awarding canine husbandry advancements safely.
 */
public final class WolfAdvancementHelper {

    private WolfAdvancementHelper() {
    }

    /**
     * Awards an advancement criterion to the player with dual namespace resolution.
     */
    public static void grantAdvancement(Player player, String criterionName) {
        if (player instanceof ServerPlayer serverPlayer && criterionName != null && serverPlayer.level() != null) {
            MinecraftServer server = serverPlayer.level().getServer();
            if (server != null) {
                // Check minecraft:husbandry/
                Identifier mcId = Identifier.fromNamespaceAndPath("minecraft", "husbandry/" + criterionName);
                AdvancementHolder adv = server.getAdvancements().get(mcId);
                if (adv != null) {
                    serverPlayer.getAdvancements().award(adv, criterionName);
                    return;
                }
                // Fallback to betterdogs:husbandry/
                Identifier modId = Identifier.fromNamespaceAndPath("betterdogs", "husbandry/" + criterionName);
                adv = server.getAdvancements().get(modId);
                if (adv != null) {
                    serverPlayer.getAdvancements().award(adv, criterionName);
                }
            }
        }
    }

    /**
     * Awards an explicit advancement and criterion by ID.
     */
    public static void grantAdvancement(Player player, String advancementName, String criterionName) {
        if (player instanceof ServerPlayer serverPlayer && advancementName != null && criterionName != null && serverPlayer.level() != null) {
            MinecraftServer server = serverPlayer.level().getServer();
            if (server != null) {
                Identifier id = Identifier.fromNamespaceAndPath("minecraft", "husbandry/" + advancementName);
                AdvancementHolder adv = server.getAdvancements().get(id);
                if (adv == null) {
                    id = Identifier.fromNamespaceAndPath("betterdogs", "husbandry/" + advancementName);
                    adv = server.getAdvancements().get(id);
                }
                if (adv != null) {
                    serverPlayer.getAdvancements().award(adv, criterionName);
                }
            }
        }
    }
}
