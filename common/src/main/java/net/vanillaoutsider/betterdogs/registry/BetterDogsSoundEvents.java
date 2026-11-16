// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * Dedicated single-purpose registry for Better Dogs custom SoundEvents in Minecraft 1.21.1.
 */
public class BetterDogsSoundEvents {

    public static final ResourceLocation WOLF_HOWL_ID = ResourceLocation.fromNamespaceAndPath("betterdogs", "entity.wolf.howl");
    public static final SoundEvent WOLF_HOWL = SoundEvent.createVariableRangeEvent(WOLF_HOWL_ID);

    public static void registerSoundEvents() {
        // Zero-registration on dedicated servers: custom sound events are not registered in the shared
        // BuiltInRegistries to protect unmodded vanilla clients from registry mismatch disconnection.
    }
}
