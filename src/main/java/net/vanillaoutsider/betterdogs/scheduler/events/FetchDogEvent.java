package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.TickContext;
import org.jspecify.annotations.Nullable;

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
        return 10; // Normal priority
    }

    @Override
    public String getTrackId() {
        return "main";
    }

    @Override
    public boolean canPreempt(SocialEvent other) {
        // Can preempt lower priority events
        return other.getPriorityValue() < 10;
    }

    @Override
    public void onStart(TickContext context) {
        this.tickCount = 0;
        // Logic to start fetching (e.g. set AI goal or state)
    }

    @Override
    public boolean tick(TickContext context) {
        this.tickCount++;

        // Placeholder for fetch logic
        // if (fetchCompleted) return true;

        return this.tickCount >= 600; // Max duration 30 seconds
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        // Cleanup fetch state
    }
}
