// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Dedicated single-purpose helper for awarding canine husbandry advancements safely.
 */
public class WolfAdvancementHelper {

    public static final double WITNESS_RADIUS = 16.0;

    public static void grantAdvancement(Player player, String criterionName) {
        if (player instanceof ServerPlayer serverPlayer && criterionName != null && serverPlayer.getServer() != null) {
            ResourceLocation id = new ResourceLocation("betterdogs", "husbandry/" + criterionName);
            Advancement adv = serverPlayer.getServer().getAdvancements().getAdvancement(id);
            if (adv != null) {
                serverPlayer.getAdvancements().award(adv, criterionName);
            }
        }
    }

    public static void grantRoughhousing(Player player) {
        grantAdvancement(player, "roughhousing");
    }

    public static void grantKnowYourPlace(Player player) {
        grantAdvancement(player, "know_your_place");
    }

    public static void awardRoughhousingNearby(ServerLevel level, Vec3 center, double radius) {
        if (level == null || center == null) return;
        List<ServerPlayer> players = level.getPlayers(p -> p.distanceToSqr(center) <= radius * radius);
        for (ServerPlayer player : players) {
            grantRoughhousing(player);
        }
    }

    public static void awardRoughhousingNearby(Wolf a, Wolf b) {
        if (a == null || b == null) return;
        if (a.level() instanceof ServerLevel serverLevel) {
            Vec3 mid = new Vec3((a.getX() + b.getX()) * 0.5, (a.getY() + b.getY()) * 0.5, (a.getZ() + b.getZ()) * 0.5);
            awardRoughhousingNearby(serverLevel, mid, WITNESS_RADIUS);
        }
    }

    public static void awardKnowYourPlaceNearby(ServerLevel level, Vec3 center, double radius) {
        if (level == null || center == null) return;
        List<ServerPlayer> players = level.getPlayers(p -> p.distanceToSqr(center) <= radius * radius);
        for (ServerPlayer player : players) {
            grantKnowYourPlace(player);
        }
    }

    public static void awardKnowYourPlaceNearby(Wolf discipliner, Wolf disciplined) {
        if (discipliner == null || disciplined == null) return;
        if (disciplined.level() instanceof ServerLevel serverLevel) {
            awardKnowYourPlaceNearby(serverLevel, disciplined.position(), WITNESS_RADIUS);
        }
    }
}
