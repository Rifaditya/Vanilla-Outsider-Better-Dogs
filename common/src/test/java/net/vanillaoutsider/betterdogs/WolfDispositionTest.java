// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.UUID;
import net.vanillaoutsider.betterdogs.util.WolfDispositionHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WolfDispositionTest {

    @Test
    @DisplayName("Verify Deterministic Seed Calculation for Wolf Disposition")
    public void testDispositionDeterminism() {
        UUID testUuid = UUID.fromString("98765432-4321-4321-4321-cba987654321");
        int roll1 = WolfDispositionHelper.getSeededRoll(testUuid, WolfDispositionHelper.SALT_FETCH);
        int roll2 = WolfDispositionHelper.getSeededRoll(testUuid, WolfDispositionHelper.SALT_FETCH);

        Assertions.assertEquals(roll1, roll2, "Same UUID must always produce identical roll.");
        Assertions.assertTrue(roll1 >= 0 && roll1 < 100, "Roll must be in [0, 99].");
    }

    @Test
    @DisplayName("Verify Full-Spectrum Offset Bounds and Clamping Across Personalities")
    public void testFullSpectrumPersonalityRanges() {
        for (int i = 0; i < 100; i++) {
            UUID randomUuid = UUID.randomUUID();

            int offsetFetch = WolfDispositionHelper.getSeededOffset(randomUuid, WolfDispositionHelper.SALT_FETCH, -100, 100);
            Assertions.assertTrue(offsetFetch >= -100 && offsetFetch <= 100, "Fetch offset must be in [-100, 100]");

            int aggroFetch = WolfDispositionHelper.getFetchReluctanceChance(randomUuid, WolfPersonality.AGGRESSIVE);
            int normalFetch = WolfDispositionHelper.getFetchReluctanceChance(randomUuid, WolfPersonality.NORMAL);
            int paciFetch = WolfDispositionHelper.getFetchReluctanceChance(randomUuid, WolfPersonality.PACIFIST);

            Assertions.assertTrue(aggroFetch >= 0 && aggroFetch <= 100, "Aggressive fetch reluctance must clamp in [0, 100]");
            Assertions.assertTrue(normalFetch >= 0 && normalFetch <= 100, "Normal fetch reluctance must clamp in [0, 100]");
            Assertions.assertTrue(paciFetch >= 0 && paciFetch <= 100, "Pacifist fetch reluctance must clamp in [0, 100]");

            int aggroStorm = WolfDispositionHelper.getStormFearlessChance(randomUuid, WolfPersonality.AGGRESSIVE);
            int normalStorm = WolfDispositionHelper.getStormFearlessChance(randomUuid, WolfPersonality.NORMAL);
            int paciStorm = WolfDispositionHelper.getStormFearlessChance(randomUuid, WolfPersonality.PACIFIST);

            Assertions.assertTrue(aggroStorm >= 0 && aggroStorm <= 100, "Aggressive storm fearlessness must clamp in [0, 100]");
            Assertions.assertTrue(normalStorm >= 0 && normalStorm <= 100, "Normal storm fearlessness must clamp in [0, 100]");
            Assertions.assertTrue(paciStorm >= 0 && paciStorm <= 100, "Pacifist storm fearlessness must clamp in [0, 100]");

            int aggroHowl = WolfDispositionHelper.getQuietHowlerChance(randomUuid, WolfPersonality.AGGRESSIVE);
            int normalHowl = WolfDispositionHelper.getQuietHowlerChance(randomUuid, WolfPersonality.NORMAL);
            int paciHowl = WolfDispositionHelper.getQuietHowlerChance(randomUuid, WolfPersonality.PACIFIST);

            Assertions.assertTrue(aggroHowl >= 0 && aggroHowl <= 100, "Aggressive quietness must clamp in [0, 100]");
            Assertions.assertTrue(normalHowl >= 0 && normalHowl <= 100, "Normal quietness must clamp in [0, 100]");
            Assertions.assertTrue(paciHowl >= 0 && paciHowl <= 100, "Pacifist quietness must clamp in [0, 100]");

            int aggroHoover = WolfDispositionHelper.getHooverChance(randomUuid, WolfPersonality.AGGRESSIVE);
            int normalHoover = WolfDispositionHelper.getHooverChance(randomUuid, WolfPersonality.NORMAL);
            int paciHoover = WolfDispositionHelper.getHooverChance(randomUuid, WolfPersonality.PACIFIST);

            Assertions.assertTrue(aggroHoover >= 0 && aggroHoover <= 100, "Aggressive hoover must clamp in [0, 100]");
            Assertions.assertTrue(normalHoover >= 0 && normalHoover <= 100, "Normal hoover must clamp in [0, 100]");
            Assertions.assertTrue(paciHoover >= 0 && paciHoover <= 100, "Pacifist hoover must clamp in [0, 100]");
        }
    }

    @Test
    @DisplayName("Verify Null Safety and Default Safe Fallbacks")
    public void testNullSafety() {
        Assertions.assertEquals(50, WolfDispositionHelper.getSeededRoll(null, 0L));
        Assertions.assertEquals(0, WolfDispositionHelper.getSeededOffset(null, 0L, -100, 100));
        Assertions.assertTrue(WolfDispositionHelper.shouldFetch(null));
        Assertions.assertFalse(WolfDispositionHelper.shouldHeadTiltOnIgnoredStick(null));
        Assertions.assertFalse(WolfDispositionHelper.isStormFearless(null));
        Assertions.assertFalse(WolfDispositionHelper.isQuietHowler(null));
        Assertions.assertFalse(WolfDispositionHelper.isHooverScavenger(null));
    }
}
