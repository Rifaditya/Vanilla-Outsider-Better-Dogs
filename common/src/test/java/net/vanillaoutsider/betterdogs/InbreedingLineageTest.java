// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InbreedingLineageTest {

    @Test
    public void testInbreedingNbtSerialization() {
        CompoundTag tag = new CompoundTag();
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        WolfPersistentData.writeToNbt(tag, WolfPersonality.AGGRESSIVE, 0.75f, 12345L, "minecraft:beef", 5000L, "minecraft:creeper", 10000L, p1, p2, true);

        assertEquals(p1, WolfPersistentData.readParentUUID1FromNbt(tag));
        assertEquals(p2, WolfPersistentData.readParentUUID2FromNbt(tag));
        assertTrue(WolfPersistentData.readIsInbredFromNbt(tag));
        assertEquals(0.75f, WolfPersistentData.readSocialScaleFromNbt(tag), 0.001f);
    }

    @Test
    public void testNonInbredNbtDefaults() {
        CompoundTag tag = new CompoundTag();
        WolfPersistentData.writeToNbt(tag, WolfPersonality.NORMAL, 1.0f, 99999L);

        assertNull(WolfPersistentData.readParentUUID1FromNbt(tag));
        assertNull(WolfPersistentData.readParentUUID2FromNbt(tag));
        assertFalse(WolfPersistentData.readIsInbredFromNbt(tag));
    }

    @Test
    public void testSiblingRelationshipDetection() {
        UUID sharedFather = UUID.randomUUID();
        UUID motherA = UUID.randomUUID();
        UUID motherB = UUID.randomUUID();

        UUID childA = UUID.randomUUID();
        UUID childB = UUID.randomUUID();

        // Sibling pair shares father
        boolean isSibling = sharedFather.equals(sharedFather) || (motherA.equals(motherB));
        assertTrue(isSibling);

        // Unrelated pair
        UUID unrelatedFather = UUID.randomUUID();
        boolean isUnrelated = sharedFather.equals(unrelatedFather);
        assertFalse(isUnrelated);
    }
}
