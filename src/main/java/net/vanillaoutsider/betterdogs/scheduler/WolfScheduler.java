package net.vanillaoutsider.betterdogs.scheduler;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.Entity;

public class WolfScheduler {

    private final Wolf wolf;

    // Runtime
    private String activeEventId = "";
    private int eventTimer = 0;

    public WolfScheduler(Wolf wolf) {
        this.wolf = wolf;
    }

    public void tickActiveEvent() {
        if (!activeEventId.isEmpty()) {
            WolfEvent event = WolfEventRegistry.get(activeEventId);
            if (event != null) {
                event.tick(wolf);

                eventTimer--;
                if (eventTimer <= 0) {
                    endActiveEvent();
                }
            } else {
                endActiveEvent();
            }
        }
    }

    public void tryStartEvent() {
        if (!activeEventId.isEmpty())
            return;

        long time = wolf.level().getDayTime() % 24000;
        if (time > 100)
            return; // Dawn only

        if (wolf.getRandom().nextFloat() < 0.10f) {
            for (WolfEvent event : WolfEventRegistry.getValues()) {
                if (event.canTrigger(wolf)) {
                    startEvent(event.getId(), 24000);
                    break;
                }
            }
        }
    }

    public void injectBehavior(String id, int durationTicks, Entity contextEntity) {
        startEvent(id, durationTicks, contextEntity);
    }

    public void startEvent(String id, int durationTicks, Entity contextEntity) {
        if (!activeEventId.isEmpty()) {
            endActiveEvent();
        }

        WolfEvent event = WolfEventRegistry.get(id);
        if (event != null) {
            this.activeEventId = id;
            this.eventTimer = durationTicks;
            event.onStart(wolf, contextEntity);
        }
    }

    public void startEvent(String id, int durationTicks) {
        startEvent(id, durationTicks, null);
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
}
