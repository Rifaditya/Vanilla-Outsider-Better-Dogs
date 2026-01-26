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
    
    // ========== 1.21.11 Parity Update Methods ==========

    fun `betterdogs$isSubmissive`(): Boolean
    fun `betterdogs$setSubmissive`(submissive: Boolean)

    fun `betterdogs$getBloodFeudTarget`(): String
    fun `betterdogs$setBloodFeudTarget`(targetUuid: String)
    fun `betterdogs$hasBloodFeud`(): Boolean

    fun `betterdogs$getLastMischiefDay`(): Long
    fun `betterdogs$setLastMischiefDay`(day: Long)

    fun `betterdogs$isBeingDisciplined`(): Boolean
    fun `betterdogs$setBeingDisciplined`(isBeingDisciplined: Boolean)

    // === SOCIAL CHANNEL SYSTEM ===
    
    enum class SocialAction {
        NONE,
        RETALIATION,
        DISCIPLINE,
        PLAY_FIGHT,
        ZOOMIES,
        HOWL,
        PLAY,
        WARNING
    }

    fun `betterdogs$setSocialState`(target: net.minecraft.world.entity.LivingEntity?, action: SocialAction, maxDurationTicks: Int)
    fun `betterdogs$getSocialTarget`(): net.minecraft.world.entity.LivingEntity?
    fun `betterdogs$getSocialAction`(): SocialAction
    fun `betterdogs$isSocialModeActive`(): Boolean
    fun `betterdogs$tickSocialMode`()

    // === SCHEDULER SYSTEM ===
    fun `betterdogs$getScheduler`(): net.vanillaoutsider.betterdogs.scheduler.WolfScheduler
    fun `betterdogs$tickScheduler`()
}

/**
 * Extension function to safely cast Wolf to WolfExtensions
 */
fun Wolf.asExtended(): WolfExtensions = this as WolfExtensions
