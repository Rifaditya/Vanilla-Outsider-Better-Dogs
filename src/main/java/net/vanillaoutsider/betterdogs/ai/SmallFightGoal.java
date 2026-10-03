// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.SmallFightHelper;

/**
 * Dedicated single-purpose AI Goal for harmless social sparring / play fighting between compatible tamed dogs.
 * Runs for ~6 seconds with mock pounces, playful audio cues, and happy villager particles. Deals 0 damage.
 */
public class SmallFightGoal extends Goal {

    private final Wolf wolf;
    private Wolf partner;
    private int pounceTimer = 0;

    public SmallFightGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!SmallFightHelper.isEligibleForPlay(this.wolf)) {
            return false;
        }

        // Idle initiator search (1 in 400 ticks, ~20s)
        if (this.wolf.getRandom().nextInt(400) == 0) {
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

        // Disengagement range abort (>10 blocks, >100.0 distance squared)
        if (this.wolf.distanceToSqr(this.partner) > SmallFightHelper.MAX_SPARRING_DISTANCE_SQR) {
            return false;
        }

        if (this.wolf.isOrderedToSit() || this.wolf.isLeashed() || this.wolf.getTarget() != null) {
            return false;
        }

        if (this.partner.isOrderedToSit() || this.partner.isLeashed() || this.partner.getTarget() != null) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isSubdued()) {
            return false;
        }
        if (this.partner instanceof WolfExtensions pExt && pExt.betterdogs$isSubdued()) {
            return false;
        }

        return true;
    }

    @Override
    public void tick() {
        if (this.partner == null) {
            return;
        }

        this.wolf.getLookControl().setLookAt(this.partner, 30.0F, 30.0F);
        this.wolf.getNavigation().moveTo(this.partner, SmallFightHelper.DEFAULT_SPEED_MODIFIER);

        // Pack Audience Reactions & Spectator Wagging (evaluated every 20 ticks / 1s during active sparring)
        if (this.wolf.tickCount % 20 == 0) {
            if (this.wolf.getUUID() == null || this.partner.getUUID() == null || this.wolf.getUUID().compareTo(this.partner.getUUID()) <= 0) {
                net.vanillaoutsider.betterdogs.util.PackAudienceHelper.tickAudience(this.wolf, this.partner);
            }
        }

        this.pounceTimer++;
        double distSqr = this.wolf.distanceToSqr(this.partner);
        if (distSqr <= 3.5 && this.pounceTimer % 25 == 0) {
            this.wolf.getJumpControl().jump();
            boolean snapped = SmallFightHelper.handlePounce(this.wolf, this.partner);
            if (snapped) {
                this.partner = null;
                return;
            }
        }
    }

    @Override
    public void stop() {
        SmallFightHelper.applyCooldown(this.wolf);
        if (this.partner != null) {
            SmallFightHelper.endSession(this.wolf, this.partner);
        }
        this.partner = null;
        this.wolf.getNavigation().stop();
    }
}
