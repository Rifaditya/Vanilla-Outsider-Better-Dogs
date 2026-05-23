// Verified against: ServerLevelMixin.java (26.1.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.dasik.social.core.GlobalSocialSystem;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$onTick(CallbackInfo ci) {
        GlobalSocialSystem.pulse((ServerLevel) (Object) this);
    }
}
