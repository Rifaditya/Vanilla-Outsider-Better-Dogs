package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TamableAnimal.class)
public abstract class TamableAnimalMixin {

    /**
     * Rules of Engagement:
     * 1. Blood Feud: Always ALLOW attacking nemesis.
     * 2. Baby Retaliation: Allow Aggressive Baby to attack Owner (once).
     * 3. Adult Correction: Allow Aggressive Adult to attack Baby (same owner).
     * 4. NO GANG-UP: Disallow all other friendly fire between same-owner wolves.
     */
    @Inject(method = "canAttack", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onCanAttack(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof Wolf wolf && (Object)this instanceof WolfExtensions wolfExt) {
            
            // Priority 1: Blood Feud (Always allow attacking nemesis)
            if (target instanceof Wolf targetWolf && wolfExt.betterdogs$hasBloodFeud()) {
                if (wolfExt.betterdogs$getBloodFeudTarget().equals(targetWolf.getStringUUID())) {
                    cir.setReturnValue(true);
                    return;
                }
            }

            // Priority 2: Baby Retaliation (Baby -> Owner)
            if (wolf.isBaby() && wolf.isTame() && wolfExt.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE) {
                if (target.equals(wolf.getOwner())) {
                    cir.setReturnValue(true);
                    return;
                }
            }

            // Priority 3: Adult Correction (Adult -> Baby)
            if (!wolf.isBaby() && wolf.isTame() && wolfExt.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE) {
                if (target instanceof Wolf targetWolf && targetWolf.isBaby() && targetWolf.isTame()) {
                     LivingEntity myOwner = wolf.getOwner();
                     LivingEntity targetOwner = targetWolf.getOwner();
                     if (myOwner != null && targetOwner != null && myOwner.equals(targetOwner)) {
                         cir.setReturnValue(true);
                         return;
                     }
                }
            }
            
            // Priority 4: Prevent "Gang-Up" (Block all other Wolf -> Wolf same owner violence)
            // This fixes the issue where bystanders (babies/adults) attack the participants of a dispute.
            // NOTE: The "Muzzle" (HurtByTargetGoalMixin) is the Primary Guard. This is the Secondary/Fallback Guard.
            if (target instanceof Wolf targetWolf && targetWolf.isTame()) {
                LivingEntity myOwner = wolf.getOwner();
                LivingEntity targetOwner = targetWolf.getOwner();
                if (wolf.isTame() && myOwner != null && targetOwner != null && myOwner.equals(targetOwner)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
