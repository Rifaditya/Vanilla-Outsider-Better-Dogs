// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.vanillaoutsider.betterdogs.util.WolfScavengeHelper;

/**
 * AI Goal for wolves to pick up and eat food items from the ground.
 * Wild wolves eat to heal. Tamed dogs also eat if enabled by gamerules.
 */
public class EatGroundFoodGoal extends Goal {

    private final Wolf wolf;
    private ItemEntity targetFood;
    private int checkCooldown = 0;

    private static final double SEARCH_RANGE = 10.0;
    private static final double PICKUP_RANGE = 1.5;

    public EatGroundFoodGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.checkCooldown > 0) {
            this.checkCooldown--;
            return false;
        }
        this.checkCooldown = 10 + wolf.getRandom().nextInt(11); // Cooldown of 10-20 ticks

        if (!WolfScavengeHelper.canScavenge(this.wolf)) {
            return false;
        }

        // Find nearby food items without stream API / lambda allocations
        List<ItemEntity> items = wolf.level().getEntitiesOfClass(
                ItemEntity.class,
                wolf.getBoundingBox().inflate(SEARCH_RANGE)
        );

        ItemEntity closestFood = null;
        double closestDistanceSqr = Double.MAX_VALUE;

        for (ItemEntity itemEntity : items) {
            if (itemEntity.isAlive() && WolfScavengeHelper.isEdible(wolf, itemEntity.getItem())) {
                double distSqr = wolf.distanceToSqr(itemEntity);
                if (distSqr < closestDistanceSqr) {
                    closestDistanceSqr = distSqr;
                    closestFood = itemEntity;
                }
            }
        }

        if (closestFood != null) {
            targetFood = closestFood;
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        if (targetFood == null)
            return;
        wolf.getNavigation().moveTo(targetFood, 1.2);
    }

    @Override
    public void tick() {
        if (targetFood == null)
            return;

        // Look at the food
        wolf.getLookControl().setLookAt(targetFood, 30f, 30f);

        // Check if close enough to eat
        if (wolf.distanceTo(targetFood) <= PICKUP_RANGE * 1.5) {
            WolfScavengeHelper.consumeGroundFood(wolf, targetFood);
            targetFood = null;
        } else {
            // Keep moving toward food
            wolf.getNavigation().moveTo(targetFood, 1.2);
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (targetFood == null)
            return false;

        // Stop if food is gone
        if (!targetFood.isAlive())
            return false;

        // Stop if fully healed or sitting
        if (wolf.getHealth() >= wolf.getMaxHealth())
            return false;

        if (wolf.isTame() && wolf.isInSittingPose())
            return false;

        return true;
    }

    @Override
    public void stop() {
        targetFood = null;
        wolf.getNavigation().stop();
    }
}
