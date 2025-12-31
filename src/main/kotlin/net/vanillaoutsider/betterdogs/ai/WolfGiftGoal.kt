package net.vanillaoutsider.betterdogs.ai

import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.animal.wolf.Wolf
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.sounds.SoundEvents
import net.vanillaoutsider.betterdogs.WolfPersonality
import net.vanillaoutsider.betterdogs.getPersistedPersonality
import net.vanillaoutsider.betterdogs.hasPersistedPersonality
import java.util.EnumSet
import kotlin.random.Random

/**
 * Handles gift bring behavior for Aggressive and Pacifist wolves.
 */
class WolfGiftGoal(private val wolf: Wolf) : Goal() {

    private var cooldown = 12000 // Default 10 minutes (in ticks)

    init {
        this.flags = EnumSet.of(Flag.LOOK)
    }

    override fun canUse(): Boolean {
        if (!wolf.isTame || !wolf.hasPersistedPersonality()) return false
        
        // Decrement cooldown
        if (cooldown > 0) {
            cooldown--
            return false
        }
        
        // Random chance to trigger logic this tick once cooldown is done
        return wolf.random.nextFloat() < 0.01
    }

    override fun start() {
        // Reset cooldown (10-15 minutes)
        cooldown = 12000 + wolf.random.nextInt(6000)
        
        val personality = wolf.getPersistedPersonality()
        when (personality) {
            WolfPersonality.AGGRESSIVE -> attemptAggressiveGift()
            WolfPersonality.PACIFIST -> attemptPacifistGift()
            else -> {}
        }
    }

    private fun attemptAggressiveGift() {
        // "Kills off-screen mob" - spawn loot near wolf
        val loot = when (Random.nextInt(100)) {
            in 0..39 -> ItemStack(Items.BONE)
            in 40..74 -> ItemStack(Items.ROTTEN_FLESH)
            in 75..89 -> ItemStack(Items.ARROW)
            else -> ItemStack(Items.IRON_NUGGET)
        }
        spawnGift(loot)
    }

    private fun attemptPacifistGift() {
        // "Forager" - spawns nature items
        val loot = when (Random.nextInt(100)) {
            in 0..29 -> ItemStack(Items.SWEET_BERRIES)
            in 30..54 -> ItemStack(Items.WHEAT_SEEDS) // Basic seeds
            in 55..74 -> ItemStack(Items.DANDELION) // Placeholder for "Random Flower"
            in 75..89 -> ItemStack(Items.RED_MUSHROOM)
            else -> ItemStack(Items.GLOW_BERRIES)
        }
        spawnGift(loot)
    }

    private fun spawnGift(stack: ItemStack) {
        val level = wolf.level()
        val itemEntity = ItemEntity(level, wolf.x, wolf.y + 0.5, wolf.z, stack)
        itemEntity.setDefaultPickUpDelay()
        level.addFreshEntity(itemEntity)
        
        // Wolf barks to alert owner
        // wolf.playSound(SoundEvents.ENTITY_WOLF_AMBIENT, 1.0f, 1.0f)
    }
}
