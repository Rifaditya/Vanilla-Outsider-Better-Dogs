// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.UUID;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose AI Goal for Blood Feud vendettas between two specific wolves by UUID.
 * Fights until the nemesis dies. Respects player sit commands.
 */
public class BloodFeudGoal extends Goal {

    private final Wolf wolf;
    private Wolf nemesis;

    public BloodFeudGoal(Wolf wolf) {
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        if (!(this.wolf instanceof WolfExtensions ext)) {
            return false;
        }

        if (!ext.betterdogs$hasBloodFeud()) {
            return false;
        }

        if (this.wolf.isOrderedToSit()) {
            return false;
        }

        String nemesisUuid = ext.betterdogs$getBloodFeudTarget();
        Wolf found = findNemesis(nemesisUuid);
        if (found == null) {
            return false;
        }

        this.nemesis = found;
        return true;
    }

    private Wolf findNemesis(String uuidString) {
        if (uuidString == null || uuidString.isEmpty()) {
            return null;
        }
        try {
            UUID uuid = UUID.fromString(uuidString);
            AABB searchBox = this.wolf.getBoundingBox().inflate(20.0);

            for (Wolf w : this.wolf.level().getEntitiesOfClass(Wolf.class, searchBox)) {
                if (w.getUUID().equals(uuid) && w.isAlive()) {
                    return w;
                }
            }
        } catch (IllegalArgumentException ignored) {
            if (this.wolf instanceof WolfExtensions ext) {
                ext.betterdogs$setBloodFeudTarget("");
            }
        }
        return null;
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
            if (this.wolf instanceof WolfExtensions ext) {
                ext.betterdogs$setBloodFeudTarget("");
            }
        }
        this.nemesis = null;
    }
}
