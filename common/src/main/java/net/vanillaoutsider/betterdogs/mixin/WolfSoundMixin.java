// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class WolfSoundMixin {

    @Inject(method = "getVoicePitch", at = @At("RETURN"), cancellable = true)
    private void betterdogs$modulateWolfVoicePitch(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof Wolf wolf) {
            float basePitch = cir.getReturnValue();
            cir.setReturnValue(CanineGeneticsHelper.getModulatedVoicePitch(wolf, basePitch));
        }
    }
}
