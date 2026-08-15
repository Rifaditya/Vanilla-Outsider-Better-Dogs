// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.vanillaoutsider.betterdogs.util.WolfGuardHelper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GuardModePatrolTest {

    @Test
    public void testGuardRadii() {
        assertEquals(12, WolfGuardHelper.getPatrolRadius(WolfPersonality.AGGRESSIVE));
        assertEquals(4, WolfGuardHelper.getPatrolRadius(WolfPersonality.PACIFIST));
        assertEquals(8, WolfGuardHelper.getPatrolRadius(WolfPersonality.NORMAL));
    }

    @Test
    public void testGuardPersistence() {
        CompoundTag tag = new CompoundTag();
        BlockPos post = new BlockPos(100, 64, -200);

        // Guarding active
        WolfPersistentData.writeToNbt(tag, WolfPersonality.AGGRESSIVE, 1.0f, 12345L, "minecraft:beef", 5000L, "minecraft:creeper", 10000L, null, null, false, true, post);
        assertTrue(WolfPersistentData.readIsGuardingFromNbt(tag));
        assertEquals(post, WolfPersistentData.readGuardPosFromNbt(tag));

        // Guarding deactivated
        WolfPersistentData.writeToNbt(tag, WolfPersonality.AGGRESSIVE, 1.0f, 12345L, "minecraft:beef", 5000L, "minecraft:creeper", 10000L, null, null, false, false, null);
        assertFalse(WolfPersistentData.readIsGuardingFromNbt(tag));
    }
}
