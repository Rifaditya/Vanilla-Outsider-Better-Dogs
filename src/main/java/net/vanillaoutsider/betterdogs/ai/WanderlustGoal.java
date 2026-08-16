// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import org.jetbrains.annotations.Nullable;

/**
 * Dedicated single-purpose AI Goal for occasional safe exploratory roaming (Wanderlust) around the owner.
 * Allows expanded exploration perimeter without breaking sitting, leash, or guarding states.
 */
public class WanderlustGoal extends WaterAvoidingRandomStrollGoal {

    private final Wolf wolf;

    public WanderlustGoal(Wolf wolf, double speedModifier) {
        super(wolf, speedModifier);
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isTame() || this.wolf.isOrderedToSit() || this.wolf.isLeashed() || this.wolf.getTarget() != null) {
            return false;
        }

        if (!(this.wolf instanceof WolfExtensions ext) || ext.betterdogs$isGuarding() || ext.betterdogs$getWanderlustTicks() <= 0) {
            return false;
        }

        LivingEntity owner = this.wolf.getOwner();
        if (owner == null || !owner.isAlive() || this.wolf.distanceToSqr(owner) > 1024.0) {
            return false;
        }

        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.isOrderedToSit() || this.wolf.isLeashed() || this.wolf.getTarget() != null) {
            return false;
        }

        if (!(this.wolf instanceof WolfExtensions ext) || ext.betterdogs$getWanderlustTicks() <= 0) {
            return false;
        }

        LivingEntity owner = this.wolf.getOwner();
        if (owner == null || !owner.isAlive() || this.wolf.distanceToSqr(owner) > 1024.0) {
            return false;
        }

        return super.canContinueToUse();
    }

    @Override
    protected @Nullable Vec3 getPosition() {
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null) {
            return super.getPosition();
        }

        Vec3 ownerPos = new Vec3(owner.getX(), owner.getY(), owner.getZ());
        double distToOwner = this.wolf.distanceTo(owner);

        // If further than 24 blocks from owner, navigate back towards owner perimeter
        if (distToOwner > 24.0) {
            Vec3 target = DefaultRandomPos.getPosTowards(this.wolf, 12, 7, ownerPos, 1.5707963705062866);
            if (target != null) {
                return target;
            }
        }

        // Expanded exploration perimeter (up to 28 blocks from owner)
        for (int i = 0; i < 10; i++) {
            Vec3 pos = DefaultRandomPos.getPos(this.wolf, 16, 7);
            if (pos != null && pos.distanceToSqr(ownerPos) <= 784.0) { // 28^2 = 784
                return pos;
            }
        }

        return super.getPosition();
    }
}
