// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.WolfTerritorialRivalryHelper;

/**
 * Dedicated single-purpose AI Goal for wild wolves following their pack leader.
 * Keeps non-leader wild wolves grouped with their designated pack alpha within 32 blocks.
 */
public class WildWolfFollowLeaderGoal extends Goal {

    private final Wolf wolf;
    private Wolf leader;
    private int checkCooldown = 0;

    public WildWolfFollowLeaderGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.wolf.isTame() || this.wolf.getTarget() != null) {
            return false;
        }

        if (!(this.wolf instanceof WolfExtensions ext) || ext.betterdogs$isPackLeader()) {
            return false;
        }

        Level level = this.wolf.getCommandSenderWorld();
        if (level == null || level.isClientSide()) {
            return false;
        }

        UUID leaderUuid = ext.betterdogs$getLeaderUUID();
        if (leaderUuid != null) {
            List<Wolf> nearby = level.getEntitiesOfClass(
                Wolf.class,
                this.wolf.getBoundingBox().inflate(32.0),
                w -> w.isAlive() && w.getUUID().equals(leaderUuid)
            );
            if (!nearby.isEmpty()) {
                this.leader = nearby.get(0);
                return this.wolf.distanceToSqr(this.leader) > 25.0; // Follow if > 5 blocks away
            }
        }

        // Periodic leader discovery & election
        if (this.checkCooldown <= 0) {
            this.checkCooldown = 60; // Check every 3s
            List<Wolf> packMembers = level.getEntitiesOfClass(
                Wolf.class,
                this.wolf.getBoundingBox().inflate(16.0),
                w -> w.isAlive() && !w.isTame()
            );

            if (packMembers.size() > 1) {
                Wolf bestAlpha = this.wolf;
                double bestScore = WolfTerritorialRivalryHelper.calculateDominanceScore(this.wolf);

                for (Wolf member : packMembers) {
                    double score = WolfTerritorialRivalryHelper.calculateDominanceScore(member);
                    if (score > bestScore) {
                        bestScore = score;
                        bestAlpha = member;
                    }
                }

                if (bestAlpha != this.wolf) {
                    ext.betterdogs$setPackLeader(false);
                    ext.betterdogs$setLeaderUUID(bestAlpha.getUUID());
                    if (bestAlpha instanceof WolfExtensions aExt) {
                        aExt.betterdogs$setPackLeader(true);
                    }
                    this.leader = bestAlpha;
                    return this.wolf.distanceToSqr(this.leader) > 25.0;
                } else {
                    ext.betterdogs$setPackLeader(true);
                    ext.betterdogs$setLeaderUUID(null);
                }
            }
        } else {
            this.checkCooldown--;
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.isTame() || this.wolf.getTarget() != null || this.leader == null || !this.leader.isAlive()) {
            return false;
        }
        return this.wolf.distanceToSqr(this.leader) > 9.0 && this.wolf.distanceToSqr(this.leader) <= 1024.0;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        this.leader = null;
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.leader == null) {
            return;
        }

        this.wolf.getLookControl().setLookAt(this.leader, 10.0F, 10.0F);
        if (this.wolf.distanceToSqr(this.leader) > 16.0) {
            this.wolf.getNavigation().moveTo(this.leader, 1.15);
        } else {
            this.wolf.getNavigation().stop();
        }
    }
}
