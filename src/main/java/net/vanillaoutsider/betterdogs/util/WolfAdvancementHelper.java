// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;

/**
 * Dedicated single-purpose helper for awarding canine husbandry advancements safely.
 */
public class WolfAdvancementHelper {

    public static final double WITNESS_RADIUS = 16.0;

    public static void grantAdvancement(Player player, String criterionName) {
        if (player instanceof ServerPlayer serverPlayer && criterionName != null && serverPlayer.getServer() != null) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("betterdogs", "husbandry/" + criterionName);
            AdvancementHolder adv = serverPlayer.getServer().getAdvancements().get(id);
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

    /**
     * Awards the Roughhousing advancement to players within the specified radius (default 16 blocks) of a location.
     */
    public static void awardRoughhousingNearby(ServerLevel level, net.minecraft.world.phys.Vec3 pos, double radius) {
        if (level == null || pos == null || radius <= 0) {
            return;
        }
        double radiusSqr = radius * radius;
        for (ServerPlayer player : level.players()) {
            if (player != null && player.distanceToSqr(pos) <= radiusSqr) {
                grantRoughhousing(player);
            }
        }
    }

    /**
     * Awards the Roughhousing advancement to players within 16 blocks witnessing playful sparring.
     */
    public static void awardRoughhousingNearby(Wolf wolfA, Wolf wolfB) {
        if (wolfA == null && wolfB == null) {
            return;
        }
        Wolf anchor = (wolfA != null) ? wolfA : wolfB;
        if (anchor.level() instanceof ServerLevel serverLevel) {
            net.minecraft.world.phys.Vec3 pos = (wolfA != null && wolfB != null)
                    ? new net.minecraft.world.phys.Vec3((wolfA.getX() + wolfB.getX()) * 0.5, (wolfA.getY() + wolfB.getY()) * 0.5, (wolfA.getZ() + wolfB.getZ()) * 0.5)
                    : anchor.position();
            awardRoughhousingNearby(serverLevel, pos, WITNESS_RADIUS);
        }
    }

    /**
     * Awards the Know Your Place advancement to players within the specified radius (default 16 blocks) of a location.
     */
    public static void awardKnowYourPlaceNearby(ServerLevel level, net.minecraft.world.phys.Vec3 pos, double radius) {
        if (level == null || pos == null || radius <= 0) {
            return;
        }
        double radiusSqr = radius * radius;
        for (ServerPlayer player : level.players()) {
            if (player != null && player.distanceToSqr(pos) <= radiusSqr) {
                grantKnowYourPlace(player);
            }
        }
    }

    /**
     * Awards the Know Your Place advancement to players within 16 blocks witnessing a hierarchy correction snap.
     */
    public static void awardKnowYourPlaceNearby(Wolf discipliner, Wolf victim) {
        if (discipliner == null && victim == null) {
            return;
        }
        Wolf anchor = (discipliner != null) ? discipliner : victim;
        if (anchor.level() instanceof ServerLevel serverLevel) {
            awardKnowYourPlaceNearby(serverLevel, anchor.position(), WITNESS_RADIUS);
        }
    }
}
