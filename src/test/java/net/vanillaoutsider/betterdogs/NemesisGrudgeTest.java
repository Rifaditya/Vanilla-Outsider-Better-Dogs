// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.minecraft.nbt.CompoundTag;
import net.vanillaoutsider.betterdogs.util.WolfNemesisHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NemesisGrudgeTest {

    @Test
    @DisplayName("Verify 3-Day Grudge Duration Tick Calculation")
    public void testGrudgeDurationCalculation() {
        long currentGameTime = 10000L;
        int durationDays = 3;
        long expiryTime = currentGameTime + (durationDays * WolfNemesisHelper.TICKS_PER_DAY);

        Assertions.assertEquals(10000L + 72000L, expiryTime, "3 in-game days must equal 72,000 game ticks.");
        Assertions.assertTrue(expiryTime > currentGameTime);
    }

    @Test
    @DisplayName("Verify Nemesis Data NBT Serialization")
    public void testNemesisNbtSerialization() {
        CompoundTag tag = new CompoundTag();
        String nemesisType = "minecraft:skeleton";
        long expiryTime = 82000L;

        WolfPersistentData.writeToNbt(tag, WolfPersonality.AGGRESSIVE, 1.0f, 12345L, "", 0L, nemesisType, expiryTime);

        String readNemesis = WolfPersistentData.readNemesisTypeFromNbt(tag);
        long readExpiry = WolfPersistentData.readNemesisExpiryFromNbt(tag);

        Assertions.assertEquals(nemesisType, readNemesis);
        Assertions.assertEquals(expiryTime, readExpiry);
    }
}
