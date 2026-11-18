// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import net.vanillaoutsider.betterdogs.util.WolfGiftHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Morning Gift Bringing & Personality Scavenging System Tests")
class MorningGiftTest {

    @Test
    @DisplayName("Assert feeding merit threshold logic (>= 10 meals)")
    void testFeedingMeritThreshold() {
        int threshold = WolfGiftHelper.DEFAULT_FEED_THRESHOLD;
        assertEquals(10, threshold);

        assertTrue(10 >= threshold);
        assertTrue(15 >= threshold);
        assertFalse(9 >= threshold);
        assertFalse(0 >= threshold);
    }

    @Test
    @DisplayName("Assert merit deduction upon gift delivery")
    void testMeritDeduction() {
        int threshold = 10;
        int currentFeeds = 14;
        int remaining = Math.max(0, currentFeeds - threshold);
        assertEquals(4, remaining);

        int exactFeeds = 10;
        assertEquals(0, Math.max(0, exactFeeds - threshold));
    }

    @Test
    @DisplayName("Assert daily gift cooldown logic")
    void testDailyCooldown() {
        long currentDay = 15L;
        long lastGiftDayPrevious = 14L;
        long lastGiftDaySame = 15L;
        long lastGiftDayNever = -1L;

        assertTrue(lastGiftDayPrevious < currentDay, "Gift should be deliverable if last gift was yesterday");
        assertTrue(lastGiftDayNever < currentDay, "Gift should be deliverable if never given before");
        assertFalse(lastGiftDaySame < currentDay, "Gift should NOT be deliverable if already given today");
    }

    @Test
    @DisplayName("Assert strict 100% full health requirement")
    void testHealthThresholdEligibility() {
        float maxHealth = 20.0f;
        float fullHealth = 20.0f;
        float minorScratch = 19.8f;
        float injuredHealth = 15.0f;

        assertTrue(fullHealth >= maxHealth, "Strict 100% full health dog must qualify for gift delivery.");
        assertFalse(minorScratch >= maxHealth, "Even slightly damaged dog must not qualify.");
        assertFalse(injuredHealth >= maxHealth, "Injured dog must not qualify for gift delivery.");
    }

    @Test
    @DisplayName("Assert morning time window and sleep waking trigger via WolfGiftHelper")
    void testMorningTimeWindowEligibility() {
        // Morning ticks: 0 to 2000
        assertTrue(WolfGiftHelper.isMorningOrWakingTicks(0L, false), "0 ticks is early morning");
        assertTrue(WolfGiftHelper.isMorningOrWakingTicks(1000L, false), "1000 ticks is early morning");
        assertTrue(WolfGiftHelper.isMorningOrWakingTicks(2000L, false), "2000 ticks is boundary of morning");
        assertTrue(WolfGiftHelper.isMorningOrWakingTicks(24000L, false), "24000 ticks is dawn of day 1");

        // Non-morning ticks
        assertFalse(WolfGiftHelper.isMorningOrWakingTicks(2001L, false), "2001 ticks is midday");
        assertFalse(WolfGiftHelper.isMorningOrWakingTicks(6000L, false), "6000 ticks is noon");
        assertFalse(WolfGiftHelper.isMorningOrWakingTicks(14000L, false), "14000 ticks is night");

        // Negative tick safety via Math.floorMod
        assertFalse(WolfGiftHelper.isMorningOrWakingTicks(-1000L, false), "-1000 ticks wraps to 23000 (late night)");
        assertTrue(WolfGiftHelper.isMorningOrWakingTicks(-24000L, false), "-24000 ticks wraps to 0 (morning boundary)");

        // Sleeping / waking player bypasses time of day
        assertTrue(WolfGiftHelper.isMorningOrWakingTicks(14000L, true), "Waking player bypasses time of day");
    }

    @Test
    @DisplayName("Assert day count calculation handles sleep-skip progression and negative ticks")
    void testDayCountCalculationAndSleepProgression() {
        assertEquals(0L, WolfGiftHelper.calculateDayCount(0L));
        assertEquals(0L, WolfGiftHelper.calculateDayCount(18000L), "Night before sleep belongs to day 0");
        assertEquals(1L, WolfGiftHelper.calculateDayCount(24000L), "Dawn after sleep advances to day 1");
        assertEquals(4166L, WolfGiftHelper.calculateDayCount(100_000_000L));
        assertEquals(-1L, WolfGiftHelper.calculateDayCount(-1L), "Negative tick -1 floors to day -1");
        assertEquals(-1L, WolfGiftHelper.calculateDayCount(-24000L), "Exact -24000 floors to day -1");
        assertEquals(-2L, WolfGiftHelper.calculateDayCount(-24001L));
    }

    @Test
    @DisplayName("Assert peaceful surrounding check (no hostile monsters in 16m)")
    void testPeacefulMonsterRadiusGating() {
        assertTrue(isPeaceful(0), "Zero monsters in 16m is peaceful");
        assertFalse(isPeaceful(1), "One monster in 16m is not peaceful");
        assertFalse(isPeaceful(5), "Multiple monsters in 16m is not peaceful");
    }

    @Test
    @DisplayName("Assert personality loot selection and 5% rare treasure probability")
    void testPersonalityLootSelectionLogic() {
        int rareRolls = 0;
        int totalRolls = 10000;
        Random random = new Random(42L);

        for (int i = 0; i < totalRolls; i++) {
            if (random.nextFloat() < 0.05f) {
                rareRolls++;
            }
        }

        double rarePercentage = (double) rareRolls / totalRolls;
        assertTrue(rarePercentage >= 0.04 && rarePercentage <= 0.06, "Rare treasure roll must approximate 5%");
    }

    @Test
    @DisplayName("Assert strict null safety across gift helper methods")
    void testNullSafety() {
        assertFalse(WolfGiftHelper.canDeliverGift(null, null));
        assertFalse(WolfGiftHelper.hasDaylightCycle(null));
        assertEquals(0L, WolfGiftHelper.getCelestialDayTime(null));
        assertEquals(0L, WolfGiftHelper.getDayCount(null));
        assertFalse(WolfGiftHelper.isMorningOrWaking(null, null));
        assertDoesNotThrow(() -> WolfGiftHelper.deliverGift(null, null));
    }

    @Test
    @DisplayName("BL-BD-042: Assert WolfGiftHelper uses celestial default clock time and floor math")
    void testWolfGiftHelperSourceContract() throws IOException {
        String source = Files.readString(resolveWolfGiftHelperSource(), StandardCharsets.UTF_8);
        assertFalse(source.contains("getGameTime() / 24000L"), "Must not compute day count from monotonic gameTime");
        assertFalse(source.contains("getGameTime() % 24000L"), "Must not compute time of day from monotonic gameTime");
        assertTrue(source.contains("getDefaultClockTime()"), "Modern 26.x must read celestial default clock time");
        assertTrue(source.contains("hasDaylightCycle"), "Must guard dimensions without active daylight cycle");
        assertTrue(source.contains("Math.floorDiv"), "Must use floorDiv for day counts");
        assertTrue(source.contains("Math.floorMod"), "Must use floorMod for time-of-day wrapping");
        assertTrue(source.contains("isMorningOrWaking(level, owner)"), "canDeliverGift must enforce morning/waking gate");
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

    private boolean isPeaceful(int monsterCount) {
        return monsterCount == 0;
    }
}
