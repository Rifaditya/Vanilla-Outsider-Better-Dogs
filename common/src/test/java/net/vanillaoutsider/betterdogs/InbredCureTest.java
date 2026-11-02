// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InbredCureTest {

    @Test
    public void testInbredStateClearing() {
        CompoundTag tag = new CompoundTag();
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        // Inbred state saved
        WolfPersistentData.writeToNbt(tag, WolfPersonality.AGGRESSIVE, 0.75f, 12345L, "minecraft:beef", 5000L, "minecraft:creeper", 10000L, p1, p2, true);
        assertTrue(WolfPersistentData.readIsInbredFromNbt(tag));
        assertEquals(0.75f, WolfPersistentData.readSocialScaleFromNbt(tag), 0.001f);

        // Simulated Cure: Inbred cleared, scale restored to 1.10f
        WolfPersistentData.writeToNbt(tag, WolfPersonality.AGGRESSIVE, 1.10f, 12345L, "minecraft:beef", 5000L, "minecraft:creeper", 10000L, p1, p2, false);
        assertFalse(WolfPersistentData.readIsInbredFromNbt(tag));
        assertEquals(1.10f, WolfPersistentData.readSocialScaleFromNbt(tag), 0.001f);
        assertEquals(p1, WolfPersistentData.readParentUUID1FromNbt(tag));
        assertEquals(p2, WolfPersistentData.readParentUUID2FromNbt(tag));
    }
}
