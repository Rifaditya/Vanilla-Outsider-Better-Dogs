package net.vanillaoutsider.betterdogs.ai

import net.minecraft.world.entity.ai.goal.AvoidEntityGoal
import net.minecraft.world.entity.animal.wolf.Wolf
import net.minecraft.world.entity.monster.Creeper

/**
 * AI Goal for tamed wolves to flee from hissing creepers.
 * Only triggers when creeper is about to explode.
 */
class FleeCreeperGoal(
    wolf: Wolf
) : AvoidEntityGoal<Creeper>(
    wolf,
    Creeper::class.java,
    6.0f,  // Flee distance
    1.0,   // Walk speed multiplier
    1.5,   // Sprint speed multiplier
    { creeper -> (creeper as? Creeper)?.isIgnited == true || (creeper as? Creeper)?.swellDir ?: 0 > 0 }
) {
    
    private val wolf: Wolf = wolf
    
    override fun canUse(): Boolean {
        // Only for tamed wolves
        if (!wolf.isTame) return false
        
        return super.canUse()
    }
}
