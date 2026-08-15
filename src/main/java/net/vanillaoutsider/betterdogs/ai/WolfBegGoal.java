// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.util.DogTreatHelper;

import java.util.EnumSet;

/**
 * Dedicated single-purpose AI goal for begging when nearby players hold the dog's favorite treat.
 */
public class WolfBegGoal extends Goal {

    private final Wolf wolf;
    private Player player;
    private final float lookDistance;
    private int lookTime;
    private final TargetingConditions begTargeting;

    public WolfBegGoal(Wolf wolf, float lookDistance) {
        this.wolf = wolf;
        this.lookDistance = lookDistance;
        this.begTargeting = TargetingConditions.forNonCombat().range(lookDistance);
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isTame() || this.wolf.isOrderedToSit()) {
            return false;
        }
        Level level = this.wolf.level();
        if (level == null) {
            return false;
        }
        this.player = level.getNearestPlayer(this.wolf, (double) this.lookDistance);
        return this.player != null && DogTreatHelper.isHoldingFavoriteTreat(this.wolf, this.player);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.player == null || !this.player.isAlive()) {
            return false;
        }
        if (this.wolf.distanceToSqr(this.player) > (double) (this.lookDistance * this.lookDistance)) {
            return false;
        }
        return this.lookTime > 0 && DogTreatHelper.isHoldingFavoriteTreat(this.wolf, this.player);
    }

    @Override
    public void start() {
        this.wolf.setIsInterested(true);
        this.lookTime = this.adjustedTickDelay(40 + this.wolf.getRandom().nextInt(40));
    }

    @Override
    public void stop() {
        this.wolf.setIsInterested(false);
        this.player = null;
    }

    @Override
    public void tick() {
        if (this.player == null) {
            return;
        }
        this.wolf.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10.0F, (float) this.wolf.getMaxHeadXRot());
        this.lookTime--;
    }
}
