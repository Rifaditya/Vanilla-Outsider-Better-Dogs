package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

/**
 * Wild wolf hunting behavior.
 * Only hunts prey when health is below threshold.
 */
public class WildWolfHuntGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    private final Wolf wolf;

    public WildWolfHuntGoal(Wolf wolf, Class<T> targetType, boolean checkSight,
            TargetingConditions.Selector targetPredicate) {
        super(wolf, targetType, 10, checkSight, false, targetPredicate);
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        // Only applies to wild wolves
        if (wolf.isTame() || wolf.isBaby())
            return false;

        // Only hunt if health is below configurable threshold
        float threshold = BetterDogsConfig.get().wildHuntHealthThreshold;
        if (wolf.getHealth() >= wolf.getMaxHealth() * threshold)
            return false;

        return super.canUse();
    }
}
