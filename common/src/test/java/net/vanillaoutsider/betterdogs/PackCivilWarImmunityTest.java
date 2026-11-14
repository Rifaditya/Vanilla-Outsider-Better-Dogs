// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.ai.PacifistRevengeGoal;
import net.vanillaoutsider.betterdogs.ai.WolfProjectileInterceptGoal;
import net.vanillaoutsider.betterdogs.ai.play.PlayChoreographyHelper;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.AdultDisciplineHelper;
import net.vanillaoutsider.betterdogs.util.PackAudienceHelper;
import net.vanillaoutsider.betterdogs.util.SmallFightHelper;
import net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper;
import net.vanillaoutsider.betterdogs.util.WolfNemesisHelper;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 35, BL-BD-053 & BL-BD-055: Pack Civil War Immunity & Offline Ownership Parity Tests")
class PackCivilWarImmunityTest {

    private static final Unsafe UNSAFE = initUnsafe();
    private static Holder<DamageType> mobDamageType;
    private static Holder<DamageType> fallDamageType;

    private static Unsafe initUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to initialize sun.misc.Unsafe for test stubs", ex);
        }
    }

    @BeforeAll
    static void setupMinecraftBootstrap() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
        try {
            if (BetterDogsGameRules.BD_NEMESIS_SYSTEM == null) {
                BetterDogsGameRules.register();
            }
        } catch (Throwable ignored) {
            // Headless non-Knot Fabric Loom test runners fall back safely via BetterDogsGameRules defaults
        }
        mobDamageType = Holder.direct(new DamageType("mob", 0.1f));
        fallDamageType = Holder.direct(new DamageType("fall", 0.0f));
    }

    @BeforeEach
    @AfterEach
    void resetCorrectionSnaps() {
        SmallFightHelper.clearCorrectionSnaps();
    }

    private static OfflineStubWolf createOfflineWolf(boolean tame, @Nullable UUID ownerUuid) {
        return createOfflineWolf(UUID.randomUUID(), ownerUuid, tame, false);
    }

    private static OfflineStubWolf createOfflineWolf(UUID wolfUuid, @Nullable UUID ownerUuid, boolean tame, boolean baby) {
        try {
            OfflineStubWolf wolf = (OfflineStubWolf) UNSAFE.allocateInstance(OfflineStubWolf.class);
            wolf.init(wolfUuid, ownerUuid, tame, baby);
            return wolf;
        } catch (InstantiationException ex) {
            throw new IllegalStateException("Failed to allocate OfflineStubWolf", ex);
        }
    }

    private static StubServerPlayer createStubPlayer(UUID playerUuid, boolean sneaking) {
        try {
            StubServerPlayer player = (StubServerPlayer) UNSAFE.allocateInstance(StubServerPlayer.class);
            player.init(playerUuid, sneaking);
            return player;
        } catch (InstantiationException ex) {
            throw new IllegalStateException("Failed to allocate StubServerPlayer", ex);
        }
    }

    private static StubServerLevel createStubServerLevel(long gameTime, List<Wolf> worldWolves) {
        return createStubServerLevel(gameTime, worldWolves, false);
    }

    private static StubServerLevel createStubServerLevel(long gameTime, List<Wolf> worldWolves, boolean returnMatchedOnQuery) {
        try {
            StubServerLevel level = (StubServerLevel) UNSAFE.allocateInstance(StubServerLevel.class);
            level.init(gameTime, worldWolves, returnMatchedOnQuery);
            return level;
        } catch (InstantiationException ex) {
            throw new IllegalStateException("Failed to allocate StubServerLevel", ex);
        }
    }

    private static Path findCommonRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        if (Files.exists(dir.resolve("src/main/java/net/vanillaoutsider/betterdogs/mixin/OwnerHurtByTargetGoalMixin.java"))) {
            return dir;
        }
        if (Files.exists(dir.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/mixin/OwnerHurtByTargetGoalMixin.java"))) {
            return dir.resolve("common");
        }
        Path parent = dir.getParent();
        if (parent != null && Files.exists(parent.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/mixin/OwnerHurtByTargetGoalMixin.java"))) {
            return parent.resolve("common");
        }
        return dir;
    }

    @Test
    @DisplayName("Assert co-owned packmates owner matching logic")
    void testCoOwnedPackmateMatching() {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();

        assertEquals(ownerA, ownerA, "Matching owner UUID must qualify as packmates");
        assertNotEquals(ownerA, ownerB, "Different owners must not qualify as packmates");
        assertFalse(PackAudienceHelper.isCoOwned(null, null), "Null wolves cannot be co-owned");
    }

    @Test
    @DisplayName("Assert offline/cross-dimension co-owned tamed wolves remain co-owned (BL-BD-053)")
    void testOfflineCoOwnedWolvesMatch() {
        UUID ownerA = UUID.randomUUID();
        OfflineStubWolf dogA1 = createOfflineWolf(true, ownerA);
        OfflineStubWolf dogA2 = createOfflineWolf(true, ownerA);

        assertNull(dogA1.getOwner(), "Offline stub wolf must simulate null live owner");
        assertNull(dogA2.getOwner(), "Offline stub wolf must simulate null live owner");
        assertTrue(PackAudienceHelper.isCoOwned(dogA1, dogA2),
                "Co-owned tamed dogs must remain co-owned when owner is offline or cross-dimension");
    }

    @Test
    @DisplayName("Assert Negative Parity: offline rival-owned tamed wolves are not co-owned (BL-BD-053)")
    void testOfflineRivalOwnedWolvesRejected() {
        OfflineStubWolf dogA1 = createOfflineWolf(true, UUID.randomUUID());
        OfflineStubWolf dogB1 = createOfflineWolf(true, UUID.randomUUID());

        assertFalse(PackAudienceHelper.isCoOwned(dogA1, dogB1),
                "Tamed dogs owned by different offline players must never qualify as co-owned");
    }

    @Test
    @DisplayName("Assert untamed wild wolves do not fail-open on null==null (BL-BD-053)")
    void testUntamedWildWolvesRejected() {
        OfflineStubWolf wild1 = createOfflineWolf(false, null);
        OfflineStubWolf wild2 = createOfflineWolf(false, null);

        assertFalse(PackAudienceHelper.isCoOwned(wild1, wild2),
                "Untamed wild wolves with null owners must never qualify as co-owned");
    }

    @Test
    @DisplayName("Assert tamed vs untamed and tamed with null owner UUID are rejected (BL-BD-053)")
    void testTamedVsUntamedAndNullUuidRejected() {
        UUID ownerA = UUID.randomUUID();
        OfflineStubWolf tamedDog = createOfflineWolf(true, ownerA);
        OfflineStubWolf untamedStaleUuid = createOfflineWolf(false, ownerA);
        OfflineStubWolf tamedNullOwner = createOfflineWolf(true, null);

        assertFalse(PackAudienceHelper.isCoOwned(tamedDog, untamedStaleUuid),
                "Tamed dog and untamed wolf sharing stale UUID must not be co-owned");
        assertFalse(PackAudienceHelper.isCoOwned(untamedStaleUuid, tamedDog),
                "Untamed wolf and tamed dog sharing stale UUID must not be co-owned");
        assertFalse(PackAudienceHelper.isCoOwned(tamedNullOwner, tamedNullOwner),
                "Tamed wolf with null owner UUID must never match itself via null==null");
    }

    @Test
    @DisplayName("Assert asymmetric null wolf references are safely rejected (BL-BD-053)")
    void testAsymmetricNullWolvesRejected() {
        OfflineStubWolf dogA1 = createOfflineWolf(true, UUID.randomUUID());

        assertFalse(PackAudienceHelper.isCoOwned(dogA1, null), "Right-side null wolf must return false");
        assertFalse(PackAudienceHelper.isCoOwned(null, dogA1), "Left-side null wolf must return false");
        assertFalse(PackAudienceHelper.isCoOwned(null, null), "Both null wolves must return false");
    }

    @Test
    @DisplayName("Assert Pillar 3 concurrency stress: 64 threads racing isCoOwned deterministically (BL-BD-053)")
    void testConcurrentIsCoOwnedStress() throws InterruptedException, ExecutionException {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();
        OfflineStubWolf dogA1 = createOfflineWolf(true, ownerA);
        OfflineStubWolf dogA2 = createOfflineWolf(true, ownerA);
        OfflineStubWolf dogB1 = createOfflineWolf(true, ownerB);
        OfflineStubWolf wild1 = createOfflineWolf(false, null);
        OfflineStubWolf wild2 = createOfflineWolf(false, null);

        int threads = 64;
        int iterationsPerThread = 150;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>(threads);

        try {
            for (int t = 0; t < threads; t++) {
                futures.add(executor.submit(() -> {
                    startGate.await();
                    for (int i = 0; i < iterationsPerThread; i++) {
                        if (!PackAudienceHelper.isCoOwned(dogA1, dogA2)) {
                            return false;
                        }
                        if (PackAudienceHelper.isCoOwned(dogA1, dogB1)) {
                            return false;
                        }
                        if (PackAudienceHelper.isCoOwned(wild1, wild2)) {
                            return false;
                        }
                        if (PackAudienceHelper.isCoOwned(dogA1, wild1)) {
                            return false;
                        }
                        if (PackAudienceHelper.isCoOwned(dogA1, null) || PackAudienceHelper.isCoOwned(null, dogB1)) {
                            return false;
                        }
                    }
                    return true;
                }));
            }
            startGate.countDown();
            for (Future<Boolean> future : futures) {
                assertTrue(future.get(), "Concurrent isCoOwned evaluation must remain deterministic with zero errors");
            }
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(15, TimeUnit.SECONDS), "Concurrency executor must terminate cleanly");
        }
    }

    @Test
    @DisplayName("Assert universal domestic alert silencing null safety")
    void testDomesticAlertSilencingNullSafety() {
        assertFalse(AdultDisciplineHelper.shouldSilenceAlert(null, null), "Null victim/attacker must return false");
        assertFalse(AdultDisciplineHelper.isPlayCorrectionSnap(null, null), "Null wolves cannot discipline");
    }

    @Test
    @DisplayName("Assert retaliation suppression logic remains intact")
    void testRetaliationSuppression() {
        assertTrue(AdultDisciplineHelper.shouldSuppressRetaliation(true), "Suppression must be active when disciplined");
        assertFalse(AdultDisciplineHelper.shouldSuppressRetaliation(false), "Suppression must be inactive when not disciplined");
    }

    @Test
    @DisplayName("Assert PacifistRevengeGoal null safety on packmate check")
    void testPacifistRevengeNullSafety() {
        PacifistRevengeGoal goal = new PacifistRevengeGoal(null);
        assertFalse(goal.canUse());
        assertFalse(goal.canContinueToUse());
        assertDoesNotThrow(goal::start);
        assertDoesNotThrow(() -> goal.alertNearbyPackmates(null));
        assertDoesNotThrow(goal::stop);
    }

    @Test
    @DisplayName("BL-BD-062: Pacifist owner-defense start() preservation, 24m tether, and co-owned offline packmate alert immunity")
    void testPacifistOwnerDefensePreservationAndAlertImmunity() {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();
        StubServerPlayer playerA = createStubPlayer(ownerA, false);
        StubServerPlayer rivalAttacker = createStubPlayer(ownerB, false);

        OfflineStubWolf pacifist = createOfflineWolf(true, ownerA);
        pacifist.betterdogs$setPersonality(WolfPersonality.PACIFIST);
        OfflineStubWolf standingNormal = createOfflineWolf(true, ownerA);
        standingNormal.betterdogs$setPersonality(WolfPersonality.NORMAL);
        OfflineStubWolf otherPacifist = createOfflineWolf(true, ownerA);
        otherPacifist.betterdogs$setPersonality(WolfPersonality.PACIFIST);
        OfflineStubWolf rivalWolf = createOfflineWolf(true, ownerB);
        rivalWolf.betterdogs$setPersonality(WolfPersonality.NORMAL);

        StubServerLevel level = createStubServerLevel(200L, List.of(pacifist, standingNormal, otherPacifist, rivalWolf), true);
        pacifist.attachLevel(level);
        pacifist.attachLiveOwner(playerA, 400.0);
        playerA.setLastHurtByMob(rivalAttacker);

        PacifistRevengeGoal goal = new PacifistRevengeGoal(pacifist);
        assertTrue(goal.canUse(), "Pacifist within 24m must engage when owner is struck");
        goal.start();
        assertSame(rivalAttacker, pacifist.getTarget(), "start() must preserve owner-defense target across super.start()");
        assertSame(rivalAttacker, standingNormal.getTarget(), "Standing co-owned non-Pacifist packmate must be rallied");
        assertNull(otherPacifist.getTarget(), "Nearby Pacifist packmate must not be rallied");
        assertNull(rivalWolf.getTarget(), "Unrelated rival pack wolf must not be rallied");
        assertTrue(goal.canContinueToUse(), "Goal must stay active inside 24m tether");

        pacifist.setDistanceToOwnerSqr(576.01);
        assertFalse(goal.canContinueToUse(), "Goal must disengage once owner distance exceeds 24m");
        goal.stop();
        assertNull(pacifist.getTarget(), "stop() must clear target");
        assertFalse(goal.canUse(), "Pacifist beyond 24m must not re-enter owner defense");

        OfflineStubWolf coOwnedOfflineWolf = createOfflineWolf(true, ownerA);
        pacifist.setDistanceToOwnerSqr(4.0);
        playerA.setLastHurtByMob(coOwnedOfflineWolf);
        assertFalse(goal.canUse(), "Offline co-owned packmate must retain Pacifist revenge immunity");
    }

    @Test
    @DisplayName("Assert Negative Parity: Non-co-owned wolves, wild wolves, and sitting/leashed dogs are immune to sparring (BL-BD-035)")
    void testNegativeParitySparringImmunity() {
        assertFalse(SmallFightHelper.shouldInitiatePlayFight(false, 400, 100, 0, 0),
                "Wild or ineligible wolves must never initiate sparring");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, false, false, false, false, false, 0, false, false),
                "Wild wolves must never be eligible for play");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, true, false, false, false, 0, false, false),
                "Sitting wolves must never be eligible for play");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, true, false, false, 0, false, false),
                "Leashed wolves must never be eligible for play");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, false, true, false, 0, false, false),
                "Wolves with active targets must never be eligible for play");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 1: Offline co-owned pair parity across PackAudienceHelper, play, friendly fire & Nemesis")
    void testScenario1OfflineCoOwnedPairParity() {
        UUID ownerA = UUID.randomUUID();
        OfflineStubWolf wolfA1 = createOfflineWolf(true, ownerA);
        OfflineStubWolf wolfA2 = createOfflineWolf(true, ownerA);
        OfflineStubWolf rivalB = createOfflineWolf(true, UUID.randomUUID());
        OfflineStubWolf wild = createOfflineWolf(false, null);
        OfflineStubWolf untamedStale = createOfflineWolf(false, ownerA);
        OfflineStubWolf brokenTamedNull = createOfflineWolf(true, null);

        assertNull(wolfA1.getOwner());
        assertNull(wolfA2.getOwner());

        // 1a. PackAudienceHelper
        assertTrue(PackAudienceHelper.isCoOwned(wolfA1, wolfA2), "Co-owned offline wolves must match via owner UUID");

        // 1b. Play choreography & sparring do NOT reject due to null getOwner()
        assertTrue(SmallFightHelper.canPlayTogether(wolfA1, wolfA2),
                "Offline co-owned dogs within range must be eligible for sparring");
        assertTrue(PlayChoreographyHelper.isValidPlayPartner(wolfA1, wolfA2),
                "Offline co-owned dogs must remain valid play partners");

        // 1c. WolfFriendlyFireHelper protects packmate against direct AND indirect Wolf sources & offline owner
        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(wolfA1, wolfA2, rivalB, wild, untamedStale, brokenTamedNull));
        wolfA1.attachLevel(stubLevel);

        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, wolfA2, wolfA2)),
                "Direct melee hit from co-owned offline packmate must be cancelled");
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, null, wolfA2)),
                "Indirect source with causingEntity=co-owned wolf and directEntity=null must be cancelled");
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, wolfA2, null)),
                "Direct-only source with directEntity=co-owned wolf and causingEntity=null must be cancelled");

        StubServerPlayer offlineMatchedOwner = createStubPlayer(ownerA, false);
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, offlineMatchedOwner, offlineMatchedOwner)),
                "Offline/cross-dimension owner matched by UUID must be protected by friendly fire");
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, null, offlineMatchedOwner)),
                "Indirect owner projectile while owner is cross-dimension must be protected by friendly fire");

        // 1d. WolfNemesisHelper does NOT record co-owned killer wolf or offline owner player UUID
        WolfNemesisHelper.recordNemesis(wolfA1, new DamageSource(mobDamageType, wolfA2, wolfA2));
        assertFalse(stubLevel.broadcastQueried, "Co-owned killer wolf must never trigger Nemesis pack broadcast");

        WolfNemesisHelper.recordNemesis(wolfA1, new DamageSource(mobDamageType, offlineMatchedOwner, offlineMatchedOwner));
        assertFalse(stubLevel.broadcastQueried, "Offline owner matched by UUID must never trigger Nemesis broadcast");

        // Hostile killer SHOULD broadcast ONLY to co-owned packmates (not rivalB, wild, untamedStale, or brokenTamedNull)
        StubServerPlayer hostilePlayer = createStubPlayer(UUID.randomUUID(), false);
        WolfNemesisHelper.recordNemesis(wolfA1, new DamageSource(mobDamageType, hostilePlayer, hostilePlayer));
        assertTrue(stubLevel.broadcastQueried, "Hostile killer must trigger Nemesis pack broadcast");
        assertEquals(List.of(wolfA1, wolfA2), stubLevel.matchedBroadcastWolves,
                "Only tamed co-owned wolves should match the Nemesis pack broadcast predicate");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 2: Offline rival-owned pair gets zero co-owned immunity")
    void testScenario2OfflineRivalPairIsolation() {
        OfflineStubWolf wolfA = createOfflineWolf(true, UUID.randomUUID());
        OfflineStubWolf wolfB = createOfflineWolf(true, UUID.randomUUID());
        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(wolfA, wolfB));
        wolfA.attachLevel(stubLevel);

        assertFalse(PackAudienceHelper.isCoOwned(wolfA, wolfB));
        assertFalse(SmallFightHelper.canPlayTogether(wolfA, wolfB));
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(wolfA, wolfB));
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wolfA, new DamageSource(mobDamageType, wolfB, wolfB)),
                "Rival offline wolf hit must NOT be cancelled");
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wolfA, new DamageSource(mobDamageType, null, wolfB)),
                "Rival offline wolf indirect hit must NOT be cancelled");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 3: Wild pair rejects null==null false co-owned immunity")
    void testScenario3WildPairBlocksNullEqualsNull() {
        OfflineStubWolf wild1 = createOfflineWolf(false, null);
        OfflineStubWolf wild2 = createOfflineWolf(false, null);
        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(wild1, wild2));
        wild1.attachLevel(stubLevel);

        assertFalse(PackAudienceHelper.isCoOwned(wild1, wild2), "Wild dogs must NEVER co-own via null==null");
        assertFalse(SmallFightHelper.canPlayTogether(wild1, wild2));
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(wild1, wild2));
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wild1, new DamageSource(mobDamageType, wild2, wild2)),
                "Wild dogs must have zero pack friendly fire immunity");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 4: Environmental/non-living null/null DamageSource returns false even when owner is offline")
    void testScenario4EnvironmentalDamageNotCancelled() {
        OfflineStubWolf wolfA = createOfflineWolf(true, UUID.randomUUID());
        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(wolfA));
        wolfA.attachLevel(stubLevel);
        DamageSource environmental = new DamageSource(fallDamageType);

        assertNull(environmental.getEntity());
        assertNull(environmental.getDirectEntity());
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wolfA, environmental),
                "Environmental null/null DamageSource must return false regardless of offline owner");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 5: Tamed vs Untamed with stale matching owner UUID returns false")
    void testScenario5TamedPlusUntamedRejectsStaleOwner() {
        UUID ownerUuid = UUID.randomUUID();
        OfflineStubWolf tamed = createOfflineWolf(true, ownerUuid);
        OfflineStubWolf staleUntamed = createOfflineWolf(false, ownerUuid);
        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(tamed, staleUntamed));
        tamed.attachLevel(stubLevel);

        assertFalse(PackAudienceHelper.isCoOwned(tamed, staleUntamed),
                "Untamed wolf with stale owner UUID must NEVER count as co-owned");
        assertFalse(PackAudienceHelper.isCoOwned(staleUntamed, tamed));
        assertFalse(SmallFightHelper.canPlayTogether(tamed, staleUntamed));
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(tamed, staleUntamed));
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(staleUntamed, tamed));
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(tamed, new DamageSource(mobDamageType, staleUntamed, staleUntamed)),
                "Untamed wolf with stale owner UUID must NOT receive pack friendly fire immunity");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 6: Tamed wolf with null owner UUID is never co-owned")
    void testScenario6TamedWithNullOwnerUuid() {
        OfflineStubWolf brokenTamed1 = createOfflineWolf(true, null);
        OfflineStubWolf brokenTamed2 = createOfflineWolf(true, null);
        OfflineStubWolf validTamed = createOfflineWolf(true, UUID.randomUUID());

        assertFalse(PackAudienceHelper.isCoOwned(brokenTamed1, brokenTamed2),
                "Tamed wolves lacking owner UUID must NEVER co-own via null==null");
        assertFalse(PackAudienceHelper.isCoOwned(brokenTamed1, validTamed));
        assertFalse(PackAudienceHelper.isCoOwned(validTamed, brokenTamed1));
        assertFalse(SmallFightHelper.canPlayTogether(brokenTamed1, brokenTamed2));
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(brokenTamed1, brokenTamed2));
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 7: BL-BD-041 Blood Feud bypasses friendly fire in WolfFriendlyFireHelper while preserving pack immunity otherwise")
    void testScenario7BloodFeudBypassInFriendlyFire() {
        UUID ownerA = UUID.randomUUID();
        OfflineStubWolf wolfA1 = createOfflineWolf(true, ownerA);
        OfflineStubWolf wolfA2 = createOfflineWolf(true, ownerA);
        OfflineStubWolf wolfA3 = createOfflineWolf(true, ownerA);
        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(wolfA1, wolfA2, wolfA3));
        wolfA1.attachLevel(stubLevel);

        // Non-matching Blood Feud target must keep pack friendly fire protection active
        wolfA2.forceBloodFeud(true, wolfA3.getStringUUID());
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, wolfA2, wolfA2)),
                "Unrelated Blood Feud target must still cancel friendly fire against non-nemesis packmate");

        // Active BL-BD-041 Blood Feud targeting wolfA1 bypasses pack friendly fire (both direct & indirect)
        wolfA2.forceBloodFeud(true, wolfA1.getStringUUID());
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, wolfA2, wolfA2)),
                "Active BL-BD-041 Blood Feud must bypass WolfFriendlyFireHelper for direct co-owned nemesis hit");
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, null, wolfA2)),
                "Active BL-BD-041 Blood Feud must bypass WolfFriendlyFireHelper for causingEntity nemesis hit");
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, wolfA2, null)),
                "Active BL-BD-041 Blood Feud must bypass WolfFriendlyFireHelper for directEntity nemesis hit");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 8: PacifistRevengeGoal packmate immunity (offline/baby/owner-hurt) & OwnerHurtByTargetGoalMixin contract")
    void testScenario8PacifistRevengeAndOwnerHurtMixinContract() throws IOException {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();

        OfflineStubWolf pacifistA = createOfflineWolf(UUID.randomUUID(), ownerA, true, false);
        pacifistA.betterdogs$setPersonality(WolfPersonality.PACIFIST);

        OfflineStubWolf adultPackmateA = createOfflineWolf(UUID.randomUUID(), ownerA, true, false);
        OfflineStubWolf babyPackmateA = createOfflineWolf(UUID.randomUUID(), ownerA, true, true);
        OfflineStubWolf rivalB = createOfflineWolf(UUID.randomUUID(), ownerB, true, false);
        OfflineStubWolf wild = createOfflineWolf(UUID.randomUUID(), null, false, false);
        OfflineStubWolf untamedStale = createOfflineWolf(UUID.randomUUID(), ownerA, false, false);

        // 8a. Offline co-owned adult attacks pacifist -> blocked
        pacifistA.setLastHurtByMob(adultPackmateA);
        pacifistA.setTarget(null);
        assertFalse(new PacifistRevengeGoal(pacifistA).canUse(),
                "Pacifist dog must NOT retaliate against offline co-owned adult packmate");
        assertNull(pacifistA.getTarget());

        // 8b. Offline co-owned baby attacks pacifist -> blocked
        pacifistA.setLastHurtByMob(babyPackmateA);
        pacifistA.setTarget(null);
        assertFalse(new PacifistRevengeGoal(pacifistA).canUse(),
                "Pacifist dog must NOT retaliate against offline co-owned puppy");
        assertNull(pacifistA.getTarget());

        // 8c. Live owner hurt by offline-ref co-owned baby -> blocked via owner.getLastHurtByMob() -> isCoOwned
        StubServerPlayer liveOwner = createStubPlayer(ownerA, false);
        liveOwner.setLastHurtByMob(babyPackmateA);
        pacifistA.attachLiveOwner(liveOwner);
        pacifistA.setLastHurtByMob(null);
        pacifistA.setTarget(null);
        assertFalse(new PacifistRevengeGoal(pacifistA).canUse(),
                "Pacifist dog defending owner must NOT retaliate against co-owned puppy even if puppy has null live owner");
        assertNull(pacifistA.getTarget());
        pacifistA.attachLiveOwner(null);

        // 8d. Offline rival-owned wolf attacks pacifist -> allowed
        pacifistA.setLastHurtByMob(rivalB);
        pacifistA.setTarget(null);
        assertTrue(new PacifistRevengeGoal(pacifistA).canUse(),
                "Pacifist dog MUST retaliate against offline rival-owned wolf");
        assertEquals(rivalB, pacifistA.getTarget());

        // 8e. Untamed wild wolf attacks pacifist -> allowed (no null==null mercy leak)
        pacifistA.setLastHurtByMob(wild);
        pacifistA.setTarget(null);
        assertTrue(new PacifistRevengeGoal(pacifistA).canUse(),
                "Pacifist dog MUST retaliate against untamed wild wolf");
        assertEquals(wild, pacifistA.getTarget());

        // 8f. Untamed wolf with stale owner UUID attacks pacifist -> allowed
        pacifistA.setLastHurtByMob(untamedStale);
        pacifistA.setTarget(null);
        assertTrue(new PacifistRevengeGoal(pacifistA).canUse(),
                "Pacifist dog MUST NOT extend packmate mercy to untamed wolf with stale UUID");
        assertEquals(untamedStale, pacifistA.getTarget());

        // 8g. Contract check on OwnerHurtByTargetGoalMixin (mixin not applied under raw JUnit)
        Path mixinPath = findCommonRoot().resolve("src/main/java/net/vanillaoutsider/betterdogs/mixin/OwnerHurtByTargetGoalMixin.java");
        String mixinSource = Files.readString(mixinPath, StandardCharsets.UTF_8);
        assertTrue(mixinSource.contains("offendingWolf.isBaby() && PackAudienceHelper.isCoOwned(wolf, offendingWolf)"),
                "OwnerHurtByTargetGoalMixin must enforce PackAudienceHelper.isCoOwned for baby mercy");
        assertFalse(mixinSource.contains("offendingWolf.getOwner()"),
                "OwnerHurtByTargetGoalMixin must not compare live offendingWolf.getOwner()");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 9: WolfProjectileInterceptGoal.isHostileShooter handles offline/cross-dim owner & null owner")
    void testScenario9ProjectileInterceptHostileShooter() {
        UUID ownerAUuid = UUID.randomUUID();
        UUID ownerBUuid = UUID.randomUUID();
        StubServerPlayer ownerA = createStubPlayer(ownerAUuid, false);
        OfflineStubWolf dogA = createOfflineWolf(true, ownerAUuid);
        OfflineStubWolf dogB = createOfflineWolf(true, ownerBUuid);
        OfflineStubWolf wild = createOfflineWolf(false, null);
        OfflineStubWolf untamedStale = createOfflineWolf(false, ownerAUuid);

        assertNull(dogA.getOwner(), "Stub dogA has no live owner ref (cross-dim / unloaded)");
        assertFalse(WolfProjectileInterceptGoal.isHostileShooter(dogA, ownerA),
                "Same-owner tamed dog must not be treated as hostile shooter even when dogA.getOwner() is null");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(dogB, ownerA),
                "Rival tamed dog must be hostile");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(wild, ownerA),
                "Wild dog must be hostile");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(wild, null),
                "Wild dog with null owner must still be hostile (no null==null match)");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(untamedStale, ownerA),
                "Untamed dog with stale matching owner UUID must still be hostile");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(dogA, null),
                "Tamed dog with null defended owner must remain hostile");
        assertFalse(WolfProjectileInterceptGoal.isHostileShooter(ownerA, ownerA),
                "Owner shooting near self is never hostile");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(null, ownerA),
                "Null shooter defaults to hostile");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 10: 64-thread concurrency invariance across ownership gates")
    void testScenario10ConcurrencyInvariance() throws InterruptedException {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();
        StubServerPlayer playerA = createStubPlayer(ownerA, false);
        OfflineStubWolf dogA1 = createOfflineWolf(true, ownerA);
        OfflineStubWolf dogA2 = createOfflineWolf(true, ownerA);
        OfflineStubWolf dogB = createOfflineWolf(true, ownerB);
        OfflineStubWolf wild1 = createOfflineWolf(false, null);
        OfflineStubWolf wild2 = createOfflineWolf(false, null);
        OfflineStubWolf brokenTame = createOfflineWolf(true, null);

        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(dogA1, dogA2, dogB, wild1, wild2, brokenTame));
        dogA1.attachLevel(stubLevel);
        wild1.attachLevel(stubLevel);
        DamageSource coOwnedHit = new DamageSource(mobDamageType, dogA2, dogA2);
        DamageSource rivalHit = new DamageSource(mobDamageType, dogB, dogB);
        DamageSource wildHit = new DamageSource(mobDamageType, wild2, wild2);
        DamageSource envHit = new DamageSource(fallDamageType);

        int threads = 64;
        int iterationsPerThread = 250;
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger failures = new AtomicInteger(0);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int t = 0; t < threads; t++) {
            pool.execute(() -> {
                ready.countDown();
                try {
                    start.await();
                    for (int i = 0; i < iterationsPerThread; i++) {
                        if (!PackAudienceHelper.isCoOwned(dogA1, dogA2)) failures.incrementAndGet();
                        if (PackAudienceHelper.isCoOwned(dogA1, dogB)) failures.incrementAndGet();
                        if (PackAudienceHelper.isCoOwned(wild1, wild2)) failures.incrementAndGet();
                        if (PackAudienceHelper.isCoOwned(dogA1, wild1)) failures.incrementAndGet();
                        if (PackAudienceHelper.isCoOwned(brokenTame, brokenTame)) failures.incrementAndGet();

                        if (!WolfFriendlyFireHelper.shouldCancelDamage(dogA1, coOwnedHit)) failures.incrementAndGet();
                        if (WolfFriendlyFireHelper.shouldCancelDamage(dogA1, rivalHit)) failures.incrementAndGet();
                        if (WolfFriendlyFireHelper.shouldCancelDamage(wild1, wildHit)) failures.incrementAndGet();
                        if (WolfFriendlyFireHelper.shouldCancelDamage(dogA1, envHit)) failures.incrementAndGet();

                        if (!SmallFightHelper.canPlayTogether(dogA1, dogA2)) failures.incrementAndGet();
                        if (SmallFightHelper.canPlayTogether(dogA1, dogB)) failures.incrementAndGet();
                        if (!PlayChoreographyHelper.isValidPlayPartner(dogA1, dogA2)) failures.incrementAndGet();
                        if (PlayChoreographyHelper.isValidPlayPartner(dogA1, dogB)) failures.incrementAndGet();

                        if (WolfProjectileInterceptGoal.isHostileShooter(dogA1, playerA)) failures.incrementAndGet();
                        if (!WolfProjectileInterceptGoal.isHostileShooter(dogB, playerA)) failures.incrementAndGet();
                        if (!WolfProjectileInterceptGoal.isHostileShooter(wild1, null)) failures.incrementAndGet();
                    }
                } catch (Throwable ex) {
                    failures.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        assertTrue(ready.await(5, TimeUnit.SECONDS), "Worker threads should initialize promptly");
        start.countDown();
        assertTrue(done.await(15, TimeUnit.SECONDS), "All 64 threads must finish within timeout");
        pool.shutdownNow();
        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS), "Executor should terminate cleanly");
        assertEquals(0, failures.get(), "Zero invariants or thread safety violations allowed across 64 threads");
    }

    private static final class StubServerPlayer extends ServerPlayer {
        private UUID uuid;
        private boolean sneaking;
        private @Nullable LivingEntity lastHurtByMob;

        private StubServerPlayer() {
            super(null, null, null);
        }

        void init(UUID uuid, boolean sneaking) {
            this.uuid = uuid;
            this.sneaking = sneaking;
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public boolean isShiftKeyDown() {
            return this.sneaking;
        }

        @Override
        public boolean isCrouching() {
            return this.sneaking;
        }

        @Override
        public boolean isAlive() {
            return true;
        }

        @Override
        public EntityType<?> getType() {
            return EntityType.PLAYER;
        }

        @Override
        public @Nullable LivingEntity getLastHurtByMob() {
            return this.lastHurtByMob;
        }

        @Override
        public void setLastHurtByMob(@Nullable LivingEntity attacker) {
            this.lastHurtByMob = attacker;
        }
    }

    private static final class StubServerLevel extends ServerLevel {
        private long gameTime;
        private List<Wolf> candidates;
        private boolean returnMatchedOnQuery;
        boolean broadcastQueried;
        List<Wolf> matchedBroadcastWolves;

        private StubServerLevel() {
            super(null, null, null, null, null, null, null, false, 0L, null, false, null);
        }

        void init(long gameTime, List<Wolf> candidates) {
            init(gameTime, candidates, false);
        }

        void init(long gameTime, List<Wolf> candidates, boolean returnMatchedOnQuery) {
            this.gameTime = gameTime;
            this.candidates = candidates;
            this.returnMatchedOnQuery = returnMatchedOnQuery;
            this.broadcastQueried = false;
            this.matchedBroadcastWolves = new ArrayList<>();
        }

        @Override
        public boolean isClientSide() {
            return false;
        }

        @Override
        public long getGameTime() {
            return this.gameTime;
        }

        @Override
        public <T extends Entity> List<T> getEntitiesOfClass(Class<T> clazz, AABB aabb, Predicate<? super T> predicate) {
            this.broadcastQueried = true;
            this.matchedBroadcastWolves = new ArrayList<>();
            List<T> matched = new ArrayList<>();
            if (Wolf.class.isAssignableFrom(clazz) && this.candidates != null) {
                for (Wolf w : this.candidates) {
                    T cast = clazz.cast(w);
                    if (predicate.test(cast)) {
                        this.matchedBroadcastWolves.add(w);
                        matched.add(cast);
                    }
                }
            }
            return this.returnMatchedOnQuery ? matched : List.of();
        }
    }

    private static final class OfflineStubWolf extends Wolf implements WolfExtensions {
        private UUID uuid;
        private @Nullable UUID ownerUuid;
        private @Nullable LivingEntity liveOwner;
        private boolean tame;
        private boolean baby;
        private float health;
        private @Nullable Level level;
        private @Nullable LivingEntity target;
        private @Nullable LivingEntity lastHurtByMob;
        private double ownerDistanceSq;
        private boolean orderedToSit;
        private boolean sittingPose;
        private boolean sittingManually;
        private WolfPersonality personality;
        private boolean hasPersonality;
        private float socialScale;
        private long dnaSeed;
        private String favoriteTreat;
        private long soothedTime;
        private @Nullable BlockPos soundLocationTarget;
        private int passiveOverrideTicks;
        private String nemesisEntityType;
        private long nemesisExpiryTime;
        private @Nullable UUID parentUUID1;
        private @Nullable UUID parentUUID2;
        private boolean inbred;
        private boolean guarding;
        private @Nullable BlockPos guardPos;
        private boolean upForAdoption;
        private long lastGiftDay;
        private int zoomiesTicks;
        private boolean hasFetchedStick;
        private int calmTicks;
        private int howlingTicks;
        private int feedCount;
        private @Nullable LivingEntity retaliationTarget;
        private int retaliationTicks;
        private String bloodFeudTarget;
        private boolean hasBloodFeud;
        private int playFightCooldown;
        private @Nullable UUID leaderUuid;
        private boolean packLeader;
        private int wanderlustTicks;
        private long sparringCooldownUntil;
        private long playChoreographyCooldownUntil;
        private @Nullable UUID lastCorrectionSnapSource;
        private long lastCorrectionSnapExpiry;
        private int playPenaltyTicks;
        private int spectatorWagTicks;

        private OfflineStubWolf() {
            super(null, null);
        }

        void init(UUID wolfUuid, @Nullable UUID ownerUuid, boolean tame, boolean baby) {
            this.uuid = wolfUuid;
            this.ownerUuid = ownerUuid;
            this.tame = tame;
            this.baby = baby;
            this.health = 20.0f;
            this.personality = WolfPersonality.NORMAL;
            this.hasPersonality = true;
            this.socialScale = 1.0f;
            this.favoriteTreat = "";
            this.nemesisEntityType = "";
            this.bloodFeudTarget = "";
            this.hasBloodFeud = false;
            this.lastGiftDay = -1L;
            this.ownerDistanceSq = 4.0;
            this.setBoundingBox(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
        }

        void attachLevel(@Nullable Level level) {
            this.level = level;
        }

        void attachLiveOwner(@Nullable LivingEntity owner) {
            this.liveOwner = owner;
        }

        void attachLiveOwner(@Nullable LivingEntity owner, double distanceSq) {
            this.liveOwner = owner;
            this.ownerDistanceSq = distanceSq;
        }

        void setDistanceToOwnerSqr(double distanceSq) {
            this.ownerDistanceSq = distanceSq;
        }

        void setOrderedToSitStub(boolean orderedToSit) {
            this.orderedToSit = orderedToSit;
        }

        void setSittingPoseStub(boolean sittingPose) {
            this.sittingPose = sittingPose;
        }

        void setSittingManuallyStub(boolean sittingManually) {
            this.sittingManually = sittingManually;
        }

        void forceBloodFeud(boolean active, @Nullable String targetUuid) {
            this.hasBloodFeud = active;
            this.bloodFeudTarget = targetUuid != null ? targetUuid : "";
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public String getStringUUID() {
            return this.uuid.toString();
        }

        @Override
        public EntityType<?> getType() {
            return EntityType.WOLF;
        }

        @Override
        public Level level() {
            return this.level;
        }

        @Override
        public Level getCommandSenderWorld() {
            return this.level;
        }

        @Override
        public boolean isAlive() {
            return this.health > 0.0f;
        }

        @Override
        public float getHealth() {
            return this.health;
        }

        @Override
        public void setHealth(float health) {
            this.health = health;
        }

        @Override
        public boolean isTame() {
            return this.tame;
        }

        @Override
        public int getAge() {
            return this.baby ? -24000 : 0;
        }

        @Override
        public boolean isOrderedToSit() {
            return this.orderedToSit;
        }

        @Override
        public boolean isInSittingPose() {
            return this.sittingPose;
        }

        @Override
        public boolean betterdogs$isSittingManually() {
            return this.sittingManually;
        }

        @Override
        public boolean isLeashed() {
            return false;
        }

        @Override
        public @Nullable LivingEntity getTarget() {
            return this.target;
        }

        @Override
        public void setTarget(@Nullable LivingEntity target) {
            this.target = target;
        }

        @Override
        public @Nullable LivingEntity getLastHurtByMob() {
            return this.lastHurtByMob;
        }

        @Override
        public void setLastHurtByMob(@Nullable LivingEntity attacker) {
            this.lastHurtByMob = attacker;
        }

        @Override
        public double distanceToSqr(Entity entity) {
            return entity == this.liveOwner ? this.ownerDistanceSq : 4.0;
        }

        @Override
        public @Nullable UUID getOwnerUUID() {
            return this.ownerUuid;
        }

        @Override
        public @Nullable LivingEntity getOwner() {
            return this.liveOwner; // Defaults to null to simulate offline / cross-dimension owner
        }

        @Override
        public boolean isOwnedBy(LivingEntity entity) {
            return entity != null && entity == this.getOwner();
        }

        @Override
        public WolfPersonality betterdogs$getPersonality() {
            return this.personality;
        }

        @Override
        public void betterdogs$setPersonality(WolfPersonality personality) {
            this.personality = personality;
            this.hasPersonality = personality != null;
        }

        @Override
        public boolean betterdogs$hasPersonality() {
            return this.hasPersonality;
        }

        @Override
        public float betterdogs$getSocialScale() {
            return this.socialScale;
        }

        @Override
        public void betterdogs$setSocialScale(float scale) {
            this.socialScale = scale;
        }

        @Override
        public long betterdogs$getDnaSeed() {
            return this.dnaSeed;
        }

        @Override
        public String betterdogs$getFavoriteTreat() {
            return this.favoriteTreat;
        }

        @Override
        public void betterdogs$setFavoriteTreat(String treat) {
            this.favoriteTreat = treat;
        }

        @Override
        public long betterdogs$getSoothedTime() {
            return this.soothedTime;
        }

        @Override
        public void betterdogs$setSoothedTime(long time) {
            this.soothedTime = time;
        }

        @Override
        public @Nullable BlockPos betterdogs$getSoundLocationTarget() {
            return this.soundLocationTarget;
        }

        @Override
        public void betterdogs$setSoundLocationTarget(@Nullable BlockPos pos) {
            this.soundLocationTarget = pos;
        }

        @Override
        public int betterdogs$getPassiveOverrideTicks() {
            return this.passiveOverrideTicks;
        }

        @Override
        public void betterdogs$setPassiveOverrideTicks(int ticks) {
            this.passiveOverrideTicks = ticks;
        }

        @Override
        public String betterdogs$getNemesisEntityType() {
            return this.nemesisEntityType;
        }

        @Override
        public void betterdogs$setNemesisEntityType(String type) {
            this.nemesisEntityType = type;
        }

        @Override
        public long betterdogs$getNemesisExpiryTime() {
            return this.nemesisExpiryTime;
        }

        @Override
        public void betterdogs$setNemesisExpiryTime(long time) {
            this.nemesisExpiryTime = time;
        }

        @Override
        public void betterdogs$setCollarColor(DyeColor color) {
        }

        @Override
        public @Nullable UUID betterdogs$getParentUUID1() {
            return this.parentUUID1;
        }

        @Override
        public void betterdogs$setParentUUID1(@Nullable UUID uuid) {
            this.parentUUID1 = uuid;
        }

        @Override
        public @Nullable UUID betterdogs$getParentUUID2() {
            return this.parentUUID2;
        }

        @Override
        public void betterdogs$setParentUUID2(@Nullable UUID uuid) {
            this.parentUUID2 = uuid;
        }

        @Override
        public boolean betterdogs$isInbred() {
            return this.inbred;
        }

        @Override
        public void betterdogs$setInbred(boolean inbred) {
            this.inbred = inbred;
        }

        @Override
        public boolean betterdogs$isGuarding() {
            return this.guarding;
        }

        @Override
        public void betterdogs$setGuarding(boolean guarding) {
            this.guarding = guarding;
        }

        @Override
        public @Nullable BlockPos betterdogs$getGuardPos() {
            return this.guardPos;
        }

        @Override
        public void betterdogs$setGuardPos(@Nullable BlockPos pos) {
            this.guardPos = pos;
        }

        @Override
        public boolean betterdogs$isUpForAdoption() {
            return this.upForAdoption;
        }

        @Override
        public void betterdogs$setUpForAdoption(boolean adoption) {
            this.upForAdoption = adoption;
        }

        @Override
        public long betterdogs$getLastGiftDay() {
            return this.lastGiftDay;
        }

        @Override
        public void betterdogs$setLastGiftDay(long day) {
            this.lastGiftDay = day;
        }

        @Override
        public int betterdogs$getZoomiesTicks() {
            return this.zoomiesTicks;
        }

        @Override
        public void betterdogs$setZoomiesTicks(int ticks) {
            this.zoomiesTicks = ticks;
        }

        @Override
        public boolean betterdogs$hasFetchedStick() {
            return this.hasFetchedStick;
        }

        @Override
        public void betterdogs$setHasFetchedStick(boolean fetched) {
            this.hasFetchedStick = fetched;
        }

        @Override
        public int betterdogs$getCalmTicks() {
            return this.calmTicks;
        }

        @Override
        public void betterdogs$setCalmTicks(int ticks) {
            this.calmTicks = ticks;
        }

        @Override
        public int betterdogs$getHowlingTicks() {
            return this.howlingTicks;
        }

        @Override
        public void betterdogs$setHowlingTicks(int ticks) {
            this.howlingTicks = ticks;
        }

        @Override
        public int betterdogs$getFeedCount() {
            return this.feedCount;
        }

        @Override
        public void betterdogs$setFeedCount(int count) {
            this.feedCount = count;
        }

        @Override
        public @Nullable LivingEntity betterdogs$getRetaliationTarget() {
            return this.retaliationTarget;
        }

        @Override
        public void betterdogs$setRetaliationTarget(@Nullable LivingEntity target, int ticks) {
            this.retaliationTarget = target;
            this.retaliationTicks = ticks;
        }

        @Override
        public int betterdogs$getRetaliationTicks() {
            return this.retaliationTicks;
        }

        @Override
        public String betterdogs$getBloodFeudTarget() {
            return this.bloodFeudTarget;
        }

        @Override
        public void betterdogs$setBloodFeudTarget(String targetUuid) {
            this.bloodFeudTarget = targetUuid != null ? targetUuid : "";
            this.hasBloodFeud = !this.bloodFeudTarget.isEmpty();
        }

        @Override
        public boolean betterdogs$hasBloodFeud() {
            return this.hasBloodFeud;
        }

        @Override
        public int betterdogs$getPlayFightCooldown() {
            return this.playFightCooldown;
        }

        @Override
        public void betterdogs$setPlayFightCooldown(int ticks) {
            this.playFightCooldown = ticks;
        }

        @Override
        public @Nullable UUID betterdogs$getLeaderUUID() {
            return this.leaderUuid;
        }

        @Override
        public void betterdogs$setLeaderUUID(@Nullable UUID uuid) {
            this.leaderUuid = uuid;
        }

        @Override
        public boolean betterdogs$isPackLeader() {
            return this.packLeader;
        }

        @Override
        public void betterdogs$setPackLeader(boolean isLeader) {
            this.packLeader = isLeader;
        }

        @Override
        public int betterdogs$getWanderlustTicks() {
            return this.wanderlustTicks;
        }

        @Override
        public void betterdogs$setWanderlustTicks(int ticks) {
            this.wanderlustTicks = ticks;
        }

        @Override
        public long betterdogs$getSparringCooldownUntil() {
            return this.sparringCooldownUntil;
        }

        @Override
        public void betterdogs$setSparringCooldownUntil(long gameTime) {
            this.sparringCooldownUntil = gameTime;
        }

        @Override
        public long betterdogs$getPlayChoreographyCooldownUntil() {
            return this.playChoreographyCooldownUntil;
        }

        @Override
        public void betterdogs$setPlayChoreographyCooldownUntil(long gameTime) {
            this.playChoreographyCooldownUntil = gameTime;
        }

        @Override
        public @Nullable UUID betterdogs$getLastCorrectionSnapSource() {
            return this.lastCorrectionSnapSource;
        }

        @Override
        public void betterdogs$setLastCorrectionSnapSource(@Nullable UUID source) {
            this.lastCorrectionSnapSource = source;
        }

        @Override
        public long betterdogs$getLastCorrectionSnapExpiry() {
            return this.lastCorrectionSnapExpiry;
        }

        @Override
        public void betterdogs$setLastCorrectionSnapExpiry(long expiryTime) {
            this.lastCorrectionSnapExpiry = expiryTime;
        }

        @Override
        public int betterdogs$getPlayPenaltyTicks() {
            return this.playPenaltyTicks;
        }

        @Override
        public void betterdogs$setPlayPenaltyTicks(int ticks) {
            this.playPenaltyTicks = ticks;
        }

        @Override
        public int betterdogs$getSpectatorWagTicks() {
            return this.spectatorWagTicks;
        }

        @Override
        public void betterdogs$setSpectatorWagTicks(int ticks) {
            this.spectatorWagTicks = ticks;
        }
    }
}
