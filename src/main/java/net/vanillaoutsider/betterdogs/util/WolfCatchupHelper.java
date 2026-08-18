// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
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

    private static final double MAX_FOLLOW_DISTANCE_SQR = 1600.0; // 40 blocks

    private WolfCatchupHelper() {
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

        AABB scanBox = player.getBoundingBox().inflate(64.0, 32.0, 64.0);
        List<Wolf> nearbyWolves = level.getEntitiesOfClass(Wolf.class, scanBox);

        if (nearbyWolves.isEmpty()) {
            return;
        }

        boolean isFlying = player.getAbilities().flying || player.isFallFlying();
        double requiredDistSq = isFlying ? 1024.0D : MAX_FOLLOW_DISTANCE_SQR;

        BlockPos playerBlockPos = player.blockPosition();
        for (Wolf wolf : nearbyWolves) {
            if (!WolfTeleportHelper.isEligibleFollowingWolf(wolf, player)) {
                continue;
            }

            if (wolf.distanceToSqr(player) > requiredDistSq) {
                Vec3 safePos = WolfTeleportHelper.findSafeTeleportPos(wolf, level, playerBlockPos);
                if (safePos != null) {
                    wolf.teleportTo(level, safePos.x, safePos.y, safePos.z, Set.of(), wolf.getYRot(), wolf.getXRot(), false);
                    wolf.getNavigation().stop();
                }
            }
        }
    }
}
