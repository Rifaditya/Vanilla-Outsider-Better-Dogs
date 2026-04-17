package net.vanillaoutsider.betterdogs.ai.group;

import net.dasik.social.ai.goal.FollowLeaderGoal;
import net.dasik.social.api.group.strategy.GroupParameters;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.LivingEntity;
import net.dasik.social.api.group.GroupMember;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/*
 * Verified against: WildWolfFollowLeaderGoal.java (26.*), FollowLeaderGoal.java (26.*)
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class WildWolfFollowLeaderGoal extends FollowLeaderGoal {

    private final Wolf wolf;
    private static final int MAX_PACK_SIZE = 8;
    private static final int SPREAD_SYNC_INTERVAL = 40;

    // Stagger per-wolf so all pack members don't re-read the GameRule on the same tick
    private int spreadSyncTick;

    public WildWolfFollowLeaderGoal(Wolf mob) {
        // Cohesion radius = 5.0, Default separation = 2.0 (GameRule default 20), Speed = 1.2
        // Default separation raised from 1.5 to 2.0 to match new bd_pack_spread default.
        super(mob, new GroupParameters(5.0f, 2.0f, 1.2f), 32.0);
        this.wolf = mob;
        this.spreadSyncTick = mob.getId() % SPREAD_SYNC_INTERVAL;
    }

    @Override
    public boolean canUse() {
        GroupMember groupWolf = (GroupMember) this.wolf;

        // Tamed wolves do not form wild packs. They follow the owner or their own AI.
        if (this.wolf.isTame()) {
            groupWolf.setLeader(null);
            return false;
        }

        // Call super to run the standard GroupManager discovery and leader election
        boolean result = super.canUse();

        // If a leader was found and elected...
        if (result) {
            LivingEntity leader = groupWolf.getLeader();
            if (leader != null && leader != this.wolf) {
                // Check if the pack is full. If it is, and we aren't already part of it, reject the leader.
                // We use the leader's perceived group size to decide.
                GroupMember groupLeader = (GroupMember) leader;
                if (groupLeader.getGroupSize() >= MAX_PACK_SIZE) {
                    groupWolf.setLeader(null);
                    return false;
                }
            }
        }

        return result && !this.wolf.isTame();
    }

    @Override
    public void tick() {
        // Periodically sync GroupParameters with the current bd_pack_spread GameRule value.
        if (--this.spreadSyncTick <= 0) {
            this.spreadSyncTick = SPREAD_SYNC_INTERVAL;
            float newSeparation = BetterDogsGameRules.getDecileFloat(this.wolf.level(), BetterDogsGameRules.BD_PACK_SPREAD);
            if (newSeparation > 0.0f) {
                float currentSeparation = this.parameters.separationRadius();
                if (Math.abs(currentSeparation - newSeparation) > 0.05f) {
                    this.setParameters(new GroupParameters(
                        this.parameters.cohesionRadius(),
                        newSeparation,
                        this.parameters.maxSpeed()
                    ));
                }
            }
        }
        super.tick();
    }
}
