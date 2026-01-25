package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.scheduler.WolfEvent;
import net.vanillaoutsider.betterdogs.BetterDogs;

/**
 * Event triggered when an Adult Wolf intervenes to correct a baby.
 * Triggered by BabyBiteBackGoal (Baby confesses -> Adult reacts).
 */
public class CorrectionDogEvent implements WolfEvent {

    public static final String ID = "betterdogs:correction";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean canTrigger(Wolf wolf) {
        // Reactive Only.
        return false;
    }

    @Override
    public void onStart(Wolf wolf, @org.jspecify.annotations.Nullable Entity contextEntity) {
        // Context Entity is the Baby Wolf that sinned.
        if (contextEntity instanceof Wolf baby && wolf instanceof WolfExtensions ext) {
            // Set Social State -> DISCIPLINE
            // 100 ticks = 5 seconds to deliver the bite.
            ext.betterdogs$setSocialState(baby, WolfExtensions.SocialAction.DISCIPLINE, 100);
            
            // Mark baby as being disciplined (Dunce Cap) to prevent other adults from joining in.
            if (baby instanceof WolfExtensions babyExt) {
                babyExt.betterdogs$setBeingDisciplined(true);
            }
            
            // BetterDogs.LOGGER.info("CorrectionEvent: Adult {} correcting Baby {}", wolf.getUUID(), baby.getUUID());
        } else {
             BetterDogs.LOGGER.warn("CorrectionEvent: Started but no baby context found for wolf " + wolf.getUUID());
        }
    }

    @Override
    public void tick(Wolf wolf) {
        // Logic handled by Goal
    }

    @Override
    public void onEnd(Wolf wolf) {
        // Clear Social State
        if (wolf instanceof WolfExtensions ext) {
             if (ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.DISCIPLINE) {
                 // Clean up the Dunce Cap on the target if possible
                 LivingEntity target = ext.betterdogs$getSocialTarget();
                 if (target instanceof Wolf baby && baby instanceof WolfExtensions babyExt) {
                     babyExt.betterdogs$setBeingDisciplined(false);
                 }
                 
                 ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
             }
        }
    }
}
