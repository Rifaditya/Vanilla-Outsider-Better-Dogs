package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.EnumSet;
import java.util.Map;
import java.util.Comparator;
import java.util.HashMap;

/**
 * AI Goal for wolves to pick up and eat food items from the ground.
 * Wild wolves eat to heal, works for raw meats and rotten flesh.
 */
public class EatGroundFoodGoal extends Goal {

    private final Wolf wolf;
    private ItemEntity targetFood;

    // Food items wolves can eat and their heal amounts
    private static final Map<Item, Float> EDIBLE_FOODS = new HashMap<>();
    static {
        EDIBLE_FOODS.put(Items.BEEF, 2.0f);
        EDIBLE_FOODS.put(Items.PORKCHOP, 2.0f);
        EDIBLE_FOODS.put(Items.MUTTON, 2.0f);
        EDIBLE_FOODS.put(Items.CHICKEN, 2.0f);
        EDIBLE_FOODS.put(Items.RABBIT, 2.0f);
        EDIBLE_FOODS.put(Items.ROTTEN_FLESH, 1.0f);
    }

    private static final double SEARCH_RANGE = 10.0;
    private static final double PICKUP_RANGE = 1.5;

    public EatGroundFoodGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Only for wild wolves that need healing
        if (wolf.isTame())
            return false;
        if (wolf.getHealth() >= wolf.getMaxHealth())
            return false;

        // Find nearby food items
        ItemEntity nearbyFood = wolf.level().getEntitiesOfClass(
                ItemEntity.class,
                wolf.getBoundingBox().inflate(SEARCH_RANGE),
                itemEntity -> EDIBLE_FOODS.containsKey(itemEntity.getItem().getItem())).stream()
                .min(Comparator.comparingDouble(wolf::distanceTo)).orElse(null);

        if (nearbyFood != null) {
            targetFood = nearbyFood;
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
        if (wolf.distanceTo(targetFood) <= PICKUP_RANGE * 1.5) { // Increased slightly for better pickup
            eatFood(targetFood);
        } else {
            // Keep moving toward food
            wolf.getNavigation().moveTo(targetFood, 1.2);
        }
    }

    private void eatFood(ItemEntity food) {
        Item item = food.getItem().getItem();
        Float healAmount = EDIBLE_FOODS.get(item);
        if (healAmount == null)
            return;

        // Consume one item
        if (food.getItem().getCount() > 1) {
            food.getItem().shrink(1);
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

        // Stop if tamed
        if (wolf.isTame())
            return false;

        return true;
    }

    @Override
    public void stop() {
        targetFood = null;
        wolf.getNavigation().stop();
    }
}
