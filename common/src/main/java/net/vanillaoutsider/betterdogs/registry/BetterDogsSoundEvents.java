// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

/**
 * Dedicated single-purpose registry for Better Dogs custom SoundEvents in Minecraft 1.21.11.
 */
public class BetterDogsSoundEvents {

    public static final Identifier WOLF_HOWL_ID = Identifier.fromNamespaceAndPath("betterdogs", "entity.wolf.howl");
    public static final SoundEvent WOLF_HOWL = SoundEvent.createVariableRangeEvent(WOLF_HOWL_ID);

    public static void registerSoundEvents() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, WOLF_HOWL_ID, WOLF_HOWL);
    }
}
