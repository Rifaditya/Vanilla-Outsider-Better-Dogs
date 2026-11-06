// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.util.WolfTeleportHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "teleport", at = @At("HEAD"))
    private void betterdogs$onPlayerTeleport(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (player.isRemoved() || transition == null) {
            return;
        }
        ServerLevel oldLevel = player.level();
        Vec3 oldPos = player.position();
        ServerLevel newLevel = transition.newLevel();
        Vec3 newPos = transition.position();

        if (oldLevel != null && newLevel != null && oldPos != null && newPos != null) {
            WolfTeleportHelper.syncOwnerTeleport(player, oldLevel, oldPos, newLevel, newPos);
        }
    }
}
