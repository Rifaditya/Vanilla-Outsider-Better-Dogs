// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.WolfDispositionHelper;
import net.vanillaoutsider.betterdogs.util.WolfHowlHelper;

/**
 * Dedicated single-purpose AI goal for nocturnal skyward chorus howling.
 */
public class GroupHowlGoal extends Goal {

    private final Wolf wolf;
    private int howlTimer = 0;

    public GroupHowlGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
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

        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$getHowlingTicks() > 0) {
            return ext.betterdogs$getHowlingTicks() <= WolfHowlHelper.BASE_HOWL_DURATION;
        }

        long dayTime = level.getDayTime() % 24000L;
        if (dayTime >= 13000L && dayTime <= 23000L) {
            boolean fullMoon = (level.getDayTime() / 24000L) % 8L == 0L;
            int chance = fullMoon ? 120 : 300;
            return this.wolf.getRandom().nextInt(chance) == 0;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf == null || this.wolf.getTarget() != null || this.wolf.isOrderedToSit()) {
            return false;
        }
        return this.howlTimer > 0;
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
            WolfHowlHelper.initiateChorusHowl(this.wolf, 24.0);
        } else {
            // Responding follower with harmonic pitch
            float pitch = WolfHowlHelper.calculateHarmonicPitch(this.wolf.getRandom().nextFloat());
            WolfHowlHelper.startHowl(this.wolf, pitch);
        }
    }

    @Override
    public void stop() {
        this.howlTimer = 0;
        if (this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setHowlingTicks(0);
        }
    }

    @Override
    public void tick() {
        this.howlTimer--;
        this.wolf.setXRot(-45.0F);
        this.wolf.getLookControl().setLookAt(this.wolf.getX(), this.wolf.getY() + 5.0, this.wolf.getZ(), 30.0F, 30.0F);
    }
}
