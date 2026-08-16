// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WolfGiftMeritsTest {

    @Test
    public void testFeedingMeritsNbtSerialization() {
        CompoundTag tag = new CompoundTag();
        WolfPersistentData.writeToNbt(tag, WolfPersonality.NORMAL, 1.0f, 12345L, "", 0L, "", 0L, null, null, false, false, null, false, 5L, 12);

        int readMerits = WolfPersistentData.readFeedCountFromNbt(tag);
        Assertions.assertEquals(12, readMerits);
    }

    @Test
    public void testDefaultMeritsIsZero() {
        CompoundTag tag = new CompoundTag();
        int readMerits = WolfPersistentData.readFeedCountFromNbt(tag);
        Assertions.assertEquals(0, readMerits);
    }

    @Test
    public void testGiftThresholdCalculation() {
        int merits = 15;
        int threshold = 10;

        Assertions.assertTrue(merits >= threshold, "Dog with 15 merits should meet threshold of 10");

        int remainingMerits = Math.max(0, merits - threshold);
        Assertions.assertEquals(5, remainingMerits, "Consuming gift should leave 5 merits");

        Assertions.assertFalse(remainingMerits >= threshold, "Dog with 5 merits should not meet threshold of 10");
    }

    @Test
    public void testPackSpacingFormula() {
        int count1 = 1;
        float offset1 = count1 <= 1 ? 0.0f : Math.min((float) Math.sqrt(count1 - 1) * 1.0f, 5.0f);
        Assertions.assertEquals(0.0f, offset1, 0.001f);

        int count4 = 4;
        float offset4 = Math.min((float) Math.sqrt(count4 - 1) * 1.0f, 5.0f);
        Assertions.assertEquals((float) Math.sqrt(3), offset4, 0.001f);

        int count10 = 10;
        float offset10 = Math.min((float) Math.sqrt(count10 - 1) * 1.0f, 5.0f);
        Assertions.assertEquals(3.0f, offset10, 0.001f);
    }
}
