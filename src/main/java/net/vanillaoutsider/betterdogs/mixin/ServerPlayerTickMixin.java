// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: ServerPlayer.java (26.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.vanillaoutsider.betterdogs.util.WolfCatchUpHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerTickMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$onPlayerTick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        // Run catch-up check once per second (20 ticks)
        if (player.tickCount % 20 == 0) {
            WolfCatchUpHelper.checkAndPerformCatchUp(player);
        }
    }
}
