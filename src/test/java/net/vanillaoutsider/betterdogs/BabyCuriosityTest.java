// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.vanillaoutsider.betterdogs.ai.BabyCuriosityGoal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BabyCuriosityTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testInterestingBlocksIdentification() {
        Assertions.assertTrue(BabyCuriosityGoal.isInterestingBlock(Blocks.POPPY.defaultBlockState()));
        Assertions.assertTrue(BabyCuriosityGoal.isInterestingBlock(Blocks.DANDELION.defaultBlockState()));
        Assertions.assertTrue(BabyCuriosityGoal.isInterestingBlock(Blocks.SHORT_GRASS.defaultBlockState()));
        Assertions.assertTrue(BabyCuriosityGoal.isInterestingBlock(Blocks.TALL_GRASS.defaultBlockState()));
        Assertions.assertTrue(BabyCuriosityGoal.isInterestingBlock(Blocks.OAK_LEAVES.defaultBlockState()));
        Assertions.assertTrue(BabyCuriosityGoal.isInterestingBlock(Blocks.PUMPKIN.defaultBlockState()));
        Assertions.assertFalse(BabyCuriosityGoal.isInterestingBlock(Blocks.STONE.defaultBlockState()));
        Assertions.assertFalse(BabyCuriosityGoal.isInterestingBlock(Blocks.DIRT.defaultBlockState()));
    }
}
