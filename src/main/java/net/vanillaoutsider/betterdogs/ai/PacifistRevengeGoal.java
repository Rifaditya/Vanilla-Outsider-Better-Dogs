// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class PacifistRevengeGoal extends HurtByTargetGoal {

    private final Wolf wolf;

    public PacifistRevengeGoal(Wolf wolf) {
        super(wolf);
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isTame() || this.wolf.isOrderedToSit() || this.wolf.isBaby()) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getPersonality() != WolfPersonality.PACIFIST) {
                return false;
            }
        } else {
            return false;
        }

        LivingEntity owner = this.wolf.getOwner();
        LivingEntity attacker = this.wolf.getLastHurtByMob();
        if (attacker == null && owner != null) {
            attacker = owner.getLastHurtByMob();
        }

        if (attacker == null || !attacker.isAlive()) {
            return false;
        }

        if (attacker instanceof Creeper) {
            return false;
        }

        // Puppy mercy check: do not retaliate against ally baby wolves
        if (attacker instanceof Wolf offendingWolf && offendingWolf.isBaby() && owner != null && owner.equals(offendingWolf.getOwner())) {
            return false;
        }

        this.wolf.setTarget(attacker);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.wolf.getTarget();
        if (target == null || !target.isAlive() || target instanceof Creeper) {
            return false;
        }

        // Defensive disengagement: do not chase if target gets too far
        LivingEntity owner = this.wolf.getOwner();
        if (owner != null && this.wolf.distanceToSqr(owner) > (24.0 * 24.0)) {
            return false;
        }

        return super.canContinueToUse();
    }
}
