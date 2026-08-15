// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class TamedWanderNearOwnerGoal extends WaterAvoidingRandomStrollGoal {
    private final Wolf wolf;

    public TamedWanderNearOwnerGoal(Wolf wolf, double speedModifier) {
        super(wolf, speedModifier);
        this.wolf = wolf;
    }

    public double getMaxRoamRadius() {
        if (this.wolf instanceof WolfExtensions ext) {
            WolfPersonality personality = ext.betterdogs$getPersonality();
            return switch (personality) {
                case AGGRESSIVE -> 14.0;
                case PACIFIST -> 4.0;
                case NORMAL -> 8.0;
            };
        }
        return 8.0;
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isTame() || this.wolf.isOrderedToSit() || this.wolf.isLeashed()) {
            return false;
        }
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null) {
            return false;
        }
        return super.canUse();
    }

    @Override
    protected Vec3 getPosition() {
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null) {
            return super.getPosition();
        }

        Vec3 ownerPos = new Vec3(owner.getX(), owner.getY(), owner.getZ());
        double distToOwner = this.wolf.distanceTo(owner);
        double maxRadius = getMaxRoamRadius();

        if (distToOwner > maxRadius) {
            Vec3 target = DefaultRandomPos.getPosTowards(this.wolf, 10, 7, ownerPos, 1.5707963705062866);
            if (target != null) {
                return target;
            }
        }

        for (int i = 0; i < 10; i++) {
            Vec3 pos = super.getPosition();
            if (pos != null && pos.distanceToSqr(ownerPos) <= maxRadius * maxRadius) {
                return pos;
            }
        }

        return null;
    }
}
