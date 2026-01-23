package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to prevent PACIFIST wolves from attacking what the owner attacks.
 * This makes PACIFIST wolves ONLY attack to defend the owner when the owner is
 * hurt.
 */
@Mixin(OwnerHurtTargetGoal.class)
public abstract class OwnerHurtTargetGoalMixin {

    @Shadow
    @Final
    private TamableAnimal tameAnimal;

    /**
     * Prevent this goal from activating for PACIFIST wolves.
     */
    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void betterdogs$blockForPacifistAndBabies(CallbackInfoReturnable<Boolean> cir) {
        if (tameAnimal instanceof Wolf wolf) {
            WolfExtensions ext = (WolfExtensions) wolf;
            boolean isAggressive = ext.betterdogs$hasPersonality() && ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE;
            
            // PACIFIST wolves don't attack what owner attacks
            if (ext.betterdogs$hasPersonality() && ext.betterdogs$getPersonality() == WolfPersonality.PACIFIST) {
                cir.setReturnValue(false);
                return;
            }

            // Baby wolves don't attack unless they are aggressive
            if (wolf.isBaby() && !isAggressive) {
                cir.setReturnValue(false);
            }
        }
    }
}
