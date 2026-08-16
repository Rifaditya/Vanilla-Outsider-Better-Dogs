// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;

/**
 * AI goal for tamed dogs moving to and mounting a commanded vehicle or seat.
 */
public class MoveToVehicleGoal extends Goal {

    private final Wolf wolf;
    private int timeElapsed;

    public MoveToVehicleGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.wolf.isOrderedToSit() || this.wolf.isPassenger()) {
            return false;
        }
        Entity target = DogCommandManager.getVehicleTarget(this.wolf.getUUID());
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (this.wolf.distanceToSqr(target) > 144.0D) {
            DogCommandManager.clearVehicleTarget(this.wolf.getUUID());
            return false;
        }
        if (!DogCommandManager.hasPassengerSpace(target)) {
            DogCommandManager.clearVehicleTarget(this.wolf.getUUID());
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        Entity target = DogCommandManager.getVehicleTarget(this.wolf.getUUID());
        if (target == null || !target.isAlive() || this.wolf.isPassenger()) {
            return false;
        }
        return this.timeElapsed < 100 && this.wolf.distanceToSqr(target) <= 144.0D;
    }

    @Override
    public void start() {
        this.timeElapsed = 0;
    }

    @Override
    public void tick() {
        Entity target = DogCommandManager.getVehicleTarget(this.wolf.getUUID());
        if (target == null) {
            return;
        }

        this.wolf.getLookControl().setLookAt(target, 10.0F, (float) this.wolf.getMaxHeadXRot());
        this.wolf.getNavigation().moveTo(target, 1.25D);
        this.timeElapsed++;

        if (this.wolf.distanceToSqr(target) < 2.25D) {
            this.wolf.startRiding(target);
            DogCommandManager.clearVehicleTarget(this.wolf.getUUID());
        }
    }

    @Override
    public void stop() {
        DogCommandManager.clearVehicleTarget(this.wolf.getUUID());
        this.wolf.getNavigation().stop();
    }
}
