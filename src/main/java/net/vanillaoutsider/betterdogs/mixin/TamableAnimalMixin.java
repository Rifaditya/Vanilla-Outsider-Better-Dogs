// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.util.WolfParticleHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TamableAnimal.class)
public abstract class TamableAnimalMixin {

    @Inject(method = "tame", at = @At("TAIL"))
    private void betterdogs$onTame(Player player, CallbackInfo ci) {
        if ((Object) this instanceof Wolf wolf) {
            if (wolf instanceof WolfExtensions ext) {
                WolfPersonality personality = ext.betterdogs$getPersonality();
                WolfParticleHandler.playTameParticles(wolf, personality);

                if (player != null && !wolf.getCommandSenderWorld().isClientSide()) {
                    String colorCode = switch (personality) {
                        case AGGRESSIVE -> "§c";
                        case PACIFIST -> "§a";
                        case NORMAL -> "§e";
                    };
                    String message = String.format("%sTamed a %s Wolf!", colorCode, personality.name());
                    player.displayClientMessage(Component.literal(message), true);
                }
            }
        }
    }
}
