package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
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
 * Mixin to prevent baby wolves from attacking what hurts the owner.
 */
@Mixin(OwnerHurtByTargetGoal.class)
public abstract class OwnerHurtByTargetGoalMixin {

    @Shadow
    @Final
    private TamableAnimal tameAnimal;

    /**
     * Prevent this goal from activating for non-aggressive baby wolves,
     * AND prevent adults from defending the owner against their own pet babies (Mercy Rule).
     */
    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void betterdogs$blockForBabies(CallbackInfoReturnable<Boolean> cir) {
        if (tameAnimal instanceof Wolf wolf) {
            WolfExtensions ext = (WolfExtensions) wolf;
            
            // Check what hurt the owner
            if (wolf.getOwner() != null && wolf.getOwner().getLastHurtByMob() instanceof Wolf offendingWolf) {
                // If the owner was hit by their own pet baby, don't let OTHER pets (adult or baby) target it via this goal.
                // This prevents the "owner hits baby -> adults kill baby" or "baby hits owner -> adults kill baby" behavior
                // for adults that aren't AGGRESSIVE and explicitly using InterventionGoal.
                if (offendingWolf.isBaby() && offendingWolf.getOwner() != null && offendingWolf.getOwner().equals(wolf.getOwner())) {
                    cir.setReturnValue(false);
                    return;
                }
            }

            boolean isAggressive = ext.betterdogs$hasPersonality() && ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE;
            
            // Baby wolves don't attack unless they are aggressive
            if (wolf.isBaby() && !isAggressive) {
                cir.setReturnValue(false);
            }
        }
    }
}
