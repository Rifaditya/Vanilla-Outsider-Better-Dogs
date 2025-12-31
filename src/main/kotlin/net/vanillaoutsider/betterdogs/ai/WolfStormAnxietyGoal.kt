package net.vanillaoutsider.betterdogs.ai

import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.animal.wolf.Wolf
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.ai.util.DefaultRandomPos
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig
import java.util.EnumSet

/**
 * AI Goal for Wolf anxiety during thunderstorms.
 * - Whines occasionally.
 * - Shakes (uses wet dog shake animation as fear response).
 * - Paces around nervously (if not sitting).
 */
class WolfStormAnxietyGoal(private val wolf: Wolf) : Goal() {

    init {
        this.flags = EnumSet.of(Flag.MOVE, Flag.LOOK)
    }

    override fun canUse(): Boolean {
        // Only trigger if tamed
        if (!wolf.isTame) return false

        // Only during thunderstorms
        if (!wolf.level().isThundering) return false

        // Only if exposed to sky? or checking for skylight?
        // For simpler "fear of thunder", just the global weather is enough.
        // But maybe simple check to see if we aren't deep underground (optional, but keep simple for now)
        
        // Random chance to start being anxious (1% per tick)
        return wolf.random.nextFloat() < BetterDogsConfig.get().stormAnxietyTriggerChance
    }

    override fun canContinueToUse(): Boolean {
        // Continue as long as it thundering and we haven't finished our "anxiety episode"
        return wolf.level().isThundering && wolf.random.nextFloat() < 0.98 // Small chance to stop
    }

    override fun start() {
        // If not sitting, maybe pick a random spot to pace to
        if (!wolf.isOrderedToSit) {
            val target = DefaultRandomPos.getPos(wolf, 3, 2)
            if (target != null) {
                wolf.navigation.moveTo(target.x, target.y, target.z, 1.0)
            }
        }
    }

    override fun tick() {
        val config = BetterDogsConfig.get()
        // 1. Whining Sound (Low chance)
        if (wolf.random.nextFloat() < config.stormWhineChance) { // 2% chance per tick
            // Try standard GENERIC_HURT if available
            wolf.playSound(net.minecraft.sounds.SoundEvents.GENERIC_HURT, 1.0f, 2.0f)
        }

        // 2. Shaking (Visual fear)
        // We use the "wet" flag or similar to trigger shake if possible, or just subtle look changes.
        // Actually, invoking the full "shake off water" animation might be too much (interrupts movement).
        // Let's stick to sound + movement for now, maybe look around randomly.
        
        if (wolf.random.nextFloat() < 0.05) {
             wolf.lookControl.setLookAt(
                 wolf.x + (wolf.random.nextDouble() - 0.5) * 10, 
                 wolf.eyeY, 
                 wolf.z + (wolf.random.nextDouble() - 0.5) * 10, 
                 10.0f, 
                 wolf.maxHeadXRot.toFloat()
             )
        }
    }

    override fun stop() {
        wolf.navigation.stop()
    }
}
