package net.vanillaoutsider.betterdogs.api;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.WolfPersonality;

/**
 * Public API for other mods to interact with Better Dogs.
 */
public class BetterDogsAPI {

    /**
     * Checks if a wolf has an assigned personality.
     */
    public static boolean hasPersonality(Wolf wolf) {
        return WolfPersistentData.hasPersistedPersonality(wolf);
    }

    /**
     * Gets the personality of a wolf.
     * Returns null if not initialized or not a Better Dog.
     */
    public static WolfPersonality getPersonality(Wolf wolf) {
        if (!hasPersonality(wolf))
            return null;
        return WolfPersistentData.getPersistedPersonality(wolf);
    }

    /**
     * Forcefully sets a wolf's personality.
     * Note: This will re-apply stats on the next tick.
     */
    public static void setPersonality(Wolf wolf, WolfPersonality personality) {
        WolfPersistentData.setPersistedPersonality(wolf, personality);

        // Update via interface if possible
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setPersonality(personality);
        }
    }
}
