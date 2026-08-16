// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.WolfFetchHelper;

/**
 * Dedicated single-purpose AI goal for fetching dropped sticks and returning them to the owner.
 */
public class WolfFetchGoal extends Goal {

    private final Wolf wolf;
    private ItemEntity targetStick;
    private Player owner;
    private int cooldown = 0;

    public WolfFetchGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isTame() || this.wolf.isOrderedToSit() || this.wolf.getTarget() != null) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isGuarding()) {
            return false;
        }

        LivingEntity livingOwner = this.wolf.getOwner();
        if (!(livingOwner instanceof Player player)) {
            return false;
        }
        this.owner = player;

        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$hasFetchedStick()) {
            return true;
        }

        this.targetStick = WolfFetchHelper.findNearbyDroppedStick(this.wolf, 16.0);
        return this.targetStick != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.isOrderedToSit() || this.wolf.getTarget() != null || this.owner == null || !this.owner.isAlive()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$hasFetchedStick()) {
                return true;
            }
        }
        return this.targetStick != null && this.targetStick.isAlive();
    }

    @Override
    public void start() {
        this.cooldown = 0;
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$hasFetchedStick()) {
            this.wolf.getNavigation().moveTo(this.owner, 1.25);
        } else if (this.targetStick != null) {
            this.wolf.getNavigation().moveTo(this.targetStick, 1.25);
        }
    }

    @Override
    public void stop() {
        this.targetStick = null;
        this.owner = null;
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.owner == null) {
            return;
        }

        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$hasFetchedStick()) {
            this.wolf.getLookControl().setLookAt(this.owner, 10.0F, (float) this.wolf.getMaxHeadXRot());
            if (this.wolf.distanceToSqr(this.owner) <= 6.25) {
                WolfFetchHelper.dropStickToOwner(this.wolf, this.owner);
                this.stop();
            } else {
                if (++this.cooldown % 10 == 0) {
                    this.wolf.getNavigation().moveTo(this.owner, 1.25);
                }
            }
        } else if (this.targetStick != null && this.targetStick.isAlive()) {
            this.wolf.getLookControl().setLookAt(this.targetStick, 10.0F, (float) this.wolf.getMaxHeadXRot());
            if (this.wolf.distanceToSqr(this.targetStick) <= 2.25) {
                WolfFetchHelper.pickupStick(this.wolf, this.targetStick);
                this.targetStick = null;
                this.wolf.getNavigation().moveTo(this.owner, 1.25);
            } else {
                if (++this.cooldown % 10 == 0) {
                    this.wolf.getNavigation().moveTo(this.targetStick, 1.25);
                }
            }
        }
    }
}
