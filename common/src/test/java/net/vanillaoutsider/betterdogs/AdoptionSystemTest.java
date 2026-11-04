// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdoptionSystemTest {

    @Test
    public void testAdoptionPersistence() {
        CompoundTag tag = new CompoundTag();
        BlockPos post = new BlockPos(0, 64, 0);

        // Dog listed for adoption
        WolfPersistentData.writeToNbt(tag, WolfPersonality.NORMAL, 1.0f, 98765L, "minecraft:cooked_beef", 0L, "", 0L, null, null, false, false, post, true);
        assertTrue(WolfPersistentData.readIsUpForAdoptionFromNbt(tag));

        // Adoption completed / cancelled
        WolfPersistentData.writeToNbt(tag, WolfPersonality.NORMAL, 1.0f, 98765L, "minecraft:cooked_beef", 0L, "", 0L, null, null, false, false, post, false);
        assertFalse(WolfPersistentData.readIsUpForAdoptionFromNbt(tag));
    }
}
