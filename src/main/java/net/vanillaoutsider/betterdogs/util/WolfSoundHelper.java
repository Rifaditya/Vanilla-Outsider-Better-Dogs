// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant.WolfSoundSet;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Dedicated single-purpose helper managing wolf ambient vocalizations and health-dependent sound cues.
 */
public class WolfSoundHelper {

    public static void handleAmbientSound(Wolf wolf, WolfSoundSet soundSet, CallbackInfoReturnable<SoundEvent> cir) {
        if (wolf == null || soundSet == null || cir == null) {
            return;
        }
        if (wolf.isAngry()) {
            cir.setReturnValue(soundSet.growlSound().value());
            return;
        }
        if (wolf.getRandom().nextInt(3) == 0) {
            if (wolf.isTame() && wolf.getHealth() < wolf.getMaxHealth() * 0.5f) {
                cir.setReturnValue(soundSet.whineSound().value());
                return;
            }
            cir.setReturnValue(soundSet.pantSound().value());
            return;
        }
        cir.setReturnValue(soundSet.ambientSound().value());
    }
}
