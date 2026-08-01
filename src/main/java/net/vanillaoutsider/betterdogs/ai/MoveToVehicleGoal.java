// SPDX-License-Identifier: GPL-3.0-or-later
/*
 * Vanilla Outsider: Better Dogs
 * AI Goal for moving to and boarding a targeted vehicle/seat.
 */
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;

public class MoveToVehicleGoal extends Goal {
    private final Wolf wolf;
    private int timeElapsed;

    public MoveToVehicleGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
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
        // Distance check (12 blocks limit = 144 squared)
        if (this.wolf.distanceToSqr(target) > 144.0D) {
            DogCommandManager.clearVehicleTarget(this.wolf.getUUID());
            return false;
        }
        // Verify target has space
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
        // Timeout check (5 seconds = 100 ticks) and distance check
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

        // Board vehicle when close enough (1.5 blocks = 2.25 squared)
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
