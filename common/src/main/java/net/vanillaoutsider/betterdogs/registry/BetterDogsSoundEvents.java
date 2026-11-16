// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

/**
 * Dedicated single-purpose registry for Better Dogs custom SoundEvents in Minecraft 1.21.11.
 * Sound events are kept as static identifiers and direct holders without global registry pollution
 * to maintain strict vanilla client compatibility on dedicated servers (BL-BD-064).
 */
public class BetterDogsSoundEvents {

    public static final Identifier WOLF_HOWL_ID = Identifier.fromNamespaceAndPath("betterdogs", "entity.wolf.howl");
    public static final SoundEvent WOLF_HOWL = SoundEvent.createVariableRangeEvent(WOLF_HOWL_ID);

    public static void registerSoundEvents() {
        // No-op for vanilla client compatibility (BL-BD-064)
    }
}
