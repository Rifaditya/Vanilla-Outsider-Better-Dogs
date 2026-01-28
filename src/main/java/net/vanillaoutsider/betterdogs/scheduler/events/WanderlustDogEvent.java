package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.social.core.SocialEntity;
import net.vanillaoutsider.social.core.SocialEvent;
import org.jspecify.annotations.Nullable;

public class WanderlustDogEvent implements SocialEvent {

    public static final String ID = "wanderlust";

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
            // Respect Sitting Pose (Vanilla Spirit)
            if (wolf.isOrderedToSit()) return false;
            
            // Pacifist Tuning: Much less likely to wander (~25% chance of passing this gate compared to others)
            // This prevents them from constantly trying to leave the owner's side.
            if (wolf instanceof net.vanillaoutsider.betterdogs.WolfExtensions ext 
                    && ext.betterdogs$getPersonality() == net.vanillaoutsider.betterdogs.WolfPersonality.PACIFIST) {
                if (wolf.getRandom().nextFloat() > 0.25f) {
                    return false;
                }
            }

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
    public int getMaxDurationTicks() {
        return 600; // 30 seconds
    }

    @Override
    public int getCooldownTicks() {
        return 6000; // 5 minutes recovery
    }

    @Override
    public void onEnd(SocialEntity entity) {
        // Cleanup
    }
}
