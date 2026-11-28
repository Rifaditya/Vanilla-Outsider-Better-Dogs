// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.mixin;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Periodically sweeps orphaned Better Dogs seat entities from loaded server levels.
 */
@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Unique
    private int betterdogs$cleanTicks = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$onTick(CallbackInfo ci) {
        if (++this.betterdogs$cleanTicks < 100) {
            return;
        }
        this.betterdogs$cleanTicks = 0;
        ServerLevel level = (ServerLevel) (Object) this;
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
