// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
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
        if (this.mob instanceof Wolf victim) {
            LivingEntity attacker = victim.getLastHurtByMob();
            if (net.vanillaoutsider.betterdogs.util.AdultDisciplineHelper.shouldSilenceAlert(victim, attacker)) {
                ci.cancel();
            }
        }
    }
}
