// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.WitherRoseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.jspecify.annotations.Nullable;

/**
 * Single-purpose helper for terrain-aware wolf pack reinforcement spawning during natural density boosts.
 */
public final class WolfReinforcementSpawnHelper {

    public static final int HORIZONTAL_SCATTER_RADIUS = 8;
    public static final int MAX_SPAWN_ATTEMPTS = 4;
    public static final int MAX_VERTICAL_DELTA = 12;
    public static final int MAX_REINFORCEMENT_COUNT = 64;
    private static final int LOCAL_FLOOR_SCAN_RADIUS = 4;
    private static final double BOUNDING_BOX_INFLATE_XZ = 0.20D;

    private WolfReinforcementSpawnHelper() {
    }

    /**
     * Evaluates whether a wolf spawn qualifies to roll for natural pack reinforcement density boosting.
     */
    public static boolean shouldTriggerDensityBoost(
            boolean isNaturalSpawn,
            boolean hasExistingGroupData,
            int boostChance,
            int roll) {
        return isNaturalSpawn && !hasExistingGroupData && boostChance > 0 && roll < boostChance;
    }

    /**
     * Calculates the saturated reinforcement wolf count from the configured pack cluster size.
     */
    public static int calculateReinforcementCount(int clusterSize) {
        if (clusterSize <= 0) {
            return 0;
        }
        return Math.min(MAX_REINFORCEMENT_COUNT, clusterSize / 2 + 1);
    }

    /**
     * Verifies whether a candidate surface Y coordinate stays within the vertical tolerance of the primary wolf.
     */
    public static boolean isWithinVerticalTolerance(int originY, int candidateY, int maxDelta) {
        if (maxDelta < 0) {
            return false;
        }
        long delta = Math.abs((long) candidateY - (long) originY);
        return delta <= maxDelta;
    }

    /**
     * Pure predicate checking floor sturdiness, empty foot/head clearance, leaf exclusion, fluids, and block hazards.
     */
    public static boolean isSafeSpawnBlockStates(
            @Nullable BlockState belowState,
            @Nullable BlockState footState,
            @Nullable BlockState headState,
            boolean belowSturdy,
            boolean footCollisionEmpty,
            boolean headCollisionEmpty) {
        if (belowState == null || footState == null || headState == null) {
            return false;
        }
        if (!belowSturdy || !footCollisionEmpty || !headCollisionEmpty) {
            return false;
        }
        if (belowState.getBlock() instanceof LeavesBlock
                || footState.getBlock() instanceof LeavesBlock
                || headState.getBlock() instanceof LeavesBlock) {
            return false;
        }
        if (!belowState.getFluidState().isEmpty()
                || !footState.getFluidState().isEmpty()
                || !headState.getFluidState().isEmpty()) {
            return false;
        }
        return !isHazardBlockState(belowState)
                && !isHazardBlockState(footState)
                && !isHazardBlockState(headState);
    }

    private static boolean isHazardBlockState(BlockState state) {
        Block block = state.getBlock();
        return block instanceof MagmaBlock
                || block instanceof CampfireBlock
                || block instanceof CactusBlock
                || block instanceof FireBlock
                || block instanceof BaseFireBlock
                || block instanceof WitherRoseBlock
                || block instanceof SweetBerryBushBlock
                || block instanceof PowderSnowBlock
                || block instanceof WebBlock;
    }

    /**
     * Attempts to trigger a density-boost reinforcement pack around a naturally spawned primary wolf.
     *
     * @return the number of reinforcement wolves successfully spawned
     */
    public static int tryTriggerDensityBoost(
            @Nullable Wolf primaryWolf,
            @Nullable ServerLevelAccessor level,
            @Nullable EntitySpawnReason spawnReason,
            @Nullable SpawnGroupData groupData) {
        if (primaryWolf == null || level == null) {
            return 0;
        }
        ServerLevel serverLevel = level.getLevel();
        if (serverLevel == null || serverLevel.isClientSide()) {
            return 0;
        }
        boolean isNaturalSpawn = spawnReason == EntitySpawnReason.NATURAL;
        boolean hasExistingGroupData = groupData != null;
        if (!isNaturalSpawn || hasExistingGroupData) {
            return 0;
        }

        int boostChance = BetterDogsGameRules.getInt(serverLevel, BetterDogsGameRules.BD_WOLF_SPAWN_DENSITY_BOOST, 0);
        RandomSource random = primaryWolf.getRandom();
        int roll = random.nextInt(100);
        WolfDebugLogger.log(primaryWolf, "Spawn", "Density Boost Evaluation - Roll: " + roll + " / Target: " + boostChance);
        if (!shouldTriggerDensityBoost(isNaturalSpawn, hasExistingGroupData, boostChance, roll)) {
            return 0;
        }

        int clusterSize = BetterDogsGameRules.getInt(serverLevel, BetterDogsGameRules.BD_WOLF_PACK_CLUSTER_SIZE, 8);
        int count = calculateReinforcementCount(clusterSize);
        if (count <= 0) {
            return 0;
        }

        BlockPos origin = primaryWolf.blockPosition();
        int spawnedCount = 0;
        for (int i = 0; i < count; i++) {
            BlockPos spawnPos = findScatteredReinforcementSpawnPos(primaryWolf, level, origin, random);
            if (spawnPos != null && spawnSingleReinforcement(serverLevel, level, spawnPos, random)) {
                spawnedCount++;
            }
        }
        WolfDebugLogger.log(primaryWolf, "Spawn",
                "Density Boost: Reinforcement pack spawned " + spawnedCount + "/" + count + " around " + origin);
        return spawnedCount;
    }

    @Nullable
    private static BlockPos findScatteredReinforcementSpawnPos(
            Wolf primaryWolf,
            ServerLevelAccessor level,
            BlockPos origin,
            RandomSource random) {
        int span = HORIZONTAL_SCATTER_RADIUS * 2 + 1;
        for (int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS; attempt++) {
            int dx = random.nextInt(span) - HORIZONTAL_SCATTER_RADIUS;
            int dz = random.nextInt(span) - HORIZONTAL_SCATTER_RADIUS;
            if (dx == 0 && dz == 0) {
                continue;
            }
            BlockPos columnPos = origin.offset(dx, 0, dz);
            BlockPos safePos = findSafeReinforcementSpawnPos(primaryWolf, level, origin, columnPos);
            if (safePos != null) {
                return safePos;
            }
        }
        return null;
    }

    /**
     * Resolves a safe, non-suffocating spawn position in {@code columnPos} without loading unloaded chunks.
     */
    @Nullable
    public static BlockPos findSafeReinforcementSpawnPos(
            @Nullable Wolf primaryWolf,
            @Nullable ServerLevelAccessor level,
            @Nullable BlockPos origin,
            @Nullable BlockPos columnPos) {
        if (primaryWolf == null || level == null || origin == null || columnPos == null) {
            return null;
        }
        if (!level.hasChunkAt(columnPos)) {
            return null;
        }

        BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, columnPos);
        if (isWithinVerticalTolerance(origin.getY(), surface.getY(), MAX_VERTICAL_DELTA)
                && isSafeSpawnBlockPos(primaryWolf, level, surface)) {
            return surface;
        }

        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();
        int minY = level.getMinY() + 1;
        int maxY = level.getMaxY() - 2;
        for (int dy = LOCAL_FLOOR_SCAN_RADIUS; dy >= -LOCAL_FLOOR_SCAN_RADIUS; dy--) {
            int y = origin.getY() + dy;
            if (y < minY || y > maxY) {
                continue;
            }
            candidate.set(columnPos.getX(), y, columnPos.getZ());
            if (isSafeSpawnBlockPos(primaryWolf, level, candidate)) {
                return candidate.immutable();
            }
        }
        return null;
    }

    /**
     * Validates chunk presence, build bounds, foot/head/floor states, and entity AABB clearance at {@code pos}.
     */
    public static boolean isSafeSpawnBlockPos(
            @Nullable Wolf primaryWolf,
            @Nullable ServerLevelAccessor level,
            @Nullable BlockPos pos) {
        if (primaryWolf == null || level == null || pos == null) {
            return false;
        }
        if (pos.getY() <= level.getMinY() || pos.getY() > level.getMaxY() - 2) {
            return false;
        }
        if (!level.hasChunkAt(pos)) {
            return false;
        }

        BlockPos belowPos = pos.below();
        BlockPos headPos = pos.above();
        BlockState belowState = level.getBlockState(belowPos);
        BlockState footState = level.getBlockState(pos);
        BlockState headState = level.getBlockState(headPos);

        boolean belowSturdy = belowState.isFaceSturdy(level, belowPos, Direction.UP);
        boolean footCollisionEmpty = footState.getCollisionShape(level, pos).isEmpty();
        boolean headCollisionEmpty = headState.getCollisionShape(level, headPos).isEmpty();

        if (!isSafeSpawnBlockStates(belowState, footState, headState, belowSturdy, footCollisionEmpty, headCollisionEmpty)) {
            return false;
        }

        AABB clearanceBox = primaryWolf.getType()
                .getDimensions()
                .makeBoundingBox(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D)
                .inflate(BOUNDING_BOX_INFLATE_XZ, 0.0D, BOUNDING_BOX_INFLATE_XZ);
        return level.noCollision(primaryWolf, clearanceBox);
    }

    private static boolean spawnSingleReinforcement(
            ServerLevel serverLevel,
            ServerLevelAccessor level,
            BlockPos spawnPos,
            RandomSource random) {
        Wolf reinforcement = EntityTypes.WOLF.create(serverLevel, EntitySpawnReason.REINFORCEMENT);
        if (reinforcement == null) {
            return false;
        }
        reinforcement.snapTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                random.nextFloat() * 360.0F, 0.0F);
        reinforcement.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.REINFORCEMENT, null);
        level.addFreshEntityWithPassengers(reinforcement);
        return true;
    }
}
