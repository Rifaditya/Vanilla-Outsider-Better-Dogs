package net.vanillaoutsider.betterdogs.ai

import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.wolf.Wolf
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import java.util.function.Predicate

/**
 * Wild wolf hunting behavior.
 * Only hunts prey when health is below 50%.
 */
class WildWolfHuntGoal<T : LivingEntity>(
    private val wolf: Wolf,
    targetType: Class<T>,
    checkSight: Boolean,
    targetPredicate: TargetingConditions.Selector? = null
) : NearestAttackableTargetGoal<T>(wolf, targetType, 10, checkSight, false, targetPredicate) {

    override fun canUse(): Boolean {
        // Only applies to wild wolves
        if (wolf.isTame) return false

        // Only hunt if health is below 50%
        if (wolf.health >= wolf.maxHealth * 0.5f) return false

        return super.canUse()
    }
}
