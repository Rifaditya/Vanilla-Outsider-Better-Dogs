// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfDispositionHelper;
import net.vanillaoutsider.betterdogs.util.WolfFetchHelper;

/**
 * Dedicated single-purpose AI goal for fetching dropped sticks/bones and returning them to the owner.
 */
public class WolfFetchGoal extends Goal {

    private final Wolf wolf;
    private ItemEntity targetItem;
    private Player owner;
    private int cooldown = 0;

    public WolfFetchGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.wolf == null || !this.wolf.isTame()) {
            return false;
        }
        if (this.wolf.isOrderedToSit() || this.wolf.isInSittingPose() || this.wolf.getTarget() != null) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
            return false;
        }
        if (!BetterDogsGameRules.isFetchEnabled(this.wolf.level())) {
            return false;
        }

        LivingEntity livingOwner = this.wolf.getOwner();
        if (!(livingOwner instanceof Player player)) {
            return false;
        }
        this.owner = player;

        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$hasFetchedItem()) {
            return true;
        }

        int range = BetterDogsGameRules.getFetchRange(this.wolf.level());
        this.targetItem = WolfFetchHelper.findNearbyDroppedFetchItem(this.wolf, range);
        if (this.targetItem != null) {
            if (!WolfDispositionHelper.shouldFetch(this.wolf)) {
                if (WolfDispositionHelper.shouldHeadTiltOnIgnoredStick(this.wolf)) {
                    this.wolf.getLookControl().setLookAt(this.targetItem, 30.0F, 30.0F);
                    this.wolf.setIsInterested(true);
                }
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.isOrderedToSit() || this.wolf.isInSittingPose() || this.wolf.getTarget() != null || this.owner == null || !this.owner.isAlive()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$hasFetchedItem()) {
            return true;
        }
        return this.targetItem != null && this.targetItem.isAlive();
    }

    @Override
    public void start() {
        this.cooldown = 0;
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$hasFetchedItem()) {
            this.wolf.getNavigation().moveTo(this.owner, 1.25D);
        } else if (this.targetItem != null) {
            this.wolf.getNavigation().moveTo(this.targetItem, 1.25D);
        }
    }

    @Override
    public void stop() {
        this.targetItem = null;
        this.owner = null;
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.owner == null) {
            return;
        }

        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$hasFetchedItem()) {
            this.wolf.getLookControl().setLookAt(this.owner, 10.0F, (float) this.wolf.getMaxHeadXRot());
            if (this.wolf.distanceToSqr(this.owner) <= 6.25D) {
                WolfFetchHelper.dropItemToOwner(this.wolf, this.owner);
                this.stop();
            } else {
                if (++this.cooldown % 10 == 0) {
                    this.wolf.getNavigation().moveTo(this.owner, 1.25D);
                }
            }
        } else if (this.targetItem != null && this.targetItem.isAlive()) {
            this.wolf.getLookControl().setLookAt(this.targetItem, 10.0F, (float) this.wolf.getMaxHeadXRot());
            if (this.wolf.distanceToSqr(this.targetItem) <= 2.25D) {
                WolfFetchHelper.pickupItem(this.wolf, this.targetItem);
                this.targetItem = null;
                this.wolf.getNavigation().moveTo(this.owner, 1.25D);
            } else {
                if (++this.cooldown % 10 == 0) {
                    this.wolf.getNavigation().moveTo(this.targetItem, 1.25D);
                }
            }
        }
    }
}
