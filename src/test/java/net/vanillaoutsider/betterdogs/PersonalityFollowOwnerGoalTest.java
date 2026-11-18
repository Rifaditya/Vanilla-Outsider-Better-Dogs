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
import java.util.Set;
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
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
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
import org.jspecify.annotations.Nullable;
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
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        if (BetterDogsGameRules.BD_AGGRO_FOLLOW_START == null) {
            BetterDogsGameRules.register();
        }
    }

    private static StubWolf createStubWolf(@Nullable UUID ownerUuid, WolfPersonality personality, CanineArchetype archetype) {
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

    private static GameRules createFollowGameRules() {
        return new GameRules(List.of(
                BetterDogsGameRules.BD_AGGRO_FOLLOW_START,
                BetterDogsGameRules.BD_PACI_FOLLOW_START,
                BetterDogsGameRules.BD_NORMAL_FOLLOW_START,
                BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MULTIPLIER,
                BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MAX,
                BetterDogsGameRules.BD_FAST_TRAVEL_CATCHUP
        ));
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
    @DisplayName("1. Aggressive default 25.0 start / 41.0 teleport, running window [25.0, 41.0), and catchup parity")
    void testAggressiveDefaultRunningWindowAndTeleportBoundary() {
        UUID ownerUuid = UUID.randomUUID();
        StubServerPlayer owner = createStubPlayer(ownerUuid, false);
        StubWolf wolf = createStubWolf(ownerUuid, WolfPersonality.AGGRESSIVE, CanineArchetype.GUARDIAN);
        StubServerLevel level = createStubServerLevel(100L, List.of(wolf), createFollowGameRules());
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

        // Still inside running window at 40.5m: goal and WolfCatchupHelper must NOT teleport
        wolf.setDistanceToOwnerSqr(40.5 * 40.5);
        goal.tick();
        WolfCatchupHelper.checkAndPerformCatchUp(owner);
        assertEquals(0, wolf.teleportCount, "Wolf at 40.5m (< 41.0m threshold) must not teleport via goal or catchup");

        // At >= 41.0m: goal.tick() triggers teleport and stops navigation
        wolf.setDistanceToOwnerSqr(41.0 * 41.0);
        goal.tick();
        assertEquals(1, wolf.teleportCount, "Wolf at >= 41.0m must trigger teleportToOwner");
        assertTrue(wolf.stubNav.stopCount >= 1, "Teleport must stop navigation");

        // Catchup helper at 41.5m triggers teleport as well
        wolf.setDistanceToOwnerSqr(41.5 * 41.5);
        WolfCatchupHelper.checkAndPerformCatchUp(owner);
        assertEquals(2, wolf.teleportCount, "Catchup helper must teleport aggressive wolf beyond 41.0m");
    }

    @Test
    @DisplayName("2. Dual-value GameRule overrides (10 and 60) + null/unregistered fallback preservation [PAT-20261115-001]")
    void testDualValueGameRuleOverridesAndFallbackPreservation() {
        UUID ownerUuid = UUID.randomUUID();
        StubServerPlayer owner = createStubPlayer(ownerUuid, false);
        StubWolf wolf = createStubWolf(ownerUuid, WolfPersonality.AGGRESSIVE, CanineArchetype.GUARDIAN);

        // Null level & null GameRules & empty GameRules preserve caller fallback 25
        assertEquals(25, BetterDogsGameRules.getInt(null, BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 25));

        StubServerLevel nullRulesLevel = createStubServerLevel(200L, List.of(wolf), null);
        assertEquals(25, BetterDogsGameRules.getInt(nullRulesLevel, BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 25));

        StubServerLevel emptyRulesLevel = createStubServerLevel(200L, List.of(wolf), new GameRules(List.of()));
        assertEquals(25, BetterDogsGameRules.getInt(emptyRulesLevel, BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 25));
        wolf.attachLevel(emptyRulesLevel);
        wolf.attachLiveOwner(owner, 30.0 * 30.0);
        PersonalityFollowOwnerGoal goal = new PersonalityFollowOwnerGoal(wolf, 1.0, false);
        assertEquals(25.0f, goal.getStartDistance(), 1e-4f,
                "Unregistered rule in ServerLevel GameRules must preserve fallback 25.0f");
        assertEquals(41.0f, goal.getTeleportThreshold(), 1e-4f);

        // Real non-null GameRules with registered rules: test BOTH lower (10) and higher (60) overrides
        GameRules rules = createFollowGameRules();
        StubServerLevel level = createStubServerLevel(200L, List.of(wolf), rules);
        owner.attachLevel(level);
        wolf.attachLevel(level);

        rules.set(BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 10, null);
        assertEquals(10.0f, goal.getStartDistance(), 1e-4f, "Lower GameRule override (10) must apply");
        assertEquals(32.0f, goal.getTeleportThreshold(), 1e-4f, "startDist=10 clamps teleport floor at 32.0f");

        rules.set(BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 60, null);
        assertEquals(60.0f, goal.getStartDistance(), 1e-4f, "Higher GameRule override (60) must apply");
        assertEquals(76.0f, goal.getTeleportThreshold(), 1e-4f, "startDist=60 scales teleport threshold to 76.0f");
        assertEquals(76.0D, WolfCatchupHelper.getCatchupTeleportDistance(wolf, false), 1e-4D);
        assertEquals(76.0D, WolfCatchupHelper.getCatchupTeleportDistance(wolf, true), 1e-4D);

        // Verify checkAndPerformCatchUp expands scan box dynamically and respects 76.0m threshold
        wolf.setDistanceToOwnerSqr(75.0 * 75.0);
        WolfCatchupHelper.checkAndPerformCatchUp(owner);
        assertEquals(0, wolf.teleportCount, "At 75m (< 76m custom threshold), catchup must not teleport");
        assertNotNull(level.lastScanBox, "Catchup must query entities with expanded scanBox");
        assertTrue(level.lastScanBox.getXsize() >= 190.0D,
                "Scan box width must dynamically expand above maxThreshold + maxSpread + 16");

        wolf.setDistanceToOwnerSqr(77.0 * 77.0);
        WolfCatchupHelper.checkAndPerformCatchUp(owner);
        assertEquals(1, wolf.teleportCount, "At 77m (> 76m custom threshold), catchup must teleport");
    }

    @Test
    @DisplayName("3. Negative Parity: below startDist, sitting, leashed, passenger, guard-mode, spectator owner, null personality")
    void testNegativeParityGuardsAndNullSafety() {
        UUID ownerUuid = UUID.randomUUID();
        StubServerPlayer owner = createStubPlayer(ownerUuid, false);
        StubWolf wolf = createStubWolf(ownerUuid, WolfPersonality.AGGRESSIVE, CanineArchetype.GUARDIAN);
        StubServerLevel level = createStubServerLevel(300L, List.of(wolf), createFollowGameRules());
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
        WolfCatchupHelper.checkAndPerformCatchUp(owner);
        assertEquals(0, wolf.teleportCount, "Leashed wolf at 100m must NEVER teleport");
        wolf.setLeashedStub(false);

        // Passenger: fail canUse/canContinueToUse AND never teleport in tick() even at 100m
        wolf.setPassengerStub(true);
        assertFalse(goal.canUse(), "Passenger wolf must fail canUse()");
        assertFalse(goal.canContinueToUse(), "Passenger wolf must fail canContinueToUse()");
        goal.tick();
        WolfCatchupHelper.checkAndPerformCatchUp(owner);
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
        StubServerLevel level = createStubServerLevel(400L, List.of(), createFollowGameRules());

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
    @DisplayName("5. Pillar 2 concurrency stress on FollowerSpacingCache & Pillar 5 sidedness scan")
    void testFollowerSpacingConcurrencyAndPillar5Sidedness() throws InterruptedException, IOException {
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
            return EntityTypes.PLAYER;
        }
    }

    private static final class StubServerLevel extends ServerLevel {
        private long gameTime;
        private List<Wolf> candidates;
        private @Nullable GameRules gameRules;
        @Nullable AABB lastScanBox;

        private StubServerLevel() {
            super(null, null, null, null, null, null, false, 0L, null, false);
        }

        void init(long gameTime, List<Wolf> candidates, @Nullable GameRules gameRules) {
            this.gameTime = gameTime;
            this.candidates = candidates;
            this.gameRules = gameRules;
            this.lastScanBox = null;
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
        public int getMinY() {
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
            this.lastScanBox = aabb;
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
            return EntityTypes.WOLF;
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
        public @Nullable EntityReference<LivingEntity> getOwnerReference() {
            return this.ownerUuid != null ? EntityReference.of(this.ownerUuid) : null;
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
        public boolean teleportTo(ServerLevel level, double x, double y, double z, Set<Relative> relatives,
                                  float yRot, float xRot, boolean resetCamera) {
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

        @Override public int betterdogs$getLastDamageTime() { return 0; }
        @Override public void betterdogs$setLastDamageTime(int time) {}
        @Override public boolean betterdogs$isSubmissive() { return false; }
        @Override public void betterdogs$setSubmissive(boolean submissive) {}
        @Override public String betterdogs$getBloodFeudTarget() { return ""; }
        @Override public void betterdogs$setBloodFeudTarget(String targetUuid) {}
        @Override public boolean betterdogs$hasBloodFeud() { return false; }
        @Override public long betterdogs$getLastMischiefDay() { return 0L; }
        @Override public void betterdogs$setLastMischiefDay(long day) {}
        @Override public boolean betterdogs$isBeingDisciplined() { return false; }
        @Override public void betterdogs$setBeingDisciplined(boolean isBeingDisciplined) {}
        @Override public void betterdogs$setSocialState(@Nullable LivingEntity target, SocialAction action, int maxDurationTicks) {}
        @Override public @Nullable LivingEntity betterdogs$getSocialTarget() { return null; }
        @Override public SocialAction betterdogs$getSocialAction() { return SocialAction.NONE; }
        @Override public boolean betterdogs$isSocialModeActive() { return false; }
        @Override public void betterdogs$tickSocialMode() {}
        @Override public long betterdogs$getDNA() { return 0L; }
        @Override public void betterdogs$setDNA(long dna) {}
        @Override public float betterdogs$getSocialScale() { return 1.0f; }
        @Override public void betterdogs$setSocialScale(float scale) {}
        @Override public @Nullable EntitySocialScheduler betterdogs$getScheduler() { return null; }
        @Override public EntitySocialScheduler betterdogs$getOrInitializeScheduler() { return null; }
        @Override public void betterdogs$tickScheduler() {}
        @Override public int betterdogs$getAffinity(String targetUuid) { return 0; }
        @Override public void betterdogs$adjustAffinity(String targetUuid, int delta) {}
        @Override public @Nullable UUID betterdogs$getLeaderUuid() { return null; }
        @Override public void betterdogs$setLeaderUuid(@Nullable UUID uuid) {}
        @Override public boolean betterdogs$isGuardMode() { return this.guardMode; }
        @Override public void betterdogs$setGuardMode(boolean guardMode) { this.guardMode = guardMode; }
        @Override public @Nullable BlockPos betterdogs$getGuardPos() { return null; }
        @Override public void betterdogs$setGuardPos(@Nullable BlockPos pos) {}
        @Override public boolean betterdogs$isSittingManually() { return this.sittingManually; }
        @Override public void betterdogs$setSittingManually(boolean sitting) { this.sittingManually = sitting; }
        @Override public boolean betterdogs$isAdoptable() { return false; }
        @Override public void betterdogs$setAdoptable(boolean adoptable) {}
        @Override public float betterdogs$getHealthBonus() { return 0.0f; }
        @Override public void betterdogs$setHealthBonus(float hp) {}
        @Override public float betterdogs$getDamageMod() { return 0.0f; }
        @Override public void betterdogs$setDamageMod(float dmg) {}
        @Override public float betterdogs$getSpeedMod() { return 0.0f; }
        @Override public void betterdogs$setSpeedMod(float speed) {}
        @Override public boolean betterdogs$areStatsRolled() { return false; }
        @Override public void betterdogs$setStatsRolled(boolean rolled) {}
        @Override public int betterdogs$getPushWaitTimer() { return 0; }
        @Override public void betterdogs$setPushWaitTimer(int ticks) {}
        @Override public @Nullable BlockPos betterdogs$getPathfindAvoidPos() { return null; }
        @Override public void betterdogs$setPathfindAvoidPos(@Nullable BlockPos pos) {}
        @Override public GoalSelector betterdogs$getGoalSelector() { return null; }
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
        @Override public boolean betterdogs$hasFetchedItem() { return false; }
        @Override public void betterdogs$setHasFetchedItem(boolean fetched) {}
        @Override public @Nullable ItemStack betterdogs$getFetchedItemStack() { return null; }
        @Override public void betterdogs$setFetchedItemStack(@Nullable ItemStack stack) {}
        @Override public int betterdogs$getZoomiesTicks() { return 0; }
        @Override public void betterdogs$setZoomiesTicks(int ticks) {}
        @Override public int betterdogs$getHowlingTicks() { return 0; }
        @Override public void betterdogs$setHowlingTicks(int ticks) {}
    }
}
