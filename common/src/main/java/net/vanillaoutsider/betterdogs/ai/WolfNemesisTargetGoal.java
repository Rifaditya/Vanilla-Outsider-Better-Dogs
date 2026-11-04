// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.WolfNemesisHelper;

/**
 * Dedicated single-purpose AI goal scanning for and attacking active nemesis mob types to avenge pack mates.
 */
public class WolfNemesisTargetGoal extends TargetGoal {

    private final Wolf wolf;
    private LivingEntity target;
    private final TargetingConditions targetConditions;

    public WolfNemesisTargetGoal(Wolf wolf) {
        super(wolf, false);
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        this.targetConditions = TargetingConditions.forCombat()
                .range(20.0D)
                .selector(entity -> entity != null && WolfNemesisHelper.isNemesisActive(this.wolf, entity));
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isTame() || this.wolf.isInSittingPose()) {
            return false;
        }
        if (!(this.wolf instanceof WolfExtensions ext)) {
            return false;
        }
        String nemesis = ext.betterdogs$getNemesisEntityType();
        if (nemesis == null || nemesis.isEmpty()) {
            return false;
        }

        List<LivingEntity> potentialTargets = this.wolf.getCommandSenderWorld().getEntitiesOfClass(
                LivingEntity.class,
                this.wolf.getBoundingBox().inflate(20.0D, 8.0D, 20.0D),
                entity -> entity != null && entity.isAlive() && WolfNemesisHelper.isNemesisActive(this.wolf, entity)
        );

        if (potentialTargets.isEmpty()) {
            return false;
        }

        // Find nearest matching nemesis target
        LivingEntity nearest = null;
        double nearestDistSq = Double.MAX_VALUE;
        for (LivingEntity entity : potentialTargets) {
            double distSq = this.wolf.distanceToSqr(entity);
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = entity;
            }
        }

        this.target = nearest;
        return this.target != null;
    }

    @Override
    public void start() {
        this.wolf.setTarget(this.target);
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.target == null || !this.target.isAlive() || this.wolf.isInSittingPose()) {
            return false;
        }
        if (!WolfNemesisHelper.isNemesisActive(this.wolf, this.target)) {
            return false;
        }
        return super.canContinueToUse();
    }

    @Override
    public void stop() {
        this.target = null;
        super.stop();
    }
}
