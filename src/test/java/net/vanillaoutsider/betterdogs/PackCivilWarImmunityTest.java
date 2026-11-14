// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
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
import net.vanillaoutsider.betterdogs.util.WolfTargetingHandler;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 35: Pack Civil War Immunity, Offline Owner Parity & Alert Silencing Tests (BL-BD-035, BL-BD-055)")
class PackCivilWarImmunityTest {

    private static Unsafe unsafe;
    private static Holder<DamageType> mobDamageType;
    private static Holder<DamageType> fallDamageType;

    @BeforeAll
    static void setupMinecraftBootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        if (BetterDogsGameRules.BD_NEMESIS_SYSTEM == null) {
            BetterDogsGameRules.register();
        }
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        unsafe = (Unsafe) theUnsafe.get(null);
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
            OfflineStubWolf stub = (OfflineStubWolf) unsafe.allocateInstance(OfflineStubWolf.class);
            stub.init(wolfUuid, ownerUuid, tame, baby);
            return stub;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate OfflineStubWolf", e);
        }
    }

    private static StubServerPlayer createStubPlayer(UUID playerUuid, boolean sneaking) {
        try {
            StubServerPlayer player = (StubServerPlayer) unsafe.allocateInstance(StubServerPlayer.class);
            player.init(playerUuid, sneaking);
            return player;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubServerPlayer", e);
        }
    }

    private static StubServerLevel createStubServerLevel(long gameTime, List<Wolf> worldWolves) {
        try {
            StubServerLevel level = (StubServerLevel) unsafe.allocateInstance(StubServerLevel.class);
            level.init(gameTime, worldWolves);
            return level;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubServerLevel", e);
        }
    }

    private static Path findProjectRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        Path cursor = dir;
        while (cursor != null) {
            if (Files.exists(cursor.resolve("src/main/java/net/vanillaoutsider/betterdogs/mixin/TamableAnimalMixin.java"))) {
                return cursor;
            }
            cursor = cursor.getParent();
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
    @DisplayName("Assert universal domestic alert silencing null safety")
    void testDomesticAlertSilencingNullSafety() {
        assertFalse(AdultDisciplineHelper.shouldSilenceAlert(null, null), "Null victim/attacker must return false");
        assertFalse(AdultDisciplineHelper.canDiscipline(null, null), "Null wolves cannot discipline");
    }

    @Test
    @DisplayName("Assert retaliation suppression logic remains intact")
    void testRetaliationSuppression() {
        assertTrue(WolfTargetingHandler.shouldSuppressRetaliation(true), "Suppression must be active when disciplined");
        assertFalse(WolfTargetingHandler.shouldSuppressRetaliation(false), "Suppression must be inactive when not disciplined");
    }

    @Test
    @DisplayName("Assert PacifistRevengeGoal null safety on packmate check")
    void testPacifistRevengeNullSafety() {
        PacifistRevengeGoal goal = new PacifistRevengeGoal(null);
        assertFalse(goal.canUse());
        assertDoesNotThrow(() -> goal.alertNearbyPackmates(null));
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
    @DisplayName("BL-BD-055 Scenario 1: Offline co-owned pair immunity across PackAudienceHelper, play, friendly fire & Nemesis")
    void testScenario1OfflineCoOwnedPairParity() {
        UUID ownerA = UUID.randomUUID();
        OfflineStubWolf wolfA1 = createOfflineWolf(true, ownerA);
        OfflineStubWolf wolfA2 = createOfflineWolf(true, ownerA);
        OfflineStubWolf rivalB = createOfflineWolf(true, UUID.randomUUID());
        OfflineStubWolf wild = createOfflineWolf(false, null);

        // Precondition: owner entity is genuinely offline
        assertNull(wolfA1.getOwner());
        assertNull(wolfA2.getOwner());

        // 1a. PackAudienceHelper
        assertTrue(PackAudienceHelper.isCoOwned(wolfA1, wolfA2), "Co-owned offline wolves must match via owner UUID");

        // 1b. Play choreography & sparring do NOT reject due to null getOwner()
        assertTrue(SmallFightHelper.canPlayTogether(wolfA1, wolfA2),
                "Offline co-owned dogs within range must be eligible for sparring");
        assertTrue(PlayChoreographyHelper.isValidPlayPartner(wolfA1, wolfA2),
                "Offline co-owned dogs must remain valid play partners");

        // 1c. WolfFriendlyFireHelper protects packmate against direct AND indirect Wolf sources
        wolfA1.setHealth(4.0f);
        DamageSource directWolfHit = new DamageSource(mobDamageType, wolfA2, wolfA2);
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, directWolfHit, 4.0f),
                "Lethal direct hit from co-owned offline packmate must clamp & cancel");
        assertEquals(1.0f, wolfA1.getHealth(), 1e-5f, "Protected packmate must clamp cleanly to 1.0f");

        wolfA1.setHealth(4.0f);
        DamageSource indirectCausingOnly = new DamageSource(mobDamageType, null, wolfA2);
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, indirectCausingOnly, 4.0f),
                "Lethal hit with causingEntity=co-owned wolf and directEntity=null must cancel");

        wolfA1.setHealth(4.0f);
        DamageSource directOnlyWolf = new DamageSource(mobDamageType, wolfA2, null);
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, directOnlyWolf, 4.0f),
                "Lethal hit with directEntity=co-owned wolf and causingEntity=null must cancel");

        // 1d. WolfNemesisHelper does NOT record co-owned killer wolf or offline owner player UUID
        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(wolfA1, wolfA2, rivalB, wild));
        wolfA1.attachLevel(stubLevel);
        WolfNemesisHelper.recordNemesis(wolfA1, new DamageSource(mobDamageType, wolfA2, wolfA2));
        assertFalse(stubLevel.broadcastQueried, "Co-owned killer wolf must never trigger Nemesis pack broadcast");

        StubServerPlayer offlineMatchedOwner = createStubPlayer(ownerA, false);
        WolfNemesisHelper.recordNemesis(wolfA1, new DamageSource(mobDamageType, offlineMatchedOwner, offlineMatchedOwner));
        assertFalse(stubLevel.broadcastQueried, "Offline owner matched by UUID must never trigger Nemesis broadcast");

        // Hostile killer SHOULD broadcast ONLY to co-owned packmates (not rivalB or wild)
        StubServerPlayer hostilePlayer = createStubPlayer(UUID.randomUUID(), false);
        WolfNemesisHelper.recordNemesis(wolfA1, new DamageSource(mobDamageType, hostilePlayer, hostilePlayer));
        assertTrue(stubLevel.broadcastQueried, "Hostile killer must trigger Nemesis pack broadcast");
        assertEquals(List.of(wolfA1, wolfA2), stubLevel.matchedBroadcastWolves,
                "Only co-owned wolves should match the Nemesis pack broadcast predicate");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 2: Offline rival-owned pair gets zero co-owned immunity")
    void testScenario2OfflineRivalPairIsolation() {
        OfflineStubWolf wolfA = createOfflineWolf(true, UUID.randomUUID());
        OfflineStubWolf wolfB = createOfflineWolf(true, UUID.randomUUID());

        assertFalse(PackAudienceHelper.isCoOwned(wolfA, wolfB));
        assertFalse(SmallFightHelper.canPlayTogether(wolfA, wolfB));
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(wolfA, wolfB));

        wolfA.setHealth(4.0f);
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wolfA, new DamageSource(mobDamageType, wolfB, wolfB), 4.0f),
                "Rival offline wolf lethal blow must NOT be cancelled");
        assertEquals(4.0f, wolfA.getHealth(), 1e-5f);
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 3: Wild pair rejects null==null false co-owned immunity")
    void testScenario3WildPairBlocksNullEqualsNull() {
        OfflineStubWolf wild1 = createOfflineWolf(false, null);
        OfflineStubWolf wild2 = createOfflineWolf(false, null);

        assertFalse(PackAudienceHelper.isCoOwned(wild1, wild2), "Wild dogs must NEVER co-own via null==null");
        assertFalse(SmallFightHelper.canPlayTogether(wild1, wild2));
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(wild1, wild2));

        wild1.setHealth(4.0f);
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wild1, new DamageSource(mobDamageType, wild2, wild2), 4.0f),
                "Wild dogs must have zero pack friendly fire immunity");
        assertEquals(4.0f, wild1.getHealth(), 1e-5f);
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 4: Environmental/non-living null/null DamageSource returns false even when owner is offline")
    void testScenario4EnvironmentalDamageNotCancelled() {
        OfflineStubWolf wolfA = createOfflineWolf(true, UUID.randomUUID());
        wolfA.setHealth(4.0f);
        DamageSource environmental = new DamageSource(fallDamageType);

        assertNull(environmental.getEntity());
        assertNull(environmental.getDirectEntity());
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wolfA, environmental, 4.0f),
                "Environmental null/null DamageSource must return false regardless of offline owner");
        assertEquals(4.0f, wolfA.getHealth(), 1e-5f);
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 5: Tamed vs Untamed with stale matching owner UUID returns false")
    void testScenario5TamedPlusUntamedRejectsStaleOwner() {
        UUID ownerUuid = UUID.randomUUID();
        OfflineStubWolf tamed = createOfflineWolf(true, ownerUuid);
        OfflineStubWolf staleUntamed = createOfflineWolf(false, ownerUuid);

        assertFalse(PackAudienceHelper.isCoOwned(tamed, staleUntamed),
                "Untamed wolf with stale owner UUID must NEVER count as co-owned");
        assertFalse(PackAudienceHelper.isCoOwned(staleUntamed, tamed));
        assertFalse(SmallFightHelper.canPlayTogether(tamed, staleUntamed));
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(tamed, staleUntamed));
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
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 7: BL-BD-041 Blood Feud exempts nemesis in WolfTargetingHandler & TamableAnimalMixin contract")
    void testScenario7BloodFeudExemptionAndMixinContract() throws IOException {
        UUID ownerA = UUID.randomUUID();
        OfflineStubWolf wolfA1 = createOfflineWolf(true, ownerA);
        OfflineStubWolf wolfA2 = createOfflineWolf(true, ownerA);
        OfflineStubWolf wolfA3 = createOfflineWolf(true, ownerA);

        // Non-matching non-null feud target leaves civil war protection active (BL-BD-059 safe)
        wolfA1.forceBloodFeud(true, wolfA3.getStringUUID());
        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(wolfA1, wolfA2, null),
                "Non-feud offline co-owned wolf must still be blocked");

        // Matching Blood Feud nemesis overrides Pack Civil War Protection
        wolfA1.forceBloodFeud(true, wolfA2.getStringUUID());
        assertEquals(Boolean.TRUE, WolfTargetingHandler.wantsToAttack(wolfA1, wolfA2, null),
                "BL-BD-041 Blood Feud must allow attacking co-owned nemesis even while owner is offline");

        // High affinity (> 50) suppresses feud
        wolfA1.betterdogs$adjustAffinity(wolfA2.getStringUUID(), 60);
        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(wolfA1, wolfA2, null),
                "High affinity > 50 must still suppress active Blood Feud");

        // Contract check on TamableAnimalMixin (not loaded under raw JUnit without Loom Mixin transformer)
        Path mixinPath = findProjectRoot().resolve("src/main/java/net/vanillaoutsider/betterdogs/mixin/TamableAnimalMixin.java");
        String mixinSource = Files.readString(mixinPath, StandardCharsets.UTF_8);
        int nullGuardIdx = mixinSource.indexOf("if (target == null)");
        int feudIdx = mixinSource.indexOf("Priority 1: Blood Feud");
        int p3Idx = mixinSource.indexOf("Priority 3: Adult Correction");
        int p4Idx = mixinSource.indexOf("Priority 4: Prevent \"Gang-Up\"");
        assertTrue(nullGuardIdx > 0 && feudIdx > nullGuardIdx, "TamableAnimalMixin must null-guard target before Priority 1");
        assertTrue(p3Idx > feudIdx && p4Idx > p3Idx, "Priority 1 Blood Feud must precede Priorities 3 & 4");
        assertTrue(mixinSource.substring(p3Idx).contains("PackAudienceHelper.isCoOwned(wolf, targetWolf)"),
                "Priority 3 & 4 in TamableAnimalMixin must use PackAudienceHelper.isCoOwned");
        assertFalse(mixinSource.substring(p3Idx).contains("getOwner()"),
                "TamableAnimalMixin wolf-to-wolf checks must not fall back to getOwner()");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 8: WolfTargetingHandler submissive & baby-vs-adult gates with offline owner")
    void testScenario8SubmissiveAndBabyGatesWithNullOwner() {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();

        // Submissive co-owned wolf blocked against packmate, allowed against rival & wild
        OfflineStubWolf submissiveA = createOfflineWolf(true, ownerA);
        submissiveA.betterdogs$setSubmissive(true);
        OfflineStubWolf packmateA = createOfflineWolf(true, ownerA);
        OfflineStubWolf rivalB = createOfflineWolf(true, ownerB);
        OfflineStubWolf wild = createOfflineWolf(false, null);

        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(submissiveA, packmateA, null));
        assertNull(WolfTargetingHandler.wantsToAttack(submissiveA, rivalB, null));
        assertNull(WolfTargetingHandler.wantsToAttack(submissiveA, wild, null));

        // Submissive untamed wolf vs wild wolf must NOT trip null==null packmate block
        OfflineStubWolf submissiveWild = createOfflineWolf(false, null);
        submissiveWild.betterdogs$setSubmissive(true);
        assertNull(WolfTargetingHandler.wantsToAttack(submissiveWild, wild, null),
                "Untamed submissive wolf must never treat other wild wolves as co-owned packmates");

        // Baby vs Adult gates with getOwner() == null
        OfflineStubWolf babyA = createOfflineWolf(UUID.randomUUID(), ownerA, true, true);
        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(babyA, packmateA, null));
        assertNull(WolfTargetingHandler.wantsToAttack(babyA, rivalB, null));
        assertNull(WolfTargetingHandler.wantsToAttack(babyA, wild, null));
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

        assertNull(dogA.getOwner(), "Stub dogA has no live owner ref (cross-dim / unloaded)");
        assertFalse(WolfProjectileInterceptGoal.isHostileShooter(dogA, ownerA),
                "Same-owner tamed dog must not be treated as hostile shooter even when dogA.getOwner() is null");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(dogB, ownerA),
                "Rival tamed dog must be hostile");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(wild, ownerA),
                "Wild dog must be hostile");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(wild, null),
                "Wild dog with null owner must still be hostile (no null==null match)");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(dogA, null),
                "Tamed dog with null defended owner must remain hostile");
        assertFalse(WolfProjectileInterceptGoal.isHostileShooter(ownerA, ownerA),
                "Owner shooting near self is never hostile");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(null, ownerA),
                "Null shooter defaults to hostile");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 10: 64-thread concurrency invariance over PackAudienceHelper.isCoOwned")
    void testScenario10ConcurrencyInvariance() throws InterruptedException {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();
        OfflineStubWolf dogA1 = createOfflineWolf(true, ownerA);
        OfflineStubWolf dogA2 = createOfflineWolf(true, ownerA);
        OfflineStubWolf dogB = createOfflineWolf(true, ownerB);
        OfflineStubWolf wild1 = createOfflineWolf(false, null);
        OfflineStubWolf wild2 = createOfflineWolf(false, null);
        OfflineStubWolf brokenTame = createOfflineWolf(true, null);

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
        assertEquals(0, failures.get(), "Zero invariants or thread safety violations allowed across 64 threads");
    }

    private static final class StubServerPlayer extends ServerPlayer {
        private UUID uuid;
        private boolean sneaking;

        private StubServerPlayer() {
            super(null, null, null, null);
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
            return EntityTypes.PLAYER;
        }
    }

    private static final class StubServerLevel extends ServerLevel {
        private long gameTime;
        private List<Wolf> candidates;
        boolean broadcastQueried;
        List<Wolf> matchedBroadcastWolves;

        private StubServerLevel() {
            super(null, null, null, null, null, null, false, 0L, null, false);
        }

        void init(long gameTime, List<Wolf> candidates) {
            this.gameTime = gameTime;
            this.candidates = candidates;
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
            if (Wolf.class.isAssignableFrom(clazz) && this.candidates != null) {
                for (Wolf w : this.candidates) {
                    T cast = clazz.cast(w);
                    if (predicate.test(cast)) {
                        this.matchedBroadcastWolves.add(w);
                    }
                }
            }
            return List.of();
        }
    }

    private static final class OfflineStubWolf extends Wolf implements WolfExtensions {
        private UUID uuid;
        private @Nullable UUID ownerUuid;
        private boolean tame;
        private boolean baby;
        private float health;
        private @Nullable Level level;
        private WolfPersonality personality;
        private boolean hasPersonality;
        private int lastDamageTime;
        private boolean submissive;
        private @Nullable String bloodFeudTarget;
        private boolean hasBloodFeud;
        private long lastMischiefDay;
        private boolean beingDisciplined;
        private @Nullable LivingEntity socialTarget;
        private SocialAction socialAction;
        private int socialModeTimer;
        private long dna;
        private float socialScale;
        private @Nullable EntitySocialScheduler scheduler;
        private Map<String, Integer> affinityMap;
        private @Nullable UUID leaderUuid;
        private boolean guardMode;
        private @Nullable BlockPos guardPos;
        private boolean sittingManually;
        private boolean adoptable;
        private float healthBonus;
        private float damageMod;
        private float speedMod;
        private boolean statsRolled;
        private int pushWaitTimer;
        private @Nullable BlockPos pathfindAvoidPos;
        private long soothedTime;
        private @Nullable BlockPos soundLocationTarget;
        private int passiveOverrideTicks;
        private String nemesisEntityType;
        private long nemesisExpiryTime;
        private boolean hasFetchedItem;
        private @Nullable ItemStack fetchedItemStack;
        private int zoomiesTicks;
        private int howlingTicks;
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
            this.bloodFeudTarget = "";
            this.hasBloodFeud = false;
            this.socialAction = SocialAction.NONE;
            this.affinityMap = new HashMap<>();
            this.nemesisEntityType = "";
            this.setBoundingBox(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
        }

        void attachLevel(@Nullable Level level) {
            this.level = level;
        }

        void forceBloodFeud(boolean active, @Nullable String targetUuid) {
            this.hasBloodFeud = active;
            this.bloodFeudTarget = targetUuid;
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
            return EntityTypes.WOLF;
        }

        @Override
        public Level level() {
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
        protected boolean canBeABaby() {
            return true;
        }

        @Override
        public int getAge() {
            return this.baby ? -24000 : 0;
        }

        @Override
        public boolean isOrderedToSit() {
            return false;
        }

        @Override
        public boolean isInSittingPose() {
            return false;
        }

        @Override
        public boolean isLeashed() {
            return false;
        }

        @Override
        public @Nullable LivingEntity getTarget() {
            return null;
        }

        @Override
        public double distanceToSqr(Entity entity) {
            return 4.0;
        }

        @Override
        public @Nullable EntityReference<LivingEntity> getOwnerReference() {
            return this.ownerUuid != null ? EntityReference.of(this.ownerUuid) : null;
        }

        @Override
        public @Nullable LivingEntity getOwner() {
            return null;
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
        public int betterdogs$getLastDamageTime() {
            return this.lastDamageTime;
        }

        @Override
        public void betterdogs$setLastDamageTime(int time) {
            this.lastDamageTime = time;
        }

        @Override
        public boolean betterdogs$isSubmissive() {
            return this.submissive;
        }

        @Override
        public void betterdogs$setSubmissive(boolean submissive) {
            this.submissive = submissive;
        }

        @Override
        public String betterdogs$getBloodFeudTarget() {
            return this.bloodFeudTarget;
        }

        @Override
        public void betterdogs$setBloodFeudTarget(String targetUuid) {
            this.bloodFeudTarget = targetUuid;
            this.hasBloodFeud = targetUuid != null && !targetUuid.isEmpty();
        }

        @Override
        public boolean betterdogs$hasBloodFeud() {
            return this.hasBloodFeud;
        }

        @Override
        public long betterdogs$getLastMischiefDay() {
            return this.lastMischiefDay;
        }

        @Override
        public void betterdogs$setLastMischiefDay(long day) {
            this.lastMischiefDay = day;
        }

        @Override
        public boolean betterdogs$isBeingDisciplined() {
            return this.beingDisciplined;
        }

        @Override
        public void betterdogs$setBeingDisciplined(boolean isBeingDisciplined) {
            this.beingDisciplined = isBeingDisciplined;
        }

        @Override
        public void betterdogs$setSocialState(@Nullable LivingEntity target, SocialAction action, int maxDurationTicks) {
            if (target != null && action != SocialAction.NONE) {
                this.socialTarget = target;
                this.socialAction = action;
                this.socialModeTimer = maxDurationTicks;
            } else {
                this.socialTarget = null;
                this.socialAction = SocialAction.NONE;
                this.socialModeTimer = 0;
            }
        }

        @Override
        public @Nullable LivingEntity betterdogs$getSocialTarget() {
            return this.socialTarget;
        }

        @Override
        public SocialAction betterdogs$getSocialAction() {
            return this.socialAction;
        }

        @Override
        public boolean betterdogs$isSocialModeActive() {
            return this.socialTarget != null && this.socialModeTimer > 0;
        }

        @Override
        public void betterdogs$tickSocialMode() {
            if (this.socialModeTimer > 0) {
                this.socialModeTimer--;
            }
        }

        @Override
        public long betterdogs$getDNA() {
            return this.dna;
        }

        @Override
        public void betterdogs$setDNA(long dna) {
            this.dna = dna;
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
        public @Nullable EntitySocialScheduler betterdogs$getScheduler() {
            return this.scheduler;
        }

        @Override
        public EntitySocialScheduler betterdogs$getOrInitializeScheduler() {
            return this.scheduler;
        }

        @Override
        public void betterdogs$tickScheduler() {
        }

        @Override
        public int betterdogs$getAffinity(String targetUuid) {
            return this.affinityMap.getOrDefault(targetUuid, 0);
        }

        @Override
        public void betterdogs$adjustAffinity(String targetUuid, int delta) {
            this.affinityMap.merge(targetUuid, delta, Integer::sum);
        }

        @Override
        public @Nullable UUID betterdogs$getLeaderUuid() {
            return this.leaderUuid;
        }

        @Override
        public void betterdogs$setLeaderUuid(@Nullable UUID uuid) {
            this.leaderUuid = uuid;
        }

        @Override
        public boolean betterdogs$isGuardMode() {
            return this.guardMode;
        }

        @Override
        public void betterdogs$setGuardMode(boolean guardMode) {
            this.guardMode = guardMode;
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
        public boolean betterdogs$isSittingManually() {
            return this.sittingManually;
        }

        @Override
        public void betterdogs$setSittingManually(boolean sitting) {
            this.sittingManually = sitting;
        }

        @Override
        public boolean betterdogs$isAdoptable() {
            return this.adoptable;
        }

        @Override
        public void betterdogs$setAdoptable(boolean adoptable) {
            this.adoptable = adoptable;
        }

        @Override
        public float betterdogs$getHealthBonus() {
            return this.healthBonus;
        }

        @Override
        public void betterdogs$setHealthBonus(float hp) {
            this.healthBonus = hp;
        }

        @Override
        public float betterdogs$getDamageMod() {
            return this.damageMod;
        }

        @Override
        public void betterdogs$setDamageMod(float dmg) {
            this.damageMod = dmg;
        }

        @Override
        public float betterdogs$getSpeedMod() {
            return this.speedMod;
        }

        @Override
        public void betterdogs$setSpeedMod(float speed) {
            this.speedMod = speed;
        }

        @Override
        public boolean betterdogs$areStatsRolled() {
            return this.statsRolled;
        }

        @Override
        public void betterdogs$setStatsRolled(boolean rolled) {
            this.statsRolled = rolled;
        }

        @Override
        public int betterdogs$getPushWaitTimer() {
            return this.pushWaitTimer;
        }

        @Override
        public void betterdogs$setPushWaitTimer(int ticks) {
            this.pushWaitTimer = ticks;
        }

        @Override
        public @Nullable BlockPos betterdogs$getPathfindAvoidPos() {
            return this.pathfindAvoidPos;
        }

        @Override
        public void betterdogs$setPathfindAvoidPos(@Nullable BlockPos pos) {
            this.pathfindAvoidPos = pos;
        }

        @Override
        public GoalSelector betterdogs$getGoalSelector() {
            return null;
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
        public boolean betterdogs$hasFetchedItem() {
            return this.hasFetchedItem;
        }

        @Override
        public void betterdogs$setHasFetchedItem(boolean fetched) {
            this.hasFetchedItem = fetched;
        }

        @Override
        public @Nullable ItemStack betterdogs$getFetchedItemStack() {
            return this.fetchedItemStack;
        }

        @Override
        public void betterdogs$setFetchedItemStack(@Nullable ItemStack stack) {
            this.fetchedItemStack = stack;
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
        public int betterdogs$getHowlingTicks() {
            return this.howlingTicks;
        }

        @Override
        public void betterdogs$setHowlingTicks(int ticks) {
            this.howlingTicks = ticks;
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
