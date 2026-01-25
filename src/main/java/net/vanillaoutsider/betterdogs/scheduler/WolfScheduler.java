package net.vanillaoutsider.betterdogs.scheduler;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.entity.Entity;
import net.vanillaoutsider.betterdogs.BetterDogs;
import net.vanillaoutsider.betterdogs.scheduler.WolfEvent;
import net.vanillaoutsider.betterdogs.scheduler.WolfEventRegistry;
import org.jspecify.annotations.Nullable;

/**
 * Entity-scoped scheduler for long-term AI events (Moods).
 * Ticks infrequently (e.g. daily checks) to manage behavioral phases.
 */
public class WolfScheduler {

    private final Wolf wolf;
    
    // Persistence
    private String activeEventId = "";
    private int eventTimer = 0;
    
    // Runtime
    private int tickCounter = 0;
    private static final int CHECK_INTERVAL = 20; // Check every second

    public WolfScheduler(Wolf wolf) {
        this.wolf = wolf;
    }

    public void tickActiveEvent() {
        tickCounter++;
        // Run active event logic every tick (or every N ticks if needed by the event)
        // Note: Mixin calls this every tick.
        
        if (!activeEventId.isEmpty()) {
             // Only decrement timer every 20 ticks (1 second) for general sanity?
             // Or let the event decide? 
             // Existing logic was checked every 20 ticks.
             // Let's check logic every tick but decrement timer properly.
             
             WolfEvent event = WolfEventRegistry.get(activeEventId);
             if (event != null) {
                 event.tick(wolf);
                 
                 // Decrement Timer
                 eventTimer--;
                 if (eventTimer <= 0) {
                     endActiveEvent();
                 }
             } else {
                 endActiveEvent();
             }
        }
    }
    
    /**
     * Called by Global System Scheduler periodically (Randomly).
     * Attempts to start a natural background event (Moods).
     */
    public void tryStartEvent() {
        // 1. If busy, don't interrupt (unless we want higher priority events?)
        if (!activeEventId.isEmpty()) return;
        
        // 2. Logic Check: Dawn, Chance, etc.
        // Previously: "Check once per in-game day (at specific time window 1000-1100) AND low chance"
        // Since Global Scheduler calls us randomly, we can keep the Time Check.
        
        long time = wolf.level().getOverworldClockTime() % 24000;
        // Check only at dawn (0 - 40 to widen window slightly for random hits)
        if (time > 100) return; // Strict dawn window
        
        // 10% chance per attempt?
        // If Global Scheduler picks us, we usually want to run if conditions met.
        // Let's assume Global Scheduler hits us rarely enough.
        
        if (wolf.getRandom().nextFloat() < 0.10f) {
             // Iterate Registry to find a triggerable event
            for (WolfEvent event : WolfEventRegistry.getValues()) {
                if (event.canTrigger(wolf)) {
                    // For now, First-Come-First-Served (FCFS).
                    startEvent(event.getId(), 24000); 
                    break;
                }
            }
        }
    }
    
    /**
     * Reactive: Force an event to start immediately (Command Center).
     * e.g. Retaliation.
     */
    public void injectBehavior(String id, int durationTicks, @Nullable Entity contextEntity) {
         // BetterDogs.LOGGER.info("WolfScheduler: Injecting behavior " + id + " for wolf " + wolf.getUUID());
         startEvent(id, durationTicks, contextEntity);
    }
    
    // Overload for backward compat or no-context calls
    public void injectBehavior(String id, int durationTicks) {
        injectBehavior(id, durationTicks, null);
    }

    public void startEvent(String id, int durationTicks) {
        startEvent(id, durationTicks, null);
    }

    public void startEvent(String id, int durationTicks, @Nullable Entity contextEntity) {
        // End current if any
        if (!activeEventId.isEmpty()) {
            endActiveEvent();
        }
        
        WolfEvent event = WolfEventRegistry.get(id);
        if (event != null) {
            this.activeEventId = id;
            this.eventTimer = durationTicks;
            event.onStart(wolf, contextEntity);
            // BetterDogs.LOGGER.info("WolfScheduler: Started event " + id + " for wolf " + wolf.getUUID());
        }
    }

    public void endActiveEvent() {
        if (!activeEventId.isEmpty()) {
            WolfEvent event = WolfEventRegistry.get(activeEventId);
            if (event != null) {
                event.onEnd(wolf);
            }
            this.activeEventId = "";
            this.eventTimer = 0;
        }
    }
    
    public boolean isEventActive(String id) {
        return activeEventId.equals(id);
    }

    // === Persistence (Protocol 26.1: ValueOutput) ===

    public void save(ValueOutput output) {
        if (!activeEventId.isEmpty()) {
            output.putString("activeEventId", activeEventId);
            output.putInt("eventTimer", eventTimer);
            
            // Allow event to save extra data
            WolfEvent event = WolfEventRegistry.get(activeEventId);
            if (event != null) {
                event.save(output);
            }
        } else {
             output.putString("activeEventId", "");
        }
    }

    public void load(ValueInput input) {
        // Use getStringOr for safety
        this.activeEventId = input.getStringOr("activeEventId", "");
        
        if (!activeEventId.isEmpty()) {
            this.eventTimer = input.getIntOr("eventTimer", 0);
            
            // Allow event to load extra data
            WolfEvent event = WolfEventRegistry.get(activeEventId);
            if (event != null) {
                event.load(input);
            }
        }
    }
}
