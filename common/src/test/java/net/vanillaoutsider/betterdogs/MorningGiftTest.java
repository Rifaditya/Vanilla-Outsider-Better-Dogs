// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.Bootstrap;
import net.vanillaoutsider.betterdogs.util.WolfGiftHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MorningGiftTest {

    @BeforeAll
    static void init() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

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

    @Test
    public void testMorningWindowAndSleepTrigger() {
        Assertions.assertTrue(WolfGiftHelper.isMorningOrWakingTicks(0L, false));
        Assertions.assertTrue(WolfGiftHelper.isMorningOrWakingTicks(2000L, false));
        Assertions.assertTrue(WolfGiftHelper.isMorningOrWakingTicks(24000L, false));

        Assertions.assertFalse(WolfGiftHelper.isMorningOrWakingTicks(2001L, false));
        Assertions.assertFalse(WolfGiftHelper.isMorningOrWakingTicks(6000L, false));
        Assertions.assertFalse(WolfGiftHelper.isMorningOrWakingTicks(14000L, false));
        Assertions.assertFalse(WolfGiftHelper.isMorningOrWakingTicks(-1000L, false));

        Assertions.assertTrue(WolfGiftHelper.isMorningOrWakingTicks(14000L, true));
        Assertions.assertTrue(WolfGiftHelper.isMorningOrWakingTicks(-24000L, false));
    }

    @Test
    public void testDayCountCalculationAndSleepProgression() {
        Assertions.assertEquals(0L, WolfGiftHelper.calculateDayCount(18000L));
        Assertions.assertEquals(1L, WolfGiftHelper.calculateDayCount(24000L));
        Assertions.assertEquals(-1L, WolfGiftHelper.calculateDayCount(-1L));
        Assertions.assertEquals(-1L, WolfGiftHelper.calculateDayCount(-24000L));
    }

    @Test
    public void testNullSafety() {
        Assertions.assertFalse(WolfGiftHelper.canDeliverGift(null, null));
        Assertions.assertFalse(WolfGiftHelper.hasDaylightCycle(null));
        Assertions.assertEquals(0L, WolfGiftHelper.getCelestialDayTime(null));
        Assertions.assertEquals(0L, WolfGiftHelper.getDayCount(null));
        Assertions.assertFalse(WolfGiftHelper.isMorningOrWaking(null, null));
    }

    @Test
    public void testWolfGiftHelperSourceContract() throws IOException {
        String source = Files.readString(resolveWolfGiftHelperSource(), StandardCharsets.UTF_8);
        Assertions.assertFalse(source.contains("getGameTime() / 24000L"));
        Assertions.assertFalse(source.contains("getGameTime() % 24000L"));
        Assertions.assertTrue(source.contains("getDayTime()"));
        Assertions.assertTrue(source.contains("hasDaylightCycle"));
        Assertions.assertTrue(source.contains("Math.floorDiv"));
        Assertions.assertTrue(source.contains("Math.floorMod"));
        Assertions.assertTrue(source.contains("isMorningOrWaking(level, owner)"));
    }

    private static Path resolveWolfGiftHelperSource() {
        Path cursor = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (cursor != null) {
            Path direct = cursor.resolve("src/main/java/net/vanillaoutsider/betterdogs/util/WolfGiftHelper.java");
            if (Files.exists(direct)) {
                return direct;
            }
            Path common = cursor.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfGiftHelper.java");
            if (Files.exists(common)) {
                return common;
            }
            cursor = cursor.getParent();
        }
        throw new AssertionError("Unable to locate WolfGiftHelper.java from " + System.getProperty("user.dir"));
    }
}
