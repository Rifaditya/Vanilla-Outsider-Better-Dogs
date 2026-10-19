// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.ai.play;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfHazardHelper;
import org.jspecify.annotations.Nullable;

/**
 * Pure evaluation and mathematical choreography helper for playful dog behaviors.
 * Handles play readiness validation, pack partner discovery, cliff/thermal hazard
 * safety checks, and circular orbit vector calculations.
 */
public class PlayChoreographyHelper {

    public static final double DEFAULT_ORBIT_RADIUS = 3.0;
    public static final double MAX_ORBIT_RADIUS = 1024.0;
    public static final double MAX_COORDINATE_BOUND = 3.0E7;
    public static final int MAX_SAFE_DROP_BLOCKS = 3;

    /**
     * Checks if a wolf is currently eligible to initiate play choreography.
     */
    public static boolean canInitiatePlay(@Nullable Wolf wolf) {
        if (wolf == null || !wolf.isAlive() || !wolf.isTame()) {
            return false;
        }
        if (wolf.isOrderedToSit() || wolf.isInSittingPose() || wolf.getTarget() != null) {
            return false;
        }
        float maxHealth = wolf.getMaxHealth();
        if (maxHealth <= 0.0f || Float.isNaN(maxHealth) || wolf.getHealth() < 0.7f * maxHealth) {
            return false;
        }
        boolean isGuardMode = false;
        boolean isSocialModeActive = false;
        if (wolf instanceof WolfExtensions ext) {
            isGuardMode = ext.betterdogs$isGuardMode();
            isSocialModeActive = ext.betterdogs$isSocialModeActive();
        }
        if (isGuardMode || isSocialModeActive) {
            return false;
        }
        Level level = wolf.level();
        if (level != null && level.isClientSide()) {
            return false;
        }
        if (!BetterDogsGameRules.isDogPlayChoreographyEnabled(level)) {
            return false;
        }
        LivingEntity owner = wolf.getOwner();
        if (owner == null || !owner.isAlive() || owner.isSpectator()) {
            return false;
        }
        int maxDist = BetterDogsGameRules.getMaxPlayDistance(level);
        double maxAllowedDistSqr = Math.min(1024.0, (double) maxDist * maxDist * 4.0);
        return wolf.distanceToSqr(owner) <= maxAllowedDistSqr;
    }

    /**
     * Pure logical predicate for testing play eligibility matrix.
     */
    public static boolean canInitiatePlay(boolean isAlive, boolean isTame, boolean isOrderedToSit,
                                          boolean isInSittingPose, boolean hasTarget, float health,
                                          float maxHealth, boolean isGuardMode, boolean isSocialModeActive,
                                          boolean isRuleEnabled, boolean hasOwner, boolean isOwnerAlive,
                                          boolean isOwnerSpectator, double distanceToOwnerSqr,
                                          double maxPlayDistance) {
        if (!isAlive || !isTame || isOrderedToSit || isInSittingPose || hasTarget) {
            return false;
        }
        if (maxHealth <= 0.0f || Float.isNaN(maxHealth) || health < 0.7f * maxHealth) {
            return false;
        }
        if (isGuardMode || isSocialModeActive || !isRuleEnabled) {
            return false;
        }
        if (!hasOwner || !isOwnerAlive || isOwnerSpectator) {
            return false;
        }
        double maxAllowedDistSqr = Math.min(1024.0, maxPlayDistance * maxPlayDistance * 4.0);
        return distanceToOwnerSqr <= maxAllowedDistSqr;
    }

    /**
     * Searches for the closest valid tamed packmate dog eligible for play within the specified radius.
     */
    @Nullable
    public static Wolf findPlayPartner(@Nullable Wolf wolf, double radius) {
        if (wolf == null || wolf.level() == null || radius <= 0.0) {
            return null;
        }
        Level level = wolf.level();
        if (level.isClientSide()) {
            return null;
        }
        List<Wolf> candidates = level.getEntitiesOfClass(
                Wolf.class,
                wolf.getBoundingBox().inflate(radius),
                candidate -> isValidPlayPartner(wolf, candidate)
        );
        if (candidates.isEmpty()) {
            return null;
        }
        Wolf closest = null;
        double closestDistSqr = Double.MAX_VALUE;
        for (Wolf candidate : candidates) {
            double distSqr = wolf.distanceToSqr(candidate);
            if (distSqr < closestDistSqr) {
                closestDistSqr = distSqr;
                closest = candidate;
            }
        }
        return closest;
    }

    /**
     * Resolves the owner UUID of a wolf, checking its owner reference or owner entity.
     */
    @Nullable
    public static UUID getOwnerUUID(@Nullable Wolf wolf) {
        if (wolf == null) {
            return null;
        }
        if (wolf.getOwnerReference() != null) {
            return wolf.getOwnerReference().getUUID();
        }
        LivingEntity owner = wolf.getOwner();
        return owner != null ? owner.getUUID() : null;
    }

    /**
     * Validates whether candidate is an eligible play partner for wolf.
     */
    public static boolean isValidPlayPartner(@Nullable Wolf wolf, @Nullable Wolf candidate) {
        if (wolf == null || candidate == null || wolf == candidate) {
            return false;
        }
        if (!candidate.isAlive() || !candidate.isTame()) {
            return false;
        }
        UUID candidateOwnerUuid = getOwnerUUID(candidate);
        if (candidateOwnerUuid == null) {
            return false;
        }
        UUID wolfOwnerUuid = getOwnerUUID(wolf);
        boolean matchingOwner = (wolfOwnerUuid != null && wolfOwnerUuid.equals(candidateOwnerUuid))
                || (wolf.getOwner() != null && wolf.getOwner() == candidate.getOwner());
        if (!matchingOwner) {
            return false;
        }
        if (candidate.isOrderedToSit() || candidate.isInSittingPose()) {
            return false;
        }
        if (candidate.getTarget() != null) {
            return false;
        }
        if (candidate instanceof WolfExtensions ext) {
            if (ext.betterdogs$isGuardMode()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Pure logical predicate for testing play partner validation matrix.
     */
    public static boolean isValidPlayPartner(boolean isSelf, boolean isAlive, boolean isTame,
                                            boolean hasMatchingOwner, boolean isOrderedToSit,
                                            boolean isInSittingPose, boolean hasTarget,
                                            boolean isGuardMode) {
        if (isSelf || !isAlive || !isTame || !hasMatchingOwner) {
            return false;
        }
        if (isOrderedToSit || isInSittingPose || hasTarget || isGuardMode) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the path to targetPos is safe from thermal hazards and cliff drops.
     * Requires target block to be non-hazardous, have a solid floor below within 3 blocks,
     * and have a drop height <= 3 blocks from the wolf's vertical position.
     */
    public static boolean isSafePath(@Nullable Wolf wolf, @Nullable Vec3 targetPos) {
        if (wolf == null || targetPos == null) {
            return false;
        }
        Level level = wolf.level();
        if (level == null) {
            return false;
        }
        if (Double.isNaN(targetPos.x) || Double.isNaN(targetPos.y) || Double.isNaN(targetPos.z)
                || Double.isInfinite(targetPos.x) || Double.isInfinite(targetPos.y) || Double.isInfinite(targetPos.z)) {
            return false;
        }

        int tx = Mth.floor(targetPos.x);
        int ty = Mth.floor(targetPos.y);
        int tz = Mth.floor(targetPos.z);
        BlockPos targetBlock = new BlockPos(tx, ty, tz);

        // Target block and block above cannot be thermal hazards
        if (WolfHazardHelper.isThermalHazard(level.getBlockState(targetBlock))
                || WolfHazardHelper.isThermalHazard(level.getBlockState(targetBlock.above()))) {
            return false;
        }

        // Must find solid floor below within 3 blocks
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();
        boolean foundSolidFloor = false;
        int floorY = Integer.MIN_VALUE;

        for (int dy = 0; dy <= MAX_SAFE_DROP_BLOCKS; dy++) {
            scanPos.set(tx, ty - dy, tz);
            BlockState state = level.getBlockState(scanPos);
            if (WolfHazardHelper.isThermalHazard(state)) {
                return false;
            }
            if (!level.isEmptyBlock(scanPos) && state.isSolidRender()) {
                foundSolidFloor = true;
                floorY = ty - dy;
                break;
            }
        }

        if (!foundSolidFloor) {
            return false;
        }

        int wolfY = Mth.floor(wolf.getY());
        int landingY = floorY + 1;
        int dropDistance = wolfY - landingY;

        return isSafeDrop(dropDistance);
    }

    /**
     * Evaluates drop height safety. Drops <= 3 blocks are considered safe; drops > 3 blocks are cliff hazards.
     */
    public static boolean isSafeDrop(int dropHeight) {
        return dropHeight <= MAX_SAFE_DROP_BLOCKS;
    }

    /**
     * Calculates the next position along a circular orbit around centerPos in the XZ plane.
     * Enforces saturated math bounds and coordinate safety.
     */
    public static Vec3 calculateOrbitVector(@Nullable Vec3 center, @Nullable Vec3 currentPos,
                                            double angularStepRadians, double radius) {
        if (center == null && currentPos == null) {
            return Vec3.ZERO;
        }
        if (center == null) {
            return currentPos;
        }
        if (currentPos == null) {
            return center;
        }

        double cx = sanitizeCoordinate(center.x);
        double cz = sanitizeCoordinate(center.z);

        double px = sanitizeCoordinate(currentPos.x);
        double py = sanitizeCoordinate(currentPos.y);
        double pz = sanitizeCoordinate(currentPos.z);

        double safeRadius = sanitizeRadius(radius);
        double safeStep = sanitizeAngle(angularStepRadians);

        double dx = px - cx;
        double dz = pz - cz;

        double currentAngle;
        if (Math.abs(dx) < 1e-7 && Math.abs(dz) < 1e-7) {
            currentAngle = 0.0;
        } else {
            currentAngle = Math.atan2(dz, dx);
        }

        double targetAngle = currentAngle + safeStep;
        double targetX = cx + safeRadius * Math.cos(targetAngle);
        double targetZ = cz + safeRadius * Math.sin(targetAngle);

        targetX = Math.max(-MAX_COORDINATE_BOUND, Math.min(MAX_COORDINATE_BOUND, targetX));
        targetZ = Math.max(-MAX_COORDINATE_BOUND, Math.min(MAX_COORDINATE_BOUND, targetZ));

        return new Vec3(targetX, py, targetZ);
    }

    /**
     * Wolf overload: calculates orbit vector for wolf around partner.
     */
    public static Vec3 calculateOrbitVector(@Nullable Wolf wolf, @Nullable Wolf partner,
                                            double angularStepRadians, double radius) {
        if (wolf == null && partner == null) {
            return Vec3.ZERO;
        }
        if (partner == null) {
            return wolf.position();
        }
        if (wolf == null) {
            return partner.position();
        }
        return calculateOrbitVector(partner.position(), wolf.position(), angularStepRadians, radius);
    }

    public static double sanitizeCoordinate(double val) {
        if (Double.isNaN(val) || Double.isInfinite(val)) {
            return 0.0;
        }
        return Math.max(-MAX_COORDINATE_BOUND, Math.min(MAX_COORDINATE_BOUND, val));
    }

    public static double sanitizeRadius(double radius) {
        if (Double.isNaN(radius) || Double.isInfinite(radius)) {
            return DEFAULT_ORBIT_RADIUS;
        }
        return Math.max(0.0, Math.min(MAX_ORBIT_RADIUS, Math.abs(radius)));
    }

    public static double sanitizeAngle(double angleRadians) {
        if (Double.isNaN(angleRadians) || Double.isInfinite(angleRadians)) {
            return 0.0;
        }
        return angleRadians;
    }
}
