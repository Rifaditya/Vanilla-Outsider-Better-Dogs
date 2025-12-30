package net.vanillaoutsider.betterdogs.ai

import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.animal.wolf.Wolf
import java.util.EnumSet

/**
 * AI Goal for wild wolves to hunt prey only when hurt.
 * Replaces vanilla always-hunting behavior.
 */
class HuntWhenHurtGoal(
    private val wolf: Wolf
) : Goal() {
    
    companion object {
        private const val HEALTH_THRESHOLD = 0.5f // 50% health
        
        // Prey animal types by name (safer than class references)
        private val PREY_TYPES = setOf("sheep", "rabbit", "chicken")
    }
    
    init {
        flags = EnumSet.of(Flag.TARGET)
    }
    
    override fun canUse(): Boolean {
        // Only for wild (non-tamed) wolves
        if (wolf.isTame) return false
        
        // Only hunt when health is low
        val healthPercent = wolf.health / wolf.maxHealth
        if (healthPercent >= HEALTH_THRESHOLD) return false
        
        return true
    }
    
    override fun start() {
        // Find nearest prey by checking entity type name
        val nearestPrey = wolf.level().getEntitiesOfClass(
            Animal::class.java,
            wolf.boundingBox.inflate(16.0)
        ) { entity ->
            val typeName = entity.type.descriptionId.lowercase()
            PREY_TYPES.any { typeName.contains(it) }
        }.minByOrNull { wolf.distanceTo(it) }
        
        if (nearestPrey != null) {
            wolf.target = nearestPrey
        }
    }
    
    override fun canContinueToUse(): Boolean {
        if (wolf.isTame) return false
        
        // Stop hunting if we're healed enough
        val healthPercent = wolf.health / wolf.maxHealth
        if (healthPercent >= 0.8f) return false
        
        // Continue if target is alive
        val target = wolf.target ?: return false
        return target.isAlive
    }
    
    override fun stop() {
        wolf.target = null
    }
}
