// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.WolfGiftHelper;

/**
 * Dedicated single-purpose AI goal for delivering morning gifts to the owner.
 */
public class WolfGiftGoal extends Goal {

    private final Wolf wolf;
    private Player owner;
    private int cooldown = 0;

    public WolfGiftGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.wolf == null || !this.wolf.isAlive() || !this.wolf.isTame()) {
            return false;
        }

        if (this.wolf.isOrderedToSit() || this.wolf.isInSittingPose() || this.wolf.isLeashed()) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext && (ext.betterdogs$isGuardMode() || ext.betterdogs$isSittingManually())) {
            return false;
        }

        LivingEntity livingOwner = this.wolf.getOwner();
        if (!(livingOwner instanceof Player player)) {
            return false;
        }

        this.owner = player;
        return WolfGiftHelper.canDeliverGift(this.wolf, this.owner);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.owner == null || !this.owner.isAlive() || this.wolf.isOrderedToSit() || this.wolf.isInSittingPose()) {
            return false;
        }
        return WolfGiftHelper.canDeliverGift(this.wolf, this.owner);
    }

    @Override
    public void start() {
        this.cooldown = 0;
        this.wolf.getNavigation().moveTo(this.owner, 1.25D);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.owner == null) {
            return;
        }

        this.wolf.getLookControl().setLookAt(this.owner, 10.0F, (float) this.wolf.getMaxHeadXRot());

        if (this.wolf.distanceToSqr(this.owner) <= 9.0D) {
            WolfGiftHelper.deliverGift(this.wolf, this.owner);
            this.stop();
        } else {
            if (++this.cooldown % 10 == 0) {
                this.wolf.getNavigation().moveTo(this.owner, 1.25D);
            }
        }
    }
}
