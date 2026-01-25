package net.vanillaoutsider.betterdogs.scheduler;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

/**
 * Interface for long-term Wolf AI events (Moods/Phases).
 * These are registered in WolfEventRegistry and managed by WolfScheduler.
 */
public interface WolfEvent {

    /**
     * Unique ID for this event.
     */
    String getId();

    /**
     * Checks if the event should trigger for the given wolf.
     * Called periodically (e.g. daily) by the Scheduler.
     */
    boolean canTrigger(Wolf wolf);



    /**
     * Called when the event starts.
     * @param contextEntity Optional entity associated with the start trigger (e.g. the attacker).
     */
    void onStart(Wolf wolf, @Nullable Entity contextEntity);

    /**
     * Called every tick while the event is active.
     */
    void tick(Wolf wolf);

    /**
     * Called when the event ends (timer expires or condition fails).
     */
    void onEnd(Wolf wolf);
    
    /**
     * Called to save event-specific data (if any).
     * Note: The Scheduler handles saving the Event ID and Timer automatically.
     * This is only for EXTRA data.
     */
    /**
     * Called to save event-specific data (if any).
     * Note: The Scheduler handles saving the Event ID and Timer automatically.
     * This is only for EXTRA data.
     */
    default void save(ValueOutput output) {}
    
    /**
     * Called to load event-specific data.
     */
    default void load(ValueInput input) {}
}
