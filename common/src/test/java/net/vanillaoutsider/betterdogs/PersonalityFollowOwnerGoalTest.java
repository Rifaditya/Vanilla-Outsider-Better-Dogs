// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathTypeCache;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.PersonalityFollowOwnerGoal;
import net.vanillaoutsider.betterdogs.ai.PersonalityFollowOwnerGoal.FollowerSpacingCache;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfCatchupHelper;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BL-BD-046: PersonalityFollowOwnerGoal Threshold, GameRule, Catchup & Negative Parity Suite")
class PersonalityFollowOwnerGoalTest {

    private static final Unsafe unsafe = initUnsafe();

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
            if (BetterDogsGameRules.BD_AGGRO_FOLLOW_START == null) {
                BetterDogsGameRules.register();
            }
        } catch (Throwable ignored) {
        }
    }

    private static StubWolf createStubWolf(@Nullable UUID ownerUuid, @Nullable WolfPersonality personality, CanineArchetype archetype) {
        try {
            StubWolf wolf = (StubWolf) unsafe.allocateInstance(StubWolf.class);
            wolf.init(UUID.randomUUID(), ownerUuid, true, personality, archetype);
            return wolf;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubWolf", e);
        }
    }

    private static StubServerPlayer createStubPlayer(UUID uuid, boolean spectator) {
        try {
            StubServerPlayer player = (StubServerPlayer) unsafe.allocateInstance(StubServerPlayer.class);
            player.init(uuid, spectator);
            return player;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubServerPlayer", e);
        }
    }

    private static StubServerLevel createStubServerLevel(long gameTime, List<Wolf> candidates, @Nullable GameRules rules) {
        try {
            StubServerLevel level = (StubServerLevel) unsafe.allocateInstance(StubServerLevel.class);
            level.init(gameTime, candidates, rules);
            return level;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubServerLevel", e);
        }
    }

    private static Path findProjectRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        Path cursor = dir;
        while (cursor != null) {
            if (Files.exists(cursor.resolve("src/main/java/net/vanillaoutsider/betterdogs/ai/PersonalityFollowOwnerGoal.java"))) {
                return cursor;
            }
            if (Files.exists(cursor.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/ai/PersonalityFollowOwnerGoal.java"))) {
                return cursor;
            }
            cursor = cursor.getParent();
        }
        return dir;
    }

    @Test
    @DisplayName("1. Aggressive default 25.0 start / 41.0 teleport, running window [25.0, 41.0), continuous follow & catchup parity")
    void testAggressiveDefaultRunningWindowAndTeleportBoundary() {
        UUID ownerUuid = UUID.randomUUID();
        StubServerPlayer owner = createStubPlayer(ownerUuid, false);
        StubWolf wolf = createStubWolf(ownerUuid, WolfPersonality.AGGRESSIVE, CanineArchetype.GUARDIAN);
        StubServerLevel level = createStubServerLevel(100L, List.of(wolf), null);
        owner.attachLevel(level);
        wolf.attachLevel(level);
        wolf.attachLiveOwner(owner, 26.0 * 26.0);

        assertEquals(25.0f, new BetterDogsConfig().getAggressiveFollowStart(), 1e-4f,
                "Default BetterDogsConfig.aggressiveFollowStart must be 25.0f");

        PersonalityFollowOwnerGoal goal = new PersonalityFollowOwnerGoal(wolf, 1.0, false);
        assertEquals(25.0f, goal.getStartDistance(), 1e-4f);
        assertEquals(41.0f, goal.getTeleportThreshold(), 1e-4f);

        // Inside running catch-up window [25.0, 41.0): navigates without teleporting
        assertTrue(goal.canUse(), "Aggressive wolf at 26m must activate follow goal");
        goal.start();
        assertTrue(goal.canContinueToUse(), "Aggressive wolf at 26m must continue following");
        goal.tick();
        assertEquals(1, wolf.stubNav.moveToEntityCount, "tick() must issue navigation.moveTo(...)");
        assertEquals(0, wolf.teleportCount, "Aggressive wolf at 26m must NOT teleport");

        // Continuous follow inside startDist (2.0 < 15.0 < 25.0): must NOT prematurely stop navigation
        wolf.setDistanceToOwnerSqr(15.0 * 15.0);
        assertTrue(goal.canContinueToUse(), "Aggressive wolf at 15.0m (between 2.0 stopDist and 25.0 startDist) must continue following");
        for (int i = 0; i < 10; i++) {
            goal.tick();
        }
        assertEquals(2, wolf.stubNav.moveToEntityCount, "Aggressive wolf at 15.0m must continue issuing navigation.moveTo(...)");
        assertEquals(0, wolf.stubNav.stopCount, "tick() must not stop navigation when dist < startDist");
        assertEquals(0, wolf.teleportCount, "Aggressive wolf at 15.0m must NOT teleport");

        // Still inside running window at 40.5m: must NOT teleport
        wolf.setDistanceToOwnerSqr(40.5 * 40.5);
        goal.tick();
        assertEquals(0, wolf.teleportCount, "Wolf at 40.5m (< 41.0m threshold) must not teleport");
        assertEquals(41.0D, WolfCatchupHelper.getCatchupTeleportDistance(wolf, false), 1e-4D);
        assertEquals(41.0D, WolfCatchupHelper.getCatchupTeleportDistance(wolf, true), 1e-4D);

        // At >= 41.0m: goal.tick() triggers teleport and stops navigation
        wolf.setDistanceToOwnerSqr(41.0 * 41.0);
        goal.tick();
        assertEquals(1, wolf.teleportCount, "Wolf at >= 41.0m must trigger teleportToOwner");
        assertTrue(wolf.stubNav.stopCount >= 1, "Teleport must stop navigation");
    }

    @Test
    @DisplayName("2. Dual-value GameRule overrides (10 and 60) + null/unregistered fallback preservation [PAT-20261115-001]")
    void testDualValueGameRuleOverridesAndFallbackPreservation() {
        GameRules.Key<GameRules.IntegerValue> previousKey = BetterDogsGameRules.BD_AGGRO_FOLLOW_START;
        GameRules.Key<GameRules.IntegerValue> activeKey = previousKey != null
                ? previousKey
                : new GameRules.Key<>("bd_aggro_follow_start", GameRules.Category.MISC);

        try {
            BetterDogsGameRules.BD_AGGRO_FOLLOW_START = activeKey;

            UUID ownerUuid = UUID.randomUUID();
            StubWolf wolf = createStubWolf(ownerUuid, WolfPersonality.AGGRESSIVE, CanineArchetype.GUARDIAN);

            // Null level & null GameRules & unregistered GameRules preserve caller fallback 25
            assertEquals(25, BetterDogsGameRules.getInt(null, activeKey, 25));

            StubServerLevel nullRulesLevel = createStubServerLevel(200L, List.of(wolf), null);
            assertEquals(25, BetterDogsGameRules.getInt(nullRulesLevel, activeKey, 25));

            StubServerLevel emptyRulesLevel = createStubServerLevel(200L, List.of(wolf), new GameRules());
            assertEquals(25, BetterDogsGameRules.getInt(emptyRulesLevel, activeKey, 25));
            GameRules.Key<GameRules.IntegerValue> unregisteredKey =
                    new GameRules.Key<>("bd_aggro_unregistered_test", GameRules.Category.MISC);
            assertEquals(42, BetterDogsGameRules.getInt(emptyRulesLevel, unregisteredKey, 42));

            wolf.attachLevel(emptyRulesLevel);
            PersonalityFollowOwnerGoal goal = new PersonalityFollowOwnerGoal(wolf, 1.0, false);
            assertEquals(25.0f, goal.getStartDistance(), 1e-4f,
                    "Unregistered rule in ServerLevel GameRules must preserve fallback 25.0f");
            assertEquals(41.0f, goal.getTeleportThreshold(), 1e-4f);

            // Live non-null GameRules subclass overriding getInt: test BOTH lower (10) and higher (60)
            final int[] aggroStart = { 10 };
            GameRules customRules = new GameRules() {
                @Override
                public int getInt(GameRules.Key<GameRules.IntegerValue> key) {
                    if (activeKey.equals(key)) {
                        return aggroStart[0];
                    }
                    return super.getInt(key);
                }
            };
            StubServerLevel customLevel = createStubServerLevel(200L, List.of(wolf), customRules);
            wolf.attachLevel(customLevel);

            assertEquals(10.0f, goal.getStartDistance(), 1e-4f, "Lower GameRule override (10) must apply");
            assertEquals(32.0f, goal.getTeleportThreshold(), 1e-4f, "startDist=10 clamps teleport floor at 32.0f");
            assertEquals(40.0D, WolfCatchupHelper.getCatchupTeleportDistance(wolf, false), 1e-4D);
            assertEquals(32.0D, WolfCatchupHelper.getCatchupTeleportDistance(wolf, true), 1e-4D);

            aggroStart[0] = 60;
            assertEquals(60.0f, goal.getStartDistance(), 1e-4f, "Higher GameRule override (60) must apply");
            assertEquals(76.0f, goal.getTeleportThreshold(), 1e-4f, "startDist=60 scales teleport threshold to 76.0f");
            assertEquals(76.0D, WolfCatchupHelper.getCatchupTeleportDistance(wolf, false), 1e-4D);
            assertEquals(76.0D, WolfCatchupHelper.getCatchupTeleportDistance(wolf, true), 1e-4D);
        } finally {
            BetterDogsGameRules.BD_AGGRO_FOLLOW_START = previousKey;
        }
    }

    @Test
    @DisplayName("3. Negative Parity: below startDist, sitting, leashed, passenger, guard-mode, spectator owner, null personality")
    void testNegativeParityGuardsAndNullSafety() {
        UUID ownerUuid = UUID.randomUUID();
        StubServerPlayer owner = createStubPlayer(ownerUuid, false);
        StubWolf wolf = createStubWolf(ownerUuid, WolfPersonality.AGGRESSIVE, CanineArchetype.GUARDIAN);
        StubServerLevel level = createStubServerLevel(300L, List.of(wolf), null);
        owner.attachLevel(level);
        wolf.attachLevel(level);
        wolf.attachLiveOwner(owner, 24.0 * 24.0);

        PersonalityFollowOwnerGoal goal = new PersonalityFollowOwnerGoal(wolf, 1.0, false);

        // Inside startDist (24.0 < 25.0)
        assertFalse(goal.canUse(), "Wolf at 24.0m must not activate follow goal");

        // Outside startDist (30.0m), verify every state rejection
        wolf.setDistanceToOwnerSqr(30.0 * 30.0);
        assertTrue(goal.canUse());

        wolf.setOrderedToSitStub(true);
        assertFalse(goal.canUse(), "Ordered-to-sit wolf must fail canUse()");
        assertFalse(goal.canContinueToUse(), "Ordered-to-sit wolf must fail canContinueToUse()");
        wolf.setOrderedToSitStub(false);

        wolf.setGuardModeStub(true);
        assertFalse(goal.canUse(), "Guard-mode wolf must fail canUse()");
        assertFalse(goal.canContinueToUse(), "Guard-mode wolf must fail canContinueToUse()");
        wolf.setGuardModeStub(false);

        // Leashed: fail canUse/canContinueToUse AND never teleport in tick() even at 100m
        wolf.setDistanceToOwnerSqr(100.0 * 100.0);
        wolf.setLeashedStub(true);
        assertFalse(goal.canUse(), "Leashed wolf must fail canUse()");
        assertFalse(goal.canContinueToUse(), "Leashed wolf must fail canContinueToUse()");
        goal.tick();
        assertEquals(0, wolf.teleportCount, "Leashed wolf at 100m must NEVER teleport");
        wolf.setLeashedStub(false);

        // Passenger: fail canUse/canContinueToUse AND never teleport in tick() even at 100m
        wolf.setPassengerStub(true);
        assertFalse(goal.canUse(), "Passenger wolf must fail canUse()");
        assertFalse(goal.canContinueToUse(), "Passenger wolf must fail canContinueToUse()");
        goal.tick();
        assertEquals(0, wolf.teleportCount, "Passenger wolf at 100m must NEVER teleport");
        wolf.setPassengerStub(false);

        // Spectator owner: fail canUse AND canContinueToUse
        owner.setSpectator(true);
        assertFalse(goal.canUse(), "Wolf with spectator owner must fail canUse()");
        assertFalse(goal.canContinueToUse(), "Wolf with spectator owner must fail canContinueToUse()");
        owner.setSpectator(false);

        // Null personality & null wolf safety
        StubWolf nullPersonalityWolf = createStubWolf(ownerUuid, null, CanineArchetype.GUARDIAN);
        nullPersonalityWolf.attachLevel(level);
        assertEquals(10.0f, PersonalityFollowOwnerGoal.calculateBaseStartDistance(nullPersonalityWolf), 1e-4f,
                "Null personality must default safely to 10.0f without NPE");
        assertEquals(10.0f, PersonalityFollowOwnerGoal.calculateBaseStartDistance(null), 1e-4f);
        assertEquals(10.0f, PersonalityFollowOwnerGoal.getEffectiveStartDistance(null), 1e-4f);
    }

    @Test
    @DisplayName("4. Personality & Archetype threshold matrix")
    void testPersonalityAndArchetypeThresholdMatrix() {
        UUID ownerUuid = UUID.randomUUID();
        StubServerLevel level = createStubServerLevel(400L, List.of(), null);

        assertThresholds(ownerUuid, level, WolfPersonality.NORMAL, CanineArchetype.GUARDIAN, 10.0f, 32.0f);
        assertThresholds(ownerUuid, level, WolfPersonality.PACIFIST, CanineArchetype.GUARDIAN, 5.0f, 32.0f);
        assertThresholds(ownerUuid, level, WolfPersonality.NORMAL, CanineArchetype.ADVENTURER, 16.0f, 32.0f);
        assertThresholds(ownerUuid, level, WolfPersonality.AGGRESSIVE, CanineArchetype.LAZY, 4.0f, 32.0f);
        assertThresholds(ownerUuid, level, WolfPersonality.AGGRESSIVE, CanineArchetype.ADVENTURER, 25.0f, 41.0f);
    }

    private static void assertThresholds(UUID ownerUuid, StubServerLevel level, WolfPersonality personality,
                                         CanineArchetype archetype, float expectedStart, float expectedTeleport) {
        StubWolf wolf = createStubWolf(ownerUuid, personality, archetype);
        wolf.attachLevel(level);
        PersonalityFollowOwnerGoal goal = new PersonalityFollowOwnerGoal(wolf, 1.0, false);
        assertEquals(expectedStart, goal.getStartDistance(), 1e-4f,
                "Start distance mismatch for " + personality + " + " + archetype);
        assertEquals(expectedTeleport, goal.getTeleportThreshold(), 1e-4f,
                "Teleport threshold mismatch for " + personality + " + " + archetype);
    }

    @Test
    @DisplayName("5. Pillar 2 concurrency stress on FollowerSpacingCache, continuous follow source contract & Pillar 5 sidedness scan")
    void testFollowerSpacingConcurrencyAndContracts() throws InterruptedException, IOException {
        int threads = 16;
        int iterations = 300;
        UUID[] owners = new UUID[] {UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()};
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger failures = new AtomicInteger(0);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int t = 0; t < threads; t++) {
            final int tid = t;
            pool.execute(() -> {
                ready.countDown();
                try {
                    start.await();
                    for (int i = 0; i < iterations; i++) {
                        UUID owner = owners[(tid + i) % owners.length];
                        long now = 1000L;
                        FollowerSpacingCache.update(owner, 3, now, 400);
                        int cached = FollowerSpacingCache.getCount(owner, now + (i % 100));
                        int known = FollowerSpacingCache.getLastKnownCount(owner);
                        if (cached != 3 || known != 3) {
                            failures.incrementAndGet();
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
        assertEquals(0, failures.get(), "Zero concurrency failures permitted in FollowerSpacingCache");

        Path root = findProjectRoot();
        String basePrefix = Files.exists(root.resolve("common/src/main/java")) ? "common/src/main/java/" : "src/main/java/";
        String[] filesToCheck = {
                basePrefix + "net/vanillaoutsider/betterdogs/ai/PersonalityFollowOwnerGoal.java",
                basePrefix + "net/vanillaoutsider/betterdogs/util/WolfCatchupHelper.java",
                basePrefix + "net/vanillaoutsider/betterdogs/registry/BetterDogsGameRules.java"
        };
        for (String rel : filesToCheck) {
            Path p = root.resolve(rel);
            assertTrue(Files.exists(p), "Expected source file must exist: " + p);
            String src = Files.readString(p, StandardCharsets.UTF_8);
            assertFalse(src.contains("net.minecraft.client."), "Zero client imports allowed in " + rel);
            assertFalse(src.contains("ClientModInitializer"), "Zero ClientModInitializer refs allowed in " + rel);
            assertFalse(src.contains("com.mojang.blaze3d."), "Zero Blaze3D refs allowed in " + rel);
        }

        String goalSrc = Files.readString(root.resolve(filesToCheck[0]), StandardCharsets.UTF_8).replace("\r\n", "\n");
        assertTrue(goalSrc.contains("if (--this.recalcTimer <= 0)"),
                "PersonalityFollowOwnerGoal must throttle navigation recalculation via recalcTimer");
        assertFalse(goalSrc.contains("else {\n            this.wolf.getNavigation().stop();"),
                "PersonalityFollowOwnerGoal must NOT stop navigation inside tick() when distSqr < startDist^2");
    }

    private static final class StubGroundPathNavigation extends GroundPathNavigation {
        boolean done;
        int moveToEntityCount;
        int stopCount;

        private StubGroundPathNavigation() {
            super(null, null);
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public void stop() {
            this.stopCount++;
            this.done = true;
        }

        @Override
        public boolean moveTo(Entity entity, double speed) {
            this.moveToEntityCount++;
            this.done = false;
            return true;
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

    private static final class StubServerPlayer extends ServerPlayer {
        private UUID uuid;
        private boolean spectator;
        private @Nullable ServerLevel level;
        private Abilities abilities;

        private StubServerPlayer() {
            super(null, null, null, null);
        }

        void init(UUID uuid, boolean spectator) {
            this.uuid = uuid;
            this.spectator = spectator;
            this.abilities = new Abilities();
            this.setBoundingBox(new AABB(-0.3, 64.0, -0.3, 0.3, 65.8, 0.3));
        }

        void attachLevel(@Nullable ServerLevel level) {
            this.level = level;
        }

        void setSpectator(boolean spectator) {
            this.spectator = spectator;
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public boolean isSpectator() {
            return this.spectator;
        }

        @Override
        public boolean isAlive() {
            return true;
        }

        @Override
        public boolean isPassenger() {
            return false;
        }

        @Override
        public boolean isFallFlying() {
            return false;
        }

        @Override
        public Vec3 getDeltaMovement() {
            return Vec3.ZERO;
        }

        @Override
        public Abilities getAbilities() {
            return this.abilities;
        }

        @Override
        public ServerLevel level() {
            return this.level;
        }

        @Override
        public BlockPos blockPosition() {
            return new BlockPos(0, 64, 0);
        }

        @Override
        public EntityType<?> getType() {
            return EntityType.PLAYER;
        }
    }

    private static final class StubServerLevel extends ServerLevel {
        private long gameTime;
        private List<Wolf> candidates;
        private @Nullable GameRules gameRules;

        private StubServerLevel() {
            super(null, null, null, null, null, null, null, false, 0L, null, false, null);
        }

        void init(long gameTime, List<Wolf> candidates, @Nullable GameRules gameRules) {
            this.gameTime = gameTime;
            this.candidates = candidates;
            this.gameRules = gameRules;
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
            return this.gameTime;
        }

        @Override
        public boolean isLoaded(BlockPos pos) {
            return true;
        }

        @Override
        public int getMinBuildHeight() {
            return -64;
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return pos.getY() < 64 ? Blocks.STONE.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return Fluids.EMPTY.defaultFluidState();
        }

        @Override
        public boolean noCollision(@Nullable Entity entity, AABB aabb) {
            return true;
        }

        @Override
        public PathTypeCache getPathTypeCache() {
            return null;
        }

        @Override
        public BlockPos getHeightmapPos(Heightmap.Types type, BlockPos pos) {
            return new BlockPos(pos.getX(), 64, pos.getZ());
        }

        @Override
        public <T extends Entity> List<T> getEntitiesOfClass(Class<T> clazz, AABB aabb, Predicate<? super T> predicate) {
            List<T> matched = new ArrayList<>();
            if (Wolf.class.isAssignableFrom(clazz) && this.candidates != null) {
                for (Wolf w : this.candidates) {
                    T cast = clazz.cast(w);
                    if (predicate.test(cast)) {
                        matched.add(cast);
                    }
                }
            }
            return matched;
        }
    }

    private static final class StubWolf extends Wolf implements WolfExtensions {
        private UUID uuid;
        private @Nullable UUID ownerUuid;
        private boolean tame;
        private @Nullable Level level;
        private @Nullable LivingEntity liveOwner;
        private double ownerDistanceSq;
        private boolean orderedToSit;
        private boolean sittingPose;
        private boolean leashed;
        private boolean passenger;
        private boolean guardMode;
        private boolean sittingManually;
        private @Nullable WolfPersonality personality;
        private CanineArchetype archetype;
        private RandomSource random;
        private StubGroundPathNavigation stubNav;
        private StubLookControl stubLook;
        int teleportCount;
        @Nullable Vec3 lastTeleportPos;

        private StubWolf() {
            super(null, null);
        }

        void init(UUID uuid, @Nullable UUID ownerUuid, boolean tame,
                  @Nullable WolfPersonality personality, CanineArchetype archetype) {
            this.uuid = uuid;
            this.ownerUuid = ownerUuid;
            this.tame = tame;
            this.personality = personality;
            this.archetype = archetype;
            this.random = RandomSource.create(12345L);
            try {
                this.stubNav = (StubGroundPathNavigation) unsafe.allocateInstance(StubGroundPathNavigation.class);
                this.stubLook = (StubLookControl) unsafe.allocateInstance(StubLookControl.class);
            } catch (InstantiationException e) {
                throw new IllegalStateException("Failed to allocate navigation/look stubs", e);
            }
            this.setBoundingBox(new AABB(0.0, 64.0, 0.0, 0.6, 64.85, 0.6));
        }

        void attachLevel(@Nullable Level level) {
            this.level = level;
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

        void setLeashedStub(boolean leashed) {
            this.leashed = leashed;
        }

        void setPassengerStub(boolean passenger) {
            this.passenger = passenger;
        }

        void setGuardModeStub(boolean guardMode) {
            this.guardMode = guardMode;
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
        public RandomSource getRandom() {
            return this.random;
        }

        @Override
        public BlockPos blockPosition() {
            return new BlockPos(0, 64, 0);
        }

        @Override
        public float getXRot() {
            return 0.0f;
        }

        @Override
        public float getYRot() {
            return 0.0f;
        }

        @Override
        public int getMaxHeadXRot() {
            return 40;
        }

        @Override
        public float getPathfindingMalus(PathType pathType) {
            return 0.0f;
        }

        @Override
        public void setPathfindingMalus(PathType pathType, float malus) {
        }

        @Override
        public boolean isAlive() {
            return true;
        }

        @Override
        public boolean isSpectator() {
            return false;
        }

        @Override
        public boolean isTame() {
            return this.tame;
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
        public void setInSittingPose(boolean pose) {
            this.sittingPose = pose;
        }

        @Override
        public boolean isLeashed() {
            return this.leashed;
        }

        @Override
        public boolean mayBeLeashed() {
            return this.leashed;
        }

        @Override
        public boolean isPassenger() {
            return this.passenger;
        }

        @Override
        public PathNavigation getNavigation() {
            return this.stubNav;
        }

        @Override
        public LookControl getLookControl() {
            return this.stubLook;
        }

        @Override
        public double distanceToSqr(Entity entity) {
            return entity == this.liveOwner ? this.ownerDistanceSq : 4.0D;
        }

        @Override
        public @Nullable UUID getOwnerUUID() {
            return this.ownerUuid;
        }

        @Override
        public @Nullable LivingEntity getOwner() {
            return this.liveOwner;
        }

        @Override
        public boolean isOwnedBy(LivingEntity entity) {
            return entity != null && entity == this.liveOwner;
        }

        @Override
        public boolean teleportTo(ServerLevel level, double x, double y, double z, Set<RelativeMovement> relatives,
                                  float yRot, float xRot) {
            this.teleportCount++;
            this.lastTeleportPos = new Vec3(x, y, z);
            return true;
        }

        @Override
        public WolfPersonality betterdogs$getPersonality() {
            return this.personality;
        }

        @Override
        public void betterdogs$setPersonality(WolfPersonality personality) {
            this.personality = personality;
        }

        @Override
        public boolean betterdogs$hasPersonality() {
            return this.personality != null;
        }

        @Override
        public CanineArchetype betterdogs$getCanineArchetype() {
            return this.archetype;
        }

        @Override
        public void betterdogs$setCanineArchetype(CanineArchetype archetype) {
            this.archetype = archetype;
        }

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
        @Override public boolean betterdogs$isGuarding() { return this.guardMode; }
        @Override public void betterdogs$setGuarding(boolean guarding) { this.guardMode = guarding; }
        @Override public @Nullable BlockPos betterdogs$getGuardPos() { return null; }
        @Override public void betterdogs$setGuardPos(@Nullable BlockPos pos) {}
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
        @Override public boolean betterdogs$isSittingManually() { return this.sittingManually; }
    }
}
