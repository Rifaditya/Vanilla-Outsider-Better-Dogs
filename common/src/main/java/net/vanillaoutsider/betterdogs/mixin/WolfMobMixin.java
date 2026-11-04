// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.PackAudienceHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class WolfMobMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void betterdogs$gatekeeperSetTarget(@Nullable LivingEntity newTarget, CallbackInfo ci) {
        if ((Object)this instanceof Wolf wolf) {
            WolfExtensions wolfExt = (WolfExtensions) wolf;

            // Pack Civil War Hard Gatekeeper (BL-BD-035):
            // A tamed wolf can NEVER acquire an allied co-owned wolf as an attack target,
            // unless executing an active Blood Feud vendetta against that specific nemesis.
            if (newTarget instanceof Wolf targetWolf && wolf.isTame() && targetWolf.isTame()) {
                if (PackAudienceHelper.isCoOwned(wolf, targetWolf)) {
                    boolean isNemesis = wolfExt.betterdogs$hasBloodFeud()
                            && wolfExt.betterdogs$getBloodFeudTarget().equals(targetWolf.getStringUUID());
                    if (!isNemesis) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}
