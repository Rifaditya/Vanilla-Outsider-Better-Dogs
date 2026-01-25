package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.scheduler.WolfEvent;
import net.vanillaoutsider.betterdogs.BetterDogs;

/**
 * Event triggered when an Aggressive Baby Wolf is punched by its owner.
 * Causes immediate retaliation (1 bite).
 */
public class RetaliationDogEvent implements WolfEvent {

    public static final String ID = "betterdogs:retaliation";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean canTrigger(Wolf wolf) {
        // This event is Reactive Only (triggered manually via injectBehavior).
        // It never occurs naturally via random scheduler ticks.
        return false;
    }

    @Override
    public void onStart(Wolf wolf, @org.jspecify.annotations.Nullable Entity contextEntity) {
        // Use context entity if provided (Reliable), otherwise fallback to last hurt (Unreliable at inject time)
        Entity target = contextEntity;
        
        if (target == null) {
            target = wolf.getLastHurtByMob();
        }

        if (target instanceof LivingEntity attacker && wolf instanceof WolfExtensions ext) {
            // Set Social State -> RETALIATION
            // Duration is managed by the Scheduler Timer, but we also set a safety timer in Social State.
            ext.betterdogs$setSocialState(attacker, WolfExtensions.SocialAction.RETALIATION, 100);
            // BetterDogs.LOGGER.info("RetaliationEvent: Wolf {} retaliating against {}", wolf.getUUID(), attacker.getName().getString());
        } else {
            // No attacker found? Abort?
            BetterDogs.LOGGER.warn("RetaliationEvent: Started but no attacker found for wolf " + wolf.getUUID());
        }
    }

    @Override
    public void tick(Wolf wolf) {
        // Ensure social state remains valid?
        if (wolf instanceof WolfExtensions ext) {
            if (!ext.betterdogs$isSocialModeActive()) {
                 // If Social Mode broke (target died, etc), scheduler ensures we don't stick here forever.
            }
        }
    }

    @Override
    public void onEnd(Wolf wolf) {
        // Clear Social State
        if (wolf instanceof WolfExtensions ext) {
             // Only clear if it's still our action?
             if (ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.RETALIATION) {
                 ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
             }
        }
    }
}
