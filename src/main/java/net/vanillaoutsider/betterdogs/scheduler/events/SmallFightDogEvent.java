package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.social.core.SocialEntity;
import net.vanillaoutsider.social.core.SocialEvent;
import org.jspecify.annotations.Nullable;

public class SmallFightDogEvent implements SocialEvent {
    public static final String ID = "play_fight";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Priority getPriority() {
        return Priority.NORMAL;
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
        return 200; // 10 seconds of play fight
    }

    @Override
    public int getCooldownTicks() {
        return 4800; // 4 minutes cooldown
    }
}
