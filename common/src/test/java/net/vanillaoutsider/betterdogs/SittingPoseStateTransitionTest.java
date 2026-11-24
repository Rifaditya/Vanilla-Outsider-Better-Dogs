// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.11
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.AvoidGhastFireballGoal;
import net.vanillaoutsider.betterdogs.ai.FleeExplodingHazardGoal;
import net.vanillaoutsider.betterdogs.ai.PersonalityFollowOwnerGoal;
import net.vanillaoutsider.betterdogs.ai.TamedWanderNearOwnerGoal;
import net.vanillaoutsider.betterdogs.ai.WanderlustGoal;
import net.vanillaoutsider.betterdogs.ai.WolfGuardGoal;
import net.vanillaoutsider.betterdogs.ai.WolfProjectileInterceptGoal;
import net.vanillaoutsider.betterdogs.ai.WolfProjectileInterceptHelper;
import net.vanillaoutsider.betterdogs.ai.WolfSeekWaterOnFireGoal;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper;
import net.vanillaoutsider.betterdogs.util.WolfSittingPoseHelper;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BL-BD-069: Sitting Pose State Sync, Goal Transitions, Damage & 5-Pillar Tests (1.21.11)")
class SittingPoseStateTransitionTest {

    private static final Unsafe unsafe = initUnsafe();
    private static Holder<DamageType> mobDamageType;
    private static Holder<DamageType> fallDamageType;
    private static long deterministicLazyPlopSeed;

    private static Unsafe initUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Unsafe", e);
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
            if (BetterDogsGameRules.BD_FRIENDLY_FIRE == null) {
                BetterDogsGameRules.register();
            }
        } catch (Throwable ignored) {
        }
        mobDamageType = Holder.direct(new DamageType("mob", 0.1f));
        fallDamageType = Holder.direct(new DamageType("fall", 0.0f));
        deterministicLazyPlopSeed = findLazyPlopSeed();
    }

    private static long findLazyPlopSeed() {
        for (long i = 1L; i <= 10000L; i++) {
            long seed = i * 0x9E3779B97F4A7C15L;
            if (RandomSource.create(seed).nextFloat() < 0.35f) {
                return seed;
            }
        }
        throw new IllegalStateException("Failed to locate deterministic seed < 0.35f");
    }

    private static StubGameRules createStubGameRules(GameRuleMap initialMap) {
        try {
            StubGameRules rules = (StubGameRules) unsafe.allocateInstance(StubGameRules.class);
            rules.init(initialMap);
            return rules;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubGameRules", e);
        }
    }

    private static GameRules createSittingGameRules() {
        GameRuleMap map = GameRuleMap.of();
        if (BetterDogsGameRules.BD_FRIENDLY_FIRE != null) {
            map.set(BetterDogsGameRules.BD_FRIENDLY_FIRE, true);
        }
        if (BetterDogsGameRules.BD_DEMERIT_ACCIDENTAL_ATTACKS != null) {
            map.set(BetterDogsGameRules.BD_DEMERIT_ACCIDENTAL_ATTACKS, false);
        }
        if (BetterDogsGameRules.BD_DEBUGGING != null) {
            map.set(BetterDogsGameRules.BD_DEBUGGING, false);
        }
        if (BetterDogsGameRules.BD_FLEE_SPEED_MULTIPLIER != null) {
            map.set(BetterDogsGameRules.BD_FLEE_SPEED_MULTIPLIER, 170);
        }
        if (BetterDogsGameRules.BD_AGGRO_FOLLOW_START != null) {
            map.set(BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 25);
        }
        if (BetterDogsGameRules.BD_PACI_FOLLOW_START != null) {
            map.set(BetterDogsGameRules.BD_PACI_FOLLOW_START, 5);
        }
        if (BetterDogsGameRules.BD_NORMAL_FOLLOW_START != null) {
            map.set(BetterDogsGameRules.BD_NORMAL_FOLLOW_START, 10);
        }
        if (BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MULTIPLIER != null) {
            map.set(BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MULTIPLIER, 100);
        }
        if (BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MAX != null) {
            map.set(BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MAX, 50);
        }
        if (BetterDogsGameRules.BD_FAST_TRAVEL_CATCHUP != null) {
            map.set(BetterDogsGameRules.BD_FAST_TRAVEL_CATCHUP, true);
        }
        return createStubGameRules(map);
    }

    private static StubServerLevel createStubServerLevel(GameRules rules) {
        try {
            StubServerLevel level = (StubServerLevel) unsafe.allocateInstance(StubServerLevel.class);
            level.init(rules);
            return level;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubServerLevel", e);
        }
    }

    private static StubClientLevel createStubClientLevel() {
        try {
            return (StubClientLevel) unsafe.allocateInstance(StubClientLevel.class);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubClientLevel", e);
        }
    }

    private static StubWolf createStubWolf(@Nullable UUID ownerUuid, boolean tame, CanineArchetype archetype) {
        try {
            StubWolf wolf = (StubWolf) unsafe.allocateInstance(StubWolf.class);
            wolf.init(UUID.randomUUID(), ownerUuid, tame, WolfPersonality.NORMAL, archetype);
            return wolf;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubWolf", e);
        }
    }

    private static StubServerPlayer createStubPlayer(UUID uuid, boolean sneaking) {
        try {
            StubServerPlayer player = (StubServerPlayer) unsafe.allocateInstance(StubServerPlayer.class);
            player.init(uuid, sneaking);
            return player;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubServerPlayer", e);
        }
    }

    private static Path findMainJavaRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        Path cursor = dir;
        while (cursor != null) {
            if (Files.exists(cursor.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfSittingPoseHelper.java"))) {
                return cursor.resolve("common/src/main/java");
            }
            if (Files.exists(cursor.resolve("src/main/java/net/vanillaoutsider/betterdogs/util/WolfSittingPoseHelper.java"))) {
                return cursor.resolve("src/main/java");
            }
            cursor = cursor.getParent();
        }
        return dir.resolve("src/main/java");
    }

    @Test
    @DisplayName("Pillar 1: 10,000-trial chaos fuzzing & resting gravity/NaN invariants")
    void testChaosPropertyFuzzingAndBoundaries() {
        assertFalse(WolfSittingPoseHelper.shouldClearSittingPose(
                false, true, false, false, true, false, 0.0, -0.0784),
                "Resting vertical gravity (-0.0784) must NOT falsely clear stationary lazy sit");
        assertTrue(WolfSittingPoseHelper.shouldClearSittingPose(
                false, true, false, false, true, false, 0.0, 0.42),
                "Jump vertical velocity (0.42 > 0.15) must clear unordered sitting pose");
        assertTrue(WolfSittingPoseHelper.shouldClearSittingPose(
                false, true, false, false, true, false, 1.5e-4, 0.0),
                "Horizontal velocity (> 1.0e-4) must clear unordered sitting pose");
        assertFalse(WolfSittingPoseHelper.shouldClearSittingPose(
                false, true, false, false, true, false, Double.NaN, Double.NaN),
                "Non-finite NaN velocity must never clear stationary sitting pose");
        assertFalse(WolfSittingPoseHelper.shouldClearSittingPose(
                false, true, false, false, true, false, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY),
                "Infinite velocity must not bypass finite checks");

        Random rng = new Random(0x517716L);
        for (int i = 0; i < 10_000; i++) {
            boolean ordered = rng.nextBoolean();
            boolean inPose = rng.nextBoolean();
            boolean passenger = rng.nextBoolean();
            boolean scooting = rng.nextBoolean();
            boolean navDone = rng.nextBoolean();
            boolean hasTarget = rng.nextBoolean();
            double dx = (rng.nextDouble() - 0.5) * 0.5;
            double dz = (rng.nextDouble() - 0.5) * 0.5;
            double horizSqr = dx * dx + dz * dz;
            double dy = rng.nextInt(5) == 0 ? -0.0784 : (rng.nextDouble() - 0.5) * 1.0;

            boolean cleared = WolfSittingPoseHelper.shouldClearSittingPose(
                    ordered, inPose, passenger, scooting, navDone, hasTarget, horizSqr, dy);
            boolean movingOrTargeted = !navDone || hasTarget
                    || horizSqr > WolfSittingPoseHelper.HORIZONTAL_MOVEMENT_EPSILON_SQR
                    || Math.abs(dy) > WolfSittingPoseHelper.VERTICAL_MOVEMENT_EPSILON;

            if (!cleared && movingOrTargeted) {
                assertTrue(ordered || !inPose || passenger || scooting,
                        "Trial " + i + ": active/moving wolf stayed seated without ordered/passenger/scoot exemption");
            }
            if (cleared) {
                assertTrue(!ordered && inPose && !passenger && !scooting && movingOrTargeted,
                        "Trial " + i + ": cleared pose violated exemption or movement precondition");
            }
        }
    }

    @Test
    @DisplayName("Pillar 2: 50-thread concurrency stress on syncSittingPoseOnTick & canEnterLazyRestingPose")
    void testConcurrencyStress() throws InterruptedException {
        StubServerLevel level = createStubServerLevel(createSittingGameRules());
        int threads = 50;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger failures = new AtomicInteger(0);

        for (int t = 0; t < threads; t++) {
            final int tid = t;
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    for (int i = 0; i < 200; i++) {
                        StubWolf wolf = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
                        wolf.attachLevel(level);
                        wolf.setOnGroundStub(true);
                        wolf.setSittingPoseStub(true);

                        int mode = (tid + i) % 4;
                        if (mode == 0) {
                            wolf.setOrderedToSitStub(true);
                            wolf.setDeltaMovement(new Vec3(0.25, 0.0, 0.0));
                            if (WolfSittingPoseHelper.syncSittingPoseOnTick(wolf) || !wolf.isInSittingPose()) {
                                failures.incrementAndGet();
                            }
                        } else if (mode == 1) {
                            wolf.stubNav.done = false;
                            if (!WolfSittingPoseHelper.syncSittingPoseOnTick(wolf) || wolf.isInSittingPose()) {
                                failures.incrementAndGet();
                            }
                        } else if (mode == 2) {
                            wolf.setDeltaMovement(new Vec3(0.0, -0.0784, 0.0));
                            if (WolfSittingPoseHelper.syncSittingPoseOnTick(wolf)
                                    || !wolf.isInSittingPose()
                                    || !WolfSittingPoseHelper.canEnterLazyRestingPose(wolf)) {
                                failures.incrementAndGet();
                            }
                        } else {
                            wolf.setPassengerStub(true);
                            wolf.stubNav.done = false;
                            if (WolfSittingPoseHelper.syncSittingPoseOnTick(wolf)
                                    || !wolf.isInSittingPose()
                                    || WolfSittingPoseHelper.canEnterLazyRestingPose(wolf)) {
                                failures.incrementAndGet();
                            }
                        }
                    }
                } catch (Throwable ex) {
                    failures.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        assertTrue(ready.await(5, TimeUnit.SECONDS));
        start.countDown();
        assertTrue(done.await(15, TimeUnit.SECONDS));
        pool.shutdownNow();
        assertEquals(0, failures.get(), "Zero failures allowed under 50-thread concurrent stress");
    }

    @Test
    @DisplayName("Zero-spam stationary rest, movement/jump/target sync, and negative parity exemptions")
    void testZeroSpamTickSyncAndNegativeParity() {
        StubServerLevel serverLevel = createStubServerLevel(createSittingGameRules());
        StubClientLevel clientLevel = createStubClientLevel();

        StubWolf lazy = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        lazy.attachLevel(serverLevel);
        lazy.setSittingPoseStub(true);
        lazy.setOrderedToSitStub(false);
        lazy.stubNav.done = true;
        lazy.setDeltaMovement(new Vec3(0.0, -0.0784, 0.0));
        lazy.resetPoseToggleCount();

        for (int tick = 0; tick < 100; tick++) {
            assertFalse(WolfSittingPoseHelper.syncSittingPoseOnTick(lazy),
                    "Stationary lazy wolf under gravity must not toggle sitting pose");
        }
        assertTrue(lazy.isInSittingPose());
        assertEquals(0, lazy.poseToggleCount, "Zero SynchedEntityData spam permitted while stationary");

        // Navigation progress triggers one clean transition
        lazy.stubNav.done = false;
        assertTrue(WolfSittingPoseHelper.syncSittingPoseOnTick(lazy));
        assertFalse(lazy.isInSittingPose());
        assertEquals(1, lazy.poseToggleCount);
        assertFalse(WolfSittingPoseHelper.syncSittingPoseOnTick(lazy));
        assertEquals(1, lazy.poseToggleCount);

        // Horizontal movement triggers clear
        lazy.stubNav.done = true;
        lazy.setSittingPoseStub(true);
        lazy.setDeltaMovement(new Vec3(0.05, -0.0784, 0.0));
        assertTrue(WolfSittingPoseHelper.syncSittingPoseOnTick(lazy));
        assertFalse(lazy.isInSittingPose());

        // Vertical hop triggers clear
        lazy.setSittingPoseStub(true);
        lazy.setDeltaMovement(new Vec3(0.0, 0.42, 0.0));
        assertTrue(WolfSittingPoseHelper.syncSittingPoseOnTick(lazy));
        assertFalse(lazy.isInSittingPose());

        // Target acquisition triggers clear
        StubWolf enemyTarget = createStubWolf(null, false, CanineArchetype.GUARDIAN);
        lazy.setSittingPoseStub(true);
        lazy.setDeltaMovement(new Vec3(0.0, -0.0784, 0.0));
        lazy.setTarget(enemyTarget);
        assertTrue(WolfSittingPoseHelper.syncSittingPoseOnTick(lazy));
        assertFalse(lazy.isInSittingPose());

        lazy.setSittingPoseStub(true);
        assertTrue(WolfSittingPoseHelper.clearUnorderedSittingPose(lazy));
        assertFalse(lazy.isInSittingPose());
        lazy.setTarget(null);

        // Negative parity 1: isOrderedToSit == true never clears
        StubWolf ordered = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        ordered.attachLevel(serverLevel);
        ordered.setOrderedToSitStub(true);
        ordered.setSittingPoseStub(true);
        ordered.stubNav.done = false;
        ordered.setDeltaMovement(new Vec3(0.4, 0.42, 0.0));
        ordered.setTarget(enemyTarget);
        ordered.resetPoseToggleCount();
        assertFalse(WolfSittingPoseHelper.syncSittingPoseOnTick(ordered));
        assertFalse(WolfSittingPoseHelper.clearUnorderedSittingPose(ordered));
        assertFalse(WolfSittingPoseHelper.clearSittingPoseOnDamage(ordered));
        assertTrue(ordered.isInSittingPose());
        assertEquals(0, ordered.poseToggleCount);

        // Negative parity 2: isPassenger == true never clears
        StubWolf passenger = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        passenger.attachLevel(serverLevel);
        passenger.setPassengerStub(true);
        passenger.setSittingPoseStub(true);
        passenger.stubNav.done = false;
        passenger.setDeltaMovement(new Vec3(0.3, 0.0, 0.0));
        passenger.resetPoseToggleCount();
        assertFalse(WolfSittingPoseHelper.syncSittingPoseOnTick(passenger));
        assertFalse(WolfSittingPoseHelper.clearUnorderedSittingPose(passenger));
        assertFalse(WolfSittingPoseHelper.clearSittingPoseOnDamage(passenger));
        assertTrue(passenger.isInSittingPose());
        assertEquals(0, passenger.poseToggleCount);

        // Negative parity 3: betterdogs$isPerformingScoot == true preserves tick & goal clear
        StubWolf scooting = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        scooting.attachLevel(serverLevel);
        scooting.setPerformingScootStub(true);
        scooting.setSittingPoseStub(true);
        scooting.stubNav.done = false;
        scooting.setDeltaMovement(new Vec3(0.2, 0.0, 0.0));
        scooting.resetPoseToggleCount();
        assertFalse(WolfSittingPoseHelper.syncSittingPoseOnTick(scooting));
        assertFalse(WolfSittingPoseHelper.clearUnorderedSittingPose(scooting));
        assertTrue(scooting.isInSittingPose());
        assertEquals(0, scooting.poseToggleCount);

        // Negative parity 4: client-side level never toggles sitting pose
        StubWolf clientWolf = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        clientWolf.attachLevel(clientLevel);
        clientWolf.setSittingPoseStub(true);
        clientWolf.stubNav.done = false;
        clientWolf.setDeltaMovement(new Vec3(0.5, 0.5, 0.0));
        clientWolf.resetPoseToggleCount();
        assertFalse(WolfSittingPoseHelper.syncSittingPoseOnTick(clientWolf));
        assertFalse(WolfSittingPoseHelper.clearUnorderedSittingPose(clientWolf));
        assertFalse(WolfSittingPoseHelper.clearSittingPoseOnDamage(clientWolf));
        assertTrue(clientWolf.isInSittingPose());
        assertEquals(0, clientWolf.poseToggleCount);
    }

    @Test
    @DisplayName("PersonalityFollowOwnerGoal lazy stop zeroes inertia & canEnterLazyRestingPose preconditions")
    void testPersonalityFollowOwnerGoalLazyRestAndInertiaZeroing() {
        StubServerLevel level = createStubServerLevel(createSittingGameRules());
        StubWolf lazy = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        lazy.attachLevel(level);
        lazy.setOnGroundStub(true);
        lazy.setDeltaMovement(new Vec3(0.18, -0.0784, -0.12));
        lazy.setRandomSeed(deterministicLazyPlopSeed);

        PersonalityFollowOwnerGoal followGoal = new PersonalityFollowOwnerGoal(lazy, 1.0, false);
        followGoal.stop();

        assertTrue(lazy.isInSittingPose(), "Eligible LAZY dog must enter lazy resting pose on stop()");
        assertFalse(lazy.isOrderedToSit(), "Lazy resting pose must never set orderedToSit");
        assertEquals(0.0, lazy.getDeltaMovement().x, 1e-6, "Horizontal X inertia must be zeroed");
        assertEquals(-0.0784, lazy.getDeltaMovement().y, 1e-6, "Vertical Y velocity must be preserved");
        assertEquals(0.0, lazy.getDeltaMovement().z, 1e-6, "Horizontal Z inertia must be zeroed");
        assertFalse(WolfSittingPoseHelper.syncSittingPoseOnTick(lazy),
                "Same-tick sync after lazy rest entry must keep resting pose");
        assertTrue(lazy.isInSittingPose());

        // start() clears unordered sitting pose without touching orderedToSit
        followGoal.start();
        assertFalse(lazy.isInSittingPose(), "PersonalityFollowOwnerGoal.start() must clear lazy sitting pose");
        assertFalse(lazy.isOrderedToSit());

        // Ordered sit preserved on follow start()
        lazy.setOrderedToSitStub(true);
        lazy.setSittingPoseStub(true);
        followGoal.start();
        assertTrue(lazy.isInSittingPose(), "PersonalityFollowOwnerGoal.start() must preserve ordered sit");

        // canEnterLazyRestingPose preconditions
        StubWolf check = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        check.attachLevel(level);
        check.setOnGroundStub(true);
        assertTrue(WolfSittingPoseHelper.canEnterLazyRestingPose(check));

        check.setOnGroundStub(false);
        assertFalse(WolfSittingPoseHelper.canEnterLazyRestingPose(check), "Airborne wolf must fail lazy rest check");
        check.setOnGroundStub(true);

        check.setInWaterStub(true);
        assertFalse(WolfSittingPoseHelper.canEnterLazyRestingPose(check), "In-water wolf must fail lazy rest check");
        check.setInWaterStub(false);

        check.setOnFireStub(true);
        assertFalse(WolfSittingPoseHelper.canEnterLazyRestingPose(check), "Burning wolf must fail lazy rest check");
        check.setOnFireStub(false);

        check.setOrderedToSitStub(true);
        assertFalse(WolfSittingPoseHelper.canEnterLazyRestingPose(check), "Ordered wolf must fail lazy rest check");
        check.setOrderedToSitStub(false);

        check.setPassengerStub(true);
        assertFalse(WolfSittingPoseHelper.canEnterLazyRestingPose(check), "Passenger wolf must fail lazy rest check");
        check.setPassengerStub(false);

        check.setLeashedStub(true);
        assertFalse(WolfSittingPoseHelper.canEnterLazyRestingPose(check), "Leashed wolf must fail lazy rest check");
        check.setLeashedStub(false);

        check.setGuardModeStub(true);
        assertFalse(WolfSittingPoseHelper.canEnterLazyRestingPose(check), "Guard wolf must fail lazy rest check");
        check.setGuardModeStub(false);

        check.setTarget(lazy);
        assertFalse(WolfSittingPoseHelper.canEnterLazyRestingPose(check), "Targeted wolf must fail lazy rest check");
        check.setTarget(null);

        check.setAliveStub(false);
        assertFalse(WolfSittingPoseHelper.canEnterLazyRestingPose(check), "Dead wolf must fail lazy rest check");
        check.setAliveStub(true);
    }

    @Test
    @DisplayName("Wander, fire, evasion, intercept, and guard goals clear unordered sit without wasSitting promotion")
    void testGoalTransitionsAndWasSittingNonPromotion() {
        StubServerLevel level = createStubServerLevel(createSittingGameRules());
        level.setWaterBlock(new BlockPos(1, 64, 0));

        // 1. TamedWanderNearOwnerGoal & WanderlustGoal
        StubWolf wanderWolf = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        wanderWolf.attachLevel(level);
        wanderWolf.setSittingPoseStub(true);
        TamedWanderNearOwnerGoal wanderGoal = new TamedWanderNearOwnerGoal(wanderWolf, 1.0);
        wanderGoal.start();
        assertFalse(wanderWolf.isInSittingPose(), "TamedWanderNearOwnerGoal.start() must clear lazy sit");
        assertFalse(wanderWolf.isOrderedToSit());

        wanderWolf.setSittingPoseStub(true);
        WanderlustGoal wanderlustGoal = new WanderlustGoal(wanderWolf, 1.0);
        wanderlustGoal.start();
        assertFalse(wanderWolf.isInSittingPose(), "WanderlustGoal.start() must clear lazy sit");
        assertFalse(wanderWolf.isOrderedToSit());

        // Ordered sit stays intact on wander goals
        wanderWolf.setOrderedToSitStub(true);
        wanderWolf.setSittingPoseStub(true);
        wanderGoal.start();
        assertTrue(wanderWolf.isInSittingPose());
        wanderlustGoal.start();
        assertTrue(wanderWolf.isInSittingPose());

        // 2. WolfSeekWaterOnFireGoal
        StubWolf fireLazy = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        fireLazy.attachLevel(level);
        fireLazy.setSittingPoseStub(true);
        WolfSeekWaterOnFireGoal seekWater = new WolfSeekWaterOnFireGoal(fireLazy);
        seekWater.start();
        assertFalse(fireLazy.isInSittingPose(), "WolfSeekWaterOnFireGoal.start() must clear lazy sit");
        assertFalse(fireLazy.isOrderedToSit());
        seekWater.stop();
        assertFalse(fireLazy.isOrderedToSit(), "WolfSeekWaterOnFireGoal.stop() must NEVER promote lazy rest to ordered sit");

        StubWolf fireOrdered = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        fireOrdered.attachLevel(level);
        fireOrdered.setOrderedToSitStub(true);
        fireOrdered.setSittingPoseStub(true);
        WolfSeekWaterOnFireGoal seekWaterOrdered = new WolfSeekWaterOnFireGoal(fireOrdered);
        seekWaterOrdered.start();
        assertFalse(fireOrdered.isOrderedToSit());
        assertFalse(fireOrdered.isInSittingPose());
        seekWaterOrdered.stop();
        assertTrue(fireOrdered.isOrderedToSit(), "Ordered sit must be restored on WolfSeekWaterOnFireGoal.stop()");
        assertTrue(fireOrdered.isInSittingPose());

        // 3. AvoidGhastFireballGoal
        StubWolf avoidLazy = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        avoidLazy.attachLevel(level);
        avoidLazy.setSittingPoseStub(true);
        AvoidGhastFireballGoal avoidGoal = new AvoidGhastFireballGoal(avoidLazy);
        avoidGoal.start();
        assertFalse(avoidLazy.isInSittingPose());
        assertFalse(avoidGoal.wasSitting(), "Lazy rest must NOT set wasSitting=true on AvoidGhastFireballGoal");
        avoidGoal.stop();
        assertFalse(avoidLazy.isOrderedToSit());

        avoidLazy.setOrderedToSitStub(true);
        avoidLazy.setSittingPoseStub(true);
        avoidGoal.start();
        assertTrue(avoidGoal.wasSitting(), "Ordered sit MUST set wasSitting=true on AvoidGhastFireballGoal");
        avoidGoal.stop();
        assertTrue(avoidLazy.isOrderedToSit());

        // 4. FleeExplodingHazardGoal
        StubWolf fleeLazy = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        fleeLazy.attachLevel(level);
        fleeLazy.setSittingPoseStub(true);
        FleeExplodingHazardGoal fleeGoal = new FleeExplodingHazardGoal(fleeLazy);
        fleeGoal.start();
        assertFalse(fleeLazy.isInSittingPose());
        assertFalse(fleeGoal.wasSitting(), "Lazy rest must NOT set wasSitting=true on FleeExplodingHazardGoal");
        fleeGoal.stop();
        assertFalse(fleeLazy.isOrderedToSit());

        fleeLazy.setOrderedToSitStub(true);
        fleeLazy.setSittingPoseStub(true);
        fleeGoal.start();
        assertTrue(fleeGoal.wasSitting(), "Ordered sit MUST set wasSitting=true on FleeExplodingHazardGoal");
        fleeGoal.stop();
        assertTrue(fleeLazy.isOrderedToSit());

        // 5. WolfProjectileInterceptGoal
        StubWolf interceptLazy = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        interceptLazy.attachLevel(level);
        interceptLazy.setSittingPoseStub(true);
        WolfProjectileInterceptGoal interceptGoal = new WolfProjectileInterceptGoal(interceptLazy);
        interceptGoal.start();
        assertFalse(interceptLazy.isInSittingPose());
        assertFalse(interceptGoal.wasSitting(), "Lazy rest must NOT set wasSitting=true on WolfProjectileInterceptGoal");
        interceptGoal.stop();
        assertFalse(interceptLazy.isOrderedToSit());

        interceptLazy.setOrderedToSitStub(true);
        interceptLazy.setSittingPoseStub(true);
        interceptGoal.start();
        assertTrue(interceptGoal.wasSitting(), "Ordered sit MUST set wasSitting=true on WolfProjectileInterceptGoal");
        interceptGoal.stop();
        assertTrue(interceptLazy.isOrderedToSit());
        WolfProjectileInterceptHelper.clearIntercept(interceptLazy);

        // 6. WolfGuardGoal checks !isInSittingPose() cleanly
        StubWolf guardWolf = createStubWolf(UUID.randomUUID(), true, CanineArchetype.LAZY);
        guardWolf.attachLevel(level);
        guardWolf.setGuardModeStub(true);
        guardWolf.setSittingPoseStub(true);
        guardWolf.setOrderedToSitStub(false);
        WolfGuardGoal guardGoal = new WolfGuardGoal(guardWolf);
        assertFalse(guardGoal.canUse(), "WolfGuardGoal.canUse() must stay blocked while seated");
    }

    @Test
    @DisplayName("Damage clears unordered sit via clearSittingPoseOnDamage & preserves cancelled friendly fire")
    void testDamageHandlerSittingPoseTransitions() {
        StubServerLevel level = createStubServerLevel(createSittingGameRules());
        UUID ownerId = UUID.randomUUID();
        StubServerPlayer owner = createStubPlayer(ownerId, false);
        StubWolf hostileAttacker = createStubWolf(null, false, CanineArchetype.GUARDIAN);

        // 1. Environmental damage (getEntity == null) clears unordered lazy sit
        StubWolf lazyWolf = createStubWolf(ownerId, true, CanineArchetype.LAZY);
        lazyWolf.attachLevel(level);
        lazyWolf.attachLiveOwner(owner);
        lazyWolf.setSittingPoseStub(true);
        DamageSource envSource = new DamageSource(fallDamageType);
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(lazyWolf, envSource));
        assertTrue(WolfSittingPoseHelper.clearSittingPoseOnDamage(lazyWolf));
        assertFalse(lazyWolf.isInSittingPose(), "Environmental damage must clear unordered sitting pose");

        // 2. Hostile attacker is NOT cancelled and clears unordered lazy sit
        lazyWolf.setSittingPoseStub(true);
        DamageSource hostileSource = new DamageSource(mobDamageType, hostileAttacker, hostileAttacker);
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(lazyWolf, hostileSource));
        assertTrue(WolfSittingPoseHelper.clearSittingPoseOnDamage(lazyWolf));
        assertFalse(lazyWolf.isInSittingPose(), "Hostile damage must clear unordered sitting pose");

        // 3. Untamed wolf clears sitting pose on damage
        StubWolf wildWolf = createStubWolf(null, false, CanineArchetype.LAZY);
        wildWolf.attachLevel(level);
        wildWolf.setSittingPoseStub(true);
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(wildWolf, envSource));
        assertTrue(WolfSittingPoseHelper.clearSittingPoseOnDamage(wildWolf));
        assertFalse(wildWolf.isInSittingPose(), "Untamed wolf damage must clear sitting pose");

        // 4. Cancelled friendly-fire hit from standing owner preserves sitting pose
        lazyWolf.setSittingPoseStub(true);
        lazyWolf.setHealth(10.0f);
        DamageSource ownerSource = new DamageSource(mobDamageType, owner, owner);
        assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(lazyWolf, ownerSource),
                "Friendly fire protection must cancel standing owner non-lethal melee attack");
        assertTrue(lazyWolf.isInSittingPose(),
                "Cancelled friendly-fire attack must preserve lazy sitting pose");

        // 5. Ordered sit stays seated even on non-cancelled hostile attack
        StubWolf orderedWolf = createStubWolf(ownerId, true, CanineArchetype.LAZY);
        orderedWolf.attachLevel(level);
        orderedWolf.setOrderedToSitStub(true);
        orderedWolf.setSittingPoseStub(true);
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(orderedWolf, hostileSource));
        assertFalse(WolfSittingPoseHelper.clearSittingPoseOnDamage(orderedWolf));
        assertTrue(orderedWolf.isInSittingPose(), "Non-cancelled damage must NEVER clear player-ordered sit");
    }

    @Test
    @DisplayName("Pillar 5: Live @Mixin source delegation, sidedness, GPLv3 headers & goal <= 300 LOC caps")
    void testPillar5MixinDelegationSidednessHeadersAndLocCaps() throws IOException {
        Path mainJava = findMainJavaRoot();
        String clientPkg = "net." + "minecraft." + "client.";

        String wolfMixinSrc = Files.readString(mainJava.resolve("net/vanillaoutsider/betterdogs/mixin/WolfMixin.java"), StandardCharsets.UTF_8);
        assertTrue(wolfMixinSrc.contains("WolfSittingPoseHelper.syncSittingPoseOnTick("),
                "WolfMixin must invoke WolfSittingPoseHelper.syncSittingPoseOnTick");
        assertTrue(wolfMixinSrc.contains("WolfSittingPoseHelper.clearSittingPoseOnDamage("),
                "WolfMixin must invoke WolfSittingPoseHelper.clearSittingPoseOnDamage");
        assertTrue(wolfMixinSrc.contains("!wolf.isInvulnerableTo("),
                "WolfMixin must guard damage pose clear behind !wolf.isInvulnerableTo");

        String wolfMobMixinSrc = Files.readString(mainJava.resolve("net/vanillaoutsider/betterdogs/mixin/WolfMobMixin.java"), StandardCharsets.UTF_8);
        assertTrue(wolfMobMixinSrc.contains("@Inject(method = \"setTarget\", at = @At(\"TAIL\"))"),
                "WolfMobMixin must inject setTarget @At(TAIL)");
        assertTrue(wolfMobMixinSrc.contains("WolfSittingPoseHelper.clearUnorderedSittingPose("),
                "WolfMobMixin must invoke WolfSittingPoseHelper.clearUnorderedSittingPose");

        String[] guardedFiles = {
                "net/vanillaoutsider/betterdogs/util/WolfSittingPoseHelper.java",
                "net/vanillaoutsider/betterdogs/mixin/WolfMixin.java",
                "net/vanillaoutsider/betterdogs/mixin/WolfMobMixin.java",
                "net/vanillaoutsider/betterdogs/ai/PersonalityFollowOwnerGoal.java",
                "net/vanillaoutsider/betterdogs/ai/TamedWanderNearOwnerGoal.java",
                "net/vanillaoutsider/betterdogs/ai/WanderlustGoal.java",
                "net/vanillaoutsider/betterdogs/ai/WolfSeekWaterOnFireGoal.java",
                "net/vanillaoutsider/betterdogs/ai/AvoidGhastFireballGoal.java",
                "net/vanillaoutsider/betterdogs/ai/FleeExplodingHazardGoal.java",
                "net/vanillaoutsider/betterdogs/ai/WolfProjectileInterceptGoal.java",
                "net/vanillaoutsider/betterdogs/ai/WolfGuardGoal.java"
        };
        for (String rel : guardedFiles) {
            List<String> lines = Files.readAllLines(mainJava.resolve(rel), StandardCharsets.UTF_8);
            assertEquals("// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3", lines.get(0).trim(),
                    "Exact GPLv3 header required on line 1 in " + rel);
            String joined = String.join("\n", lines);
            assertFalse(joined.contains(clientPkg), "Zero client imports allowed in " + rel);
            assertFalse(joined.contains("com.mojang.blaze3d."), "Zero Blaze3D imports allowed in " + rel);
            assertFalse(joined.contains("ClientModInitializer"), "Zero ClientModInitializer refs allowed in " + rel);
        }

        String[] cappedGoals = {
                "net/vanillaoutsider/betterdogs/ai/AvoidGhastFireballGoal.java",
                "net/vanillaoutsider/betterdogs/ai/FleeExplodingHazardGoal.java",
                "net/vanillaoutsider/betterdogs/ai/WolfProjectileInterceptGoal.java",
                "net/vanillaoutsider/betterdogs/ai/WolfGuardGoal.java"
        };
        for (String rel : cappedGoals) {
            List<String> lines = Files.readAllLines(mainJava.resolve(rel), StandardCharsets.UTF_8);
            assertTrue(lines.size() <= 300,
                    rel + " exceeded 300 LOC ceiling: " + lines.size());
        }
    }

    private static final class StubGameRules extends GameRules {
        private GameRuleMap map;

        private StubGameRules() {
            super(FeatureFlags.DEFAULT_FLAGS);
        }

        void init(GameRuleMap map) {
            this.map = map;
        }

        @Override
        public <T> T get(GameRule<T> rule) {
            T val = this.map != null ? this.map.get(rule) : null;
            if (val == null) {
                throw new IllegalArgumentException("Rule not set in StubGameRules: " + rule);
            }
            return val;
        }
    }

    private static final class StubNavigation extends GroundPathNavigation {
        boolean done = true;

        private StubNavigation() {
            super(null, null);
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public void stop() {
            this.done = true;
        }

        @Override
        public boolean moveTo(double x, double y, double z, double speed) {
            this.done = false;
            return true;
        }

        @Override
        public boolean moveTo(Entity entity, double speed) {
            this.done = false;
            return true;
        }

        @Override
        public boolean isStableDestination(BlockPos pos) {
            return false;
        }
    }

    private static final class StubLookControl extends LookControl {
        private StubLookControl() {
            super(null);
        }

        @Override
        public void setLookAt(Entity entity, float maxYaw, float maxPitch) {
        }
    }

    private static final class StubJumpControl extends JumpControl {
        private StubJumpControl() {
            super(null);
        }

        @Override
        public void jump() {
        }
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
            return EntityType.PLAYER;
        }
    }

    private static final class StubServerLevel extends ServerLevel {
        private GameRules gameRules;
        private @Nullable BlockPos waterBlock;

        private StubServerLevel() {
            super(null, null, null, null, null, null, false, 0L, null, false, null);
        }

        void init(GameRules gameRules) {
            this.gameRules = gameRules;
        }

        void setWaterBlock(@Nullable BlockPos waterBlock) {
            this.waterBlock = waterBlock;
        }

        @Override
        public GameRules getGameRules() {
            return this.gameRules;
        }

        @Override
        public boolean isClientSide() {
            return false;
        }

        @Override
        public long getGameTime() {
            return 100L;
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            if (this.waterBlock != null && this.waterBlock.equals(pos)) {
                return Fluids.WATER.defaultFluidState();
            }
            return Fluids.EMPTY.defaultFluidState();
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return pos.getY() < 64 ? Blocks.STONE.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }

        @Override
        public void playSound(@Nullable Entity source, double x, double y, double z, SoundEvent sound,
                              SoundSource category, float volume, float pitch) {
        }

        @Override
        public <T extends ParticleOptions> int sendParticles(T particle, double x, double y, double z,
                                                             int count, double dx, double dy, double dz, double speed) {
            return count;
        }

        @Override
        public int getMinY() {
            return -64;
        }

        @Override
        public int getHeight() {
            return 384;
        }
    }

    private static final class StubClientLevel extends ServerLevel {
        private StubClientLevel() {
            super(null, null, null, null, null, null, false, 0L, null, false, null);
        }

        @Override
        public boolean isClientSide() {
            return true;
        }
    }

    private static final class StubWolf extends Wolf implements WolfExtensions {
        private static final Field ENTITY_POSITION_FIELD = initEntityPositionField();

        private static Field initEntityPositionField() {
            try {
                Field f = Entity.class.getDeclaredField("position");
                f.setAccessible(true);
                return f;
            } catch (Exception e) {
                throw new IllegalStateException("Failed to locate Entity.position field", e);
            }
        }

        private UUID uuid;
        private @Nullable UUID ownerUuid;
        private @Nullable LivingEntity liveOwner;
        private boolean tame;
        private boolean alive;
        private boolean onGround;
        private boolean inWater;
        private boolean onFire;
        private boolean orderedToSit;
        private boolean sittingPose;
        private boolean passenger;
        private boolean leashed;
        private boolean guardMode;
        private boolean performingScoot;
        private float health;
        private Vec3 deltaMovement;
        private @Nullable Level level;
        private @Nullable LivingEntity target;
        private WolfPersonality personality;
        private CanineArchetype archetype;
        private RandomSource random;
        StubNavigation stubNav;
        private StubLookControl stubLook;
        private StubJumpControl stubJump;
        int poseToggleCount;

        private StubWolf() {
            super(null, null);
        }

        void init(UUID uuid, @Nullable UUID ownerUuid, boolean tame,
                  WolfPersonality personality, CanineArchetype archetype) {
            this.uuid = uuid;
            this.ownerUuid = ownerUuid;
            this.tame = tame;
            this.alive = true;
            this.onGround = true;
            this.health = 20.0f;
            this.deltaMovement = Vec3.ZERO;
            this.personality = personality;
            this.archetype = archetype;
            this.random = RandomSource.create(12345L);
            try {
                this.stubNav = (StubNavigation) unsafe.allocateInstance(StubNavigation.class);
                this.stubNav.done = true;
                this.stubLook = (StubLookControl) unsafe.allocateInstance(StubLookControl.class);
                this.stubJump = (StubJumpControl) unsafe.allocateInstance(StubJumpControl.class);
            } catch (InstantiationException e) {
                throw new IllegalStateException("Failed to allocate stub controls", e);
            }
            this.setBoundingBox(new AABB(0.0, 64.0, 0.0, 0.6, 64.85, 0.6));
            try {
                ENTITY_POSITION_FIELD.set(this, new Vec3(0.5, 64.0, 0.5));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to initialize Entity.position", e);
            }
        }

        void attachLevel(@Nullable Level level) { this.level = level; }
        void attachLiveOwner(@Nullable LivingEntity owner) { this.liveOwner = owner; }
        void setOrderedToSitStub(boolean v) { this.orderedToSit = v; }
        void setSittingPoseStub(boolean v) { this.sittingPose = v; }
        void setPassengerStub(boolean v) { this.passenger = v; }
        void setLeashedStub(boolean v) { this.leashed = v; }
        void setGuardModeStub(boolean v) { this.guardMode = v; }
        void setPerformingScootStub(boolean v) { this.performingScoot = v; }
        void setOnGroundStub(boolean v) { this.onGround = v; }
        void setInWaterStub(boolean v) { this.inWater = v; }
        void setOnFireStub(boolean v) { this.onFire = v; }
        void setAliveStub(boolean v) { this.alive = v; }
        void setRandomSeed(long seed) { this.random = RandomSource.create(seed); }
        void resetPoseToggleCount() { this.poseToggleCount = 0; }

        @Override public UUID getUUID() { return this.uuid; }
        @Override public String getStringUUID() { return this.uuid.toString(); }
        @Override public EntityType<?> getType() { return EntityType.WOLF; }
        @Override public Level level() { return this.level; }
        @Override public RandomSource getRandom() { return this.random; }
        @Override public BlockPos blockPosition() { return new BlockPos(0, 64, 0); }
        @Override public float getXRot() { return 0.0f; }
        @Override public float getYRot() { return 0.0f; }
        @Override public int getMaxHeadXRot() { return 40; }
        @Override public float getPathfindingMalus(PathType pathType) { return 0.0f; }
        @Override public void setPathfindingMalus(PathType pathType, float cost) {}
        @Override public boolean hasHome() { return false; }
        @Override public BlockPos getHomePosition() { return BlockPos.ZERO; }
        @Override public int getHomeRadius() { return -1; }
        @Override public boolean isWithinHome() { return true; }
        @Override public boolean isWithinHome(BlockPos pos) { return true; }
        @Override public boolean isWithinHome(Vec3 pos) { return true; }
        @Override public Component getName() { return Component.literal("StubWolf"); }
        @Override public SoundSource getSoundSource() { return SoundSource.NEUTRAL; }
        @Override public boolean isAlive() { return this.alive; }
        @Override public boolean isTame() { return this.tame; }
        @Override public int getAge() { return 0; }
        @Override public boolean onGround() { return this.onGround; }
        @Override public boolean isInWater() { return this.inWater; }
        @Override public boolean isOnFire() { return this.onFire; }
        @Override public int getRemainingFireTicks() { return this.onFire ? 40 : 0; }
        @Override public boolean isOrderedToSit() { return this.orderedToSit; }
        @Override public void setOrderedToSit(boolean ordered) { this.orderedToSit = ordered; }
        @Override public boolean isInSittingPose() { return this.sittingPose; }
        @Override public void setInSittingPose(boolean pose) { this.poseToggleCount++; this.sittingPose = pose; }
        @Override public boolean isPassenger() { return this.passenger; }
        @Override public boolean isLeashed() { return this.leashed; }
        @Override public boolean mayBeLeashed() { return this.leashed; }
        @Override public float getHealth() { return this.health; }
        @Override public void setHealth(float health) { this.health = health; }
        @Override public Vec3 getDeltaMovement() { return this.deltaMovement; }
        @Override public void setDeltaMovement(Vec3 dm) { this.deltaMovement = dm != null ? dm : Vec3.ZERO; }
        @Override public void setDeltaMovement(double x, double y, double z) { this.deltaMovement = new Vec3(x, y, z); }
        @Override public PathNavigation getNavigation() { return this.stubNav; }
        @Override public LookControl getLookControl() { return this.stubLook; }
        @Override public JumpControl getJumpControl() { return this.stubJump; }
        @Override public @Nullable LivingEntity getTarget() { return this.target; }
        @Override public void setTarget(@Nullable LivingEntity target) { this.target = target; }
        @Override public @Nullable EntityReference<LivingEntity> getOwnerReference() {
            return this.ownerUuid != null ? EntityReference.of(this.ownerUuid) : null;
        }
        @Override public @Nullable LivingEntity getOwner() { return this.liveOwner; }
        @Override public boolean isOwnedBy(LivingEntity entity) {
            return entity != null && (entity == this.liveOwner || (this.ownerUuid != null && this.ownerUuid.equals(entity.getUUID())));
        }

        @Override public WolfPersonality betterdogs$getPersonality() { return this.personality; }
        @Override public void betterdogs$setPersonality(WolfPersonality p) { this.personality = p; }
        @Override public boolean betterdogs$hasPersonality() { return this.personality != null; }
        @Override public CanineArchetype betterdogs$getCanineArchetype() { return this.archetype; }
        @Override public void betterdogs$setCanineArchetype(CanineArchetype a) { this.archetype = a; }
        @Override public boolean betterdogs$isPerformingScoot() { return this.performingScoot; }
        @Override public boolean betterdogs$isGuarding() { return this.guardMode; }
        @Override public void betterdogs$setGuarding(boolean g) { this.guardMode = g; }
        @Override public @Nullable BlockPos betterdogs$getGuardPos() { return null; }
        @Override public void betterdogs$setGuardPos(@Nullable BlockPos pos) {}
        @Override public float betterdogs$getSocialScale() { return 1.0f; }
        @Override public void betterdogs$setSocialScale(float scale) {}
        @Override public long betterdogs$getDnaSeed() { return 0L; }
        @Override public String betterdogs$getFavoriteTreat() { return ""; }
        @Override public void betterdogs$setFavoriteTreat(String treat) {}
        @Override public long betterdogs$getSoothedTime() { return 0L; }
        @Override public void betterdogs$setSoothedTime(long time) {}
        @Override public @Nullable BlockPos betterdogs$getSoundLocationTarget() { return null; }
        @Override public void betterdogs$setSoundLocationTarget(@Nullable BlockPos pos) {}
        @Override public int betterdogs$getPassiveOverrideTicks() { return 0; }
        @Override public void betterdogs$setPassiveOverrideTicks(int ticks) {}
        @Override public String betterdogs$getNemesisEntityType() { return ""; }
        @Override public void betterdogs$setNemesisEntityType(String type) {}
        @Override public long betterdogs$getNemesisExpiryTime() { return 0L; }
        @Override public void betterdogs$setNemesisExpiryTime(long time) {}
        @Override public void betterdogs$setCollarColor(DyeColor color) {}
        @Override public @Nullable UUID betterdogs$getParentUUID1() { return null; }
        @Override public void betterdogs$setParentUUID1(@Nullable UUID uuid) {}
        @Override public @Nullable UUID betterdogs$getParentUUID2() { return null; }
        @Override public void betterdogs$setParentUUID2(@Nullable UUID uuid) {}
        @Override public boolean betterdogs$isInbred() { return false; }
        @Override public void betterdogs$setInbred(boolean inbred) {}
        @Override public boolean betterdogs$isUpForAdoption() { return false; }
        @Override public void betterdogs$setUpForAdoption(boolean adoption) {}
        @Override public long betterdogs$getLastGiftDay() { return -1L; }
        @Override public void betterdogs$setLastGiftDay(long day) {}
        @Override public int betterdogs$getZoomiesTicks() { return 0; }
        @Override public void betterdogs$setZoomiesTicks(int ticks) {}
        @Override public boolean betterdogs$hasFetchedStick() { return false; }
        @Override public void betterdogs$setHasFetchedStick(boolean fetched) {}
        @Override public int betterdogs$getCalmTicks() { return 0; }
        @Override public void betterdogs$setCalmTicks(int ticks) {}
        @Override public int betterdogs$getHowlingTicks() { return 0; }
        @Override public void betterdogs$setHowlingTicks(int ticks) {}
        @Override public @Nullable Holder<WolfVariant> betterdogs$getVariant() { return null; }
        @Override public void betterdogs$setVariant(Holder<WolfVariant> variant) {}
        @Override public @Nullable Holder<WolfSoundVariant> betterdogs$getSoundVariant() { return null; }
        @Override public int betterdogs$getFeedCount() { return 0; }
        @Override public void betterdogs$setFeedCount(int count) {}
        @Override public @Nullable LivingEntity betterdogs$getRetaliationTarget() { return null; }
        @Override public void betterdogs$setRetaliationTarget(@Nullable LivingEntity target, int ticks) {}
        @Override public int betterdogs$getRetaliationTicks() { return 0; }
        @Override public String betterdogs$getBloodFeudTarget() { return ""; }
        @Override public void betterdogs$setBloodFeudTarget(String targetUuid) {}
        @Override public boolean betterdogs$hasBloodFeud() { return false; }
        @Override public int betterdogs$getPlayFightCooldown() { return 0; }
        @Override public void betterdogs$setPlayFightCooldown(int ticks) {}
        @Override public @Nullable UUID betterdogs$getLeaderUUID() { return null; }
        @Override public void betterdogs$setLeaderUUID(@Nullable UUID uuid) {}
        @Override public boolean betterdogs$isPackLeader() { return false; }
        @Override public void betterdogs$setPackLeader(boolean isLeader) {}
        @Override public int betterdogs$getWanderlustTicks() { return 0; }
        @Override public void betterdogs$setWanderlustTicks(int ticks) {}
        @Override public boolean betterdogs$isSittingManually() { return false; }
    }
}
