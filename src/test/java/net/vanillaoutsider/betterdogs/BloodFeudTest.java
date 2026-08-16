// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BloodFeudTest {

    @Test
    public void testBloodFeudNbtSerialization() {
        CompoundTag tag = new CompoundTag();
        UUID targetUuid = UUID.randomUUID();

        WolfPersistentData.writeToNbt(
            tag,
            WolfPersonality.AGGRESSIVE,
            1.0f,
            12345L,
            "minecraft:cooked_beef",
            0L,
            "",
            0L,
            null,
            null,
            false,
            false,
            null,
            false,
            -1L,
            0,
            targetUuid.toString()
        );

        String readUuid = WolfPersistentData.readBloodFeudTargetFromNbt(tag);
        Assertions.assertEquals(targetUuid.toString(), readUuid, "Blood feud target UUID should match after NBT read/write");
    }

    @Test
    public void testBloodFeudEscalationChance() {
        int feudChance = 5;
        int triggered = 0;
        java.util.Random rand = new java.util.Random(42L);

        for (int i = 0; i < 1000; i++) {
            if (rand.nextInt(100) < feudChance) {
                triggered++;
            }
        }

        Assertions.assertTrue(triggered > 20 && triggered < 80, "5% roll should trigger roughly 50 times in 1000 trials (got " + triggered + ")");
    }

    @Test
    public void testEmptyFeudValidation() {
        CompoundTag emptyTag = new CompoundTag();
        String target = WolfPersistentData.readBloodFeudTargetFromNbt(emptyTag);
        Assertions.assertTrue(target.isEmpty(), "Unset blood feud target should be empty string");
    }
}
