// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Dedicated single-purpose client Mixin for rendering dynamic physical scaling (Runts, Giants, Genetics).
 */
@Mixin(LivingEntityRenderer.class)
public abstract class WolfRendererMixin {

    @Inject(method = "scale", at = @At("TAIL"))
    private void betterdogs$onScale(LivingEntity entity, PoseStack poseStack, float partialTickTime, CallbackInfo ci) {
        if (entity instanceof Wolf && entity instanceof WolfExtensions ext) {
            float scale = ext.betterdogs$getSocialScale();
            if (scale > 0.0f && Math.abs(scale - 1.0f) > 0.001f) {
                poseStack.scale(scale, scale, scale);
            }
        }
    }
}
