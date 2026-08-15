// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.util.EntityRaycastHelper;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

/**
 * Dedicated single-purpose AI goal for coordinated pack flanking and multi-angle attack pursuit.
 */
public class WolfFlankAttackGoal extends Goal {

    private final Wolf wolf;
    private final double speedModifier;
    private int attackCooldown;
    private int recheckPackTimer;
    private int packSlotIndex;
    private int totalPackCount;

    public WolfFlankAttackGoal(Wolf wolf, double speedModifier) {
        this.wolf = wolf;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isTame() || this.wolf.isOrderedToSit()) {
            return false;
        }
        LivingEntity target = this.wolf.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.wolf.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return !this.wolf.isOrderedToSit();
    }

    @Override
    public void start() {
        this.attackCooldown = 0;
        this.recheckPackTimer = 0;
        this.packSlotIndex = 0;
        this.totalPackCount = 1;
    }

    @Override
    public void stop() {
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.wolf.getTarget();
        if (target == null) {
            return;
        }

        this.wolf.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (--this.recheckPackTimer <= 0) {
            this.recheckPackTimer = 10;
            updatePackSlot(target);
        }

        Level level = this.wolf.level();
        double distSq = this.wolf.distanceToSqr(target);

        if (this.packSlotIndex == 0 || this.totalPackCount <= 1 || distSq <= 6.25) {
            this.wolf.getNavigation().moveTo(target, this.speedModifier);
        } else {
            double baseAngle = Math.atan2(this.wolf.getZ() - target.getZ(), this.wolf.getX() - target.getX());
            double angleOffset = (this.packSlotIndex % 2 == 1 ? -1.0 : 1.0) * (Math.PI / 3.0) * ((this.packSlotIndex + 1) / 2);
            double flankAngle = baseAngle + angleOffset;
            double flankRadius = 3.5;

            Vec3 flankPos = EntityRaycastHelper.findClearFlankPos(level, target.position(), flankAngle, flankRadius);
            this.wolf.getNavigation().moveTo(flankPos.x, flankPos.y, flankPos.z, this.speedModifier);
        }

        checkAndPerformAttack(target, distSq);
    }

    private void updatePackSlot(LivingEntity target) {
        Level level = this.wolf.level();
        if (level == null) {
            this.packSlotIndex = 0;
            this.totalPackCount = 1;
            return;
        }

        List<Wolf> pack = level.getEntitiesOfClass(Wolf.class, this.wolf.getBoundingBox().inflate(12.0),
                other -> other.isAlive() && other.isTame() && other.getTarget() == target);

        pack.sort(Comparator.comparing(Wolf::getUUID));
        this.totalPackCount = pack.size();
        this.packSlotIndex = pack.indexOf(this.wolf);
        if (this.packSlotIndex < 0) {
            this.packSlotIndex = 0;
        }
    }

    private void checkAndPerformAttack(LivingEntity target, double distSq) {
        double reachSq = getAttackReachSqr(target);
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        if (distSq <= reachSq && this.attackCooldown <= 0) {
            this.attackCooldown = 20;
            this.wolf.swing(InteractionHand.MAIN_HAND);
            if (this.wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                this.wolf.doHurtTarget(serverLevel, target);
            }
        }
    }

    private double getAttackReachSqr(LivingEntity target) {
        return (double) (this.wolf.getBbWidth() * 2.0F * this.wolf.getBbWidth() * 2.0F + target.getBbWidth());
    }
}
