// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.List;

public class WolfTeleportHelper {

    /**
     * Strictly verifies if a wolf is in an active owner follow state.
     */
    public static boolean isEligibleFollowingWolf(Wolf wolf, ServerPlayer player) {
        if (wolf == null || !wolf.isAlive() || !wolf.isTame() || !wolf.isOwnedBy(player)) {
            return false;
        }
        if (wolf.unableToMoveToOwner()) {
            return false; // Sitting, passenger, leashed, or spectator owner
        }
        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$isGuardMode() || ext.betterdogs$isSittingManually()) {
                return false; // Sitting manually or in Guard Mode / Sentry
            }
        }
        return true;
    }

    /**
     * Strictly verifies if a wolf is an owned sitting wolf eligible for resume follow commands (Yearn Horn).
     */
    public static boolean isEligibleSittingWolf(Wolf wolf, ServerPlayer player) {
        if (wolf == null || !wolf.isAlive() || !wolf.isTame() || !wolf.isOwnedBy(player)) {
            return false;
        }
        if (wolf.isLeashed()) {
            return false;
        }
        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$isGuardMode()) {
                return false; // Guard Mode sentries remain on post
            }
            if (ext.betterdogs$isSittingManually()) {
                return true;
            }
        }
        return wolf.isOrderedToSit();
    }

    /**
     * Synchronizes active following wolves when the owner teleports.
     */
    public static void syncOwnerTeleport(ServerPlayer player, ServerLevel oldLevel, Vec3 oldPos, ServerLevel newLevel, Vec3 newPos) {
        if (player == null || oldLevel == null || newLevel == null) {
            return;
        }

        boolean sameLevel = (oldLevel == newLevel);
        if (sameLevel && oldPos.distanceToSqr(newPos) < 576.0) { // < 24 blocks
            return;
        }

        if (!DynamicGameRuleManager.getBoolean(newLevel, BetterDogsGameRules.BD_SYNC_OWNER_TELEPORT)) {
            return;
        }

        AABB searchBox = new AABB(oldPos.x - 32.0, oldPos.y - 16.0, oldPos.z - 32.0,
                oldPos.x + 32.0, oldPos.y + 16.0, oldPos.z + 32.0);

        List<Wolf> candidateWolves = oldLevel.getEntitiesOfClass(Wolf.class, searchBox,
                wolf -> isEligibleFollowingWolf(wolf, player));

        if (candidateWolves.isEmpty()) {
            return;
        }

        BlockPos targetCenter = BlockPos.containing(newPos);
        for (Wolf wolf : candidateWolves) {
            Vec3 safePos = findSafeTeleportPos(wolf, newLevel, targetCenter);
            if (safePos != null) {
                wolf.teleportTo(newLevel, safePos.x, safePos.y, safePos.z, java.util.Set.of(), wolf.getYRot(), wolf.getXRot(), false);
                wolf.getNavigation().stop();
            }
        }
    }

    /**
     * Finds a safe, solid, walkable teleport position near the given center block position.
     * If the player is airborne or flying, performs ground-tracing to land safely on solid terrain below.
     * Returns null if no safe walkable solid block exists (strictly preventing mid-air / sky teleports).
     */
    public static Vec3 findSafeTeleportPos(Wolf wolf, ServerLevel level, BlockPos center) {
        if (wolf == null || level == null || center == null) {
            return null;
        }

        // 1. Direct offset scan around target center (for grounded/near-surface players)
        for (int attempt = 0; attempt < 12; ++attempt) {
            int xOffset = wolf.getRandom().nextIntBetweenInclusive(-3, 3);
            int zOffset = wolf.getRandom().nextIntBetweenInclusive(-3, 3);
            if (Math.abs(xOffset) < 1 && Math.abs(zOffset) < 1) continue;
            int yOffset = wolf.getRandom().nextIntBetweenInclusive(-1, 1);

            BlockPos candidate = center.offset(xOffset, yOffset, zOffset);
            if (canTeleportTo(wolf, level, candidate)) {
                return Vec3.atBottomCenterOf(candidate);
            }
        }

        // 2. Ground-tracing fallback: If player is airborne / flying, find solid ground directly below
        return findSafeGroundPosBelow(wolf, level, center);
    }

    /**
     * Performs hazard-aware ground scanning below the player's coordinates using heightmaps and raycasts.
     */
    public static Vec3 findSafeGroundPosBelow(Wolf wolf, ServerLevel level, BlockPos center) {
        if (wolf == null || level == null || center == null) {
            return null;
        }

        // A. Heightmap scan for outdoor / overworld flight
        try {
            BlockPos heightmapPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, center);
            if (heightmapPos.getY() <= center.getY() + 2 && heightmapPos.getY() > level.getMinY()) {
                for (int attempt = 0; attempt < 10; ++attempt) {
                    int xOffset = wolf.getRandom().nextIntBetweenInclusive(-3, 3);
                    int zOffset = wolf.getRandom().nextIntBetweenInclusive(-3, 3);
                    int yOffset = wolf.getRandom().nextIntBetweenInclusive(-1, 1);

                    BlockPos candidate = heightmapPos.offset(xOffset, yOffset, zOffset);
                    if (canTeleportTo(wolf, level, candidate)) {
                        return Vec3.atBottomCenterOf(candidate);
                    }
                }
            }
        } catch (Throwable ignored) {
            // Headless / non-standard level fallback
        }

        // B. Downward vertical scan (for caves, nether, structures)
        for (int dy = 1; dy <= 32; dy++) {
            BlockPos scanPos = center.below(dy);
            if (scanPos.getY() < level.getMinY()) {
                break;
            }
            if (canTeleportTo(wolf, level, scanPos)) {
                return Vec3.atBottomCenterOf(scanPos);
            }
        }

        // No safe walkable ground exists (e.g. high over open void, deep ocean, lava) -> suppress teleportation
        return null;
    }

    private static boolean canTeleportTo(Wolf wolf, ServerLevel level, BlockPos pos) {
        if (wolf == null || level == null || pos == null || !level.isLoaded(pos)) {
            return false;
        }
        PathType pathType = WalkNodeEvaluator.getPathTypeStatic(wolf, pos);
        if (pathType != PathType.WALKABLE) {
            return false;
        }
        BlockState blockStateBelow = level.getBlockState(pos.below());
        if (blockStateBelow.isAir() || blockStateBelow.getBlock() instanceof LeavesBlock) {
            return false;
        }
        BlockPos delta = pos.subtract(wolf.blockPosition());
        return level.noCollision(wolf, wolf.getBoundingBox().move(delta));
    }
}
