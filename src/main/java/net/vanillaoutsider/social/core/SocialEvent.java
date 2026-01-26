package net.vanillaoutsider.social.core;

import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

/**
 * Contract for all social AI events.
 */
public interface SocialEvent {
    String getId();

    /**
     * @return Priority of this event. HIGH interrupts others, LOW runs on ambient track.
     */
    Priority getPriority();

    boolean canTrigger(SocialEntity entity);

    void onStart(SocialEntity entity, @Nullable Entity contextEntity);

    void tick(SocialEntity entity);

    void onEnd(SocialEntity entity);

    /**
     * @return The maximum duration of this event in ticks.
     */
    int getMaxDurationTicks();

    /**
     * @return The recovery cooldown after this event finishes.
     */
    int getCooldownTicks();

    enum Priority {
        LOW,    // Ambient interactions (Begging, Sniffing)
        NORMAL, // Standard mutually exclusive moods (Zoomies, Wanderlust)
        HIGH    // Interruptive reactions (Retaliation, Fear)
    }
}
