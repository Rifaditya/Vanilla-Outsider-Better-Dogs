package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.TickContext;
import org.jspecify.annotations.Nullable;

/**
 * DNA-driven event where dogs beg for food or attention from their owner.
 */
public class BeggingDogEvent implements SocialEvent {
    public static final String ID = "begging";
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
        return other.getPriorityValue() < 10;
    }

    @Override
    public void onStart(TickContext context) {
        this.tickCount = 0;
        // Logic to start begging (e.g. look at player, sit)
    }

    @Override
    public boolean tick(TickContext context) {
        this.tickCount++;
        // Stop if duration exceeded (e.g. 20 seconds) or logic satisfied
        return this.tickCount >= 400;
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        // Reset state
    }
}
