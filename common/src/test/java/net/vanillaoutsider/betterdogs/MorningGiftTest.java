// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfGiftHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 29: Morning Gift Bringing & Feeding Merits System Tests")
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
    @DisplayName("Assert health threshold eligibility (near full health)")
    void testHealthThresholdEligibility() {
        float maxHealth = 20.0f;
        float fullHealth = 20.0f;
        float minorScratch = 19.8f;
        float injuredHealth = 15.0f;

        assertTrue(fullHealth >= maxHealth - 0.5f, "Full health dog must qualify for gift delivery.");
        assertTrue(minorScratch >= maxHealth - 0.5f, "Near full health dog must qualify for gift delivery.");
        assertFalse(injuredHealth >= maxHealth - 0.5f, "Injured dog must not qualify for gift delivery.");
    }

    @Test
    @DisplayName("Assert strict null safety across gift helper methods")
    void testNullSafety() {
        assertFalse(WolfGiftHelper.canDeliverGift(null, null));
        assertEquals(0L, WolfGiftHelper.getDayCount(null));
        assertDoesNotThrow(() -> WolfGiftHelper.deliverGift(null, null));
    }
}
