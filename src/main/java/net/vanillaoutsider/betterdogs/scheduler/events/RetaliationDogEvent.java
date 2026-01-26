package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.social.core.SocialEntity;
import net.vanillaoutsider.social.core.SocialEvent;
import org.jspecify.annotations.Nullable;

public class RetaliationDogEvent implements SocialEvent {
    public static final String ID = "retaliation";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Priority getPriority() {
        return Priority.HIGH;
    }

    @Override
    public boolean canTrigger(SocialEntity entity) {
        return true; // Triggered by damage hooks
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
        return 100; // 5 seconds of fury
    }

    @Override
    public int getCooldownTicks() {
        return 0; // Reactions have no cooldown (survival)
    }
}
