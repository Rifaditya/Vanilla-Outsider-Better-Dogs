package net.vanillaoutsider.betterdogs.mixin;

import net.dasik.social.core.GlobalSocialSystem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Unique
    private int betterdogs$cleanTicks = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$onTick(CallbackInfo ci) {
        GlobalSocialSystem.pulse((ServerLevel) (Object) this);
        
        if (++betterdogs$cleanTicks >= 100) {
            betterdogs$cleanTicks = 0;
            ServerLevel level = (ServerLevel) (Object) this;
            for (Entity entity : level.getAllEntities()) {
                if (entity.entityTags().contains("betterdogs:seat") && entity.getPassengers().isEmpty()) {
                    entity.discard();
                }
            }
        }
    }
}
