// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.mixin;

import java.util.ArrayList;
import java.util.List;
import net.dasik.social.core.GlobalSocialSystem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;
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
        ServerLevel level = (ServerLevel) (Object) this;
        GlobalSocialSystem.pulse(level);

        if (++betterdogs$cleanTicks >= 100) {
            betterdogs$cleanTicks = 0;
            List<Entity> toDiscard = null;
            for (Entity entity : level.getAllEntities()) {
                if (DogCommandManager.shouldSweepOrphanedSeat(level, entity)) {
                    if (toDiscard == null) {
                        toDiscard = new ArrayList<>();
                    }
                    toDiscard.add(entity);
                }
            }
            if (toDiscard != null) {
                for (Entity orphan : toDiscard) {
                    orphan.discard();
                }
            }
        }
    }
}
