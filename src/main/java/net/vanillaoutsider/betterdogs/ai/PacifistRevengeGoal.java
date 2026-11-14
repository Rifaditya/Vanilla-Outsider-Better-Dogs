// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.util.PackAudienceHelper;
import net.vanillaoutsider.betterdogs.util.WolfTargetingHandler;
import org.jspecify.annotations.Nullable;

/**
 * Defensive retaliation target goal for Pacifist wolves (BL-BD-062).
 * Pacifists never initiate fights, but defend themselves when struck and defend their owner
 * when their owner is struck within {@value #OWNER_DEFENSE_RADIUS} blocks, while rallying nearby
 * non-Pacifist standing packmates.
 */
public class PacifistRevengeGoal extends HurtByTargetGoal {

    /**
     * Maximum distance from the owner within which a Pacifist enters and maintains defensive retaliation.
     */
    public static final double OWNER_DEFENSE_RADIUS = 24.0D;

    /**
     * Squared owner-defense radius ({@code 24.0 * 24.0 = 576.0}).
     */
    public static final double OWNER_DEFENSE_RADIUS_SQR = OWNER_DEFENSE_RADIUS * OWNER_DEFENSE_RADIUS;

    /**
     * Radius in blocks within which nearby standing co-owned adult packmates are rallied.
     */
    public static final double DEFENSE_ALERT_RADIUS = 16.0D;

    private final Wolf wolf;
    private @Nullable LivingEntity pendingAttacker;

    public PacifistRevengeGoal(Wolf wolf) {
        super(wolf);
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        if (!isEligibleDefender()) {
            this.pendingAttacker = null;
            return false;
        }

        LivingEntity owner = this.wolf.getOwner();
        if (!isWithinOwnerDefenseTether(owner)) {
            this.pendingAttacker = null;
            return false;
        }

        LivingEntity attacker = resolveAttacker(owner);
        if (!isEligibleTarget(attacker, owner)) {
            this.pendingAttacker = null;
            return false;
        }

        this.pendingAttacker = attacker;
        this.wolf.setTarget(attacker);
        return true;
    }

    @Override
    public void start() {
        if (this.wolf == null) {
            this.pendingAttacker = null;
            return;
        }

        LivingEntity owner = this.wolf.getOwner();
        LivingEntity resolved = resolveAttacker(owner);
        if (!isEligibleTarget(resolved, owner)) {
            resolved = this.pendingAttacker;
        }

        super.start();
        this.pendingAttacker = null;

        // HurtByTargetGoal.start() unconditionally calls mob.setTarget(mob.getLastHurtByMob()),
        // which wipes an owner-defense target back to null when only the owner was struck.
        if (isEligibleDefender() && isWithinOwnerDefenseTether(owner) && isEligibleTarget(resolved, owner)) {
            this.wolf.setTarget(resolved);
            this.targetMob = resolved;
            this.unseenMemoryTicks = 300;
            alertNearbyPackmates(resolved);
        } else {
            this.wolf.setTarget(null);
            this.targetMob = null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (!isEligibleDefender()) {
            return false;
        }

        LivingEntity owner = this.wolf.getOwner();
        if (!isWithinOwnerDefenseTether(owner)) {
            return false;
        }

        LivingEntity target = this.wolf.getTarget();
        if (!isEligibleTarget(target, owner)) {
            return false;
        }

        if (this.wolf.getSensing() == null) {
            return true;
        }
        return super.canContinueToUse();
    }

    @Override
    public void stop() {
        this.pendingAttacker = null;
        if (this.wolf == null) {
            this.targetMob = null;
            return;
        }
        super.stop();
    }

    /**
     * Alerts nearby non-Pacifist standing co-owned adult packmates within {@value #DEFENSE_ALERT_RADIUS} blocks
     * to assist against a validated hostile attacker.
     */
    public void alertNearbyPackmates(@Nullable LivingEntity attacker) {
        if (this.wolf == null || attacker == null || attacker == this.wolf || !attacker.isAlive()) {
            return;
        }
        Level level = this.wolf.level();
        if (level == null || level.isClientSide()) {
            return;
        }
        if (attacker instanceof Creeper) {
            return;
        }
        if (attacker instanceof Wolf offendingWolf && PackAudienceHelper.isCoOwned(this.wolf, offendingWolf)) {
            return;
        }

        AABB alertBounds = this.wolf.getBoundingBox().inflate(DEFENSE_ALERT_RADIUS);
        List<Wolf> nearbyWolves = level.getEntitiesOfClass(
                Wolf.class,
                alertBounds,
                packmate -> isEligiblePackmateForAlert(packmate, attacker)
        );
        for (Wolf packmate : nearbyWolves) {
            if (packmate.getTarget() == null) {
                packmate.setTarget(attacker);
            }
        }
    }

    private boolean isEligibleDefender() {
        if (this.wolf == null || !this.wolf.isAlive() || !this.wolf.isTame() || this.wolf.isBaby()) {
            return false;
        }
        if (this.wolf.isOrderedToSit() || this.wolf.isInSittingPose()) {
            return false;
        }
        if (!(this.wolf instanceof WolfExtensions ext)) {
            return false;
        }
        if (ext.betterdogs$isSittingManually()) {
            return false;
        }
        return ext.betterdogs$getPersonality() == WolfPersonality.PACIFIST;
    }

    private boolean isWithinOwnerDefenseTether(@Nullable LivingEntity owner) {
        return owner == null || this.wolf.distanceToSqr(owner) <= OWNER_DEFENSE_RADIUS_SQR;
    }

    private @Nullable LivingEntity resolveAttacker(@Nullable LivingEntity owner) {
        LivingEntity attacker = this.wolf.getLastHurtByMob();
        if ((attacker == null || !isEligibleTarget(attacker, owner)) && owner != null) {
            attacker = owner.getLastHurtByMob();
        }
        return attacker;
    }

    private boolean isEligibleTarget(@Nullable LivingEntity attacker, @Nullable LivingEntity owner) {
        if (attacker == null || attacker == this.wolf || attacker == owner || !attacker.isAlive()) {
            return false;
        }
        if (this.wolf.isOwnedBy(attacker)) {
            return false;
        }
        if (attacker instanceof Creeper) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext
                && (WolfTargetingHandler.shouldSuppressRetaliation(ext.betterdogs$isBeingDisciplined())
                        || WolfTargetingHandler.shouldSuppressRetaliation(this.wolf, attacker))) {
            return false;
        }
        if (attacker instanceof Wolf offendingWolf && offendingWolf.isTame()
                && PackAudienceHelper.isCoOwned(this.wolf, offendingWolf)) {
            return false;
        }
        return true;
    }

    private boolean isEligiblePackmateForAlert(@Nullable Wolf packmate, LivingEntity attacker) {
        if (packmate == null || packmate == this.wolf || packmate == attacker || !packmate.isAlive()) {
            return false;
        }
        if (!packmate.isTame() || packmate.isBaby() || packmate.isOrderedToSit() || packmate.isInSittingPose()) {
            return false;
        }
        if (!PackAudienceHelper.isCoOwned(this.wolf, packmate)) {
            return false;
        }
        if (packmate instanceof WolfExtensions ext) {
            if (ext.betterdogs$isSittingManually() || ext.betterdogs$getPersonality() == WolfPersonality.PACIFIST) {
                return false;
            }
        }
        return !WolfTargetingHandler.shouldSuppressRetaliation(packmate, attacker);
    }
}
