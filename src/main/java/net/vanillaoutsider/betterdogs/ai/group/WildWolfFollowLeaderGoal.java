// Verified against: WildWolfFollowLeaderGoal.java (26.1.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.ai.group;

import net.dasik.social.ai.goal.FollowLeaderGoal;
import net.dasik.social.api.group.GroupMember;
import net.dasik.social.api.group.strategy.GroupParameters;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;

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
    public void tick() {
        LivingEntity leader = ((GroupMember)this.mob).getLeader();
        if (leader instanceof GroupMember groupLeader) {
            int N = groupLeader.getGroupSize();
            float multiplier = net.dasik.social.api.gamerule.DynamicGameRuleManager.getInt(this.mob.level(), net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.BD_WILD_PACK_SPREAD_MULTIPLIER) / 100.0f;
            float maxExtra = net.dasik.social.api.gamerule.DynamicGameRuleManager.getInt(this.mob.level(), net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.BD_WILD_PACK_SPREAD_MAX) / 10.0f;
            float extra = Math.min((float) Math.sqrt(Math.max(0, N - 1)) * multiplier, maxExtra);
            
            float cohesion = 5.0f + extra;
            float separation = 2.5f + extra;
            
            this.setParameters(new GroupParameters(
                cohesion, separation, 1.2f, true, 144.0f, 6.0f, 2.0f, 0.0f, 0.0f, 0.0f
            ));
        }
        super.tick();
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
