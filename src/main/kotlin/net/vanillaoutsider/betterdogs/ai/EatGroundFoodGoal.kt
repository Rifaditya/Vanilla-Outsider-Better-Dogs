package net.vanillaoutsider.betterdogs.ai

import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.animal.wolf.Wolf
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig
import net.vanillaoutsider.betterdogs.registry.BetterDogsTags
import java.util.*

/**
 * AI Goal for wolves to pick up and eat food items from the ground.
 * Wild wolves eat to heal. Tamed dogs also eat if enabled in config.
 */
class EatGroundFoodGoal(private val wolf: Wolf) : Goal() {

    private var targetFood: ItemEntity? = null

    companion object {
        private const val SEARCH_RANGE = 10.0
        private const val PICKUP_RANGE = 1.5
    }

    init {
        this.flags = EnumSet.of(Flag.MOVE, Flag.LOOK)
    }

    override fun canUse(): Boolean {
        if (wolf.health >= wolf.maxHealth) return false

        // If tamed, check if sitting and config
        if (wolf.isTame) {
            if (wolf.isInSittingPose) return false
        }

        // Find nearby food items
        val nearbyFood = wolf.level().getEntitiesOfClass(
            ItemEntity::class.java,
            wolf.boundingBox.inflate(SEARCH_RANGE)
        ) { itemEntity -> isEdible(itemEntity.item) }
            .minByOrNull { wolf.distanceTo(it) }

        if (nearbyFood != null) {
            targetFood = nearbyFood
            return true
        }

        return false
    }

    private fun isEdible(stack: ItemStack): Boolean {
        if (!wolf.isFood(stack)) return false

        // Wild wolves eat anything that is food
        if (!wolf.isTame) return true

        // Tamed dogs check config
        val config = BetterDogsConfig.get()
        
        if (stack.`is`(BetterDogsTags.RAW_FOOD)) {
            return config.enableDogsEatRawGroundFood
        }

        if (stack.`is`(BetterDogsTags.COOKED_FOOD)) {
            return config.enableDogsEatCookedGroundFood
        }

        // Fallback heuristic for modded food
        val path = stack.itemHolder.unwrapKey().map { it.location().path }.orElse("").lowercase()
        val isCooked = path.contains("cooked") || path.contains("roasted") || path.contains("grilled")

        return if (isCooked) {
            config.enableDogsEatCookedGroundFood
        } else {
            config.enableDogsEatRawGroundFood
        }
    }

    override fun start() {
        targetFood?.let {
            wolf.navigation.moveTo(it, 1.2)
        }
    }

    override fun tick() {
        targetFood?.let { food ->
            // Look at the food
            wolf.lookControl.setLookAt(food, 30f, 30f)

            // Check if close enough to eat
            if (wolf.distanceTo(food) <= PICKUP_RANGE * 1.5) {
                eatFood(food)
            } else {
                // Keep moving toward food
                wolf.navigation.moveTo(food, 1.2)
            }
        }
    }

    private fun eatFood(food: ItemEntity) {
        val stack = food.item
        
        // Healing logic: 2.0 default, 1.0 for rotten flesh
        var healAmount = 2.0f
        if (stack.`is`(Items.ROTTEN_FLESH)) {
            healAmount = 1.0f
        } else {
            val foodComp = stack.item.components.get(net.minecraft.core.component.DataComponents.FOOD)
            if (foodComp != null) {
                healAmount = foodComp.nutrition.toFloat() / 2.0f
            }
        }

        // Consume one item
        if (stack.count > 1) {
            stack.shrink(1)
        } else {
            food.discard()
        }

        // Heal the wolf
        wolf.heal(healAmount)

        targetFood = null
    }

    override fun canContinueToUse(): Boolean {
        val food = targetFood ?: return false

        // Stop if food is gone
        if (!food.isAlive) return false

        // Stop if fully healed
        if (wolf.health >= wolf.maxHealth) return false

        // Stop if ordered to sit while moving
        if (wolf.isTame && wolf.isInSittingPose) return false

        return true
    }

    override fun stop() {
        targetFood = null
        wolf.navigation.stop()
    }
}
