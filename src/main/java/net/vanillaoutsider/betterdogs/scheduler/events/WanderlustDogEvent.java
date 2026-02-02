package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.TickContext;
import org.jspecify.annotations.Nullable;

public class WanderlustDogEvent implements SocialEvent {
    public static final String ID = "wanderlust";
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
    }

    @Override
    public boolean tick(TickContext context) {
        this.tickCount++;
        return this.tickCount >= 600; // 30 seconds
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
    }
}
