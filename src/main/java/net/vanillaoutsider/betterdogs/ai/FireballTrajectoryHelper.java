// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.util.WolfHazardHelper;

/**
 * Closed-form 3D raycast time-to-closest-approach trajectory math and lateral evasion solver.
 * Provides zero-allocation hot-path interception detection and ground safety verification
 * for the Ballistic Evasion Reflex system (BL-BD-020).
 */
public final class FireballTrajectoryHelper {

    public static final double DEFAULT_THREAT_RADIUS = 1.75;
    public static final double DEFAULT_REACTION_TICKS = 20.0;
    public static final double DEFAULT_LATERAL_IMPULSE = 85.0;
    public static final double VERTICAL_HOP_IMPULSE = 0.28;
    private static final double EPSILON = 1e-6;

    public record TrajectoryInterception(
            boolean intercepted,
            double timeToImpactTicks,
            double perpendicularDistance,
            Vec3 dodgeImpulse
    ) {
        public static final TrajectoryInterception NONE =
                new TrajectoryInterception(false, -1.0, Double.MAX_VALUE, Vec3.ZERO);
    }

    private FireballTrajectoryHelper() {
        // Pure static utility class
    }

    /**
     * Computes whether an incoming projectile will intercept the dog within reactionTicks,
     * passing within threatRadius. If intercepted, computes an outward lateral dodge impulse.
     *
     * @param dogPos                Dog's current position vector
     * @param projPos               Projectile's current position vector
     * @param projVel               Projectile's velocity vector (blocks per tick)
     * @param reactionTicks         Maximum lookahead window in ticks
     * @param threatRadius          Maximum perpendicular collision radius (blocks)
     * @param lateralImpulsePercent Lateral impulse percentage (e.g. 85 = 0.85 horizontal velocity)
     * @return TrajectoryInterception containing result, tau, perpendicular distance, and dodge vector
     */
    public static TrajectoryInterception computeInterception(
            Vec3 dogPos,
            Vec3 projPos,
            Vec3 projVel,
            double reactionTicks,
            double threatRadius,
            double lateralImpulsePercent
    ) {
        if (dogPos == null || projPos == null || projVel == null) {
            return TrajectoryInterception.NONE;
        }

        if (reactionTicks <= 0.0 || threatRadius <= 0.0) {
            return TrajectoryInterception.NONE;
        }

        double px = dogPos.x;
        double py = dogPos.y;
        double pz = dogPos.z;

        double qx = projPos.x;
        double qy = projPos.y;
        double qz = projPos.z;

        double vx = projVel.x;
        double vy = projVel.y;
        double vz = projVel.z;

        // Guard against NaN and infinities
        if (Double.isNaN(px) || Double.isNaN(py) || Double.isNaN(pz) ||
            Double.isNaN(qx) || Double.isNaN(qy) || Double.isNaN(qz) ||
            Double.isNaN(vx) || Double.isNaN(vy) || Double.isNaN(vz) ||
            Double.isNaN(reactionTicks) || Double.isNaN(threatRadius) || Double.isNaN(lateralImpulsePercent)) {
            return TrajectoryInterception.NONE;
        }

        if (Double.isInfinite(px) || Double.isInfinite(py) || Double.isInfinite(pz) ||
            Double.isInfinite(qx) || Double.isInfinite(qy) || Double.isInfinite(qz) ||
            Double.isInfinite(vx) || Double.isInfinite(vy) || Double.isInfinite(vz) ||
            Double.isInfinite(reactionTicks) || Double.isInfinite(threatRadius) || Double.isInfinite(lateralImpulsePercent)) {
            return TrajectoryInterception.NONE;
        }

        // Velocity magnitude squared: |projVel|^2
        double vSq = vx * vx + vy * vy + vz * vz;
        if (vSq < EPSILON) {
            return TrajectoryInterception.NONE; // Stationary or negligible projectile velocity
        }

        // Delta vector: dogPos - projPos
        double dx = px - qx;
        double dy = py - qy;
        double dz = pz - qz;

        // Time to closest approach: tau = (delta . projVel) / |projVel|^2
        double tau = (dx * vx + dy * vy + dz * vz) / vSq;
        if (tau <= 0.0 || tau > reactionTicks) {
            return TrajectoryInterception.NONE; // Diverging, already passed, or too far in the future
        }

        // Closest point on projectile trajectory line: P_closest = projPos + projVel * tau
        double closestX = qx + vx * tau;
        double closestY = qy + vy * tau;
        double closestZ = qz + vz * tau;

        // Perpendicular displacement: dogPos - P_closest
        double perpX = px - closestX;
        double perpY = py - closestY;
        double perpZ = pz - closestZ;

        double distPerpSq = perpX * perpX + perpY * perpY + perpZ * perpZ;
        double threatRadiusSq = threatRadius * threatRadius;
        if (distPerpSq > threatRadiusSq) {
            return TrajectoryInterception.NONE; // Will pass outside threat threshold
        }

        double distPerp = Math.sqrt(distPerpSq);

        // Lateral dodge unit vector: D_cross = projVel x (0, 1, 0) = (-vz, 0, vx)
        double cLenSq = vx * vx + vz * vz;
        double dCrossX;
        double dCrossZ;

        if (cLenSq < EPSILON) {
            // Projectile trajectory is strictly vertical (falling straight down or up)
            dCrossX = 1.0;
            dCrossZ = 0.0;
        } else {
            double invLen = 1.0 / Math.sqrt(cLenSq);
            dCrossX = -vz * invLen;
            dCrossZ = vx * invLen;
        }

        // Choose lateral sign based on which side the dog is currently positioned:
        // sign of (dogPos - P_closest) . D_cross
        double sideDot = perpX * dCrossX + perpZ * dCrossZ;
        double sign = sideDot < 0.0 ? -1.0 : 1.0;

        double latX = dCrossX * sign;
        double latZ = dCrossZ * sign;

        double horizontalScale = lateralImpulsePercent / 100.0;
        Vec3 dodgeImpulse = new Vec3(latX * horizontalScale, VERTICAL_HOP_IMPULSE, latZ * horizontalScale);

        return new TrajectoryInterception(true, tau, distPerp, dodgeImpulse);
    }

    /**
     * Overloaded convenience method with default threat radius (1.75m) and impulse (85%).
     */
    public static TrajectoryInterception computeInterception(
            Vec3 dogPos,
            Vec3 projPos,
            Vec3 projVel,
            double reactionTicks
    ) {
        return computeInterception(dogPos, projPos, projVel, reactionTicks, DEFAULT_THREAT_RADIUS, DEFAULT_LATERAL_IMPULSE);
    }

    /**
     * Checks if a target ground coordinate is safe for a wolf to land/dodge upon.
     * Prevents dodging into fire, lava, magma, campfire, or jumping off high cliff drops (>3 blocks) into the abyss.
     *
     * @param level     World level instance
     * @param targetPos Target block position to evaluate
     * @return true if the ground position is safe and walkable; false otherwise
     */
    public static boolean isGroundSafe(Level level, BlockPos targetPos) {
        if (level == null || targetPos == null) {
            return false;
        }
        if (level.isOutsideBuildHeight(targetPos)) {
            return false;
        }

        BlockState targetState = level.getBlockState(targetPos);
        if (WolfHazardHelper.isThermalHazard(targetState)) {
            return false;
        }

        // Check the block beneath the target position
        BlockPos belowPos = targetPos.below();
        if (level.isOutsideBuildHeight(belowPos)) {
            return false;
        }
        BlockState belowState = level.getBlockState(belowPos);
        if (WolfHazardHelper.isThermalHazard(belowState)) {
            return false;
        }

        // If block beneath is empty/air, probe downward up to 3 blocks for solid ground
        if (level.isEmptyBlock(belowPos)) {
            boolean hasGround = false;
            for (int dy = 2; dy <= 4; dy++) {
                BlockPos dropPos = targetPos.below(dy);
                if (level.isOutsideBuildHeight(dropPos)) {
                    return false;
                }
                BlockState dropState = level.getBlockState(dropPos);
                if (WolfHazardHelper.isThermalHazard(dropState)) {
                    return false;
                }
                if (!level.isEmptyBlock(dropPos)) {
                    if (dy <= 3) {
                        hasGround = true;
                    }
                    break;
                }
            }
            if (!hasGround) {
                return false; // Cliff drop > 3 blocks or abyss
            }
        }

        return true;
    }
}
