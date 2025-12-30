package net.vanillaoutsider.betterdogs.ai

import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.animal.wolf.Wolf
import net.minecraft.world.item.Items
import java.util.EnumSet

/**
 * AI Goal for wolves to pick up and eat food items from the ground.
 * Wild wolves eat to heal, works for raw meats and rotten flesh.
 */
class EatGroundFoodGoal(
    private val wolf: Wolf
) : Goal() {
    
    private var targetFood: ItemEntity? = null
    
    companion object {
        // Food items wolves can eat and their heal amounts
        private val EDIBLE_FOODS = mapOf(
            Items.BEEF to 2.0f,
            Items.PORKCHOP to 2.0f,
            Items.MUTTON to 2.0f,
            Items.CHICKEN to 2.0f,
            Items.RABBIT to 2.0f,
            Items.ROTTEN_FLESH to 1.0f
        )
        
        private const val SEARCH_RANGE = 10.0
        private const val PICKUP_RANGE = 1.5
    }
    
    init {
        flags = EnumSet.of(Flag.MOVE, Flag.LOOK)
    }
    
    override fun canUse(): Boolean {
        // Only for wild wolves that need healing
        if (wolf.isTame) return false
        if (wolf.health >= wolf.maxHealth) return false
        
        // Find nearby food items
        val nearbyFood = wolf.level().getEntitiesOfClass(
            ItemEntity::class.java,
            wolf.boundingBox.inflate(SEARCH_RANGE)
        ) { itemEntity ->
            EDIBLE_FOODS.containsKey(itemEntity.item.item)
        }.minByOrNull { wolf.distanceTo(it) }
        
        if (nearbyFood != null) {
            targetFood = nearbyFood
            return true
        }
        
        return false
    }
    
    override fun start() {
        val food = targetFood ?: return
        wolf.navigation.moveTo(food, 1.2)
    }
    
    override fun tick() {
        val food = targetFood ?: return
        
        // Look at the food
        wolf.lookControl.setLookAt(food, 30f, 30f)
        
        // Check if close enough to eat
        if (wolf.distanceTo(food) <= PICKUP_RANGE) {
            eatFood(food)
        } else {
            // Keep moving toward food
            wolf.navigation.moveTo(food, 1.2)
        }
    }
    
    private fun eatFood(food: ItemEntity) {
        val item = food.item.item
        val healAmount = EDIBLE_FOODS[item] ?: return
        
        // Consume one item
        if (food.item.count > 1) {
            food.item.shrink(1)
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
        
        // Stop if tamed (shouldn't happen mid-goal but safety check)
        if (wolf.isTame) return false
        
        return true
    }
    
    override fun stop() {
        targetFood = null
        wolf.navigation.stop()
    }
}
