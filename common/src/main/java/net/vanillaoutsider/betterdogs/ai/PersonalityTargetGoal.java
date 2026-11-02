// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class PersonalityTargetGoal {

    public static boolean shouldTargetEntity(Wolf wolf, LivingEntity target, boolean isOwnerAttackingTarget) {
        if (wolf == null || target == null || !target.isAlive()) {
            return false;
        }

        if (wolf instanceof WolfExtensions ext) {
            WolfPersonality personality = ext.betterdogs$getPersonality();
            switch (personality) {
                case PACIFIST -> {
                    if (isOwnerAttackingTarget) {
                        return false;
                    }
                    LivingEntity owner = wolf.getOwner();
                    Mob mobTarget = target instanceof Mob m ? m : null;
                    boolean isTargetAttackingWolf = target.getLastHurtMob() == wolf || (mobTarget != null && mobTarget.getTarget() == wolf);
                    boolean isTargetAttackingOwner = owner != null && (target.getLastHurtMob() == owner || (mobTarget != null && mobTarget.getTarget() == owner));
                    return isTargetAttackingWolf || isTargetAttackingOwner;
                }
                case AGGRESSIVE -> {
                    if (target instanceof Monster) {
                        return wolf.distanceToSqr(target) <= (20.0 * 20.0);
                    }
                    return isOwnerAttackingTarget;
                }
                case NORMAL -> {
                    return isOwnerAttackingTarget || target instanceof Monster;
                }
            }
        }
        return isOwnerAttackingTarget;
    }
}
