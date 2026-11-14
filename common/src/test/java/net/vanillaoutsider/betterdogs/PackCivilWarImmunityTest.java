// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.11
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
import java.util.function.Predicate;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

@DisplayName("Step 35, BL-BD-053 & BL-BD-055: Pack Civil War Immunity & Offline Co-Ownership Tests")
class PackCivilWarImmunityTest {

    private static final Unsafe UNSAFE = initUnsafe();
    private static final long ENTITY_POSITION_OFFSET = initPositionOffset();
    private static Holder<DamageType> mobDamageType;
    private static Holder<DamageType> fallDamageType;

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
        }
        mobDamageType = Holder.direct(new DamageType("mob", 0.1f));
        fallDamageType = Holder.direct(new DamageType("fall", 0.0f));
    }

    @BeforeEach
    @AfterEach
    void resetTransientState() {
        SmallFightHelper.clearCooldowns();
        SmallFightHelper.clearCorrectionSnaps();
        SmallFightHelper.clearPlayPenalties();
    }

    private static Unsafe initUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to initialize sun.misc.Unsafe for OfflineStubWolf", ex);
        }
    }

    private static long initPositionOffset() {
        try {
            Field posField = Entity.class.getDeclaredField("position");
            return UNSAFE.objectFieldOffset(posField);
        } catch (NoSuchFieldException ex) {
            throw new IllegalStateException("Failed to resolve Entity.position field offset", ex);
        }
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

    private static StubServerPlayer createStubPlayer(UUID playerUuid) {
        try {
            StubServerPlayer player = (StubServerPlayer) UNSAFE.allocateInstance(StubServerPlayer.class);
            player.init(playerUuid);
            return player;
        } catch (InstantiationException ex) {
            throw new IllegalStateException("Failed to allocate StubServerPlayer", ex);
        }
    }

    private static StubServerLevel createStubServerLevel(long gameTime, List<Wolf> candidates) {
        try {
            StubServerLevel level = (StubServerLevel) UNSAFE.allocateInstance(StubServerLevel.class);
            level.init(gameTime, candidates);
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
    @DisplayName("BL-BD-055 Scenario 1: Offline co-owned pair immunity across PackAudienceHelper, play, friendly fire & Nemesis")
    void testOfflineCoOwnedPairAcrossAllSubsystems() {
        UUID ownerA = UUID.randomUUID();
        OfflineStubWolf wolfA1 = createOfflineWolf(true, ownerA);
        OfflineStubWolf wolfA2 = createOfflineWolf(true, ownerA);
        OfflineStubWolf rivalB = createOfflineWolf(true, UUID.randomUUID());
        OfflineStubWolf wild = createOfflineWolf(false, null);

        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(wolfA1, wolfA2, rivalB, wild));
        wolfA1.attachLevel(stubLevel);
        wolfA2.attachLevel(stubLevel);
        rivalB.attachLevel(stubLevel);
        wild.attachLevel(stubLevel);

        assertNull(wolfA1.getOwner());
        assertNull(wolfA2.getOwner());

        // 1a. PackAudienceHelper
        assertTrue(PackAudienceHelper.isCoOwned(wolfA1, wolfA2),
                "Co-owned offline wolves must match via owner UUID");

        // 1b. SmallFightHelper & PlayChoreographyHelper do NOT drop due to null getOwner()
        assertTrue(SmallFightHelper.canPlayTogether(wolfA1, wolfA2),
                "Offline co-owned dogs within range must remain eligible for play sparring");
        assertTrue(PlayChoreographyHelper.isValidPlayPartner(wolfA1, wolfA2),
                "Offline co-owned dogs must remain valid choreography play partners");

        // 1c. WolfFriendlyFireHelper protects packmate against direct AND indirect co-owned Wolf sources
        DamageSource directWolfHit = new DamageSource(mobDamageType, wolfA2, wolfA2);
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, directWolfHit),
                "Direct hit from co-owned offline packmate must be cancelled");

        DamageSource indirectCausingOnly = new DamageSource(mobDamageType, null, wolfA2);
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, indirectCausingOnly),
                "Indirect hit with causingEntity=co-owned wolf and directEntity=null must be cancelled");

        DamageSource directOnlyWolf = new DamageSource(mobDamageType, wolfA2, null);
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, directOnlyWolf),
                "Hit with directEntity=co-owned wolf and causingEntity=null must be cancelled");

        // 1d. WolfFriendlyFireHelper still protects against online owner attacks (direct & indirect)
        StubServerPlayer liveOwner = createStubPlayer(ownerA);
        wolfA1.attachLiveOwner(liveOwner);
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, liveOwner, liveOwner)),
                "Direct hit from live owner must be cancelled");
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, null, liveOwner)),
                "Indirect hit from live owner must be cancelled");
        wolfA1.attachLiveOwner(null);

        // 1d2. WolfFriendlyFireHelper protects against cross-dimension / UUID-matched owner even if getOwner() is null
        StubServerPlayer uuidOwner = createStubPlayer(ownerA);
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, uuidOwner, uuidOwner)),
                "Direct hit from UUID-matched owner must be cancelled even when getOwner() is null");
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, null, uuidOwner)),
                "Indirect hit from UUID-matched owner must be cancelled even when getOwner() is null");

        // 1e. WolfNemesisHelper exempts co-owned killer wolf AND offline owner matched by UUID
        WolfNemesisHelper.recordNemesis(wolfA1, new DamageSource(mobDamageType, wolfA2, wolfA2));
        assertFalse(stubLevel.broadcastQueried,
                "Co-owned killer wolf must never trigger Nemesis pack broadcast");

        StubServerPlayer offlineMatchedOwner = createStubPlayer(ownerA);
        WolfNemesisHelper.recordNemesis(wolfA1, new DamageSource(mobDamageType, offlineMatchedOwner, offlineMatchedOwner));
        assertFalse(stubLevel.broadcastQueried,
                "Offline owner matched by UUID must never trigger Nemesis pack broadcast");

        // Hostile killer broadcasts ONLY to co-owned packmates (not rivalB or wild)
        StubServerPlayer hostilePlayer = createStubPlayer(UUID.randomUUID());
        WolfNemesisHelper.recordNemesis(wolfA1, new DamageSource(mobDamageType, hostilePlayer, hostilePlayer));
        assertTrue(stubLevel.broadcastQueried, "Hostile killer must trigger Nemesis pack broadcast");
        assertEquals(List.of(wolfA1, wolfA2), stubLevel.matchedBroadcastWolves,
                "Only co-owned wolves should match the Nemesis pack broadcast predicate");
        assertEquals("minecraft:player", wolfA1.betterdogs$getNemesisEntityType());
        assertEquals("minecraft:player", wolfA2.betterdogs$getNemesisEntityType());
        assertEquals("", rivalB.betterdogs$getNemesisEntityType());
        assertEquals("", wild.betterdogs$getNemesisEntityType());
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 2: Offline rival-owned pair gets zero co-owned immunity")
    void testOfflineRivalOwnedPairIsolation() {
        OfflineStubWolf wolfA = createOfflineWolf(true, UUID.randomUUID());
        OfflineStubWolf wolfB = createOfflineWolf(true, UUID.randomUUID());
        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(wolfA, wolfB));
        wolfA.attachLevel(stubLevel);
        wolfB.attachLevel(stubLevel);

        assertFalse(PackAudienceHelper.isCoOwned(wolfA, wolfB));
        assertFalse(SmallFightHelper.canPlayTogether(wolfA, wolfB));
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(wolfA, wolfB));
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wolfA, new DamageSource(mobDamageType, wolfB, wolfB)),
                "Rival offline wolf attack must NOT be cancelled");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 3: Wild pair rejects null==null & environmental null/null returns false")
    void testWildPairAndEnvironmentalDamage() {
        OfflineStubWolf wild1 = createOfflineWolf(false, null);
        OfflineStubWolf wild2 = createOfflineWolf(false, null);
        OfflineStubWolf offlineTamed = createOfflineWolf(true, UUID.randomUUID());
        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(wild1, wild2, offlineTamed));
        wild1.attachLevel(stubLevel);
        wild2.attachLevel(stubLevel);
        offlineTamed.attachLevel(stubLevel);

        assertFalse(PackAudienceHelper.isCoOwned(wild1, wild2), "Wild wolves must NEVER co-own via null==null");
        assertFalse(SmallFightHelper.canPlayTogether(wild1, wild2));
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(wild1, wild2));
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wild1, new DamageSource(mobDamageType, wild2, wild2)),
                "Wild wolves have zero friendly fire immunity");

        DamageSource environmental = new DamageSource(fallDamageType);
        assertNull(environmental.getEntity());
        assertNull(environmental.getDirectEntity());
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(offlineTamed, environmental),
                "Environmental null/null DamageSource must return false even when owner is offline");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 4: Tamed vs untamed stale UUID & tamed null-UUID wolves rejected")
    void testStaleUntamedAndNullUuidPlayIsolation() {
        UUID ownerUuid = UUID.randomUUID();
        OfflineStubWolf tamed = createOfflineWolf(true, ownerUuid);
        OfflineStubWolf staleUntamed = createOfflineWolf(false, ownerUuid);
        OfflineStubWolf nullOwnerTamed = createOfflineWolf(true, null);
        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(tamed, staleUntamed, nullOwnerTamed));
        tamed.attachLevel(stubLevel);
        staleUntamed.attachLevel(stubLevel);
        nullOwnerTamed.attachLevel(stubLevel);

        assertFalse(SmallFightHelper.canPlayTogether(tamed, staleUntamed));
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(tamed, staleUntamed));
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(tamed, new DamageSource(mobDamageType, staleUntamed, staleUntamed)),
                "Untamed wolf with stale owner UUID must not receive friendly fire immunity");

        assertFalse(SmallFightHelper.canPlayTogether(nullOwnerTamed, nullOwnerTamed));
        assertFalse(PlayChoreographyHelper.isValidPlayPartner(nullOwnerTamed, nullOwnerTamed));
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 5: Active BL-BD-041 Blood Feud bypass preserved inside WolfFriendlyFireHelper")
    void testBloodFeudBypassInFriendlyFireHelper() {
        UUID ownerA = UUID.randomUUID();
        OfflineStubWolf wolfA1 = createOfflineWolf(true, ownerA);
        OfflineStubWolf wolfA2 = createOfflineWolf(true, ownerA);
        OfflineStubWolf wolfA3 = createOfflineWolf(true, ownerA);
        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(wolfA1, wolfA2, wolfA3));
        wolfA1.attachLevel(stubLevel);
        wolfA2.attachLevel(stubLevel);
        wolfA3.attachLevel(stubLevel);

        // Non-matching feud target leaves packmate friendly-fire immunity intact
        wolfA2.betterdogs$setBloodFeudTarget(wolfA3.getStringUUID());
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, wolfA2, wolfA2)),
                "Co-owned wolf without active Blood Feud against victim must remain protected");

        // Active Blood Feud targeting wolfA1 bypasses friendly fire for both rawAttacker and directAttacker
        wolfA2.betterdogs$setBloodFeudTarget(wolfA1.getStringUUID());
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, wolfA2, wolfA2)),
                "BL-BD-041 Blood Feud must bypass pack friendly fire when attacker targets victim");
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, null, wolfA2)),
                "BL-BD-041 Blood Feud must bypass when causingEntity targets victim");
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wolfA1, new DamageSource(mobDamageType, wolfA2, null)),
                "BL-BD-041 Blood Feud must bypass when directEntity targets victim");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 6: WolfProjectileInterceptGoal.isHostileShooter handles offline/cross-dim owner & null owner")
    void testProjectileInterceptHostileShooter() {
        UUID ownerAUuid = UUID.randomUUID();
        UUID ownerBUuid = UUID.randomUUID();
        StubServerPlayer ownerA = createStubPlayer(ownerAUuid);
        OfflineStubWolf dogA = createOfflineWolf(true, ownerAUuid);
        OfflineStubWolf dogB = createOfflineWolf(true, ownerBUuid);
        OfflineStubWolf wild = createOfflineWolf(false, null);
        OfflineStubWolf staleUntamed = createOfflineWolf(false, ownerAUuid);

        assertNull(dogA.getOwner(), "Stub dogA simulates offline / cross-dimension null live owner");
        assertFalse(WolfProjectileInterceptGoal.isHostileShooter(dogA, ownerA),
                "Same-owner tamed dog must not be treated as hostile shooter even when getOwner() is null");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(dogB, ownerA),
                "Rival tamed dog must remain hostile");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(wild, ownerA),
                "Wild wolf must remain hostile");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(staleUntamed, ownerA),
                "Untamed wolf with stale owner UUID must remain hostile");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(wild, null),
                "Wild wolf with null owner must remain hostile (no null==null bypass)");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(dogA, null),
                "Tamed dog with null defended owner must remain hostile");
        assertFalse(WolfProjectileInterceptGoal.isHostileShooter(ownerA, ownerA),
                "Owner shooting near self is never hostile");
        assertTrue(WolfProjectileInterceptGoal.isHostileShooter(null, ownerA),
                "Null shooter defaults to hostile");
    }

    @Test
    @DisplayName("BL-BD-055 Scenario 7: PacifistRevengeGoal blocks co-owned adult/puppy retaliation offline & contracts hold")
    void testPacifistRevengeGoalOfflineCoOwnedAndContracts() throws IOException {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();

        OfflineStubWolf pacifist = createOfflineWolf(UUID.randomUUID(), ownerA, true, false);
        pacifist.betterdogs$setPersonality(WolfPersonality.PACIFIST);

        OfflineStubWolf coOwnedAdult = createOfflineWolf(UUID.randomUUID(), ownerA, true, false);
        OfflineStubWolf coOwnedBaby = createOfflineWolf(UUID.randomUUID(), ownerA, true, true);
        OfflineStubWolf rivalAdult = createOfflineWolf(UUID.randomUUID(), ownerB, true, false);
        OfflineStubWolf wildWolf = createOfflineWolf(UUID.randomUUID(), null, false, false);

        PacifistRevengeGoal goal = new PacifistRevengeGoal(pacifist);

        pacifist.setLastHurtByMobStub(coOwnedAdult);
        assertFalse(goal.canUse(), "Pacifist wolf must never retaliate against offline co-owned adult wolf");
        assertNull(pacifist.getTarget());

        pacifist.setLastHurtByMobStub(coOwnedBaby);
        assertFalse(goal.canUse(), "Pacifist wolf must never retaliate against offline co-owned baby wolf");
        assertNull(pacifist.getTarget());

        pacifist.setLastHurtByMobStub(rivalAdult);
        assertTrue(goal.canUse(), "Pacifist wolf must still retaliate against rival player tamed wolf");
        assertEquals(rivalAdult, pacifist.getTarget());

        pacifist.setTarget(null);
        pacifist.setLastHurtByMobStub(wildWolf);
        assertTrue(goal.canUse(), "Pacifist wolf must still retaliate against wild wolf");
        assertEquals(wildWolf, pacifist.getTarget());

        // Verify source contracts on PacifistRevengeGoal, OwnerHurtByTargetGoalMixin, and WolfMobMixin
        Path commonRoot = findCommonRoot();
        String pacifistSrc = Files.readString(
                commonRoot.resolve("src/main/java/net/vanillaoutsider/betterdogs/ai/PacifistRevengeGoal.java"),
                StandardCharsets.UTF_8);
        assertTrue(pacifistSrc.contains("attacker = owner.getLastHurtByMob();"),
                "PacifistRevengeGoal must preserve owner.getLastHurtByMob() retaliation check");
        assertFalse(pacifistSrc.contains("offendingWolf.getOwner()"),
                "PacifistRevengeGoal must not contain redundant offendingWolf.getOwner() check");

        String ownerHurtByMixinSrc = Files.readString(
                commonRoot.resolve("src/main/java/net/vanillaoutsider/betterdogs/mixin/OwnerHurtByTargetGoalMixin.java"),
                StandardCharsets.UTF_8);
        assertTrue(ownerHurtByMixinSrc.contains("if (offendingWolf.isBaby() && PackAudienceHelper.isCoOwned(wolf, offendingWolf))"),
                "OwnerHurtByTargetGoalMixin must gate puppy mercy via PackAudienceHelper.isCoOwned");
        assertFalse(ownerHurtByMixinSrc.contains("offendingWolf.getOwner()"),
                "OwnerHurtByTargetGoalMixin must not fall back to offendingWolf.getOwner()");

        String wolfMobMixinSrc = Files.readString(
                commonRoot.resolve("src/main/java/net/vanillaoutsider/betterdogs/mixin/WolfMobMixin.java"),
                StandardCharsets.UTF_8);
        assertTrue(wolfMobMixinSrc.contains("PackAudienceHelper.isCoOwned(wolf, targetWolf)"),
                "WolfMobMixin must gate setTarget pack immunity via PackAudienceHelper.isCoOwned");
    }

    @Test
    @DisplayName("Assert Pillar 3 concurrency stress: 64 threads racing co-ownership invariants deterministically (BL-BD-053 & BL-BD-055)")
    void testConcurrentIsCoOwnedStress() throws InterruptedException, ExecutionException {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();
        StubServerPlayer ownerAPlayer = createStubPlayer(ownerA);
        OfflineStubWolf dogA1 = createOfflineWolf(true, ownerA);
        OfflineStubWolf dogA2 = createOfflineWolf(true, ownerA);
        OfflineStubWolf dogB1 = createOfflineWolf(true, ownerB);
        OfflineStubWolf wild1 = createOfflineWolf(false, null);
        OfflineStubWolf wild2 = createOfflineWolf(false, null);
        OfflineStubWolf brokenTame = createOfflineWolf(true, null);

        StubServerLevel stubLevel = createStubServerLevel(100L, List.of(dogA1, dogA2, dogB1, wild1, wild2, brokenTame));
        dogA1.attachLevel(stubLevel);
        dogA2.attachLevel(stubLevel);
        dogB1.attachLevel(stubLevel);
        wild1.attachLevel(stubLevel);
        wild2.attachLevel(stubLevel);
        brokenTame.attachLevel(stubLevel);

        DamageSource coOwnedHit = new DamageSource(mobDamageType, dogA2, dogA2);
        DamageSource rivalHit = new DamageSource(mobDamageType, dogB1, dogB1);
        DamageSource envHit = new DamageSource(fallDamageType);

        int threads = 64;
        int iterationsPerThread = 200;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>(threads);

        try {
            for (int t = 0; t < threads; t++) {
                futures.add(executor.submit(() -> {
                    startGate.await();
                    for (int i = 0; i < iterationsPerThread; i++) {
                        if (!PackAudienceHelper.isCoOwned(dogA1, dogA2)) return false;
                        if (PackAudienceHelper.isCoOwned(dogA1, dogB1)) return false;
                        if (PackAudienceHelper.isCoOwned(wild1, wild2)) return false;
                        if (PackAudienceHelper.isCoOwned(dogA1, wild1)) return false;
                        if (PackAudienceHelper.isCoOwned(brokenTame, brokenTame)) return false;
                        if (PackAudienceHelper.isCoOwned(dogA1, null) || PackAudienceHelper.isCoOwned(null, dogB1)) return false;
                        if (WolfProjectileInterceptGoal.isHostileShooter(dogA1, ownerAPlayer)) return false;
                        if (!WolfProjectileInterceptGoal.isHostileShooter(dogB1, ownerAPlayer)) return false;
                        if (!WolfProjectileInterceptGoal.isHostileShooter(wild1, null)) return false;
                        if (!WolfFriendlyFireHelper.shouldCancelDamage(dogA1, coOwnedHit)) return false;
                        if (WolfFriendlyFireHelper.shouldCancelDamage(dogA1, rivalHit)) return false;
                        if (WolfFriendlyFireHelper.shouldCancelDamage(dogA1, envHit)) return false;
                        if (!SmallFightHelper.canPlayTogether(dogA1, dogA2)) return false;
                        if (SmallFightHelper.canPlayTogether(dogA1, dogB1)) return false;
                        if (!PlayChoreographyHelper.isValidPlayPartner(dogA1, dogA2)) return false;
                        if (PlayChoreographyHelper.isValidPlayPartner(wild1, wild2)) return false;
                    }
                    return true;
                }));
            }
            startGate.countDown();
            for (Future<Boolean> future : futures) {
                assertTrue(future.get(), "Concurrent BL-BD-055 evaluation must remain deterministic with zero errors");
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
        StubServerPlayer playerA = createStubPlayer(ownerA);
        StubServerPlayer rivalAttacker = createStubPlayer(ownerB);

        OfflineStubWolf pacifist = createOfflineWolf(true, ownerA);
        pacifist.betterdogs$setPersonality(WolfPersonality.PACIFIST);
        OfflineStubWolf standingNormal = createOfflineWolf(true, ownerA);
        standingNormal.betterdogs$setPersonality(WolfPersonality.NORMAL);
        OfflineStubWolf otherPacifist = createOfflineWolf(true, ownerA);
        otherPacifist.betterdogs$setPersonality(WolfPersonality.PACIFIST);
        OfflineStubWolf rivalWolf = createOfflineWolf(true, ownerB);
        rivalWolf.betterdogs$setPersonality(WolfPersonality.NORMAL);

        StubServerLevel level = createStubServerLevel(200L, List.of(pacifist, standingNormal, otherPacifist, rivalWolf));
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

        // Co-owned offline wolf must never be targeted or rallied against
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

    private static final class StubServerPlayer extends ServerPlayer {
        private UUID uuid;
        private @Nullable LivingEntity lastHurtByMob;

        private StubServerPlayer() {
            super(null, null, null, null);
        }

        void init(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public boolean isAlive() {
            return true;
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
        public EntityType<?> getType() {
            return EntityType.PLAYER;
        }
    }

    private static final class StubServerLevel extends ServerLevel {
        private long gameTime;
        private List<Wolf> candidates;
        boolean broadcastQueried;
        List<Wolf> matchedBroadcastWolves;

        private StubServerLevel() {
            super(null, null, null, null, null, null, false, 0L, null, false, null);
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
        public GameRules getGameRules() {
            return null;
        }

        @Override
        public <T extends ParticleOptions> int sendParticles(T type, double x, double y, double z, int count,
                                                             double dx, double dy, double dz, double speed) {
            return 0;
        }

        @Override
        @SuppressWarnings("unchecked")
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
            return matched;
        }
    }

    private static final class OfflineStubWolf extends Wolf implements WolfExtensions {
        private UUID uuid;
        private volatile boolean tame;
        private volatile boolean baby;
        private volatile @Nullable UUID ownerUuid;
        private volatile @Nullable LivingEntity liveOwner;
        private @Nullable Level level;
        private @Nullable LivingEntity lastHurtByMob;
        private @Nullable LivingEntity target;
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

        void init(UUID uuid, @Nullable UUID ownerUuid, boolean tame, boolean baby) {
            this.uuid = uuid;
            this.ownerUuid = ownerUuid;
            this.tame = tame;
            this.baby = baby;
            UNSAFE.putObject(this, ENTITY_POSITION_OFFSET, Vec3.ZERO);
            this.setBoundingBox(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
            this.personality = WolfPersonality.NORMAL;
            this.hasPersonality = true;
            this.socialScale = 1.0f;
            this.favoriteTreat = "";
            this.nemesisEntityType = "";
            this.bloodFeudTarget = "";
            this.hasBloodFeud = false;
            this.ownerDistanceSq = 4.0;
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

        void setLastHurtByMobStub(@Nullable LivingEntity attacker) {
            this.lastHurtByMob = attacker;
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
        public void playSound(SoundEvent sound, float volume, float pitch) {
        }

        @Override
        public boolean isAlive() {
            return true;
        }

        @Override
        public boolean isTame() {
            return this.tame;
        }

        @Override
        public boolean isBaby() {
            return this.baby;
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
        public @Nullable LivingEntity getLastHurtByMob() {
            return this.lastHurtByMob;
        }

        @Override
        public void setLastHurtByMob(@Nullable LivingEntity attacker) {
            this.lastHurtByMob = attacker;
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
        public double distanceToSqr(Entity entity) {
            return entity == this.liveOwner ? this.ownerDistanceSq : 4.0;
        }

        @Override
        public @Nullable LivingEntity getOwner() {
            return this.liveOwner;
        }

        @Override
        public boolean isOwnedBy(LivingEntity entity) {
            return entity != null && entity == this.getOwner();
        }

        @Override
        public @Nullable EntityReference<LivingEntity> getOwnerReference() {
            return this.ownerUuid != null ? EntityReference.of(this.ownerUuid) : null;
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
        public @Nullable Holder<WolfVariant> betterdogs$getVariant() {
            return null;
        }

        @Override
        public void betterdogs$setVariant(Holder<WolfVariant> variant) {
        }

        @Override
        public @Nullable Holder<WolfSoundVariant> betterdogs$getSoundVariant() {
            return null;
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
            this.retaliationTicks = Math.max(0, ticks);
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
            this.bloodFeudTarget = targetUuid;
            this.hasBloodFeud = targetUuid != null && !targetUuid.isEmpty();
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
