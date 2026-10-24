// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.phys.Vec3;

/**
 * Closed-form 3D raycast time-to-closest-approach trajectory math and owner ballistic defense solver.
 * Provides zero-allocation hot-path interception detection, cylinder collision validation,
 * and intercept waypoint computation for the Ballistic Interceptor system (BL-BD-021).
 */
public final class WolfProjectileInterceptHelper {

    public static final double DEFAULT_LOOKAHEAD_TICKS = 24.0;
    public static final double MIN_LOOKAHEAD_TICKS = 3.0;
    public static final double DEFAULT_OWNER_COLLISION_RADIUS = 1.25;
    public static final double OWNER_CYLINDER_MIN_Y = -0.5;
    public static final double OWNER_CYLINDER_MAX_Y = 2.0;
    private static final double EPSILON = 1e-6;

    public record InterceptPrediction(
            boolean willHitOwner,
            double timeToImpactTicks,
            double perpendicularDistance,
            Vec3 interceptPosition
    ) {
        public static final InterceptPrediction NONE =
                new InterceptPrediction(false, -1.0, Double.MAX_VALUE, Vec3.ZERO);
    }

    private WolfProjectileInterceptHelper() {
        // Pure static utility class
    }

    /**
     * Closed-form TCA 3D raycast solver calculating if an incoming projectile will strike the owner's
     * bounding cylinder within the given lookahead window, and computing the optimal interception coordinates.
     *
     * @param ownerPos              Target owner's position vector (typically feet coordinate)
     * @param projPos               Projectile's current position vector
     * @param projVel               Projectile's velocity vector (blocks per tick)
     * @param minLookaheadTicks     Minimum lookahead ticks required for interception reaction
     * @param maxLookaheadTicks     Maximum lookahead ticks window
     * @param ownerCollisionRadius  Horizontal collision cylinder radius around owner (default 1.25m)
     * @return InterceptPrediction containing collision prediction, tau, perpendicular distance, and intercept position
     */
    public static InterceptPrediction computeOwnerIntercept(
            Vec3 ownerPos,
            Vec3 projPos,
            Vec3 projVel,
            double minLookaheadTicks,
            double maxLookaheadTicks,
            double ownerCollisionRadius
    ) {
        if (ownerPos == null || projPos == null || projVel == null) {
            return InterceptPrediction.NONE;
        }

        double ox = ownerPos.x;
        double oy = ownerPos.y;
        double oz = ownerPos.z;

        double px = projPos.x;
        double py = projPos.y;
        double pz = projPos.z;

        double vx = projVel.x;
        double vy = projVel.y;
        double vz = projVel.z;

        // Guard against NaN values
        if (Double.isNaN(ox) || Double.isNaN(oy) || Double.isNaN(oz) ||
            Double.isNaN(px) || Double.isNaN(py) || Double.isNaN(pz) ||
            Double.isNaN(vx) || Double.isNaN(vy) || Double.isNaN(vz) ||
            Double.isNaN(minLookaheadTicks) || Double.isNaN(maxLookaheadTicks) || Double.isNaN(ownerCollisionRadius)) {
            return InterceptPrediction.NONE;
        }

        // Guard against Infinite values
        if (Double.isInfinite(ox) || Double.isInfinite(oy) || Double.isInfinite(oz) ||
            Double.isInfinite(px) || Double.isInfinite(py) || Double.isInfinite(pz) ||
            Double.isInfinite(vx) || Double.isInfinite(vy) || Double.isInfinite(vz) ||
            Double.isInfinite(minLookaheadTicks) || Double.isInfinite(maxLookaheadTicks) || Double.isInfinite(ownerCollisionRadius)) {
            return InterceptPrediction.NONE;
        }

        if (maxLookaheadTicks <= 0.0 || ownerCollisionRadius <= 0.0 || minLookaheadTicks < 0.0 || minLookaheadTicks > maxLookaheadTicks) {
            return InterceptPrediction.NONE;
        }

        // Velocity magnitude squared: ||V||^2
        double vSq = vx * vx + vy * vy + vz * vz;
        if (vSq < EPSILON) {
            return InterceptPrediction.NONE; // Stationary or subnormal projectile velocity
        }

        // Delta vector: P_owner - P_proj
        double dx = ox - px;
        double dy = oy - py;
        double dz = oz - pz;

        // Time of closest approach: tau = (delta . V) / ||V||^2
        double tau = (dx * vx + dy * vy + dz * vz) / vSq;
        if (tau < minLookaheadTicks || tau > maxLookaheadTicks) {
            return InterceptPrediction.NONE; // Moving away, past closest point, or outside reaction window
        }

        // Closest point on projectile trajectory line: P_closest = P_proj + V * tau
        double closestX = px + vx * tau;
        double closestY = py + vy * tau;
        double closestZ = pz + vz * tau;

        // Displacement from owner at closest approach
        double relX = closestX - ox;
        double relY = closestY - oy;
        double relZ = closestZ - oz;

        double horizDistSq = relX * relX + relZ * relZ;
        double radiusSq = ownerCollisionRadius * ownerCollisionRadius;
        double perpDist = Math.sqrt(relX * relX + relY * relY + relZ * relZ);

        // Cylinder check: horizontal radius <= ownerCollisionRadius and delta y in [-0.5, 2.0]
        boolean inCylinder = horizDistSq <= radiusSq && relY >= OWNER_CYLINDER_MIN_Y && relY <= OWNER_CYLINDER_MAX_Y;
        if (!inCylinder) {
            return new InterceptPrediction(false, tau, perpDist, Vec3.ZERO);
        }

        // Intercept coordinate: P_proj + max(0.0, tau - 1.0) * V
        double interceptTime = Math.max(0.0, tau - 1.0);
        Vec3 interceptPos = new Vec3(
                px + vx * interceptTime,
                py + vy * interceptTime,
                pz + vz * interceptTime
        );

        return new InterceptPrediction(true, tau, perpDist, interceptPos);
    }

    /**
     * Closed-form TCA 3D raycast solver with standard minimum lookahead window (3.0 ticks).
     */
    public static InterceptPrediction computeOwnerIntercept(
            Vec3 ownerPos,
            Vec3 projPos,
            Vec3 projVel,
            double lookaheadTicks,
            double ownerCollisionRadius
    ) {
        return computeOwnerIntercept(ownerPos, projPos, projVel, MIN_LOOKAHEAD_TICKS, lookaheadTicks, ownerCollisionRadius);
    }

    /**
     * Convenience solver with default lookahead (24 ticks) and default owner radius (1.25m).
     */
    public static InterceptPrediction computeOwnerIntercept(
            Vec3 ownerPos,
            Vec3 projPos,
            Vec3 projVel
    ) {
        return computeOwnerIntercept(ownerPos, projPos, projVel, MIN_LOOKAHEAD_TICKS, DEFAULT_LOOKAHEAD_TICKS, DEFAULT_OWNER_COLLISION_RADIUS);
    }

    /**
     * Resolves the wolf's owner and evaluates projectile threat against the owner.
     */
    public static InterceptPrediction predictOwnerThreat(
            Wolf wolf,
            Vec3 projPos,
            Vec3 projVel,
            double lookaheadTicks
    ) {
        if (wolf == null) {
            return InterceptPrediction.NONE;
        }
        LivingEntity owner = wolf.getOwner();
        if (owner == null) {
            return InterceptPrediction.NONE;
        }
        return computeOwnerIntercept(owner.position(), projPos, projVel, lookaheadTicks, DEFAULT_OWNER_COLLISION_RADIUS);
    }
}
