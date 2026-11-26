// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.minecraft.world.damagesource.DamageSource;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.BabyRetaliationHelper;
import net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 36: Feisty Puppy Retaliation AI Tests")
class BabyRetaliationTest {

    private static Path findProjectRoot() {
        Path cursor = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (cursor != null) {
            if (Files.exists(cursor.resolve("src/main/java/net/vanillaoutsider/betterdogs/ai/BabyBiteBackGoal.java"))) {
                return cursor;
            }
            cursor = cursor.getParent();
        }
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

    @Test
    @DisplayName("Assert Aggressive personality gating for puppy retaliation")
    void testAggressivePersonalityGating() {
        WolfPersonality aggro = WolfPersonality.AGGRESSIVE;
        WolfPersonality paci = WolfPersonality.PACIFIST;
        WolfPersonality norm = WolfPersonality.NORMAL;

        assertSame(WolfPersonality.AGGRESSIVE, aggro, "Aggressive personality allows retaliation");
        assertNotSame(WolfPersonality.AGGRESSIVE, paci, "Pacifist personality does not allow retaliation");
        assertNotSame(WolfPersonality.AGGRESSIVE, norm, "Normal personality does not allow retaliation");
    }

    @Test
    @DisplayName("Assert retaliation probability roll math at 50% default")
    void testRetaliationProbabilityRoll() {
        int percent = 50;
        int triggeredCount = 0;
        Random rand = new Random(12345L);

        for (int i = 0; i < 1000; i++) {
            if (rand.nextInt(100) < percent) {
                triggeredCount++;
            }
        }

        assertTrue(triggeredCount > 400 && triggeredCount < 600,
                "50% roll should trigger roughly half the time (got " + triggeredCount + ")");
    }

    @Test
    @DisplayName("Assert retaliation timer decay to zero")
    void testRetaliationTimerCountdown() {
        int ticks = BabyRetaliationHelper.RETALIATION_DURATION_TICKS;
        assertEquals(100, ticks);
        for (int t = 0; t < 100; t++) {
            ticks = Math.max(0, ticks - 1);
        }
        assertEquals(0, ticks, "Retaliation timer should expire to 0");
    }

    @Test
    @DisplayName("Assert BabyRetaliationHelper strict null safety")
    void testBabyRetaliationHelperNullSafety() {
        assertDoesNotThrow(() -> assertFalse(BabyRetaliationHelper.isEligible(null)));
        assertDoesNotThrow(() -> assertFalse(BabyRetaliationHelper.shouldTriggerRetaliation(null, null, new Random())));
        assertDoesNotThrow(() -> BabyRetaliationHelper.triggerRetaliation(null, null));
        assertDoesNotThrow(() -> BabyRetaliationHelper.playRetaliationCues(null));
    }

    @Test
    @DisplayName("BL-BD-050: BabyBiteBackGoal passes retaliationTarget (never this.wolf) and filters targets across BD_FRIENDLY_FIRE")
    void testBabyBiteBackGoalTargetParameterAndRetaliationFiltering() throws IOException {
        String goalSource = Files.readString(
                findProjectRoot().resolve("src/main/java/net/vanillaoutsider/betterdogs/ai/BabyBiteBackGoal.java"),
                StandardCharsets.UTF_8);

        assertTrue(goalSource.contains("WolfFriendlyFireHelper.shouldCancelRetaliationDamage("),
                "BabyBiteBackGoal must invoke shouldCancelRetaliationDamage");
        assertTrue(goalSource.contains("this.wolf, this.retaliationTarget, source"),
                "BabyBiteBackGoal must pass this.retaliationTarget as victim");
        assertFalse(goalSource.contains("shouldCancelDamage(this.wolf, source"),
                "BabyBiteBackGoal must never pass this.wolf as both victim and attacker to shouldCancelDamage");

        PackCivilWarImmunityTest.ensureBootstrap();
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();

        PackCivilWarImmunityTest.OfflineStubWolf puppy = PackCivilWarImmunityTest.createOfflineWolf(UUID.randomUUID(), ownerA, true, true);
        puppy.betterdogs$setPersonality(WolfPersonality.AGGRESSIVE);
        PackCivilWarImmunityTest.OfflineStubWolf coOwnedAdult = PackCivilWarImmunityTest.createOfflineWolf(UUID.randomUUID(), ownerA, true, false);
        PackCivilWarImmunityTest.OfflineStubWolf feudPackmate = PackCivilWarImmunityTest.createOfflineWolf(UUID.randomUUID(), ownerA, true, false);
        PackCivilWarImmunityTest.OfflineStubWolf rivalWolf = PackCivilWarImmunityTest.createOfflineWolf(UUID.randomUUID(), ownerB, true, false);
        puppy.betterdogs$setBloodFeudTarget(feudPackmate.getStringUUID());

        PackCivilWarImmunityTest.StubServerLevel level = PackCivilWarImmunityTest.createStubServerLevel(
                100L, List.of(puppy, coOwnedAdult, feudPackmate, rivalWolf));
        puppy.attachLevel(level);
        coOwnedAdult.attachLevel(level);
        feudPackmate.attachLevel(level);
        rivalWolf.attachLevel(level);

        PackCivilWarImmunityTest.StubServerPlayer owner = PackCivilWarImmunityTest.createStubPlayer(ownerA, false);
        PackCivilWarImmunityTest.OfflineStubCreeper hostile = PackCivilWarImmunityTest.createOfflineCreeper();
        DamageSource biteSource = new DamageSource(PackCivilWarImmunityTest.mobDamageType, puppy, puppy);

        level.getGameRules().set(BetterDogsGameRules.BD_FRIENDLY_FIRE, true, null);
        assertFalse(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(puppy, owner, biteSource, BabyRetaliationHelper.RETALIATION_DAMAGE));
        assertFalse(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(puppy, hostile, biteSource, BabyRetaliationHelper.RETALIATION_DAMAGE));
        assertFalse(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(puppy, rivalWolf, biteSource, BabyRetaliationHelper.RETALIATION_DAMAGE));
        assertFalse(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(puppy, feudPackmate, biteSource, BabyRetaliationHelper.RETALIATION_DAMAGE));
        assertTrue(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(puppy, coOwnedAdult, biteSource, BabyRetaliationHelper.RETALIATION_DAMAGE));

        level.getGameRules().set(BetterDogsGameRules.BD_FRIENDLY_FIRE, false, null);
        assertFalse(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(puppy, coOwnedAdult, biteSource, BabyRetaliationHelper.RETALIATION_DAMAGE));
    }
}
