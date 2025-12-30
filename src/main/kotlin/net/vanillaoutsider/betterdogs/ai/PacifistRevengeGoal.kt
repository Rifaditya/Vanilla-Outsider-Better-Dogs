package net.vanillaoutsider.betterdogs.ai

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.animal.wolf.Wolf
import net.vanillaoutsider.betterdogs.WolfPersonality
import net.vanillaoutsider.betterdogs.asExtended

/**
 * AI Goal for Pacifist personality wolves.
 * Only attacks when the owner takes damage from a mob.
 */
class PacifistRevengeGoal(
    private val wolf: Wolf
) : HurtByTargetGoal(wolf) {
    
    override fun canUse(): Boolean {
        // Only for tamed wolves with Pacifist personality
        if (!wolf.isTame) return false
        
        val ext = wolf.asExtended()
        if (ext.`betterdogs$getPersonality`() != WolfPersonality.PACIFIST) return false
        
        // Check if owner was recently hurt
        val owner = wolf.owner as? LivingEntity ?: return false
        val lastHurtByMob = owner.lastHurtByMob ?: return false
        
        // Target the mob that hurt our owner
        wolf.target = lastHurtByMob
        return true
    }
    
    override fun canContinueToUse(): Boolean {
        // Continue as long as target is alive
        val target = wolf.target ?: return false
        return target.isAlive && super.canContinueToUse()
    }
}
