package net.vanillaoutsider.social.core;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

/**
 * Entity-scoped multi-track scheduler for the Hive Mind.
 * Manages Mood and Ambient tracks independently.
 */
public class EntitySocialScheduler {
    private final SocialEntity socialEntity;

    // Tracks
    private String activeMoodId = "";
    private int moodTimer = 0;

    private String activeAmbientId = "";
    private int ambientTimer = 0;

    private int globalCooldownTimer = 0;

    public EntitySocialScheduler(SocialEntity socialEntity) {
        this.socialEntity = socialEntity;
    }

    public void tick() {
        if (globalCooldownTimer > 0) {
            globalCooldownTimer--;
        }

        // 1. Tick Mood Track
        if (!activeMoodId.isEmpty()) {
            SocialEvent event = SocialEventRegistry.get(activeMoodId);
            if (event != null) {
                event.tick(socialEntity);
                moodTimer--;
                if (moodTimer <= 0) endMood();
            } else {
                activeMoodId = "";
            }
        }

        // 2. Tick Ambient Track
        if (!activeAmbientId.isEmpty()) {
            SocialEvent event = SocialEventRegistry.get(activeAmbientId);
            if (event != null) {
                event.tick(socialEntity);
                ambientTimer--;
                if (ambientTimer <= 0) endAmbient();
            } else {
                activeAmbientId = "";
            }
        }
    }

    public void startEvent(String id, int durationTicks, @Nullable Entity context) {
        SocialEvent event = SocialEventRegistry.get(id);
        if (event == null) return;

        // HIGH priority reactions bypass global cooldown
        if (event.getPriority() == SocialEvent.Priority.HIGH) {
            startMood(event, durationTicks, context);
            return;
        }

        if (globalCooldownTimer > 0) return;

        if (event.getPriority() == SocialEvent.Priority.LOW) {
            startAmbient(event, durationTicks, context);
        } else {
            startMood(event, durationTicks, context);
        }
    }

    /**
     * Compatibility alias for startEvent.
     */
    public void injectBehavior(String id, int durationTicks, @Nullable Entity context) {
        startEvent(id, durationTicks, context);
    }

    private void startMood(SocialEvent event, int duration, @Nullable Entity context) {
        if (!activeMoodId.isEmpty()) endMood();
        this.activeMoodId = event.getId();
        this.moodTimer = duration;
        event.onStart(socialEntity, context);
    }

    private void startAmbient(SocialEvent event, int duration, @Nullable Entity context) {
        if (!activeAmbientId.isEmpty()) endAmbient();
        this.activeAmbientId = event.getId();
        this.ambientTimer = duration;
        event.onStart(socialEntity, context);
    }

    public void endMood() {
        if (!activeMoodId.isEmpty()) {
            SocialEvent event = SocialEventRegistry.get(activeMoodId);
            if (event != null) {
                event.onEnd(socialEntity);
                // Apply post-event cooldown
                this.globalCooldownTimer = event.getCooldownTicks() + socialEntity.betterdogs$asEntity().getRandom().nextInt(1200);
            }
            activeMoodId = "";
            moodTimer = 0;
        }
    }

    public void endAmbient() {
        if (!activeAmbientId.isEmpty()) {
            SocialEvent event = SocialEventRegistry.get(activeAmbientId);
            if (event != null) {
                event.onEnd(socialEntity);
                // Ambient events also trigger global cooldown (to avoid spam)
                this.globalCooldownTimer = event.getCooldownTicks() / 2 + socialEntity.betterdogs$asEntity().getRandom().nextInt(600);
            }
            activeAmbientId = "";
            ambientTimer = 0;
        }
    }

    public void tryStartEvent() {
        // BUSY CHECK: If mood track is active or in cooldown, cancel new events
        if (!activeMoodId.isEmpty() || globalCooldownTimer > 0) return;

        // 2. Iterate Global Social Event Registry
        for (SocialEvent event : SocialEventRegistry.getValues()) {
            if (event.getPriority() != SocialEvent.Priority.LOW && event.canTrigger(socialEntity)) {
                // 10% chance per pulse if conditions met
                if (socialEntity.betterdogs$asEntity().getRandom().nextFloat() < 0.10f) {
                    startMood(event, event.getMaxDurationTicks(), null); 
                    break;
                }
            }
        }
    }

    public boolean isEventActive(String id) {
        return activeMoodId.equals(id) || activeAmbientId.equals(id);
    }
}
