package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.BetterDogs;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.scheduler.WolfEvent;

import java.util.List;
import java.util.Random;

/**
 * Group Howl Event.
 * Concept: One wolf starts howling, others join in.
 * Acceptance: Depends on Personality + UUID (Individual DNA).
 */
public class HowlDogEvent implements WolfEvent {

    public static final String ID = "betterdogs:group_howl";
    private static final int HOWL_DURATION = 200; // 10 seconds

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean canTrigger(Wolf wolf) {
        // Optimization: Staggered Check (Once per minute)
        if ((wolf.getId() + wolf.level().getGameTime()) % 1200 != 0) {
            return false;
        }

        if (!wolf.isTame() || !(wolf instanceof WolfExtensions)) return false;

        // Condition 1: Time is NIGHT (18000 - 22000)
        long time = wolf.level().getLevelData().getDayTime() % 24000;
        if (time < 18000 || time > 22000) return false;

        // Condition 2: Probability Check
        // Full Moon (Day % 8 == 0) -> 100% chance to *attempt*
        // Regular Night -> 5% chance
        boolean isFullMoon = (wolf.level().getLevelData().getDayTime() / 24000) % 8 == 0;
        Random rand = new Random();
        
        if (!isFullMoon && rand.nextFloat() > 0.05f) {
            return false;
        }

        // Condition 3: Individual Acceptance (The DNA Check)
        // Note: For Howling, we are selecting the LEADER here.
        // If the Leader accepts, they trigger the event.
        return canAccept(wolf);
    }

    /**
     * The "Free Will" Check (Individual DNA).
     * Formula: Personality + Characteristic = Do/Not
     */
    private boolean canAccept(Wolf wolf) {
        if (!(wolf instanceof WolfExtensions ext)) return false;

        WolfPersonality personality = ext.betterdogs$getPersonality();
        
        // 1. Determine Threshold based on Personality
        int threshold = 0;
        switch (personality) {
            case AGGRESSIVE -> threshold = 100; // Alphas love to howl
            case NORMAL -> threshold = 80;
            case PACIFIST -> threshold = 40;    // Shy
            default -> threshold = 50; 
        }

        // 2. Determine Characteristic Roll (UUID DNA)
        int characteristicRoll = Math.abs((wolf.getUUID().hashCode() ^ ID.hashCode()) % 100);

        // 3. Logic
        return characteristicRoll < threshold;
    }

    @Override
    public void onStart(Wolf wolf, @org.jspecify.annotations.Nullable Entity contextEntity) {
        // Wolf is the Leader.
        // Start Leader Howl
        startHowl(wolf);

        // Notify Neighbors to Join In
        // They perform their own Acceptance Check
        AABB searchBox = wolf.getBoundingBox().inflate(20.0);
        List<Wolf> nearbyWolves = wolf.level().getEntitiesOfClass(Wolf.class, searchBox, w -> w != wolf && w.isTame());
        
        for (Wolf neighbor : nearbyWolves) {
            if (neighbor instanceof WolfExtensions neighborExt) {
                // Neighbors perform strict DNA check
                // Note: We reuse canAccept() but logic handles "Joining" same as "Starting" for simplicity
                // Or we can implement distinct logic. Here we use distinct logic implicitly via the canAccept check.
                if (canAccept(neighbor)) {
                    // Delay joining slightly for realism
                    // But for simplicity in this Scheduler Event (which controls state), we set state immediately.
                    // The Goal can handle animation delay.
                    startHowl(neighbor);
                }
            }
        }
        
        BetterDogs.LOGGER.info("Wolf [{}] started a Group Howl!", wolf.getUUID());
    }
    
    private void startHowl(Wolf wolf) {
        if (wolf instanceof WolfExtensions ext) {
            // Check if already busy
            if (ext.betterdogs$isSocialModeActive()) return;
            
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.HOWL, HOWL_DURATION);
        }
    }

    @Override
    public void tick(Wolf wolf) {
        // Goal handles particles/sound
    }

    @Override
    public void onEnd(Wolf wolf) {
        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.HOWL) {
                ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
            }
        }
    }
}
