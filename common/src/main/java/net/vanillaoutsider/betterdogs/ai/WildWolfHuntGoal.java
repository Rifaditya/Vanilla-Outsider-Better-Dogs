// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs.ai;

import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Wild wolf hunting behavior.
 * Only hunts prey when health is below threshold.
 */
public class WildWolfHuntGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    private final Wolf wolf;

    public WildWolfHuntGoal(Wolf wolf, Class<T> targetType, boolean checkSight,
            Predicate<LivingEntity> targetPredicate) {
        super(wolf, targetType, 10, checkSight, false, targetPredicate);
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        // Only applies to wild wolves
        if (wolf.isTame() || wolf.isBaby())
            return false;

        // Only hunt if health is below configurable threshold
        int threshold = BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_WILD_HUNT_HEALTH_THRESHOLD, 50);
        if (wolf.getHealth() >= wolf.getMaxHealth() * (threshold / 100.0f))
            return false;

        return super.canUse();
    }
}
