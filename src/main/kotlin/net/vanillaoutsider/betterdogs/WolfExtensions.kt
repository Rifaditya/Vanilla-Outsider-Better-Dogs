package net.vanillaoutsider.betterdogs

import net.minecraft.world.entity.animal.wolf.Wolf

/**
 * Extension interface for Wolf entity to access personality data.
 * Implemented via mixin.
 */
interface WolfExtensions {
    /**
     * Get the wolf's personality (only meaningful for tamed wolves)
     */
    fun `betterdogs$getPersonality`(): WolfPersonality
    
    /**
     * Set the wolf's personality (called on tame)
     */
    fun `betterdogs$setPersonality`(personality: WolfPersonality)
    
    /**
     * Check if this wolf has been assigned a personality
     */
    fun `betterdogs$hasPersonality`(): Boolean
    
    /**
     * Get the last time this wolf took damage (for passive healing)
     */
    fun `betterdogs$getLastDamageTime`(): Int
    
    /**
     * Update the last damage time
     */
    fun `betterdogs$setLastDamageTime`(time: Int)
}

/**
 * Extension function to safely cast Wolf to WolfExtensions
 */
fun Wolf.asExtended(): WolfExtensions = this as WolfExtensions
