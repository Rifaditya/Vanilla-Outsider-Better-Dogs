// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
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
        Level level = self.getCommandSenderWorld();
        if (level == null || level.isClientSide) {
            return;
        }

        if (self instanceof Wolf thisWolf && thisWolf.isTame()) {
            double xa = entity.getX() - thisWolf.getX();
            double za = entity.getZ() - thisWolf.getZ();
            boolean dangerous = false;

            if (BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_CLIFF_SAFETY, true)) {
                dangerous = WolfCliffSafetyHelper.isDangerousPushDirection(thisWolf, -xa, -za);
            }

            if (dangerous || thisWolf.isOrderedToSit()) {
                ci.cancel();
                return;
            }
        }

        if (entity instanceof Wolf otherWolf && otherWolf.isTame()) {
            double xa = otherWolf.getX() - self.getX();
            double za = otherWolf.getZ() - self.getZ();
            boolean dangerous = false;

            if (BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_CLIFF_SAFETY, true)) {
                dangerous = WolfCliffSafetyHelper.isDangerousPushDirection(otherWolf, xa, za);
            }

            if (dangerous || otherWolf.isOrderedToSit()) {
                ci.cancel();
            }
        }
    }
}
