// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
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

    public static final double WITNESS_RADIUS = 16.0;

    /**
     * Awards the Roughhousing advancement to the given player.
     */
    public static void grantRoughhousing(Player player) {
        grantAdvancement(player, "roughhousing", "roughhousing");
    }

    /**
     * Awards the Know Your Place advancement to the given player.
     */
    public static void grantKnowYourPlace(Player player) {
        grantAdvancement(player, "know_your_place", "know_your_place");
    }

    /**
     * Awards the Roughhousing advancement to players within the specified radius (default 16 blocks) of a location.
     */
    public static void awardRoughhousingNearby(net.minecraft.server.level.ServerLevel level, net.minecraft.world.phys.Vec3 pos, double radius) {
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
    public static void awardRoughhousingNearby(net.minecraft.world.entity.animal.wolf.Wolf wolfA, net.minecraft.world.entity.animal.wolf.Wolf wolfB) {
        if (wolfA == null && wolfB == null) {
            return;
        }
        net.minecraft.world.entity.animal.wolf.Wolf anchor = (wolfA != null) ? wolfA : wolfB;
        if (anchor.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            net.minecraft.world.phys.Vec3 pos = (wolfA != null && wolfB != null)
                    ? new net.minecraft.world.phys.Vec3((wolfA.getX() + wolfB.getX()) * 0.5, (wolfA.getY() + wolfB.getY()) * 0.5, (wolfA.getZ() + wolfB.getZ()) * 0.5)
                    : anchor.position();
            awardRoughhousingNearby(serverLevel, pos, WITNESS_RADIUS);
        }
    }

    /**
     * Awards the Know Your Place advancement to players within the specified radius (default 16 blocks) of a location.
     */
    public static void awardKnowYourPlaceNearby(net.minecraft.server.level.ServerLevel level, net.minecraft.world.phys.Vec3 pos, double radius) {
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
    public static void awardKnowYourPlaceNearby(net.minecraft.world.entity.animal.wolf.Wolf discipliner, net.minecraft.world.entity.animal.wolf.Wolf victim) {
        if (discipliner == null && victim == null) {
            return;
        }
        net.minecraft.world.entity.animal.wolf.Wolf anchor = (discipliner != null) ? discipliner : victim;
        if (anchor.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            awardKnowYourPlaceNearby(serverLevel, anchor.position(), WITNESS_RADIUS);
        }
    }
}
