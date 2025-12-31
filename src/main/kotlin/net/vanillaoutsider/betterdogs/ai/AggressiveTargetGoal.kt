package net.vanillaoutsider.betterdogs.ai

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.wolf.Wolf
import net.minecraft.world.entity.monster.*
import net.vanillaoutsider.betterdogs.WolfPersonality
import net.vanillaoutsider.betterdogs.asExtended

/**
 * AI Goal for Aggressive personality wolves.
 * Automatically targets hostile mobs near the owner.
 */
class AggressiveTargetGoal(
    private val wolf: Wolf
) : NearestAttackableTargetGoal<Monster>(
    wolf,
    Monster::class.java,
    10,
    true,
    false,
    { target, level -> isValidTarget(wolf, target) }
) {
    companion object {
        private const val DETECTION_RANGE = 20.0
        private const val MAX_CHASE_DISTANCE = 20.0
        
        private fun isValidTarget(wolf: Wolf, target: LivingEntity): Boolean {
            // Must have an owner
            val owner = wolf.owner ?: return false
            
            // Target must be within range of owner
            if (target.distanceTo(owner) > DETECTION_RANGE) return false
            
            // Don't attack creepers
            if (target is Creeper) return false
            
            // Don't attack the warden (too dangerous)
            if (target.type.descriptionId.contains("warden")) return false
            
            return true
        }
    }
    
    override fun canUse(): Boolean {
        // Only for tamed wolves with Aggressive personality
        if (!wolf.isTame) return false
        
        val ext = wolf.asExtended()
        if (ext.`betterdogs$getPersonality`() != WolfPersonality.AGGRESSIVE) return false
        
        // Must have an owner nearby
        val owner = wolf.owner ?: return false
        if (wolf.distanceTo(owner) > MAX_CHASE_DISTANCE) return false
        
        return super.canUse()
    }
    
    override fun canContinueToUse(): Boolean {
        // Stop if too far from owner
        val owner = wolf.owner ?: return false
        if (wolf.distanceTo(owner) > MAX_CHASE_DISTANCE) return false
        
        return super.canContinueToUse()
    }
}
