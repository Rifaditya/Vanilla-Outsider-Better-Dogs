package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.EnumSet;
import java.util.List;

/**
 * AI Goal: Group Howl - Pack vocalization during night.
 * All wolves in range join the howl when one starts.
 * Concept: "Full moon pack vocalization"
 * Note: Using wolf shake/whine sounds since WOLF_HOWL was removed in MC 26.1
 */
public class GroupHowlGoal extends Goal {

    private final Wolf wolf;
    private int howlTimer = 0;
    private int howlCooldown = 0;
    private static final int HOWL_DURATION = 60; // 3 seconds
    private static final int HOWL_COOLDOWN = 2400; // 2 minutes

    public GroupHowlGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Must be tamed wolf
        if (!wolf.isTame()) return false;
        
        // Must be night (use sky darken for detection)
        if (!isNightTime()) return false;
        
        // Cooldown check
        if (howlCooldown > 0) {
            howlCooldown--;
            return false;
        }
        
        // Sitting wolves don't howl
        if (wolf.isOrderedToSit()) return false;
        
        // Random chance to start howl (permille per tick during night)
        float chance = BetterDogsGameRules.getProb(wolf.level(), BetterDogsGameRules.BD_HOWL_CHANCE);
        if (chance <= 0) return false;
        
        return wolf.getRandom().nextFloat() < chance;
    }

    private boolean isNightTime() {
        if (wolf.level().isClientSide()) return false;
        
        // Use sky darken level to detect night (higher = darker = night)
        int skyDarken = wolf.level().getSkyDarken();
        return skyDarken >= 4; // Night when sky darken is 4+
    }

    @Override
    public void start() {
        howlTimer = HOWL_DURATION;
        
        // Play howl-like vocalization using wolf shake at low pitch
        playHowlSound(wolf, 1.5f, 0.5f + wolf.getRandom().nextFloat() * 0.2f);
        
        // Spread howl to nearby wolves
        spreadHowl();
    }
    
    private void playHowlSound(Wolf target, float volume, float pitch) {
        // Use wolf shake sound at low pitch for howl-like effect
        target.playSound(SoundEvents.WOLF_SHAKE, volume, pitch);
    }

    private void spreadHowl() {
        double range = BetterDogsConfig.get().getHowlSpreadRange();
        AABB searchBox = wolf.getBoundingBox().inflate(range);
        
        List<Wolf> nearbyWolves = wolf.level().getEntitiesOfClass(Wolf.class, searchBox, w -> {
            if (w == wolf) return false;
            if (!w.isTame()) return false;
            if (w.isOrderedToSit()) return false;
            return true;
        });
        
        // Trigger howl sounds in nearby wolves with varied timing
        for (Wolf nearbyWolf : nearbyWolves) {
            // Play howl sounds with varied pitch for natural pack howl effect
            float pitch = 0.45f + wolf.getRandom().nextFloat() * 0.3f;
            playHowlSound(nearbyWolf, 1.3f, pitch);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return howlTimer > 0 && !wolf.isOrderedToSit();
    }

    @Override
    public void tick() {
        howlTimer--;
        
        // Look up at the moon
        wolf.getLookControl().setLookAt(
            wolf.getX(),
            wolf.getY() + 10.0,
            wolf.getZ(),
            10.0f,
            wolf.getMaxHeadXRot()
        );
        
        // Additional howl sounds during the duration
        if (howlTimer == 30 || howlTimer == 15) {
            playHowlSound(wolf, 1.2f, 0.55f + wolf.getRandom().nextFloat() * 0.2f);
        }
    }

    @Override
    public void stop() {
        howlCooldown = HOWL_COOLDOWN;
        howlTimer = 0;
    }
}
