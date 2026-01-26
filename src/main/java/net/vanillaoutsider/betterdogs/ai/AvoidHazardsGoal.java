package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Node;
import java.util.EnumSet;

/**
 * AI Goal for wolves to avoid hazardous blocks.
 * Prevents wolves from walking into lava, fire, or off high ledges.
 */
public class AvoidHazardsGoal extends Goal {

    private final Wolf wolf;

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
            BlockPos pos = new BlockPos(node.x, node.y, node.z);

            if (isHazard(pos)) {
                return true;
            }
        }

        return false;
    }

    private boolean isHazard(BlockPos pos) {
        Level level = wolf.level();
        BlockState state = level.getBlockState(pos);

        // Check for lava or fire
        if (state.is(Blocks.LAVA) || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
            return true;
        }

        // Check for high fall
        int fallDistance = 0;
        BlockPos checkPos = pos.below();
        int searchLimit = BetterDogsConfig.get().getHazardFallSearchLimit();
        while (fallDistance < searchLimit && level.getBlockState(checkPos).isAir()) {
            fallDistance++;
            checkPos = checkPos.below();
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
