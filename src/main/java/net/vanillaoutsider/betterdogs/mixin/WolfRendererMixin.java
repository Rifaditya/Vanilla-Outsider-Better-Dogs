// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client-side renderer mixin for Wolf to apply Subdued Mood Posture:
 * tucks tail angle downward and lowers head pitch slightly.
 */
@Environment(EnvType.CLIENT)
@Mixin(WolfRenderer.class)
public abstract class WolfRendererMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void betterdogs$onExtractRenderState(Wolf entity, WolfRenderState state, float partialTicks, CallbackInfo ci) {
        if (entity instanceof WolfExtensions ext) {
            if (ext.betterdogs$isSubdued()) {
                // Adjust tail downward (subdued mood)
                state.tailAngle = 0.10F * (float) Math.PI;
                // Lower head pitch slightly (~12 degrees down)
                state.xRot += 12.0F;
            } else if (ext.betterdogs$isSpectatorWagging()) {
                // High tail with smooth partial-tick wagging oscillation
                float wag = net.minecraft.util.Mth.cos((entity.tickCount + partialTicks) * 0.6F) * 0.08F;
                state.tailAngle = (0.60F + wag) * (float) Math.PI;
            }
        }
    }
}
