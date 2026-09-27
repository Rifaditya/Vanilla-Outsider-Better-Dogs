// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.scheduler.events;

import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.TickContext;
import net.minecraft.world.entity.LivingEntity;
import net.vanillaoutsider.betterdogs.WolfExtensions;

public class RetaliationDogEvent implements SocialEvent {
    public static final String ID = "retaliation";
    private int tickCount = 0;
    private final LivingEntity target;

    public RetaliationDogEvent(LivingEntity target) {
        this.target = target;
    }

    public RetaliationDogEvent() {
        this.target = null;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int getPriorityValue() {
        return 20;
    }

    @Override
    public String getTrackId() {
        return "main";
    }

    @Override
    public boolean canPreempt(SocialEvent other) {
        return other.getPriorityValue() < 20;
    }

    @Override
    public void onStart(TickContext context) {
        this.tickCount = 0;
        if (this.target != null && context.entity().dasik$asEntity() instanceof WolfExtensions ext) {
            ext.betterdogs$setRetaliationTarget(this.target, 100);
        }
    }

    @Override
    public boolean tick(TickContext context) {
        this.tickCount++;
        if (this.target == null || !this.target.isAlive()) {
            return true;
        }

        if (context.entity().dasik$asEntity() instanceof WolfExtensions ext) {
            if (ext.betterdogs$getRetaliationTicks() <= 0) {
                ext.betterdogs$setRetaliationTarget(this.target, 100 - this.tickCount);
            }
        }

        return this.tickCount >= 100;
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        if (entity != null && entity.dasik$asEntity() instanceof WolfExtensions ext) {
            ext.betterdogs$setRetaliationTarget(null, 0);
        }
    }
}
