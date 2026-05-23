// Verified against: EatGroundFoodGoal.java (26.1.2+)
package net.vanillaoutsider.betterdogs.ai;

import java.util.Comparator;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.registry.BetterDogsTags;

/**
 * AI Goal for wolves to pick up and eat food items from the ground.
 * Wild wolves eat to heal. Tamed dogs also eat if enabled by gamerules.
 */
public class EatGroundFoodGoal extends Goal {

    private final Wolf wolf;
    private ItemEntity targetFood;

    private static final double SEARCH_RANGE = 10.0;
    private static final double PICKUP_RANGE = 1.5;

    public EatGroundFoodGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (wolf.getHealth() >= wolf.getMaxHealth())
            return false;

        // If tamed, check if sitting and gamerules
        if (wolf.isTame()) {
            if (wolf.isInSittingPose())
                return false;
        }

        // Find nearby food items
        ItemEntity nearbyFood = wolf.level().getEntitiesOfClass(
                ItemEntity.class,
                wolf.getBoundingBox().inflate(SEARCH_RANGE),
                itemEntity -> isEdible(itemEntity.getItem())).stream()
                .min(Comparator.comparingDouble(wolf::distanceTo)).orElse(null);

        if (nearbyFood != null) {
            targetFood = nearbyFood;
            return true;
        }

        return false;
    }

    private boolean isEdible(ItemStack stack) {
        if (!wolf.isFood(stack))
            return false;

        // Wild wolves eat anything that is food (usually meat/rotten flesh)
        if (!wolf.isTame())
            return true;

        // Tamed dogs check gamerules
        if (stack.is(BetterDogsTags.RAW_FOOD)) {
            return BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_DOGS_EAT_RAW_FOOD);
        }

        if (stack.is(BetterDogsTags.COOKED_FOOD)) {
            return BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_DOGS_EAT_COOKED_FOOD);
        }

        // Fallback heuristic for modded food
        String path = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().toLowerCase();
        boolean isCooked = path.contains("cooked") || path.contains("roasted") || path.contains("grilled");

        if (isCooked) {
            return BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_DOGS_EAT_COOKED_FOOD);
        } else {
            return BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_DOGS_EAT_RAW_FOOD);
        }
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
            eatFood(targetFood);
        } else {
            // Keep moving toward food
            wolf.getNavigation().moveTo(targetFood, 1.2);
        }
    }

    private void eatFood(ItemEntity food) {
        ItemStack stack = food.getItem();
        
        // Healing logic: 2.0 default, 1.0 for rotten flesh
        float healAmount = 2.0f;
        if (stack.is(Items.ROTTEN_FLESH)) {
            healAmount = 1.0f;
        } else if (stack.getComponents().has(net.minecraft.core.component.DataComponents.FOOD)) {
            // Try to use nutrition for better scaling
            var foodComp = stack.getComponents().get(net.minecraft.core.component.DataComponents.FOOD);
            if (foodComp != null) {
                healAmount = (float) foodComp.nutrition() / 2.0f;
            }
        }

        // Consume one item
        if (stack.getCount() > 1) {
            stack.shrink(1);
        } else {
            food.discard();
        }

        // Heal the wolf
        wolf.heal(healAmount);

        targetFood = null;
    }

    @Override
    public boolean canContinueToUse() {
        if (targetFood == null)
            return false;

        // Stop if food is gone
        if (!targetFood.isAlive())
            return false;

        // Stop if fully healed
        if (wolf.getHealth() >= wolf.getMaxHealth())
            return false;

        // Stop if ordered to sit while moving
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
