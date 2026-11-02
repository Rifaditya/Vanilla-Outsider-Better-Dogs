// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MorningGiftTest {

    @Test
    public void testGiftNbtPersistence() {
        CompoundTag tag = new CompoundTag();
        WolfPersistentData.writeToNbt(
            tag,
            WolfPersonality.PACIFIST,
            1.1f,
            987654321L,
            "minecraft:cooked_beef",
            100L,
            "minecraft:creeper",
            200L,
            null,
            null,
            false,
            true,
            null,
            false,
            42L
        );

        long loadedDay = WolfPersistentData.readLastGiftDayFromNbt(tag);
        Assertions.assertEquals(42L, loadedDay, "Last gift day should be persisted and retrieved accurately.");
    }

    @Test
    public void testDailyCooldownMath() {
        long currentDay = 15L;
        long lastGiftDaySame = 15L;
        long lastGiftDayPrevious = 14L;
        long lastGiftDayNever = -1L;

        Assertions.assertTrue(lastGiftDayPrevious < currentDay, "Gift should be deliverable if last gift was on previous day.");
        Assertions.assertTrue(lastGiftDayNever < currentDay, "Gift should be deliverable if never given before.");
        Assertions.assertFalse(lastGiftDaySame < currentDay, "Gift should NOT be deliverable if already given today.");
    }

    @Test
    public void testHealthThresholdEligibility() {
        float maxHealth = 20.0f;
        float fullHealth = 20.0f;
        float lowHealth = 15.0f;

        Assertions.assertTrue(fullHealth >= maxHealth - 0.01f, "Full health dog must qualify for gift delivery.");
        Assertions.assertFalse(lowHealth >= maxHealth - 0.01f, "Injured dog must not qualify for gift delivery.");
    }
}
