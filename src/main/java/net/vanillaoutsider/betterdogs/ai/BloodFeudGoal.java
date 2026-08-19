// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.util.BloodFeudHelper;

import java.util.EnumSet;

/**
 * Dedicated single-purpose AI Goal for Blood Feud vendettas between two specific wolves by UUID.
 * Fights until the nemesis dies. Strictly respects player sit commands.
 */
public class BloodFeudGoal extends Goal {

    private final Wolf wolf;
    private Wolf nemesis;

    public BloodFeudGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!BloodFeudHelper.hasBloodFeud(this.wolf)) {
            return false;
        }

        // Strictly respect player sit command
        if (this.wolf.isOrderedToSit()) {
            return false;
        }

        String nemesisUuid = BloodFeudHelper.getBloodFeudTarget(this.wolf);
        Wolf found = BloodFeudHelper.findNemesis(this.wolf, nemesisUuid, BloodFeudHelper.DEFAULT_SEARCH_RADIUS);
        if (found == null) {
            return false;
        }

        this.nemesis = found;
        return true;
    }

    @Override
    public void start() {
        if (this.nemesis != null) {
            this.wolf.setTarget(this.nemesis);
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.nemesis == null || !this.nemesis.isAlive()) {
            return false;
        }

        if (this.wolf.isOrderedToSit()) {
            return false;
        }

        return this.wolf.getTarget() == this.nemesis;
    }

    @Override
    public void stop() {
        if (this.nemesis != null && !this.nemesis.isAlive()) {
            BloodFeudHelper.clearBloodFeud(this.wolf);
        }
        this.nemesis = null;
    }
}
