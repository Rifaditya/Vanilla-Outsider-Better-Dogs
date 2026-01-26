package net.vanillaoutsider.betterdogs.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
// BetterDogs.LOGGER access via System.out if needed or mixin accessor.
// I'll skip logging to avoid dependency issues for now.

public class WolfEventRegistry {
    private static final Map<String, WolfEvent> REGISTRY = new HashMap<>();

    public static void register(WolfEvent event) {
        if (REGISTRY.containsKey(event.getId())) {
            return;
        }
        REGISTRY.put(event.getId(), event);
    }

    public static WolfEvent get(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<WolfEvent> getValues() {
        return REGISTRY.values();
    }
}
