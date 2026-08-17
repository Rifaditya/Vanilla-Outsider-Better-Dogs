// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.EnumSet;

/**
 * Dedicated single-purpose AI goal for pathing towards acoustic horn sound coordinates.
 */
public class WolfHornGoal extends Goal {

    private final Wolf wolf;
    private int ticksRunning;

    public WolfHornGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.wolf.isOrderedToSit() || !this.wolf.isTame()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext) {
            return ext.betterdogs$getSoundLocationTarget() != null;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.isOrderedToSit()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext) {
            BlockPos target = ext.betterdogs$getSoundLocationTarget();
            if (target == null) {
                return false;
            }
            int timeout = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_HORN_PATHING_TIMEOUT, 300);
            if (this.ticksRunning >= timeout) {
                return false;
            }
            return this.wolf.distanceToSqr(target.getX() + 0.5, target.getY(), target.getZ() + 0.5) > 4.0;
        }
        return false;
    }

    @Override
    public void start() {
        this.ticksRunning = 0;
        if (this.wolf instanceof WolfExtensions ext) {
            BlockPos target = ext.betterdogs$getSoundLocationTarget();
            if (target != null) {
                this.wolf.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 1.25);
            }
        }
    }

    @Override
    public void tick() {
        this.ticksRunning++;
        if (this.wolf instanceof WolfExtensions ext) {
            BlockPos target = ext.betterdogs$getSoundLocationTarget();
            if (target != null) {
                if (this.wolf.getNavigation().isDone()) {
                    this.wolf.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 1.25);
                }
                if (this.wolf.distanceToSqr(target.getX() + 0.5, target.getY(), target.getZ() + 0.5) <= 4.0) {
                    ext.betterdogs$setSoundLocationTarget(null);
                }
            }
        }
    }

    @Override
    public void stop() {
        this.wolf.getNavigation().stop();
    }
}
