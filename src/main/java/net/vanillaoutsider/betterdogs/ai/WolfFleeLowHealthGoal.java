// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.util.WolfFleeHelper;

import java.util.EnumSet;

/**
 * AI goal for all wolves (tamed or wild) to tactically disengage from combat when low health.
 * Triggers when health falls below 30% of max health.
 */
public class WolfFleeLowHealthGoal extends Goal {

    private final Wolf wolf;
    private final double speedModifier;
    private double posX;
    private double posY;
    private double posZ;

    public WolfFleeLowHealthGoal(Wolf wolf, double speedModifier) {
        this.wolf = wolf;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!WolfFleeHelper.shouldFlee(this.wolf)) {
            return false;
        }

        LivingEntity attacker = this.wolf.getLastHurtByMob();
        LivingEntity target = this.wolf.getTarget();
        LivingEntity avoidTarget = attacker != null ? attacker : target;

        Vec3 escapePos = WolfFleeHelper.calculateEscapePosition(this.wolf, avoidTarget, 10, 5);
        if (escapePos == null) {
            return false;
        }

        this.posX = escapePos.x;
        this.posY = escapePos.y;
        this.posZ = escapePos.z;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.isOrderedToSit()) {
            return false;
        }
        if (!WolfFleeHelper.isLowHealth(this.wolf)) {
            return false;
        }
        return !this.wolf.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.wolf.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
        // Clear active target to stop attacking while fleeing
        this.wolf.setTarget(null);
        // Dispatch whine sound and sweat droplet particles
        WolfFleeHelper.playDisengagementFeedback(this.wolf);
    }
}
