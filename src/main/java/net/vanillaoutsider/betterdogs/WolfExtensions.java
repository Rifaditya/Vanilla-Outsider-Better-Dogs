package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.WolfPersonality;

/**
 * Extension interface for Wolf entity to access personality data.
 * Implemented via mixin.
 */
public interface WolfExtensions {
    /**
     * Get the wolf's personality (only meaningful for tamed wolves)
     */
    WolfPersonality betterdogs$getPersonality();

    /**
     * Set the wolf's personality (called on tame)
     */
    void betterdogs$setPersonality(WolfPersonality personality);

    /**
     * Check if this wolf has been assigned a personality
     */
    boolean betterdogs$hasPersonality();

    /**
     * Get the last time this wolf took damage (for passive healing)
     */
    int betterdogs$getLastDamageTime();

    /**
     * Update the last damage time
     */
    void betterdogs$setLastDamageTime(int time);
}
