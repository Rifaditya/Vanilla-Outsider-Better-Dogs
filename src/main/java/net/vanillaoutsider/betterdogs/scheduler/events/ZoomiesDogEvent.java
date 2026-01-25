package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.scheduler.WolfEvent;

import java.util.Random;

/**
 * Zoomies Event.
 * Concept: Hyperactive running after waking up or rain.
 * Acceptance: Depends on Personality + UUID (Individual DNA).
 */
public class ZoomiesDogEvent implements WolfEvent {

    public static final String ID = "betterdogs:zoomies";
    private static final int ZOOMIES_DURATION = 140; // 7 seconds (approx 5-8s)

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean canTrigger(Wolf wolf) {
        // Optimization: Staggered Check (Once per 30s)
        if ((wolf.getId() + wolf.level().getGameTime()) % 600 != 0) {
            return false;
        }

        if (!wolf.isTame() || !(wolf instanceof WolfExtensions)) return false;

        // Condition 1: Morning (0 - 2000 ticks) OR Wet (After Rain/Water)
        long time = wolf.level().getLevelData().getDayTime() % 24000;
        boolean isMorning = time >= 0 && time < 2000;
        boolean isWet = wolf.isWet(); // Basic wet check
        
        // Random Chance trigger for the *event opportunity*
        // Morning: 10% chance
        // Wet: 20% chance
        Random rand = new Random();
        boolean attemptTrigger = false;
        
        if (isMorning && rand.nextFloat() < 0.10f) attemptTrigger = true;
        if (isWet && rand.nextFloat() < 0.20f) attemptTrigger = true;

        if (!attemptTrigger) return false;

        // Condition 2: Individual Acceptance (The DNA Check)
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
        if (wolf.isBaby()) {
            threshold = 100; // Babies always want to play
        } else {
            switch (personality) {
                case NORMAL -> threshold = 50;
                case PACIFIST -> threshold = 20;
                case AGGRESSIVE -> threshold = 0; // Aggressive dogs never do zoomies
                // Default handling for other future personalities
                default -> threshold = 30; 
            }
        }

        // 2. Determine Characteristic Roll (UUID DNA)
        // Formula: Math.abs((wolf.getUUID().hashCode() ^ EVENT_ID) % 100)
        // We use the ID string hash as the EVENT_ID modifier
        int characteristicRoll = Math.abs((wolf.getUUID().hashCode() ^ ID.hashCode()) % 100);

        // 3. Logic
        return characteristicRoll < threshold;
    }

    @Override
    public void onStart(Wolf wolf, @org.jspecify.annotations.Nullable Entity contextEntity) {
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.ZOOMIES, ZOOMIES_DURATION);
        }
    }

    @Override
    public void tick(Wolf wolf) {
        // Goal handles movement.
        // We could add particles here if needed.
    }

    @Override
    public void onEnd(Wolf wolf) {
        if (wolf instanceof WolfExtensions ext) {
            // Only clear if we are still in zoomies mode (don't overwrite play_fight etc)
            if (ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.ZOOMIES) {
                ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
            }
        }
    }
}
