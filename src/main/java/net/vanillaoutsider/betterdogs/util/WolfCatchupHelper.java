// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Dedicated single-purpose helper for high-speed fast-travel catchup and interdimensional teleport sync.
 */
public final class WolfCatchupHelper {

    private static final double BASE_FOLLOW_DISTANCE = 40.0D;
    private static final double FLYING_FOLLOW_DISTANCE = 32.0D;
    private static final double MIN_SCAN_RADIUS = 64.0D;

    private WolfCatchupHelper() {
    }

    public static double getCatchupTeleportDistance(Wolf wolf, boolean isFlying) {
        double baseMaxFollow = isFlying ? FLYING_FOLLOW_DISTANCE : BASE_FOLLOW_DISTANCE;
        if (wolf == null) {
            return baseMaxFollow;
        }
        float startDist = net.vanillaoutsider.betterdogs.ai.PersonalityFollowOwnerGoal.getEffectiveStartDistance(wolf);
        float goalTeleportThreshold = net.vanillaoutsider.betterdogs.ai.PersonalityFollowOwnerGoal.calculateTeleportThreshold(startDist);
        return Math.max(baseMaxFollow, goalTeleportThreshold);
    }

    public static double calculateCatchupSpeed(Wolf wolf, LivingEntity owner, double baseSpeed) {
        if (wolf == null || owner == null || !wolf.isTame()) {
            return baseSpeed;
        }
        Level level = wolf.level();
        if (level == null) {
            return baseSpeed;
        }

        if (!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_FAST_TRAVEL_CATCHUP, true)) {
            return baseSpeed;
        }

        double distSq = wolf.distanceToSqr(owner);
        boolean isOwnerFast = owner.isPassenger() || owner.getDeltaMovement().lengthSqr() > 0.04D || owner.isFallFlying();

        if (isOwnerFast || distSq > 100.0D) { // > 10 blocks away
            if (distSq > 400.0D) { // > 20 blocks away
                return baseSpeed * 2.0D;
            }
            return baseSpeed * 1.5D;
        }

        return baseSpeed;
    }

    public static void syncOwnerDimensionTeleport(ServerPlayer player, ServerLevel originLevel, ServerLevel destLevel, Vec3 destPos) {
        if (player == null || originLevel == null || destLevel == null || destPos == null) {
            return;
        }
        if (!BetterDogsGameRules.getBoolean(originLevel, BetterDogsGameRules.BD_SYNC_OWNER_TELEPORT, true)) {
            return;
        }

        List<Wolf> nearbyDogs = originLevel.getEntitiesOfClass(
                Wolf.class,
                player.getBoundingBox().inflate(16.0D),
                w -> w != null && w.isTame() && w.isOwnedBy(player) && !w.isInSittingPose() && !w.isLeashed()
        );

        for (Wolf dog : nearbyDogs) {
            dog.teleportTo(destLevel, destPos.x(), destPos.y(), destPos.z(), Collections.emptySet(), dog.getYRot(), dog.getXRot(), false);
            dog.getNavigation().stop();
        }
    }

    public static void checkAndPerformCatchUp(ServerPlayer player) {
        if (player == null || player.isSpectator()) {
            return;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        if (!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_FAST_TRAVEL_CATCHUP, true)) {
            return;
        }

        int aggroStart = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 25);
        int normalStart = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_NORMAL_FOLLOW_START, 10);
        int paciStart = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_PACI_FOLLOW_START, 5);
        float maxConfiguredStart = Math.max(aggroStart, Math.max(normalStart, paciStart));
        double maxThreshold = net.vanillaoutsider.betterdogs.ai.PersonalityFollowOwnerGoal.calculateTeleportThreshold(maxConfiguredStart);
        double maxSpread = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MAX, 50) / 10.0D;
        double scanHorizontal = Math.max(MIN_SCAN_RADIUS, maxThreshold + maxSpread + 16.0D);

        AABB scanBox = player.getBoundingBox().inflate(scanHorizontal, Math.max(32.0D, scanHorizontal * 0.5D), scanHorizontal);
        List<Wolf> nearbyWolves = level.getEntitiesOfClass(Wolf.class, scanBox);

        if (nearbyWolves.isEmpty()) {
            return;
        }

        boolean isFlying = player.getAbilities().flying || player.isFallFlying();
        BlockPos playerBlockPos = player.blockPosition();
        for (Wolf wolf : nearbyWolves) {
            if (!WolfTeleportHelper.isEligibleFollowingWolf(wolf, player)) {
                continue;
            }

            double requiredDist = getCatchupTeleportDistance(wolf, isFlying);
            if (wolf.distanceToSqr(player) > (requiredDist * requiredDist)) {
                Vec3 safePos = WolfTeleportHelper.findSafeTeleportPos(wolf, level, playerBlockPos);
                if (safePos != null) {
                    wolf.teleportTo(level, safePos.x, safePos.y, safePos.z, Set.of(), wolf.getYRot(), wolf.getXRot(), false);
                    wolf.getNavigation().stop();
                }
            }
        }
    }
}
