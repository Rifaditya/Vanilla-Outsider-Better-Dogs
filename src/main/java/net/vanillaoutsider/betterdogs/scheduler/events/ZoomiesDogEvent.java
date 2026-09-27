// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.scheduler.events;

import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.TickContext;
import net.vanillaoutsider.betterdogs.WolfExtensions;

public class ZoomiesDogEvent implements SocialEvent {
    public static final String ID = "zoomies";
    private int tickCount = 0;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int getPriorityValue() {
        return 10;
    }

    @Override
    public String getTrackId() {
        return "main";
    }

    @Override
    public boolean canPreempt(SocialEvent other) {
        return other.getPriorityValue() < 10;
    }

    @Override
    public void onStart(TickContext context) {
        this.tickCount = 0;
        if (context.entity().dasik$asEntity() instanceof WolfExtensions ext) {
            ext.betterdogs$setZoomiesTicks(240);
        }
    }

    @Override
    public boolean tick(TickContext context) {
        this.tickCount++;
        return this.tickCount >= 240;
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        if (entity != null && entity.dasik$asEntity() instanceof WolfExtensions ext) {
            ext.betterdogs$setZoomiesTicks(0);
        }
    }
}
