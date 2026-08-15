// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class AggressiveTargetGoal extends NearestAttackableTargetGoal<Monster> {
    private final Wolf wolf;

    public AggressiveTargetGoal(Wolf wolf) {
        super(wolf, Monster.class, true);
        this.wolf = wolf;
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

        if (this.target != null && this.target instanceof Creeper) {
            return false;
        }

        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$getPassiveOverrideTicks() > 0) {
            return false;
        }
        if (this.target != null && this.target instanceof Creeper) {
            return false;
        }
        return super.canContinueToUse();
    }
}
