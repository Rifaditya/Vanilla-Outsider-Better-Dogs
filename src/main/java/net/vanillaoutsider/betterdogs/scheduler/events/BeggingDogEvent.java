package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.social.core.SocialEntity;
import net.vanillaoutsider.social.core.SocialEvent;
import org.jspecify.annotations.Nullable;

/**
 * DNA-driven event where dogs beg for food or attention from their owner.
 */
public class BeggingDogEvent implements SocialEvent {
    public static final String ID = "begging";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW; // Ambient track: Doesn't block other moods
    }

    @Override
    public boolean canTrigger(SocialEntity entity) {
        if (entity.betterdogs$asEntity() instanceof Wolf wolf) {
            if (wolf.isOrderedToSit()) return false;
            return wolf.isTame() && wolf.onGround();
        }
        return false;
    }

    @Override
    public void onStart(SocialEntity entity, @Nullable Entity contextEntity) {
    }

    @Override
    public void tick(SocialEntity entity) {
    }

    @Override
    public void onEnd(SocialEntity entity) {
    }

    @Override
    public int getMaxDurationTicks() {
        return 400; // 20 seconds
    }

    @Override
    public int getCooldownTicks() {
        return 6000; // 5 minutes cooldown
    }
}
