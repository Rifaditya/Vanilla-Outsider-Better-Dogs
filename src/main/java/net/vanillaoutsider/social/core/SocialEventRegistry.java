package net.vanillaoutsider.social.core;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for all Social Events across all mods.
 */
public class SocialEventRegistry {
    private static final Map<String, SocialEvent> EVENTS = new ConcurrentHashMap<>();

    public static void register(SocialEvent event) {
        EVENTS.put(event.getId(), event);
    }

    public static SocialEvent get(String id) {
        return EVENTS.get(id);
    }

    public static Collection<SocialEvent> getValues() {
        return EVENTS.values();
    }
}
