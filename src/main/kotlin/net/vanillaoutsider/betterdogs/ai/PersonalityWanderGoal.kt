package net.vanillaoutsider.betterdogs.ai

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.animal.wolf.Wolf
import net.vanillaoutsider.betterdogs.WolfPersonality
import net.vanillaoutsider.betterdogs.asExtended

/**
 * A wrapper around WaterAvoidingRandomStrollGoal that respects personality.
 * Pacifist wolves are much less likely to wander.
 */
class PersonalityWanderGoal(
    private val wolf: Wolf,
    speedModifier: Double
) : WaterAvoidingRandomStrollGoal(wolf, speedModifier) {

    override fun canUse(): Boolean {
        // Vanilla checks (mounted, water, etc) done in super
        if (!super.canUse()) return false

        // Pacifist Tuning: Reduce wander chance significantly (25% of normal)
        if (wolf.isTame) {
            val ext = wolf.asExtended()
            if (ext.`betterdogs$getPersonality`() == WolfPersonality.PACIFIST) {
                // Vanilla chance is usually 1/120 per tick via random.
                // We add another gate.
                if (wolf.random.nextFloat() > 0.25f) {
                    return false
                }
            }
        }
        
        return true
    }
}
