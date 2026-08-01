// Verified against: IdleCuriosityEvent.java (26.1.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.scheduler.events;

import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.TickContext;

/**
 * DNA-driven event where dogs investigate nearby blocks or entities.
 */
public class IdleCuriosityEvent implements SocialEvent {
    public static final String ID = "idle_curiosity";
    private int tickCount = 0;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int getPriorityValue() {
        return 5; // Low priority
    }

    @Override
    public String getTrackId() {
        return "main";
    }

    @Override
    public boolean canPreempt(SocialEvent other) {
        return other.getPriorityValue() < 5;
    }

    @Override
    public void onStart(TickContext context) {
        this.tickCount = 0;
    }

    @Override
    public boolean tick(TickContext context) {
        this.tickCount++;
        return this.tickCount >= 300; // 15 seconds
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
    }
}
