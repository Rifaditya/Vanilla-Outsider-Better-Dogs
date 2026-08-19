// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.scheduler.events.CorrectionDogEvent;
import net.vanillaoutsider.betterdogs.util.BabyRetaliationHelper;
import net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper;

import java.util.EnumSet;
import java.util.List;

/**
 * Dedicated single-purpose AI goal for aggressive puppy snap retaliation.
 */
public class BabyBiteBackGoal extends Goal {

    private final Wolf wolf;
    private LivingEntity retaliationTarget;
    private boolean hasAttacked;
    private int attackDelay;

    public BabyBiteBackGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!BabyRetaliationHelper.isEligible(this.wolf)) {
            return false;
        }
        if (!(this.wolf instanceof WolfExtensions ext)) {
            return false;
        }
        if (ext.betterdogs$getSocialAction() != WolfExtensions.SocialAction.RETALIATION) {
            return false;
        }
        LivingEntity target = ext.betterdogs$getSocialTarget();
        if (target != null && target.isAlive() && target != this.wolf) {
            this.retaliationTarget = target;
            return true;
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
        if (this.hasAttacked) {
            return false;
        }
        if (this.retaliationTarget == null || !this.retaliationTarget.isAlive()) {
            return false;
        }
        if (this.wolf.isOrderedToSit() || this.wolf.isLeashed()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext) {
            return ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.RETALIATION;
        }
        return false;
    }

    @Override
    public void tick() {
        if (this.retaliationTarget == null) {
            return;
        }

        this.wolf.getLookControl().setLookAt(this.retaliationTarget, 30.0F, (float) this.wolf.getMaxHeadXRot());
        this.wolf.getNavigation().moveTo(this.retaliationTarget, BabyRetaliationHelper.DEFAULT_SPEED_MODIFIER);

        this.attackDelay = Math.max(this.attackDelay - 1, 0);
        double distSq = this.wolf.distanceToSqr(this.retaliationTarget);
        double reachSq = (double) (this.wolf.getBbWidth() * 2.0F * this.wolf.getBbWidth() * 2.0F + this.retaliationTarget.getBbWidth() + 1.0);

        if (distSq <= reachSq && this.attackDelay <= 0) {
            this.attackDelay = 20;
            this.wolf.swing(InteractionHand.MAIN_HAND);
            BabyRetaliationHelper.playRetaliationCues(this.wolf);

            if (this.wolf.level() instanceof ServerLevel serverLevel) {
                DamageSource source = serverLevel.damageSources().mobAttack(this.wolf);
                if (!WolfFriendlyFireHelper.shouldCancelDamage(this.wolf, source, BabyRetaliationHelper.RETALIATION_DAMAGE)) {
                    this.retaliationTarget.hurtServer(serverLevel, source, BabyRetaliationHelper.RETALIATION_DAMAGE);
                }
                this.hasAttacked = true;

                // Provoke adult discipline from nearby co-owned aggressive adults
                double searchDist = 16.0;
                AABB searchBox = this.wolf.getBoundingBox().inflate(searchDist);
                List<Wolf> nearbyWolves = this.wolf.level().getEntitiesOfClass(
                        Wolf.class,
                        searchBox,
                        w -> w != this.wolf && !w.isBaby() && w.isTame() && w.getOwner() == this.wolf.getOwner());

                for (Wolf adult : nearbyWolves) {
                    if (adult instanceof WolfExtensions adultExt) {
                        if (adultExt.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE
                                && !adultExt.betterdogs$isSocialModeActive()) {
                            adultExt.betterdogs$getOrInitializeScheduler().schedule(
                                    new CorrectionDogEvent(this.wolf));
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void stop() {
        if (this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
        this.wolf.getNavigation().stop();
        this.retaliationTarget = null;
        this.hasAttacked = false;
    }
}
