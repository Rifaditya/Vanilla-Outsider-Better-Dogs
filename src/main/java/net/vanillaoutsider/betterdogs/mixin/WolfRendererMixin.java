package net.vanillaoutsider.betterdogs.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.model.animal.wolf.WolfModel;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.render.WolfRenderStateExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfRenderer.class)
public abstract class WolfRendererMixin extends AgeableMobRenderer<Wolf, WolfRenderState, WolfModel> {

    public WolfRendererMixin(EntityRendererProvider.Context context, WolfModel adultModel, WolfModel babyModel, float shadowRadius) {
        super(context, adultModel, babyModel, shadowRadius);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/wolf/Wolf;Lnet/minecraft/client/renderer/entity/state/WolfRenderState;F)V", at = @At("TAIL"))
    private void betterdogs$onExtractRenderState(Wolf entity, WolfRenderState state, float partialTicks, CallbackInfo ci) {
        if (entity instanceof WolfExtensions ext && state instanceof WolfRenderStateExtensions stateExt) {
            stateExt.betterdogs$setSocialScale(ext.betterdogs$getSocialScale());
        }
    }

    @Override
    protected void scale(WolfRenderState state, PoseStack poseStack) {
        super.scale(state, poseStack);
        if (state instanceof WolfRenderStateExtensions stateExt) {
            float scale = stateExt.betterdogs$getSocialScale();
            if (scale != 1.0f) {
                poseStack.scale(scale, scale, scale);
            }
        }
    }
}
