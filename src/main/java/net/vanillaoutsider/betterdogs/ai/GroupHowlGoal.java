// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
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
        if (this.wolf.isBaby() || this.wolf.isOrderedToSit() || this.wolf.getTarget() != null) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isGuarding()) {
            return false;
        }

        Level level = this.wolf.level();
        if (level == null || level.isClientSide()) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$getHowlingTicks() > 0) {
            return true;
        }

        long dayTime = level.getDayTime() % 24000L;
        if (dayTime >= 13000L && dayTime <= 23000L) {
            boolean fullMoon = (level.getDayTime() / 24000L) % 8L == 0L;
            int chance = fullMoon ? 120 : 300;
            if (this.wolf.getRandom().nextInt(chance) == 0) {
                WolfHowlHelper.initiateChorusHowl(this.wolf, 24.0);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.getTarget() != null || this.wolf.isOrderedToSit()) {
            return false;
        }
        return this.howlTimer > 0;
    }

    @Override
    public void start() {
        this.howlTimer = 60;
        this.wolf.getNavigation().stop();
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
