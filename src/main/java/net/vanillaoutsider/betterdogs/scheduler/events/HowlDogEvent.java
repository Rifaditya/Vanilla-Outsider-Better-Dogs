package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.TickContext;
import org.jspecify.annotations.Nullable;

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
            // Play initial howl sound using wolf shake at low pitch
            wolf.playSound(SoundEvents.WOLF_SHAKE, 1.3f, 0.5f + wolf.getRandom().nextFloat() * 0.2f);
        }
    }

    @Override
    public boolean tick(TickContext context) {
        timer++;

        if (context.entity().dasik$asEntity() instanceof Wolf wolf) {
            // Look up at the moon
            wolf.getLookControl().setLookAt(
                    wolf.getX(),
                    wolf.getY() + 10.0,
                    wolf.getZ(),
                    10.0f,
                    wolf.getMaxHeadXRot());

            // Additional howl at midpoint
            if (timer == 30) {
                wolf.playSound(SoundEvents.WOLF_SHAKE, 1.1f, 0.55f + wolf.getRandom().nextFloat() * 0.2f);
            }
        }
        return timer >= 60;
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        timer = 0;
    }
}
