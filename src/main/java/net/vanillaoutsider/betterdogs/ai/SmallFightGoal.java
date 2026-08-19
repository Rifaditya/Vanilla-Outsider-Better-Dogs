// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.SmallFightHelper;

import java.util.EnumSet;

/**
 * Dedicated single-purpose AI Goal for harmless social sparring / play fighting between compatible tamed dogs.
 * Runs for ~6 seconds with mock pounces, playful audio cues, and happy villager particles. Deals 0 damage.
 */
public class SmallFightGoal extends Goal {

    private final Wolf wolf;
    private LivingEntity partner;
    private int pounceTimer = 0;

    public SmallFightGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!(this.wolf instanceof WolfExtensions ext)) {
            return false;
        }

        // Active in PLAY_FIGHT social mode
        if (ext.betterdogs$isSocialModeActive() && ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.PLAY_FIGHT) {
            LivingEntity target = ext.betterdogs$getSocialTarget();
            if (target instanceof Wolf targetWolf && targetWolf.isAlive()) {
                this.partner = targetWolf;
                return true;
            }
        }

        // Idle initiator search (1 in 40 ticks)
        if (SmallFightHelper.isEligibleForPlay(this.wolf) && this.wolf.getRandom().nextInt(40) == 0) {
            Wolf found = SmallFightHelper.findPlayPartner(this.wolf, SmallFightHelper.DEFAULT_PARTNER_RADIUS);
            if (found != null) {
                SmallFightHelper.startPlaySession(this.wolf, found);
                this.partner = found;
                return true;
            }
        }

        return false;
    }

    @Override
    public void start() {
        this.pounceTimer = 0;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.partner == null || !this.partner.isAlive()) {
            return false;
        }

        if (this.wolf.isOrderedToSit() || this.wolf.isLeashed() || this.wolf.getTarget() != null) {
            return false;
        }

        if (this.partner instanceof Wolf partnerWolf) {
            if (partnerWolf.isOrderedToSit() || partnerWolf.isLeashed() || partnerWolf.getTarget() != null) {
                return false;
            }
        }

        if (this.wolf instanceof WolfExtensions ext) {
            return ext.betterdogs$isSocialModeActive() && ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.PLAY_FIGHT;
        }

        return false;
    }

    @Override
    public void tick() {
        if (this.partner == null) {
            return;
        }

        this.wolf.getLookControl().setLookAt(this.partner, 30.0F, 30.0F);
        this.wolf.getNavigation().moveTo(this.partner, SmallFightHelper.DEFAULT_SPEED_MODIFIER);

        this.pounceTimer++;
        double distSqr = this.wolf.distanceToSqr(this.partner);
        if (distSqr <= 3.5 && this.pounceTimer % 25 == 0) {
            this.wolf.getJumpControl().jump();
            if (this.partner instanceof Wolf partnerWolf) {
                SmallFightHelper.applyPlayFeedback(this.wolf, partnerWolf);
            }
        }
    }

    @Override
    public void stop() {
        if (this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
        this.partner = null;
        this.wolf.getNavigation().stop();
    }
}
