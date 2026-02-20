package net.vanillaoutsider.betterdogs.ai.group;

import net.dasik.social.ai.goal.FollowLeaderGoal;
import net.dasik.social.api.group.strategy.GroupParameters;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.dasik.social.api.group.GroupMember;

@SuppressWarnings({"unchecked", "rawtypes"})
public class WildWolfFollowLeaderGoal extends FollowLeaderGoal {

    private final Wolf wolf;
    private static final int MAX_PACK_SIZE = 8;

    public WildWolfFollowLeaderGoal(Wolf mob) {
        // Cohesion radius = 5.0, Separation = 1.5, Speed = 1.2
        // We use Dasik's defaults and a 32-block search radius.
        super(mob, GroupParameters.DEFAULT_TERRESTRIAL, 32.0);
        this.wolf = mob;
    }

    @Override
    public boolean canUse() {
        GroupMember<Wolf> groupWolf = (GroupMember<Wolf>) this.wolf;

        // Tamed wolves do not form wild packs. They follow the owner or their own AI.
        if (this.wolf.isTame()) {
            groupWolf.setLeader(null);
            return false;
        }

        // Call super to run the standard GroupManager discovery and leader election
        boolean result = super.canUse();

        // If a leader was found and elected...
        if (result) {
            Wolf leader = groupWolf.getLeader();
            if (leader != null && leader != this.wolf) {
                // Check if the pack is full. If it is, and we aren't already part of it, reject the leader.
                // We use the leader's perceived group size to decide.
                GroupMember<Wolf> groupLeader = (GroupMember<Wolf>) leader;
                if (groupLeader.getGroupSize() >= MAX_PACK_SIZE) {
                    groupWolf.setLeader(null);
                    return false;
                }
            }
        }

        return result && !this.wolf.isTame();
    }
}
