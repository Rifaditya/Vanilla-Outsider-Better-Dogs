// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.scheduler.events;

import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.TickContext;

/**
 * DNA-driven event where dogs find and bring dropped items to their owner.
 */
public class FetchDogEvent implements SocialEvent {
    public static final String ID = "fetch";
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
    }

    @Override
    public boolean tick(TickContext context) {
        this.tickCount++;
        return this.tickCount >= 600;
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
    }
}
