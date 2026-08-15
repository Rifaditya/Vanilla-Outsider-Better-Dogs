// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.phys.Vec3;

public class WolfFleeLowHealthGoal extends Goal {

    private final Wolf wolf;
    private final double speedModifier;
    private double posX;
    private double posY;
    private double posZ;

    public WolfFleeLowHealthGoal(Wolf wolf, double speedModifier) {
        this.wolf = wolf;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.wolf.isOrderedToSit()) {
            return false;
        }

        float lowHealthThreshold = wolf.getMaxHealth() * 0.30f;
        if (wolf.getHealth() >= lowHealthThreshold) {
            return false;
        }

        LivingEntity attacker = wolf.getLastHurtByMob();
        LivingEntity target = wolf.getTarget();
        LivingEntity avoidTarget = attacker != null ? attacker : target;

        Vec3 escapePos = null;
        if (avoidTarget != null) {
            Vec3 avoidPos = new Vec3(avoidTarget.getX(), avoidTarget.getY(), avoidTarget.getZ());
            escapePos = DefaultRandomPos.getPosAway(wolf, 10, 5, avoidPos);
        }
        if (escapePos == null) {
            escapePos = DefaultRandomPos.getPos(wolf, 10, 5);
        }

        if (escapePos == null) {
            return false;
        }

        this.posX = escapePos.x;
        this.posY = escapePos.y;
        this.posZ = escapePos.z;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.isOrderedToSit()) {
            return false;
        }
        float lowHealthThreshold = wolf.getMaxHealth() * 0.30f;
        if (wolf.getHealth() >= lowHealthThreshold) {
            return false;
        }
        return !wolf.getNavigation().isDone();
    }

    @Override
    public void start() {
        wolf.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
        wolf.setTarget(null);
    }
}
