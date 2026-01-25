package net.vanillaoutsider.betterdogs.ai;

import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

/**
 * AI Goal: Aggressive baby randomly attacks a nearby entity (unprovoked mischief).
 * "Natural Selection" mechanic - puppy might attack an Iron Golem and die.
 * Once per Minecraft day, configurable 2.5% chance by default.
 */
public class BabyMischiefGoal extends Goal {

    private final Wolf wolf;
    private LivingEntity target;

    public BabyMischiefGoal(Wolf wolf) {
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        // Must be baby, tamed, aggressive
        if (!wolf.isBaby() || !wolf.isTame())
            return false;

        if (wolf.isOrderedToSit())
            return false;

        if (!(wolf instanceof WolfExtensions ext))
            return false;

        if (ext.betterdogs$getPersonality() != WolfPersonality.AGGRESSIVE)
            return false;

        // Check daily mischief limit
        long currentDay = wolf.level().getGameTime() / 24000L;
        if (ext.betterdogs$getLastMischiefDay() >= currentDay)
            return false;

        // Roll for mischief chance
        float chance = BetterDogsConfig.get().getBabyMischiefChance();
        if (chance <= 0 || wolf.getRandom().nextFloat() * 100 >= chance)
            return false;

        // Find a random target within 10 blocks
        LivingEntity foundTarget = findRandomTarget();
        if (foundTarget == null)
            return false;

        this.target = foundTarget;
        return true;
    }

    private LivingEntity findRandomTarget() {
        AABB searchBox = wolf.getBoundingBox().inflate(10.0);
        List<LivingEntity> nearby = wolf.level().getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != wolf && entity.isAlive() && wolf.hasLineOfSight(entity)
        );

        if (nearby.isEmpty())
            return null;

        // Pick a random target
        return nearby.get(wolf.getRandom().nextInt(nearby.size()));
    }

    @Override
    public void start() {
        if (target != null) {
            wolf.setTarget(target);

            // Mark mischief for today
            if (wolf instanceof WolfExtensions ext) {
                long currentDay = wolf.level().getGameTime() / 24000L;
                ext.betterdogs$setLastMischiefDay(currentDay);
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        // One attack and done - target is set, let normal attack AI handle it
        return false;
    }

    @Override
    public void stop() {
        this.target = null;
    }
}
