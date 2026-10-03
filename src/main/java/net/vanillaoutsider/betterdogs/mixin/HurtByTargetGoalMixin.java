// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.util.AdultDisciplineHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Muzzle Logic: Prevents "Call for Help" broadcast when a wolf is disciplined or receiving a hierarchy correction snap.
 * This ensures domestic disputes and play reprimands don't wake up the whole pack.
 */
@Mixin(HurtByTargetGoal.class)
public abstract class HurtByTargetGoalMixin extends TargetGoal {

    public HurtByTargetGoalMixin(PathfinderMob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @Inject(method = "alertOthers", at = @At("HEAD"), cancellable = true)
    protected void betterdogs$onAlertOthers(CallbackInfo ci) {
        // "The Muzzle": If this is a domestic dispute or hierarchy correction snap, SILENCE the victim.
        if (this.mob instanceof Wolf victim) {
            LivingEntity attacker = victim.getLastHurtByMob();
            if (AdultDisciplineHelper.shouldSilenceAlert(victim, attacker)) {
                ci.cancel();
            }
        }
    }
}
