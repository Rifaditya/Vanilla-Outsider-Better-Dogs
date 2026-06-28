// Verified against: Wolf.java (26.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
/*
 * Copyright (c) 2026 Vanilla Outsider
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.util.WolfCombatHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for Wolf entity to handle combat hooks, extracted from WolfMixin 
 * to adhere strictly to the 300 LOC limit.
 */
@Mixin(Wolf.class)
public abstract class WolfCombatMixin {

    @Inject(method = "die", at = @At("HEAD"))
    private void betterdogs$onDie(DamageSource source, CallbackInfo ci) {
        WolfCombatHooks.onDeath((Wolf) (Object) this, source);
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onActuallyHurt(ServerLevel level, DamageSource source, float amount, CallbackInfo ci) {
        if (WolfCombatHooks.onActuallyHurt((Wolf) (Object) this, source, amount)) {
            ci.cancel();
        }
    }

    @Inject(method = "wantsToAttack", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onWantsToAttack(LivingEntity target, LivingEntity owner,
            CallbackInfoReturnable<Boolean> cir) {
        Boolean result = WolfCombatHooks.wantsToAttack((Wolf) (Object) this, target, owner);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }
}
