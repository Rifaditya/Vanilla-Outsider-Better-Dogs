package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.jspecify.annotations.Nullable;

@Mixin(Mob.class)
public abstract class WolfMobMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void betterdogs$gatekeeperSetTarget(@Nullable LivingEntity newTarget, CallbackInfo ci) {
        if ((Object)this instanceof Wolf) {
            WolfExtensions wolfExt = (WolfExtensions) this;
            
            // If this wolf is in SOCIAL MODE
            if (wolfExt.betterdogs$isSocialModeActive()) {
                // RULE: Only the Social Target is allowed to pass.
                if (newTarget != wolfExt.betterdogs$getSocialTarget()) {
                    // DENY (Master Brain is Dormant. Ignore "Enemies", "Attackers", "Owner Hitting something")
                    ci.cancel(); 
                }
            }
        }
    }
}
