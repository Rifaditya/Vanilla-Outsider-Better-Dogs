// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
        if (Double.isNaN(tau) || Double.isInfinite(tau) || tau < minLookaheadTicks || tau > maxLookaheadTicks) {
            return InterceptPrediction.NONE; // Moving away, past closest point, or outside reaction window
        }

        // Closest point on projectile trajectory line: P_closest = P_proj + V * tau
        double closestX = px + vx * tau;
        double closestY = py + vy * tau;
        double closestZ = pz + vz * tau;
        if (Double.isNaN(closestX) || Double.isNaN(closestY) || Double.isNaN(closestZ) ||
            Double.isInfinite(closestX) || Double.isInfinite(closestY) || Double.isInfinite(closestZ)) {
            return InterceptPrediction.NONE;
        }

        // Displacement from owner at closest approach
        double relX = closestX - ox;
        double relY = closestY - oy;
        double relZ = closestZ - oz;

        double horizDistSq = relX * relX + relZ * relZ;
        double radiusSq = ownerCollisionRadius * ownerCollisionRadius;
        double perpDist = Math.sqrt(relX * relX + relY * relY + relZ * relZ);
        if (Double.isNaN(perpDist) || Double.isInfinite(perpDist)) {
            perpDist = Double.MAX_VALUE;
        }

        // Cylinder check: horizontal radius <= ownerCollisionRadius and delta y in [-0.5, 2.0]
        boolean inCylinder = horizDistSq <= radiusSq && relY >= OWNER_CYLINDER_MIN_Y && relY <= OWNER_CYLINDER_MAX_Y;
        if (!inCylinder) {
            return new InterceptPrediction(false, tau, perpDist, Vec3.ZERO);
        }

        // Intercept coordinate: P_proj + max(0.0, tau - 1.0) * V
        double interceptTime = Math.max(0.0, tau - 1.0);
        double ix = px + vx * interceptTime;
        double iy = py + vy * interceptTime;
        double iz = pz + vz * interceptTime;
        if (Double.isNaN(ix) || Double.isNaN(iy) || Double.isNaN(iz) ||
            Double.isInfinite(ix) || Double.isInfinite(iy) || Double.isInfinite(iz)) {
            return InterceptPrediction.NONE;
        }
        Vec3 interceptPos = new Vec3(ix, iy, iz);

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

    private static final Map<UUID, Long> RECENT_INTERCEPTS = new ConcurrentHashMap<>();
    public static final long INTERCEPT_WINDOW_TICKS = 30L;

    /**
     * Marks the given wolf as actively intercepting a projectile for the next window of ticks.
     */
    public static void recordIntercept(Wolf wolf) {
        if (wolf == null) return;
        long currentTick = getTick(wolf);
        UUID id = getWolfId(wolf);
        RECENT_INTERCEPTS.put(id, currentTick + INTERCEPT_WINDOW_TICKS);
    }

    /**
     * Evaluates if the wolf has recently initiated or executed a ballistic interception leap.
     */
    public static boolean isRecentlyIntercepting(Wolf wolf) {
        if (wolf == null) return false;
        UUID id = getWolfId(wolf);
        Long expiry = RECENT_INTERCEPTS.get(id);
        if (expiry == null) return false;
        long currentTick = getTick(wolf);
        if (currentTick <= expiry) {
            return true;
        }
        RECENT_INTERCEPTS.remove(id);
        return false;
    }

    /**
     * Clears intercept tracking for the wolf (e.g. on death or state reset).
     */
    public static void clearIntercept(Wolf wolf) {
        if (wolf == null) return;
        RECENT_INTERCEPTS.remove(getWolfId(wolf));
    }

    private static UUID getWolfId(Wolf wolf) {
        try {
            UUID id = wolf.getUUID();
            if (id != null) return id;
        } catch (Throwable ignored) {}
        return new UUID(0L, System.identityHashCode(wolf));
    }

    private static long getTick(Wolf wolf) {
        try {
            if (wolf.level() != null) {
                return wolf.level().getGameTime();
            }
        } catch (Throwable ignored) {}
        return wolf.tickCount;
    }

    /**
     * Pure testable logic determining whether anti-one-shot protection should mitigate damage.
     *
     * @param isDirectProjectile Whether the damage was dealt by a direct projectile
     * @param isIntercepting     Whether the wolf is in an active intercept window
     * @param currentHealth      Current health of the wolf
     * @param incomingDamage     Damage amount being dealt
     * @return true if damage qualifies for anti-one-shot protection and clamping to 1.0f
     */
    public static boolean shouldMitigateInterceptDamage(
            boolean isDirectProjectile,
            boolean isIntercepting,
            float currentHealth,
            float incomingDamage
    ) {
        if (!isDirectProjectile || !isIntercepting) {
            return false;
        }
        // Anti-One-Shot Protection: If amount >= wolf.getHealth() and wolf health >= 3.0f:
        // Clamp health to 1.0f and cancel killing blow / absorb lethal excess.
        return incomingDamage >= currentHealth && currentHealth >= 3.0f;
    }

    /**
     * Computes the post-mitigation health. If anti-one-shot is triggered, clamped to 1.0f.
     */
    public static float calculateMitigatedHealth(float currentHealth, float incomingDamage, boolean qualifies) {
        if (qualifies) {
            return 1.0f;
        }
        return Math.max(0.0f, currentHealth - incomingDamage);
    }

    /**
     * Handles damage routing, anti-one-shot protection, armor absorption, and sensory polish
     * when a wolf intercepts a projectile targeting its owner (BL-BD-021).
     *
     * @param wolf   Target wolf taking damage
     * @param source DamageSource being applied
     * @param amount Damage amount
     * @return true if damage was mitigated / absorbed, cancelling killing blow; false otherwise
     */
    public static boolean handleInterceptDamage(Wolf wolf, DamageSource source, float amount) {
        if (wolf == null || source == null) {
            return false;
        }

        boolean isDirectProjectile = source.getDirectEntity() instanceof Projectile;
        boolean isIntercepting = isRecentlyIntercepting(wolf);
        float currentHealth = wolf.getHealth();

        if (shouldMitigateInterceptDamage(isDirectProjectile, isIntercepting, currentHealth, amount)) {
            wolf.setHealth(1.0f);
            applyInvulnerability(wolf, 20);
            applyArmorAbsorption(wolf, amount);
            triggerSensoryPolish(wolf);
            return true;
        }

        return false;
    }

    private static void applyInvulnerability(Wolf wolf, int ticks) {
        wolf.invulnerableTime = ticks;
    }

    private static void applyArmorAbsorption(Wolf wolf, float amount) {
        try {
            ItemStack armor = wolf.getBodyArmorItem();
            if (armor != null && !armor.isEmpty()) {
                armor.hurtAndBreak((int) Math.max(1.0f, amount), wolf, EquipmentSlot.BODY);
            }
        } catch (Throwable ignored) {
            // Gracefully ignore in headless or mock test environments
        }
    }

    private static void triggerSensoryPolish(Wolf wolf) {
        try {
            if (wolf.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CRIT, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 10, 0.2, 0.2, 0.2, 0.1);
                serverLevel.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.SHIELD_BLOCK.value(), wolf.getSoundSource(), 1.0F, 1.0F);
            }
        } catch (Throwable ignored) {
            // Gracefully ignore in headless or mock test environments
        }
    }
}
