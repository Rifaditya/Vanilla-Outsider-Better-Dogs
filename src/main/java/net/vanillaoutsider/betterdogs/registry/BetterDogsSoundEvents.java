// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * Dedicated single-purpose registry for Better Dogs custom SoundEvents in Minecraft 1.20.1.
 */
public class BetterDogsSoundEvents {

    public static final ResourceLocation WOLF_HOWL_ID = new ResourceLocation("betterdogs", "entity.wolf.howl");
    public static final SoundEvent WOLF_HOWL = SoundEvent.createVariableRangeEvent(WOLF_HOWL_ID);

    public static void registerSoundEvents() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, WOLF_HOWL_ID, WOLF_HOWL);
    }
}
