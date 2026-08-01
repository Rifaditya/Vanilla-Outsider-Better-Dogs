// Verified against: AvoidHazardsGoal.java (26.1.2+), BlockPos.java (26.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

/**
 * AI Goal for wolves to avoid hazardous blocks.
 * Prevents wolves from walking into lava, fire, or off high ledges.
 */
public class AvoidHazardsGoal extends Goal {

    private final Wolf wolf;
    private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos mutableCheckPos = new BlockPos.MutableBlockPos();

    public AvoidHazardsGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // Check if there's a hazard in our movement path
        if (wolf.getNavigation().isDone())
            return false;

        Path path = wolf.getNavigation().getPath();
        if (path == null)
            return false;

        int checkLimit = Math.min(path.getNodeCount(), BetterDogsConfig.get().getHazardCheckLimit());
        for (int i = 0; i < checkLimit; i++) {
            Node node = path.getNode(i);
            mutablePos.set(node.x, node.y, node.z);

            if (isHazard(mutablePos)) {
                return true;
            }
        }

        return false;
    }

    private boolean isHazard(BlockPos pos) {
        Level level = wolf.level();
        BlockState state = level.getBlockState(pos);

        // Check for lava, magma block, or fire
        if (state.is(Blocks.LAVA) || state.is(Blocks.MAGMA_BLOCK) || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
            return true;
        }

        // Check for high fall
        int fallDistance = 0;
        mutableCheckPos.set(pos.getX(), pos.getY() - 1, pos.getZ());
        int searchLimit = BetterDogsConfig.get().getHazardFallSearchLimit();
        while (fallDistance < searchLimit && level.getBlockState(mutableCheckPos).isAir()) {
            fallDistance++;
            mutableCheckPos.setY(mutableCheckPos.getY() - 1);
        }

        return fallDistance > BetterDogsConfig.get().getMaxSafeFall();
    }

    @Override
    public void start() {
        // Stop current navigation to avoid hazard
        wolf.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return false; // One-shot goal
    }
}
