package net.vanillaoutsider.betterdogs.scheduler;

import java.util.HashMap;
import java.util.Map;
import net.vanillaoutsider.betterdogs.BetterDogs;
import net.vanillaoutsider.betterdogs.scheduler.WolfEvent;

/**
 * Central registry for all Wolf Scheduler Events.
 */
public class WolfEventRegistry {
    
    private static final Map<String, WolfEvent> REGISTRY = new HashMap<>();

    public static void register(WolfEvent event) {
        if (REGISTRY.containsKey(event.getId())) {
             BetterDogs.LOGGER.warn("Duplicate WolfEvent registered: " + event.getId());
             return;
        }
        REGISTRY.put(event.getId(), event);
        BetterDogs.LOGGER.info("Registered WolfEvent: " + event.getId());
    }

    public static WolfEvent get(String id) {
        return REGISTRY.get(id);
    }

    public static java.util.Collection<WolfEvent> getValues() {
        return REGISTRY.values();
    }
}
