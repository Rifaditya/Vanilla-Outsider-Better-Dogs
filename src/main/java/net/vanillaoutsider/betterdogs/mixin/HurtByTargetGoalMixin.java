package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Muzzle Logic: Prevents "Call for Help" broadcast when a baby wolf is being disciplined.
 * This ensures domestic disputes don't wake up the whole pack.
 */
@Mixin(HurtByTargetGoal.class)
public abstract class HurtByTargetGoalMixin extends TargetGoal {

    public HurtByTargetGoalMixin(PathfinderMob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @Inject(method = "alertOthers", at = @At("HEAD"), cancellable = true)
    protected void betterdogs$onAlertOthers(CallbackInfo ci) {
        // "The Muzzle": If this is a domestic dispute, SILENCE the victim.
        if (this.mob instanceof Wolf baby && baby.isTame()) {
            LivingEntity attacker = baby.getLastHurtByMob();
            
            // Check if attacker is an Adult Wolf with the same owner
            if (attacker instanceof Wolf adult && adult.isTame() && !adult.isBaby()) {
                if (baby.getOwner() != null && adult.getOwner() != null 
                    && baby.getOwner().equals(adult.getOwner())) {
                    // It's a family matter. Keep it quiet.
                    ci.cancel();
                }
            }
        }
    }
}
