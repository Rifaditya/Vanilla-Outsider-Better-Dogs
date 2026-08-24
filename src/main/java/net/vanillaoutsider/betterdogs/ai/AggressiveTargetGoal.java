// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

public class AggressiveTargetGoal extends NearestAttackableTargetGoal<Monster> {
    private final Wolf wolf;

    public AggressiveTargetGoal(Wolf wolf) {
        super(wolf, Monster.class, 10, true, false, target -> isEligibleTarget(wolf, target));
        this.wolf = wolf;
    }

    public static boolean isEligibleTarget(Wolf wolf, LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (target instanceof Creeper || target instanceof Ghast) {
            return false;
        }
        if (wolf != null) {
            LivingEntity owner = wolf.getOwner();
            if (owner != null) {
                var level = wolf.level();
                int detectRange = level != null ? BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_AGGRO_DETECT_RANGE, 16) : 16;
                return target.distanceToSqr(owner) <= (double) (detectRange * detectRange);
            }
        }
        return true;
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isTame() || this.wolf.isOrderedToSit() || this.wolf.isLeashed()) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getPassiveOverrideTicks() > 0) {
                return false;
            }
            if (ext.betterdogs$getPersonality() != WolfPersonality.AGGRESSIVE) {
                return false;
            }
        } else {
            return false;
        }

        LivingEntity owner = this.wolf.getOwner();
        if (owner == null) {
            return false;
        }

        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$getPassiveOverrideTicks() > 0) {
            return false;
        }
        if (this.target != null && !isEligibleTarget(this.wolf, this.target)) {
            return false;
        }
        return super.canContinueToUse();
    }
}
