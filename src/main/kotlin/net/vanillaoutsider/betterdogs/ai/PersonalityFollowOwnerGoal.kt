package net.vanillaoutsider.betterdogs.ai

import net.minecraft.world.entity.ai.goal.FollowOwnerGoal
import net.minecraft.world.entity.animal.wolf.Wolf
import net.vanillaoutsider.betterdogs.WolfPersonality
import net.vanillaoutsider.betterdogs.asExtended
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig

/**
 * A custom FollowOwnerGoal that adjusts its start/stop distance based on the Wolf's personality.
 * This allows "Granular" control via the config.
 */
class PersonalityFollowOwnerGoal(
    private val wolf: Wolf,
    private val speedModifier: Double,
    private val leavesAllowed: Boolean
) : FollowOwnerGoal(wolf, speedModifier, 10.0f, 2.0f) {

    private var recalcTimer = 0

    // Helper to get config based on personality
    private fun getStartDistance(): Float {
        if (!wolf.isTame) return 10.0f // Fallback
        
        val ext = wolf.asExtended()
        return try {
             when (ext.`betterdogs$getPersonality`()) {
                WolfPersonality.AGGRESSIVE -> BetterDogsConfig.get().aggressiveFollowStart
                WolfPersonality.PACIFIST -> BetterDogsConfig.get().pacifistFollowStart
                WolfPersonality.NORMAL -> BetterDogsConfig.get().normalFollowStart
                else -> 10.0f
            }
        } catch (e: Exception) {
            10.0f
        }
    }

    private fun getStopDistance(): Float {
        if (!wolf.isTame) return 2.0f
        
        val ext = wolf.asExtended()
        return try {
            when (ext.`betterdogs$getPersonality`()) {
                WolfPersonality.AGGRESSIVE -> BetterDogsConfig.get().aggressiveFollowStop
                WolfPersonality.PACIFIST -> BetterDogsConfig.get().pacifistFollowStop
                WolfPersonality.NORMAL -> BetterDogsConfig.get().normalFollowStop
                else -> 2.0f
            }
        } catch (e: Exception) {
            2.0f
        }
    }

    /**
     * We override tick to hijack the logic.
     * Since FollowOwnerGoal's fields (startDistance, stopDistance) are private/final,
     * we cannot easily change them.
     * 
     * However, the main logic is in canUse() and canContinueToUse() and tick().
     * We can reimplement the checks using our dynamic getters.
     */
     
    // We override canUse to use our dynamic start distance
    override fun canUse(): Boolean {
        // Base checks from vanilla FollowOwnerGoal
        val owner = wolf.owner ?: return false
        if (owner.isSpectator) return false
        
        // Use OUR dynamic start distance
        if (wolf.distanceToSqr(owner) < (getStartDistance() * getStartDistance())) {
            return false
        }
        
        // Respect sitting/leashed
        if (wolf.isOrderedToSit || wolf.isLeashed) return false
        
        return true
    }

    // We override canContinueToUse to use our dynamic stop distance
    override fun canContinueToUse(): Boolean {
        if (wolf.navigation.isDone) return false
        if (wolf.isOrderedToSit || wolf.isLeashed) return false
        
        val owner = wolf.owner ?: return false
        return wolf.distanceToSqr(owner) > (getStopDistance() * getStopDistance())
    }

    override fun start() {
        super.start()
    }

    override fun stop() {
        wolf.navigation.stop()
        super.stop()
    }

    override fun tick() {
        // Teleport Logic (usually done in tick)
        // Vanilla: if dist > startDist (or 12), teleport.
        // We use getStartDistance() as the threshold for consistency,
        // or we can just call super.tick() IF we can trust it.
        // BUT super.tick() uses the hardcoded '10.0f' we passed in constructor.
        
        // Wait, super.tick() calls helper methods. 
        // Let's reimplement tick to be safe and use our values.
        
        val owner = wolf.owner ?: return
        wolf.lookControl.setLookAt(owner, 10.0f, wolf.maxHeadXRot.toFloat())
        
        if (--this.recalcTimer <= 0) {
            this.recalcTimer = 10
            
            // Move to owner
            if (!wolf.isLeashed && !wolf.isPassenger) {
                 if (wolf.distanceToSqr(owner) >= (getStartDistance() * getStartDistance())) {
                     wolf.navigation.moveTo(owner, speedModifier)
                 } else {
                     wolf.navigation.stop()
                 }
            }
        }
        
        // Teleport check
        // If distance is significantly larger than start distance (e.g. +2 blocks or just start distance)
        // Vanilla constant is usually 12 (144 sqr).
        // Let's use startDistance + 2 as teleport range? 
        // The user config says "Follow Start / Teleport Dist". So we treat them as same or close.
        // Let's say if dist > startDistance, we TRY to walk.
        // If dist > startDistance * 2 (or a hard cap), we teleport?
        // Actually, vanilla FollowOwnerGoal has `teleportToOwner()` logic.
        // We should just call the internal teleport logic if we are broken.
        
        // Simplified: Call super.tick() creates movement based on the 10.0 we passed in constructor.
        // WE CANNOT rely on super.tick() because it uses the old values.
        // We must fully implement movement here.
        
        if (!wolf.isLeashed && !wolf.isPassenger) {
             // Teleport if very far
             if (wolf.distanceToSqr(owner) >= 144.0 || wolf.distanceToSqr(owner) >= (getStartDistance() * getStartDistance() * 2)) {
                 // Custom teleport logic since super.teleportToOwner is private/inaccessible
                 for (i in 0..9) {
                     val dx = (wolf.random.nextFloat() - 0.5) * 4.0
                     val dy = (wolf.random.nextFloat() - 0.5) * 4.0
                     val dz = (wolf.random.nextFloat() - 0.5) * 4.0
                     if (wolf.randomTeleport(owner.x + dx, owner.y + dy, owner.z + dz, false)) break
                 }
             }
        }
    }
}
