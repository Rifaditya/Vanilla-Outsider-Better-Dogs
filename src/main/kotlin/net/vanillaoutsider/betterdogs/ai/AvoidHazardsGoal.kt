package net.vanillaoutsider.betterdogs.ai

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.animal.wolf.Wolf
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.pathfinder.PathType
import java.util.EnumSet

/**
 * AI Goal for wolves to avoid hazardous blocks.
 * Prevents wolves from walking into lava, fire, or off high ledges.
 */
class AvoidHazardsGoal(
    private val wolf: Wolf
) : Goal() {
    
    companion object {
        private const val MAX_SAFE_FALL = 3
        private const val CHECK_RANGE = 2
    }
    
    init {
        flags = EnumSet.of(Flag.MOVE)
    }
    
    override fun canUse(): Boolean {
        // Check if there's a hazard in our movement path
        val path = wolf.navigation.path ?: return false
        
        for (i in 0 until minOf(path.nodeCount, 5)) {
            val node = path.getNode(i)
            val pos = BlockPos(node.x, node.y, node.z)
            
            if (isHazard(pos)) {
                return true
            }
        }
        
        return false
    }
    
    private fun isHazard(pos: BlockPos): Boolean {
        val level = wolf.level()
        val block = level.getBlockState(pos).block
        
        // Check for lava or fire
        if (block == Blocks.LAVA || block == Blocks.FIRE || block == Blocks.SOUL_FIRE) {
            return true
        }
        
        // Check for high fall
        var fallDistance = 0
        var checkPos = pos.below()
        while (fallDistance < 10 && level.getBlockState(checkPos).isAir) {
            fallDistance++
            checkPos = checkPos.below()
        }
        
        if (fallDistance > MAX_SAFE_FALL) {
            return true
        }
        
        return false
    }
    
    override fun start() {
        // Stop current navigation to avoid hazard
        wolf.navigation.stop()
    }
    
    override fun canContinueToUse(): Boolean {
        return false // One-shot goal
    }
}
