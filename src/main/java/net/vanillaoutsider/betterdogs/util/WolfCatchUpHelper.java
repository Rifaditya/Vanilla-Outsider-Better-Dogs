// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: ServerPlayer.java, Wolf.java (26.2+)
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.List;
import java.util.Set;

public class WolfCatchUpHelper {

    private static final double MAX_FOLLOW_DISTANCE_SQR = 1600.0; // 40 blocks

    /**
     * Checks nearby loaded wolves and teleports active followers that fall too far behind.
     */
    public static void checkAndPerformCatchUp(ServerPlayer player) {
        if (player == null || player.isSpectator()) {
            return;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        if (!DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_FAST_TRAVEL_CATCHUP)) {
            return;
        }

        AABB scanBox = player.getBoundingBox().inflate(64.0, 32.0, 64.0);
        List<Wolf> nearbyWolves = level.getEntitiesOfClass(Wolf.class, scanBox);

        if (nearbyWolves.isEmpty()) {
            return;
        }

        BlockPos playerBlockPos = player.blockPosition();
        for (Wolf wolf : nearbyWolves) {
            // Strictly check if wolf is in active follow state (ignoring sitting, leashed, or guarding dogs)
            if (!WolfTeleportHelper.isEligibleFollowingWolf(wolf, player)) {
                continue;
            }

            if (wolf.distanceToSqr(player) > MAX_FOLLOW_DISTANCE_SQR) {
                Vec3 safePos = WolfTeleportHelper.findSafeTeleportPos(wolf, level, playerBlockPos);
                if (safePos != null) {
                    wolf.teleportTo(level, safePos.x, safePos.y, safePos.z, Set.of(), wolf.getYRot(), wolf.getXRot(), false);
                    wolf.getNavigation().stop();
                }
            }
        }
    }
}
