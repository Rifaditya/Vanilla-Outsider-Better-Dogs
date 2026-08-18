// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.util.WolfFleeHelper;

import java.util.EnumSet;
import java.util.List;

/**
 * Dedicated single-purpose AI goal for Pacifist personality wolves.
 * When attacked or threatened, Pacifist wolves do not initiate direct violence;
 * they alert nearby packmates within 16 blocks to defend them and flee tactically toward safety/owner.
 */
public class PacifistRevengeGoal extends Goal {

    public static final double DEFENSE_ALERT_RADIUS = 16.0D;
    public static final double DEFAULT_FLEE_SPEED = 1.25D;

    private final Wolf wolf;
    private final double speedModifier;
    private double posX;
    private double posY;
    private double posZ;

    public PacifistRevengeGoal(Wolf wolf) {
        this(wolf, DEFAULT_FLEE_SPEED);
    }

    public PacifistRevengeGoal(Wolf wolf, double speedModifier) {
        this.wolf = wolf;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.wolf == null || !this.wolf.isAlive() || this.wolf.isBaby() || this.wolf.isOrderedToSit() || this.wolf.isInSittingPose()) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getPersonality() != WolfPersonality.PACIFIST) {
                return false;
            }
            if (ext.betterdogs$isGuardMode() || ext.betterdogs$isSittingManually()) {
                return false;
            }
        } else {
            return false;
        }

        LivingEntity attacker = this.wolf.getLastHurtByMob();
        LivingEntity owner = this.wolf.getOwner();
        if (attacker == null && owner != null) {
            attacker = owner.getLastHurtByMob();
        }

        if (attacker == null || !attacker.isAlive() || attacker instanceof Creeper) {
            return false;
        }

        // Puppy mercy check: do not alert or flee from allied baby wolf
        if (attacker instanceof Wolf offendingWolf && offendingWolf.isBaby() && owner != null && owner.equals(offendingWolf.getOwner())) {
            return false;
        }

        Vec3 escapePos = WolfFleeHelper.calculateEscapePosition(this.wolf, attacker, 10, 5);
        if (escapePos == null) {
            return false;
        }

        this.posX = escapePos.x;
        this.posY = escapePos.y;
        this.posZ = escapePos.z;
        return true;
    }

    @Override
    public void start() {
        this.wolf.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
        this.wolf.setTarget(null); // Preserve pacifist non-retaliation

        LivingEntity attacker = this.wolf.getLastHurtByMob();
        LivingEntity owner = this.wolf.getOwner();
        if (attacker == null && owner != null) {
            attacker = owner.getLastHurtByMob();
        }

        if (attacker != null) {
            alertNearbyPackmates(attacker);
        }

        WolfFleeHelper.playDisengagementFeedback(this.wolf);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf == null || this.wolf.isOrderedToSit() || this.wolf.isInSittingPose()) {
            return false;
        }
        return this.wolf.getNavigation() != null && !this.wolf.getNavigation().isDone();
    }

    @Override
    public void stop() {
        if (this.wolf != null && this.wolf.getNavigation() != null) {
            this.wolf.getNavigation().stop();
        }
    }

    /**
     * Alerts nearby packmates (Aggressive/Normal) to target and neutralize the threat.
     */
    public void alertNearbyPackmates(LivingEntity attacker) {
        if (attacker == null || !attacker.isAlive() || this.wolf == null) {
            return;
        }

        Level level = this.wolf.level();
        if (level == null || level.isClientSide()) {
            return;
        }

        LivingEntity owner = this.wolf.getOwner();
        List<Wolf> nearbyWolves = level.getEntitiesOfClass(
                Wolf.class,
                this.wolf.getBoundingBox().inflate(DEFENSE_ALERT_RADIUS),
                w -> w != this.wolf && w.isAlive() && w.isTame() && !w.isBaby() && !w.isOrderedToSit() && (owner == null || owner.equals(w.getOwner()))
        );

        for (Wolf packWolf : nearbyWolves) {
            if (packWolf instanceof WolfExtensions ext) {
                if (ext.betterdogs$getPersonality() == WolfPersonality.PACIFIST) {
                    continue; // Fellow pacifists do not attack
                }
            }
            if (packWolf.getTarget() == null || !packWolf.getTarget().isAlive()) {
                packWolf.setTarget(attacker);
            }
        }
    }
}
