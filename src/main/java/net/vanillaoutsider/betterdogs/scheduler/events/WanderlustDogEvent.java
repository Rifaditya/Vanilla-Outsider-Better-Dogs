package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.Entity;
import net.vanillaoutsider.betterdogs.scheduler.WolfEvent;
import net.vanillaoutsider.betterdogs.WolfExtensions;

public class WanderlustDogEvent implements WolfEvent {

    public static final String ID = "wanderlust";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean canTrigger(Wolf wolf) {
        // Condition:
        // 1. Must be Tame (Wild wolves already wander heavily, this is for pets)
        // 2. Must not be sitting (Sitting wolves shouldn't theoretically get wanderlust, or maybe they do but can't act on it)
        // 3. Must rely on Scheduler logic (1% chance per day check) which calls this.
        // The Scheduler decides *when* to check, this function decides *if* it's valid.
        
        return wolf.isTame() && !wolf.isOrderedToSit();
    }

    @Override
    public void onStart(Wolf wolf, @org.jspecify.annotations.Nullable Entity contextEntity) {
        // No specific setup needed, the goal will check the scheduler availability.
        // We could maybe play a sound or particle?
    }

    @Override
    public void tick(Wolf wolf) {
        // Logic handled by Goal
    }

    @Override
    public void onEnd(Wolf wolf) {
        // Cleanup?
    }
}
