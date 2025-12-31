package net.vanillaoutsider.betterdogs

import net.vanillaoutsider.betterdogs.config.BetterDogsConfig
import kotlin.random.Random

/**
 * Wolf personality types that affect combat behavior.
 * Assigned randomly on tame and stored permanently in NBT.
 */
enum class WolfPersonality(val id: Int) {
    /** Vanilla behavior - attacks what owner attacks */
    NORMAL(0),
    
    /** Auto-attacks hostile mobs near owner */
    AGGRESSIVE(1),
    
    /** Won't attack when player attacks - only defends when player is hurt */
    PACIFIST(2);
    
    companion object {
        /**
         * Randomly select a personality with the following distribution:
         * - NORMAL: 60%
         * - AGGRESSIVE: 20%
         * - PACIFIST: 20%
         */
        fun random(): WolfPersonality {
            val config = BetterDogsConfig.get()
            val normal = config.tamingChanceNormal
            val aggressive = config.tamingChanceAggressive
            val pacifist = config.tamingChancePacifist
            val total = normal + aggressive + pacifist
            
            val roll = Random.nextInt(if (total > 0) total else 100)
            return when {
                roll < normal -> NORMAL
                roll < (normal + aggressive) -> AGGRESSIVE
                else -> PACIFIST
            }
        }
        
        /**
         * Get personality from NBT id value
         */
        fun fromId(id: Int): WolfPersonality {
            return entries.find { it.id == id } ?: NORMAL
        }
    }
}
