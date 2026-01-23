package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {
    @Inject(method = "doHurtTarget", at = @At("RETURN"), remap = false)
    private void betterdogs$afterMobHurt(ServerLevel level, Entity target, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof Wolf wolf && (Object)this instanceof WolfExtensions wolfExt) {
            if (cir.getReturnValue() && wolfExt.betterdogs$getStrikesRemaining() > 0) {
                wolfExt.betterdogs$setStrikesRemaining(wolfExt.betterdogs$getStrikesRemaining() - 1);
                if (wolfExt.betterdogs$getStrikesRemaining() <= 0) {
                    wolf.setTarget(null);
                }
            }
        }
    }
}
