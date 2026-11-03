// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PersonalityDnaTest {

    @Test
    @DisplayName("Verify WolfPersonality Enum Conversions & Rotation")
    public void testPersonalityEnum() {
        Assertions.assertEquals(WolfPersonality.NORMAL, WolfPersonality.fromString("normal"));
        Assertions.assertEquals(WolfPersonality.AGGRESSIVE, WolfPersonality.fromString("AGGRESSIVE"));
        Assertions.assertEquals(WolfPersonality.PACIFIST, WolfPersonality.fromString("pacifist"));
        Assertions.assertEquals(WolfPersonality.NORMAL, WolfPersonality.fromString("unknown_value"));

        Assertions.assertEquals(WolfPersonality.AGGRESSIVE, WolfPersonality.NORMAL.next());
        Assertions.assertEquals(WolfPersonality.PACIFIST, WolfPersonality.AGGRESSIVE.next());
        Assertions.assertEquals(WolfPersonality.NORMAL, WolfPersonality.PACIFIST.next());
    }

    @Test
    @DisplayName("Verify Deterministic DNA Seed Calculation")
    public void testDnaSeedDeterminism() {
        UUID testUuid = UUID.fromString("12345678-1234-1234-1234-123456789abc");
        long seed1 = WolfPersistentData.generateDnaSeed(testUuid);
        long seed2 = WolfPersistentData.generateDnaSeed(testUuid);

        Assertions.assertEquals(seed1, seed2, "DNA seed calculation must be 100% deterministic for identical UUIDs.");
        Assertions.assertNotEquals(0L, seed1);
    }

    @Test
    @DisplayName("Verify WolfPersistentData NBT Serialization")
    public void testNbtSerialization() {
        CompoundTag tag = new CompoundTag();
        UUID uuid = UUID.randomUUID();
        long dnaSeed = WolfPersistentData.generateDnaSeed(uuid);

        WolfPersistentData.writeToNbt(tag, WolfPersonality.PACIFIST, 1.25f, dnaSeed);

        WolfPersonality readPersonality = WolfPersistentData.readPersonalityFromNbt(tag);
        float readScale = WolfPersistentData.readSocialScaleFromNbt(tag);
        long readSeed = WolfPersistentData.readDnaSeedFromNbt(tag, uuid);

        Assertions.assertEquals(WolfPersonality.PACIFIST, readPersonality);
        Assertions.assertEquals(1.25f, readScale, 0.0001f);
        Assertions.assertEquals(dnaSeed, readSeed);
    }
}
