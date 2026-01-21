package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

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
        // Only for tamed wolves with Pacifist personality
        if (!wolf.isTame())
            return false;

        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getPersonality() != WolfPersonality.PACIFIST)
                return false;
        } else {
            return false;
        }

        // Check if owner was recently hurt
        LivingEntity owner = wolf.getOwner();
        if (owner == null)
            return false;

        LivingEntity lastHurtByMob = owner.getLastHurtByMob();
        if (lastHurtByMob == null)
            return false;

        // Target the mob that hurt our owner
        wolf.setTarget(lastHurtByMob);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        // Continue as long as target is alive
        LivingEntity target = wolf.getTarget();
        return target != null && target.isAlive() && super.canContinueToUse();
    }
}
