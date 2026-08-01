// Verified against: WildWolfPackWarGoal.java (26.1.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import java.util.List;
import net.dasik.social.api.group.GroupMember;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

/**
 * AI Goal for wild wolf pack members during a Territorial War.
 * Enforces member-vs-member combat while leaders fight 1v1.
 */
public class WildWolfPackWarGoal extends Goal {
    private final Wolf wolf;
    private LivingEntity rivalMember;
    private int cooldown = 0;

    public WildWolfPackWarGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.wolf.isTame()) return false;
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        LivingEntity leader = ((GroupMember)this.wolf).getLeader();
        if (leader == null || !leader.isAlive()) return false;

        if (leader instanceof WolfExtensions leaderExt) {
            if (leaderExt.betterdogs$getSocialAction() != WolfExtensions.SocialAction.TERRITORIAL_WAR) {
                return false;
            }

            // Personality Check
            WolfPersonality personality = ((WolfExtensions)this.wolf).betterdogs$getPersonality();
            if (personality == WolfPersonality.PACIFIST) {
                // Pacifists only join if they were attacked
                LivingEntity attacker = this.wolf.getLastHurtByMob();
                if (attacker == null || this.wolf.tickCount - this.wolf.getLastHurtByMobTimestamp() > 100) {
                    return false;
                }
                // Attacker must be from the rival pack
                LivingEntity rivalLeader = leaderExt.betterdogs$getSocialTarget();
                if (rivalLeader != null && attacker instanceof Wolf rivalWolf) {
                    if (((GroupMember)rivalWolf).getLeader() == rivalLeader) {
                        return true;
                    }
                }
                return false;
            }

            return this.findTarget();
        }

        return false;
    }

    private boolean findTarget() {
        LivingEntity leader = ((GroupMember)this.wolf).getLeader();
        if (!(leader instanceof WolfExtensions leaderExt)) return false;

        LivingEntity rivalLeader = leaderExt.betterdogs$getSocialTarget();
        if (rivalLeader == null) return false;

        List<Wolf> potentialRivals = this.wolf.level().getEntitiesOfClass(Wolf.class, this.wolf.getBoundingBox().inflate(16.0),
            other -> other.isAlive() && !other.isTame() && other instanceof GroupMember otherGM && otherGM.getLeader() == rivalLeader && other != rivalLeader);

        if (!potentialRivals.isEmpty()) {
            this.rivalMember = potentialRivals.get(this.wolf.getRandom().nextInt(potentialRivals.size()));
            return true;
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity leader = ((GroupMember)this.wolf).getLeader();
        if (leader == null || !(leader instanceof WolfExtensions leaderExt) || leaderExt.betterdogs$getSocialAction() != WolfExtensions.SocialAction.TERRITORIAL_WAR) {
            return false;
        }
        return this.rivalMember != null && this.rivalMember.isAlive() && this.wolf.distanceToSqr(this.rivalMember) < 256.0;
    }

    @Override
    public void start() {
        this.wolf.setTarget(this.rivalMember);
    }

    @Override
    public void stop() {
        this.rivalMember = null;
        this.wolf.setTarget(null);
        this.cooldown = 40;
    }

    @Override
    public void tick() {
        if (this.rivalMember != null) {
            this.wolf.getNavigation().moveTo(this.rivalMember, 1.3);
            this.wolf.getLookControl().setLookAt(this.rivalMember, 30.0f, 30.0f);

            if (this.wolf.distanceToSqr(this.rivalMember) < 2.5) {
                if (this.wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                   this.wolf.doHurtTarget(serverLevel, this.rivalMember);
                }
            }
        }
    }
}
