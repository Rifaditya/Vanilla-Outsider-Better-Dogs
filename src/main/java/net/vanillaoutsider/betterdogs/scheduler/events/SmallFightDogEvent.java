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
import java.util.stream.Collectors;

/**
 * Small Fight Event.
 * Condition: >10 tamed dogs nearby, >70% are aggressive.
 * Action: Two dogs start fighting for 10 seconds.
 */
public class SmallFightDogEvent implements WolfEvent {

    public static final String ID = "betterdogs:small_fight";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean canTrigger(Wolf wolf) {
        // Optimization 1: Staggered Trigger (Once per minute per wolf)
        // (ID + Time) % 1200 == 0 ensures perfectly even distribution.
        if ((wolf.getId() + wolf.level().getGameTime()) % 1200 != 0) {
            return false;
        }

        if (!wolf.isTame() || !(wolf instanceof WolfExtensions ext)) return false;
        if (ext.betterdogs$getPersonality() != WolfPersonality.AGGRESSIVE) return false;

        // Optimization 2: Neighbor Sampling & Radius Cap
        // We only check the first 15 wolves we find within 20 blocks.
        // This makes the check O(1) effectively.
        AABB searchBox = wolf.getBoundingBox().inflate(20.0);
        List<Wolf> nearbyWolves = wolf.level().getEntitiesOfClass(Wolf.class, searchBox, w -> w.isTame());
        
        // Sampling Limit
        int sampleSize = Math.min(nearbyWolves.size(), 15);
        if (sampleSize <= 10) return false; // Need at least 10 in the sample to care.
        
        // Condition 2: >70% are AGGRESSIVE in the sample
        int aggressiveCount = 0;
        for (int i = 0; i < sampleSize; i++) {
            Wolf w = nearbyWolves.get(i);
            if (w instanceof WolfExtensions wExt && wExt.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE) {
                aggressiveCount++;
            }
        }

        double aggroPercentage = (double) aggressiveCount / sampleSize;
        return aggroPercentage > 0.70;
    }

    @Override
    public void onStart(Wolf wolf, @org.jspecify.annotations.Nullable Entity contextEntity) {
        // "Wolf" here is the initiator (selected by scheduler).
        // Find a partner to fight.
        AABB searchBox = wolf.getBoundingBox().inflate(10.0);
        
        // Valid Partner:
        // 1. Another Tamed Aggressive Wolf
        // 2. Can SEE the initiator (Line of Sight via TargetingConditions)
        List<Wolf> candidates = wolf.level().getEntitiesOfClass(Wolf.class, searchBox, w -> 
            w != wolf && w.isTame() && w instanceof WolfExtensions wExt 
            && wExt.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE
            && net.minecraft.world.entity.ai.targeting.TargetingConditions.forCombat().range(20.0).test(wolf, w)
        );

        if (candidates.isEmpty()) return;

        Wolf opponent = candidates.get(new Random().nextInt(candidates.size()));

        // Start Fight!
        // Duration: 10 seconds = 200 ticks.
        startFight(wolf, opponent, 200);
        startFight(opponent, wolf, 200);

        BetterDogs.LOGGER.info("SmallFightDogEvent: {} and {} are play-fighting!", wolf.getUUID(), opponent.getUUID());
    }

    private void startFight(Wolf fighter, Wolf target, int duration) {
        if (fighter instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(target, WolfExtensions.SocialAction.PLAY_FIGHT, duration);
        }
    }

    @Override
    public void tick(Wolf wolf) {
        // Ensure they keep fighting?
    }

    @Override
    public void onEnd(Wolf wolf) {
        // Stop fighting
        wolf.setTarget(null);
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
    }
}
