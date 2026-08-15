// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfHazardHelper;

/**
 * AI Goal for wolves to avoid hazardous thermal blocks (Lava, Fire, Magma, Lit Campfires).
 */
public class AvoidHazardsGoal extends Goal {

    private final Wolf wolf;
    private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos mutableBelowPos = new BlockPos.MutableBlockPos();

    public AvoidHazardsGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (wolf.getNavigation().isDone()) {
            return false;
        }

        Level level = wolf.level();
        if (level == null || level.isClientSide()) {
            return false;
        }

        if (!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_CLIFF_SAFETY, true)) {
            return false;
        }

        Path path = wolf.getNavigation().getPath();
        if (path == null) {
            return false;
        }

        int checkLimit = Math.min(path.getNodeCount(), 8);
        for (int i = 0; i < checkLimit; i++) {
            Node node = path.getNode(i);
            mutablePos.set(node.x, node.y, node.z);
            mutableBelowPos.set(node.x, node.y - 1, node.z);

            if (WolfHazardHelper.isThermalHazard(level.getBlockState(mutablePos)) ||
                WolfHazardHelper.isThermalHazard(level.getBlockState(mutableBelowPos))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void start() {
        wolf.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}
