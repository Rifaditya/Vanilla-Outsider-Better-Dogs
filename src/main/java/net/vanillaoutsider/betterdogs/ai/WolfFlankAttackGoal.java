// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfFlankingHelper;

public class WolfFlankAttackGoal extends MeleeAttackGoal {
    private final Wolf wolf;
    private final double speedModifier;
    private int pathRecalcTimer = 0;
    private int ticksUntilNextAttack = 0;

    public WolfFlankAttackGoal(Wolf wolf, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(wolf, speedModifier, followingTargetEvenIfNotSeen);
        this.wolf = wolf;
        this.speedModifier = speedModifier;
    }

    @Override
    public void start() {
        super.start();
        this.pathRecalcTimer = 0;
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.wolf.getTarget();
        if (target == null) {
            return;
        }

        // Look at target
        this.wolf.getLookControl().setLookAt(target, 30.0F, 30.0F);

        boolean flankingTacticsEnabled = net.dasik.social.api.gamerule.DynamicGameRuleManager.getBoolean(
                this.wolf.level(), BetterDogsGameRules.BD_PACK_FLANKING_TACTICS);
        boolean isFarEnough = this.wolf.distanceToSqr(target) > 9.0;
        boolean moved = false;

        if (flankingTacticsEnabled && isFarEnough && WolfFlankingHelper.isFlanker(this.wolf, target)) {
            if (--this.pathRecalcTimer <= 0) {
                this.pathRecalcTimer = 4 + this.wolf.getRandom().nextInt(5);
                boolean performRaycast = net.dasik.social.api.gamerule.DynamicGameRuleManager.getBoolean(
                        this.wolf.level(), BetterDogsGameRules.BD_FLANKING_RAYCAST_CHECK);
                Vec3 destination = WolfFlankingHelper.calculateFlankDestination(this.wolf, target, performRaycast);
                if (destination != null) {
                    this.wolf.getNavigation().moveTo(destination.x, target.getY(), destination.z, this.speedModifier);
                    moved = true;
                }
            } else {
                moved = true;
            }
        }

        if (!moved) {
            // Standard direct attack movement
            if (--this.pathRecalcTimer <= 0) {
                this.pathRecalcTimer = 4 + this.wolf.getRandom().nextInt(7);
                double currentSpeed = this.speedModifier;
                if (this.wolf.distanceToSqr(target) > 9.0) {
                    currentSpeed = this.speedModifier * 0.5D;
                }
                this.wolf.getNavigation().moveTo(target, currentSpeed);
            }
        }

        // Ticking attack cooldown and triggers
        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        this.checkAndPerformAttackInternal(target);
    }

    private void checkAndPerformAttackInternal(LivingEntity target) {
        if (this.ticksUntilNextAttack <= 0 && this.wolf.isWithinMeleeAttackRange(target) && this.wolf.getSensing().hasLineOfSight(target)) {
            this.ticksUntilNextAttack = this.adjustedTickDelay(20);
            this.wolf.swing(InteractionHand.MAIN_HAND);
            this.wolf.doHurtTarget(MeleeAttackGoal.getServerLevel(this.wolf), target);
        }
    }
}
