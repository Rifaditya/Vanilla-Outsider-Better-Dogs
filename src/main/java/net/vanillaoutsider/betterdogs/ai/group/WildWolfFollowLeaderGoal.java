package net.vanillaoutsider.betterdogs.ai.group;

import net.dasik.social.ai.goal.FollowLeaderGoal;
import net.dasik.social.api.group.strategy.GroupParameters;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;
import net.dasik.social.api.group.GroupMember;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.WolfExtensions;

import java.util.EnumSet;

/**
 * Optimized Wild Wolf Group AI.
 * Wild wolves follow a designated leader with logic mirroring FollowOwnerGoal.
 * Leadership is stable (never changes) and persists across world reloads.
 * If the leader is tamed, wild followers continue to follow them (following the player by proxy).
 * If a follower is tamed, they leave the wild pack and follow their owner.
 */
public class WildWolfFollowLeaderGoal extends FollowLeaderGoal<Wolf> {
    private static final float SEARCH_RADIUS = 32.0f;
    private static final int MAX_PACK_SIZE = 8;
    
    private static final GroupParameters WOLF_PARAMS = new GroupParameters(
        5.0f, 2.5f, 1.2f, true, 144.0f, 6.0f, 2.0f, 0.0f, 0.0f, 0.0f
    );

    public WildWolfFollowLeaderGoal(Wolf wolf) {
        super(wolf, WOLF_PARAMS, SEARCH_RADIUS);
    }

    @Override
    public boolean canUse() {
        // Tamed wolves follow owners, not wild leaders.
        if (this.mob.isTame()) {
            return false;
        }

        // Parent handles leader election and distance check
        if (!super.canUse()) {
            return false;
        }

        LivingEntity leader = ((GroupMember)this.mob).getLeader();
        
        // Pack Size Limit check (only for NEW members joining - stable members stay)
        if (this.mob.tickCount % 100 == 0) {
             GroupMember groupLeader = (GroupMember) leader;
             if (groupLeader.getGroupSize() > MAX_PACK_SIZE && !this.isAlreadyInGroup(leader)) {
                 ((GroupMember)this.mob).setLeader(null);
                 return false;
             }
        }

        return true;
    }

    private boolean isAlreadyInGroup(LivingEntity leader) {
        return this.mob.distanceToSqr(leader) < 400.0;
    }
}
