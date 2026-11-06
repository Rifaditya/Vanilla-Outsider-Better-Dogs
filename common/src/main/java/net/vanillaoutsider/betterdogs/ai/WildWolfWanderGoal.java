// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Wolf;

/**
 * Dedicated ambient wandering stroll AI Goal for untamed wild wolves (BL-BD-040).
 * Restores ambient roaming for wild wolves and pack leaders without duplicating stroll
 * goals on tamed dogs.
 */
public class WildWolfWanderGoal extends WaterAvoidingRandomStrollGoal {
    private final Wolf wolf;

    public WildWolfWanderGoal(Wolf wolf, double speedModifier) {
        super(wolf, speedModifier);
        this.wolf = wolf;
    }

    /**
     * Evaluates whether the given wolf is eligible for ambient wild wandering.
     * Wild wandering is strictly reserved for untamed, living wolves.
     *
     * @param wolf the wolf entity to check, may be null
     * @return true if the wolf exists, is alive, and is untamed; false otherwise
     */
    public static boolean isEligible(Wolf wolf) {
        return wolf != null && wolf.isAlive() && !wolf.isTame();
    }

    /**
     * Evaluates whether a wolf with the given tame state is eligible for wild wandering.
     *
     * @param isTame the wolf tame status
     * @return true if untamed (!isTame); false if tamed
     */
    public static boolean isEligible(boolean isTame) {
        return !isTame;
    }

    @Override
    public boolean canUse() {
        if (!isEligible(this.wolf)) {
            return false;
        }
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (!isEligible(this.wolf)) {
            return false;
        }
        return super.canContinueToUse();
    }
}
