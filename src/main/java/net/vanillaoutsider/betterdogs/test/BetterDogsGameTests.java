// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: GameTestHelper.java, Wolf.java (26.3+)
package net.vanillaoutsider.betterdogs.test;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class BetterDogsGameTests {

    public static void testWolfSpawnAndInit(GameTestHelper helper) {
        BlockPos spawnPos = new BlockPos(1, 2, 1);
        Wolf wolf = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);

        helper.assertTrue(wolf != null, "Wolf entity failed to spawn");
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setPersonality(WolfPersonality.NORMAL);
            helper.assertTrue(ext.betterdogs$hasPersonality(), "Wolf personality failed to initialize");
        }

        helper.succeed();
    }

    public static void testGuardModeToggle(GameTestHelper helper) {
        BlockPos spawnPos = new BlockPos(2, 2, 2);
        Wolf wolf = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);

        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setGuardMode(true);
            ext.betterdogs$setGuardPos(spawnPos);

            helper.assertTrue(ext.betterdogs$isGuardMode(), "Guard mode toggle failed");
            helper.assertTrue(spawnPos.equals(ext.betterdogs$getGuardPos()), "Guard position anchor failed");
        }

        helper.succeed();
    }

    public static void testStateCleanupHooks(GameTestHelper helper) {
        BlockPos spawnPos = new BlockPos(3, 2, 3);
        Wolf wolf = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);

        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSoundLocationTarget(spawnPos);
            ext.betterdogs$setPassiveOverrideTicks(100);
            ext.betterdogs$clearTransientState();

            helper.assertTrue(ext.betterdogs$getSoundLocationTarget() == null, "Sound location target cleanup failed");
            helper.assertTrue(ext.betterdogs$getPassiveOverrideTicks() == 0, "Passive override ticks cleanup failed");
        }

        helper.succeed();
    }
}
