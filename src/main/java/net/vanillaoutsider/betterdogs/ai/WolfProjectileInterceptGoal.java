// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfDispositionHelper;

import java.util.EnumSet;
import java.util.List;

/**
 * Priority 1 canine bodyguard AI goal for intercepting incoming hostile ballistic projectiles
 * aimed at low-health owners (BL-BD-021).
 */
public class WolfProjectileInterceptGoal extends Goal {

    public static final double SCAN_RADIUS = 14.0;
    public static final double EMERGENCY_UNSEAT_MAX_DISTANCE = 2.5;
    public static final double LEAP_DISTANCE_THRESHOLD = 3.0;
    public static final double LEAP_TIME_THRESHOLD_TICKS = 6.0;
    public static final double SPRINT_SPEED_MODIFIER = 1.6;
    public static final double LEAP_HORIZONTAL_SCALE = 0.45;
    public static final double LEAP_VERTICAL_IMPULSE = 0.35;
    public static final int COOLDOWN_TICKS_AFTER_STOP = 10;
    public static final int MAX_ACTIVE_TICKS = 40;

    private final Wolf wolf;
    private Projectile targetProjectile;
    private Vec3 cachedInterceptPos = Vec3.ZERO;
    private boolean wasSitting = false;
    private boolean hasLeaped = false;
    private int cooldownTicks = 0;
    private int activeTicks = 0;

    public WolfProjectileInterceptGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    /**
     * Predicate evaluating whether an entity is an incoming hostile damage-dealing projectile.
     */
    public static boolean isHostileProjectile(Entity entity, LivingEntity owner) {
        if (!(entity instanceof Projectile proj) || !proj.isAlive() || proj.onGround()) {
            return false;
        }
        if (!isHostileShooter(proj.getOwner(), owner)) {
            return false;
        }
        return isDamageDealingProjectile(proj);
    }

    /**
     * Checks if a shooter is considered hostile relative to the owner.
     */
    public static boolean isHostileShooter(Entity shooter, LivingEntity owner) {
        if (shooter == null) return true;
        if (shooter == owner) return false;
        if (shooter instanceof Wolf w && w.getOwner() == owner) return false;
        return true;
    }

    /**
     * Checks if a projectile is a damage-dealing ballistic type.
     */
    public static boolean isDamageDealingProjectile(Projectile proj) {
        if (proj == null) return false;
        return proj instanceof AbstractArrow || proj instanceof AbstractHurtingProjectile || proj instanceof ShulkerBullet;
    }

    /**
     * Evaluates if a dog is permitted to unseat to perform an emergency intercept.
     */
    public static boolean isEmergencyUnseatAllowed(boolean isSitting, double distanceToOwner) {
        return !isSitting || distanceToOwner <= EMERGENCY_UNSEAT_MAX_DISTANCE;
    }

    /**
     * Evaluates if the dog has sufficient velocity and lookahead time to reach the intercept point.
     */
    public static boolean canDogReach(double distanceToIntercept, double tauTicks) {
        if (tauTicks <= 0.0) return false;
        if (distanceToIntercept <= 2.0) return true;
        return tauTicks >= (distanceToIntercept / 0.5);
    }

    /**
     * Computes the 3D leap impulse towards the target intercept coordinate.
     */
    public static Vec3 calculateLeapImpulse(Vec3 dogPos, Vec3 interceptPos) {
        if (dogPos == null || interceptPos == null) return Vec3.ZERO;
        double dx = interceptPos.x - dogPos.x;
        double dz = interceptPos.z - dogPos.z;
        double horizDist = Math.sqrt(dx * dx + dz * dz);
        if (horizDist > 1e-4) {
            dx = (dx / horizDist) * LEAP_HORIZONTAL_SCALE;
            dz = (dz / horizDist) * LEAP_HORIZONTAL_SCALE;
        } else {
            dx = 0.0;
            dz = 0.0;
        }
        return new Vec3(dx, LEAP_VERTICAL_IMPULSE, dz);
    }

    @Override
    public boolean canUse() {
        if (this.wolf == null || !this.wolf.isAlive() || this.wolf.isPassenger()) return false;
        if (this.wolf.level() == null || this.wolf.level().isClientSide()) return false;
        if (this.cooldownTicks > 0) {
            this.cooldownTicks--;
            return false;
        }
        if (!BetterDogsGameRules.isProjectileInterceptEnabled(this.wolf.level())) return false;
        if (!WolfDispositionHelper.isBallisticInterceptor(this.wolf)) return false;

        LivingEntity owner = this.wolf.getOwner();
        if (owner == null || !owner.isAlive() || owner.isSpectator()) return false;
        if (owner instanceof Player player && player.isCreative()) return false;

        int healthThreshold = BetterDogsGameRules.getInterceptorOwnerHealthThreshold(this.wolf.level());
        if (owner.getHealth() > (float) healthThreshold) return false;

        boolean isSitting = this.wolf.isOrderedToSit() || this.wolf.isInSittingPose();
        if (!isEmergencyUnseatAllowed(isSitting, this.wolf.distanceTo(owner))) return false;

        List<Projectile> projectiles = this.wolf.level().getEntitiesOfClass(
                Projectile.class,
                owner.getBoundingBox().inflate(SCAN_RADIUS),
                p -> isHostileProjectile(p, owner)
        );
        if (projectiles.isEmpty()) return false;

        Projectile bestProj = null;
        WolfProjectileInterceptHelper.InterceptPrediction bestPrediction = null;
        double minTau = Double.MAX_VALUE;
        Vec3 wolfPos = this.wolf.position();

        for (Projectile proj : projectiles) {
            Vec3 projPos = proj.position();
            Vec3 projVel = proj.getDeltaMovement();

            WolfProjectileInterceptHelper.InterceptPrediction prediction =
                    WolfProjectileInterceptHelper.computeOwnerIntercept(owner.position(), projPos, projVel, 24.0, 1.25);

            if (!prediction.willHitOwner()) continue;

            double distToIntercept = wolfPos.distanceTo(prediction.interceptPosition());
            if (!canDogReach(distToIntercept, prediction.timeToImpactTicks())) continue;

            if (prediction.timeToImpactTicks() < minTau) {
                minTau = prediction.timeToImpactTicks();
                bestProj = proj;
                bestPrediction = prediction;
            }
        }

        if (bestProj == null || bestPrediction == null) return false;

        this.targetProjectile = bestProj;
        this.cachedInterceptPos = bestPrediction.interceptPosition();
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf == null || !this.wolf.isAlive()) return false;
        if (this.targetProjectile == null || !this.targetProjectile.isAlive() || this.targetProjectile.onGround()) return false;
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null || !owner.isAlive()) return false;
        return this.activeTicks > 0;
    }

    @Override
    public void start() {
        boolean isSitting = this.wolf.isOrderedToSit() || this.wolf.isInSittingPose();
        if (isSitting) {
            this.wasSitting = true;
            this.wolf.setOrderedToSit(false);
            this.wolf.setInSittingPose(false);
        } else {
            this.wasSitting = false;
        }
        this.hasLeaped = false;
        this.activeTicks = MAX_ACTIVE_TICKS;

        if (this.cachedInterceptPos != null && !Vec3.ZERO.equals(this.cachedInterceptPos)) {
            this.wolf.getNavigation().moveTo(
                    this.cachedInterceptPos.x,
                    this.cachedInterceptPos.y,
                    this.cachedInterceptPos.z,
                    SPRINT_SPEED_MODIFIER
            );
        }
        if (this.targetProjectile != null) {
            this.wolf.getLookControl().setLookAt(this.targetProjectile, 30.0F, 30.0F);
        }
    }

    @Override
    public void tick() {
        if (this.activeTicks > 0) {
            this.activeTicks--;
        }
        if (this.wolf == null || this.targetProjectile == null || !this.targetProjectile.isAlive() || this.targetProjectile.onGround()) {
            return;
        }

        LivingEntity owner = this.wolf.getOwner();
        if (owner != null) {
            WolfProjectileInterceptHelper.InterceptPrediction currentPred =
                    WolfProjectileInterceptHelper.computeOwnerIntercept(
                            owner.position(),
                            this.targetProjectile.position(),
                            this.targetProjectile.getDeltaMovement(),
                            24.0,
                            1.25
                    );
            if (currentPred.willHitOwner()) {
                this.cachedInterceptPos = currentPred.interceptPosition();
            }
        }

        if (this.cachedInterceptPos != null && !Vec3.ZERO.equals(this.cachedInterceptPos)) {
            this.wolf.getNavigation().moveTo(
                    this.cachedInterceptPos.x,
                    this.cachedInterceptPos.y,
                    this.cachedInterceptPos.z,
                    SPRINT_SPEED_MODIFIER
            );
        }
        this.wolf.getLookControl().setLookAt(this.targetProjectile, 30.0F, 30.0F);

        Vec3 wolfPos = this.wolf.position();
        double distToIntercept = wolfPos.distanceTo(this.cachedInterceptPos);
        double tau = this.targetProjectile.getDeltaMovement().lengthSqr() > 1e-6
                ? wolfPos.distanceTo(this.targetProjectile.position()) / this.targetProjectile.getDeltaMovement().length()
                : 99.0;

        if (!this.hasLeaped && distToIntercept <= LEAP_DISTANCE_THRESHOLD && tau <= LEAP_TIME_THRESHOLD_TICKS) {
            Vec3 leapImpulse = calculateLeapImpulse(wolfPos, this.cachedInterceptPos);
            this.wolf.setDeltaMovement(leapImpulse);
            this.wolf.getJumpControl().jump();
            this.hasLeaped = true;
        }
    }

    @Override
    public void stop() {
        this.wolf.getNavigation().stop();
        if (this.wasSitting) {
            this.wolf.setOrderedToSit(true);
            this.wolf.setInSittingPose(true);
            this.wasSitting = false;
        }
        this.targetProjectile = null;
        this.cachedInterceptPos = Vec3.ZERO;
        this.hasLeaped = false;
        this.activeTicks = 0;
        this.cooldownTicks = COOLDOWN_TICKS_AFTER_STOP;
    }

    public Wolf getWolf() { return this.wolf; }
    public Projectile getTargetProjectile() { return this.targetProjectile; }
    public Vec3 getCachedInterceptPos() { return this.cachedInterceptPos; }
    public boolean wasSitting() { return this.wasSitting; }
    public boolean hasLeaped() { return this.hasLeaped; }
    public int getActiveTicks() { return this.activeTicks; }
    public int getCooldownTicks() { return this.cooldownTicks; }
    public void setCooldownTicks(int cooldownTicks) { this.cooldownTicks = cooldownTicks; }
}
