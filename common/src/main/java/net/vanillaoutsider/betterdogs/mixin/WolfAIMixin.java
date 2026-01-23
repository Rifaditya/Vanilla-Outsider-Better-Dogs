package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public abstract class WolfAIMixin {

    /**
     * Add knockback resistance to wolves (50% reduction)
     */
    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void betterdogs$addKnockbackResistance(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.getReturnValue().add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }
}
