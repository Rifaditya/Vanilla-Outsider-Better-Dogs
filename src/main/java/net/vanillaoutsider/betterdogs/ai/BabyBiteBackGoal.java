// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper;

/**
 * Dedicated single-purpose AI goal for aggressive puppy snap retaliation and bite back.
 * Aggressive puppies retaliate with a 1.0 HP nip when disciplined or attacked.
 */
public class BabyBiteBackGoal extends Goal {

    private final Wolf wolf;
    private final double speedModifier;
    private LivingEntity target;
    private boolean hasAttacked;
    private int attackDelay;

    public BabyBiteBackGoal(Wolf wolf, double speedModifier) {
        this.wolf = wolf;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isBaby() || !this.wolf.isTame() || this.wolf.isOrderedToSit() || this.wolf.isLeashed()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getPersonality() != WolfPersonality.AGGRESSIVE) {
                return false;
            }
            if (ext.betterdogs$getRetaliationTicks() <= 0) {
                return false;
            }
            LivingEntity retTarget = ext.betterdogs$getRetaliationTarget();
            if (retTarget != null && retTarget.isAlive() && retTarget != this.wolf) {
                this.target = retTarget;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.hasAttacked = false;
        this.attackDelay = 0;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.hasAttacked || this.wolf.isOrderedToSit() || this.wolf.isLeashed()) {
            return false;
        }
        if (this.target == null || !this.target.isAlive()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext) {
            return ext.betterdogs$getRetaliationTicks() > 0;
        }
        return false;
    }

    @Override
    public void tick() {
        if (this.target == null) {
            return;
        }

        this.wolf.getLookControl().setLookAt(this.target, 30.0F, (float) this.wolf.getMaxHeadXRot());
        this.wolf.getNavigation().moveTo(this.target, this.speedModifier);

        double distSq = this.wolf.distanceToSqr(this.target);
        double reachSq = (this.wolf.getBbWidth() * 2.0F) * (this.wolf.getBbWidth() * 2.0F) + this.target.getBbWidth() + 1.0;

        if (distSq <= reachSq) {
            Level level = this.wolf.getCommandSenderWorld();
            if (level != null && !level.isClientSide()) {
                this.wolf.swing(InteractionHand.MAIN_HAND);
                level.playSound(null, this.wolf.getX(), this.wolf.getY(), this.wolf.getZ(), SoundEvents.WOLF_GROWL, SoundSource.NEUTRAL, 0.8F, 1.4F);

                DamageSource source = level.damageSources().mobAttack(this.wolf);
                if (!WolfFriendlyFireHelper.shouldCancelDamage(this.wolf, source)) {
                    this.target.hurt(source, 1.0F);
                }
            }
            this.hasAttacked = true;
            if (this.wolf instanceof WolfExtensions ext) {
                ext.betterdogs$setRetaliationTarget(null, 0);
            }
        }
    }

    @Override
    public void stop() {
        this.target = null;
        this.hasAttacked = false;
        this.wolf.getNavigation().stop();
        if (this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setRetaliationTarget(null, 0);
        }
    }
}
