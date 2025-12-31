package net.vanillaoutsider.betterdogs.api

import net.minecraft.world.entity.animal.wolf.Wolf
import net.vanillaoutsider.betterdogs.WolfPersonality
import net.vanillaoutsider.betterdogs.WolfExtensions
import net.vanillaoutsider.betterdogs.hasPersistedPersonality
import net.vanillaoutsider.betterdogs.getPersistedPersonality
import net.vanillaoutsider.betterdogs.setPersistedPersonality

/**
 * Public API for other mods to interact with Better Dogs.
 */
object BetterDogsAPI {

    /**
     * Checks if a wolf has an assigned personality.
     */
    @JvmStatic
    fun hasPersonality(wolf: Wolf): Boolean {
        return wolf.hasPersistedPersonality()
    }

    /**
     * Gets the personality of a wolf.
     * Returns null if not initialized or not a Better Dog.
     */
    @JvmStatic
    fun getPersonality(wolf: Wolf): WolfPersonality? {
        if (!hasPersonality(wolf)) return null
        return wolf.getPersistedPersonality()
    }

    /**
     * Forcefully sets a wolf's personality.
     * Note: This will re-apply stats on the next tick.
     */
    @JvmStatic
    fun setPersonality(wolf: Wolf, personality: WolfPersonality) {
        wolf.setPersistedPersonality(personality)
        // We need to trigger the mixin update. 
        // We can do this by casting to our interface if it was available, 
        // or by letting the tick handler handle it (it checks on load).
        // Since we can't easily force an immediate update without exposing internals,
        // we'll rely on the tick handler or re-taming logic.
        
        // However, to be nice, let's try to update if possible
        if (wolf is WolfExtensions) {
            (wolf as WolfExtensions).`betterdogs$setPersonality`(personality)
            // Ideally we would call applyStats but that's private.
            // But setting the persistent data is the most important part for other mods.
        }
    }
}
