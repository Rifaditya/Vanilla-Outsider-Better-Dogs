// Verified against: PacifistRevengeGoal.java (26.1.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.ai;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.dasik.social.api.group.GroupMember;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * AI Goal for Pacifist personality wolves.
 * Only attacks when the owner takes damage from a mob.
 */
public class PacifistRevengeGoal extends HurtByTargetGoal {

    private final Wolf wolf;

    public PacifistRevengeGoal(Wolf wolf) {
        super(wolf);
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        // Only for wolves with Pacifist personality (Adults)
        boolean isWildEnabled = DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_WILD_PERSONALITY_BEHAVIOR);

        if (!wolf.isTame() && (!isWildEnabled || ((GroupMember)wolf).getLeader() == null)) {
            return false;
        }

        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getPersonality() != WolfPersonality.PACIFIST || wolf.isBaby())
                return false;
        } else {
            return false;
        }

        // Check if anchor was recently hurt
        LivingEntity anchor = getAnchor();
        if (anchor == null)
            return false;

        LivingEntity lastHurtByMob = anchor.getLastHurtByMob();
        if (lastHurtByMob == null)
            return false;

        // Target the mob that hurt our anchor
        wolf.setTarget(lastHurtByMob);
        return true;
    }

    private LivingEntity getAnchor() {
        if (wolf.isTame()) {
            return wolf.getOwner();
        } else if (wolf instanceof GroupMember member) {
            return member.getLeader();
        }
        return null;
    }

    @Override
    public boolean canContinueToUse() {
        // Continue as long as target is alive
        LivingEntity target = wolf.getTarget();
        return target != null && target.isAlive() && super.canContinueToUse();
    }
}
