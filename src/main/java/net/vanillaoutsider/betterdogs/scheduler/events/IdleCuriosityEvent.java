package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.social.core.SocialEntity;
import net.vanillaoutsider.social.core.SocialEvent;
import org.jspecify.annotations.Nullable;

/**
 * DNA-driven event where dogs investigate nearby blocks or entities.
 */
public class IdleCuriosityEvent implements SocialEvent {
    public static final String ID = "idle_curiosity";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }

    @Override
    public boolean canTrigger(SocialEntity entity) {
        if (entity.betterdogs$asEntity() instanceof Wolf wolf) {
            if (wolf.isOrderedToSit()) return false;
            return wolf.isBaby() && wolf.onGround();
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
        return 300; // 15 seconds of curiosity
    }

    @Override
    public int getCooldownTicks() {
        return 2400; // 2 minutes cooldown
    }
}
