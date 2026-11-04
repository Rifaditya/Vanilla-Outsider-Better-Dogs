// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfCliffSafetyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class WolfPushMixin {

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onPushSafety(Entity entity, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        Level level = self.level();
        if (level == null || level.isClientSide()) {
            return;
        }

        if (self instanceof Wolf thisWolf && thisWolf.isTame()) {
            if (thisWolf.isOrderedToSit()) {
                ci.cancel();
                return;
            }

            // Minimum separation guard: if entities are overlapping (< 1.5 blocks),
            // always allow vanilla collision physics to push them apart (BL-BD-037).
            double distSq = thisWolf.distanceToSqr(entity);
            if (distSq >= 2.25 && BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_CLIFF_SAFETY, true)) {
                double xa = entity.getX() - thisWolf.getX();
                double za = entity.getZ() - thisWolf.getZ();
                if (WolfCliffSafetyHelper.isDangerousPushDirection(thisWolf, -xa, -za)) {
                    ci.cancel();
                    return;
                }
            }
        }

        if (entity instanceof Wolf otherWolf && otherWolf.isTame()) {
            if (otherWolf.isOrderedToSit()) {
                ci.cancel();
                return;
            }

            double distSq = self.distanceToSqr(otherWolf);
            if (distSq >= 2.25 && BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_CLIFF_SAFETY, true)) {
                double xa = otherWolf.getX() - self.getX();
                double za = otherWolf.getZ() - self.getZ();
                if (WolfCliffSafetyHelper.isDangerousPushDirection(otherWolf, xa, za)) {
                    ci.cancel();
                }
            }
        }
    }
}
