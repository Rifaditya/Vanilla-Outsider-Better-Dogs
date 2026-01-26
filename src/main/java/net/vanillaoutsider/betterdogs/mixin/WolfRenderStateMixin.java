package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.vanillaoutsider.betterdogs.render.WolfRenderStateExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WolfRenderState.class)
public class WolfRenderStateMixin implements WolfRenderStateExtensions {
    @Unique
    private float betterdogs$socialScale = 1.0f;

    @Override
    public float betterdogs$getSocialScale() {
        return this.betterdogs$socialScale;
    }

    @Override
    public void betterdogs$setSocialScale(float scale) {
        this.betterdogs$socialScale = scale;
    }
}
