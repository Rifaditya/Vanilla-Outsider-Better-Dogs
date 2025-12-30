package net.vanillaoutsider.betterdogs

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
    
    /** Only attacks when owner takes damage */
    PACIFIST(2);
    
    companion object {
        /**
         * Randomly select a personality with the following distribution:
         * - AGGRESSIVE: 33%
         * - PACIFIST: 33%
         * - NORMAL: 34%
         */
        fun random(): WolfPersonality {
            val roll = Random.nextInt(100)
            return when {
                roll < 33 -> AGGRESSIVE
                roll < 66 -> PACIFIST
                else -> NORMAL
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
