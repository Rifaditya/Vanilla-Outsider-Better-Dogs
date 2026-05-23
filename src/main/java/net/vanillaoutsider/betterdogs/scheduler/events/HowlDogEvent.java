// Verified against: HowlDogEvent.java (26.1.2+)
package net.vanillaoutsider.betterdogs.scheduler.events;

import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.TickContext;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Social Event: Pack howl during full moon.
 * Triggered by GroupHowlGoal spreading to nearby wolves.
 */
public class HowlDogEvent implements SocialEvent {
    public static final String ID = "howl";

    private int timer = 0;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int getPriorityValue() {
        return 20; // High
    }

    @Override
    public String getTrackId() {
        return "main";
    }

    @Override
    public boolean canPreempt(SocialEvent other) {
        return false;
    }

    @Override
    public void onStart(TickContext context) {
        if (context.entity().dasik$asEntity() instanceof Wolf wolf) {
            // Play initial howl sound using lowered-pitch baby whine
            wolf.playSound(SoundEvents.WOLF_WHINE_BABY.value(), 1.3f, 0.5f + wolf.getRandom().nextFloat() * 0.2f);

            // INTEGRATION: Howling together increases affinity
            if (context.entity() instanceof WolfExtensions ext) {
                wolf.level().getEntitiesOfClass(Wolf.class, wolf.getBoundingBox().inflate(8.0))
                        .stream()
                        .filter(w -> w != wolf && w.isTame() && w.isOwnedBy(wolf.getOwner()))
                        .forEach(w -> ext.betterdogs$adjustAffinity(w.getStringUUID(), 2));
            }
        }
    }

    @Override
    public boolean tick(TickContext context) {
        timer++;
        return timer >= 60;
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        timer = 0;
    }
}
