// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.ai;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.EnumSet;

public class PathToSoundLocationGoal extends Goal {

    private final Wolf wolf;
    private BlockPos targetPos;
    private int timer;

    public PathToSoundLocationGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!(this.wolf instanceof WolfExtensions ext)) {
            return false;
        }

        this.targetPos = ext.betterdogs$getSoundLocationTarget();
        if (this.targetPos == null || !this.wolf.isAlive() || this.wolf.unableToMoveToOwner()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.targetPos == null || !this.wolf.isAlive() || this.wolf.unableToMoveToOwner()) {
            return false;
        }

        int maxTimeout = 400;
        if (this.wolf.level() instanceof ServerLevel serverLevel) {
            maxTimeout = DynamicGameRuleManager.getInt(serverLevel, BetterDogsGameRules.BD_HORN_PATHING_TIMEOUT);
        }

        if (this.timer >= maxTimeout) {
            return false;
        }

        if (this.wolf.blockPosition().closerThan(this.targetPos, 3.0)) {
            return false;
        }

        return true;
    }

    @Override
    public void start() {
        this.timer = 0;
        if (this.targetPos != null) {
            this.wolf.getNavigation().moveTo(
                    this.targetPos.getX() + 0.5,
                    this.targetPos.getY() + 0.5,
                    this.targetPos.getZ() + 0.5,
                    1.25
            );
        }
    }

    @Override
    public void tick() {
        this.timer++;
        if (this.timer % 10 == 0 && this.targetPos != null) {
            this.wolf.getNavigation().moveTo(
                    this.targetPos.getX() + 0.5,
                    this.targetPos.getY() + 0.5,
                    this.targetPos.getZ() + 0.5,
                    1.25
            );
        }
    }

    @Override
    public void stop() {
        if (this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSoundLocationTarget(null);
        }
        this.wolf.getNavigation().stop();
        this.targetPos = null;
    }
}
