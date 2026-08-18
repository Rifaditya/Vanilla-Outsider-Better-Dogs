// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfHowlHelper;

import java.util.EnumSet;

/**
 * Dedicated single-purpose AI goal for nocturnal pack chorus howling.
 * Standing pack wolves vocalize together under night skies and full moons with harmonized pitches.
 */
public class GroupHowlGoal extends Goal {

    private final Wolf wolf;
    private int howlTimer = 0;
    private int howlCooldown = 0;
    private static final int HOWL_COOLDOWN = 12000; // 10 minutes

    public GroupHowlGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!WolfHowlHelper.canJoinHowl(this.wolf)) {
            return false;
        }

        Level level = this.wolf.level();
        if (level == null || level.isClientSide()) {
            return false;
        }

        // Responding to an active packmate's chorus howl signal
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$getHowlingTicks() > 0) {
            return true;
        }

        // Cooldown check for self-initiated howling
        if (this.howlCooldown > 0) {
            this.howlCooldown--;
            return false;
        }

        // Night time check
        if (!isNightTime(level)) {
            return false;
        }

        // Throttle evaluation to once every 100 ticks
        if (this.wolf.tickCount % 100 != 0) {
            return false;
        }

        float chance = DynamicGameRuleManager.getProb(level, BetterDogsGameRules.BD_HOWL_CHANCE);
        if (chance <= 0.0f) {
            return false;
        }

        return this.wolf.getRandom().nextFloat() < chance;
    }

    private boolean isNightTime(Level level) {
        return level.getSkyDarken() >= 4;
    }

    @Override
    public void start() {
        this.howlTimer = WolfHowlHelper.BASE_HOWL_DURATION;
        this.wolf.getNavigation().stop();

        boolean isAlertedFollower = false;
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$getHowlingTicks() > 0) {
            isAlertedFollower = true;
        }

        if (!isAlertedFollower) {
            // Initiator howling and alerting packmates
            WolfHowlHelper.initiateChorusHowl(this.wolf, BetterDogsConfig.get().getHowlSpreadRange());
        } else {
            // Responding follower with harmonic pitch
            float pitch = WolfHowlHelper.calculateHarmonicPitch(this.wolf.getRandom().nextFloat());
            WolfHowlHelper.startHowl(this.wolf, pitch);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.howlTimer > 0 && !this.wolf.isOrderedToSit() && this.wolf.getTarget() == null;
    }

    @Override
    public void tick() {
        this.howlTimer--;
        this.wolf.setXRot(-45.0F);
        this.wolf.getLookControl().setLookAt(this.wolf.getX(), this.wolf.getY() + 5.0, this.wolf.getZ(), 30.0F, 30.0F);
    }

    @Override
    public void stop() {
        this.howlCooldown = HOWL_COOLDOWN;
        this.howlTimer = 0;
        if (this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setHowlingTicks(0);
        }
    }

    public void betterdogs$setHowlCooldown(int cooldown) {
        this.howlCooldown = cooldown;
    }
}
