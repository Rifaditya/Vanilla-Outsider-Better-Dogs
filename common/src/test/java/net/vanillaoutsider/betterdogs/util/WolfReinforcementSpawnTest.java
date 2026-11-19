// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BL-BD-047: WolfReinforcementSpawnHelper 5-Pillar Unit, Safety & Contract Suite")
class WolfReinforcementSpawnTest {

    private static final Unsafe UNSAFE = initUnsafe();

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
    static void initMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        if (BetterDogsGameRules.BD_WOLF_SPAWN_DENSITY_BOOST == null) {
            BetterDogsGameRules.init();
        }
    }

    private static StubServerLevel newStubLevel() {
        try {
            StubServerLevel level = (StubServerLevel) UNSAFE.allocateInstance(StubServerLevel.class);
            level.init();
            return level;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubServerLevel", e);
        }
    }

    private static StubWolf newStubWolf(BlockPos pos) {
        try {
            StubWolf wolf = (StubWolf) UNSAFE.allocateInstance(StubWolf.class);
            wolf.init(pos);
            return wolf;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubWolf", e);
        }
    }

    private static Path findProjectRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        Path cursor = dir;
        while (cursor != null) {
            if (Files.exists(cursor.resolve("src/main/java/net/vanillaoutsider/betterdogs/mixin/WolfMixin.java"))) {
                return cursor;
            }
            if (Files.exists(cursor.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/mixin/WolfMixin.java"))) {
                return cursor;
            }
            cursor = cursor.getParent();
        }
        return dir;
    }

    @Test
    @DisplayName("Pillar 1: calculateReinforcementCount handles zero, negative, normal, and saturated 64 bounds")
    void testCalculateReinforcementCountBounds() {
        assertEquals(0, WolfReinforcementSpawnHelper.calculateReinforcementCount(0));
        assertEquals(0, WolfReinforcementSpawnHelper.calculateReinforcementCount(-1));
        assertEquals(0, WolfReinforcementSpawnHelper.calculateReinforcementCount(-100));
        assertEquals(0, WolfReinforcementSpawnHelper.calculateReinforcementCount(Integer.MIN_VALUE));

        assertEquals(1, WolfReinforcementSpawnHelper.calculateReinforcementCount(1));
        assertEquals(2, WolfReinforcementSpawnHelper.calculateReinforcementCount(2));
        assertEquals(4, WolfReinforcementSpawnHelper.calculateReinforcementCount(6));
        assertEquals(5, WolfReinforcementSpawnHelper.calculateReinforcementCount(8));
        assertEquals(7, WolfReinforcementSpawnHelper.calculateReinforcementCount(12));

        assertEquals(64, WolfReinforcementSpawnHelper.calculateReinforcementCount(126));
        assertEquals(64, WolfReinforcementSpawnHelper.calculateReinforcementCount(250));
        assertEquals(64, WolfReinforcementSpawnHelper.calculateReinforcementCount(Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("Pillar 1: isWithinVerticalTolerance accepts |dy| <= 12 and rejects |dy| == 13 or overflow")
    void testVerticalToleranceBounds() {
        assertTrue(WolfReinforcementSpawnHelper.isWithinVerticalTolerance(64, 64, 12));
        assertTrue(WolfReinforcementSpawnHelper.isWithinVerticalTolerance(64, 76, 12));
        assertTrue(WolfReinforcementSpawnHelper.isWithinVerticalTolerance(64, 52, 12));

        assertFalse(WolfReinforcementSpawnHelper.isWithinVerticalTolerance(64, 77, 12));
        assertFalse(WolfReinforcementSpawnHelper.isWithinVerticalTolerance(64, 51, 12));
        assertFalse(WolfReinforcementSpawnHelper.isWithinVerticalTolerance(Integer.MIN_VALUE, Integer.MAX_VALUE, 12));
        assertFalse(WolfReinforcementSpawnHelper.isWithinVerticalTolerance(64, 64, -1));
    }

    @Test
    @DisplayName("Negative Parity: shouldTriggerDensityBoost and tryTriggerDensityBoost block non-natural, groupData != null, and 0% boost")
    void testNegativeParityBoostGating() {
        assertFalse(WolfReinforcementSpawnHelper.shouldTriggerDensityBoost(false, false, 100, 0),
                "Non-natural spawn (REINFORCEMENT/BREEDING/COMMAND) must never trigger density boost");
        assertFalse(WolfReinforcementSpawnHelper.shouldTriggerDensityBoost(true, true, 100, 0),
                "Existing non-null SpawnGroupData must never trigger density boost");
        assertFalse(WolfReinforcementSpawnHelper.shouldTriggerDensityBoost(true, false, 0, 0),
                "Zero boostChance must never trigger density boost");
        assertFalse(WolfReinforcementSpawnHelper.shouldTriggerDensityBoost(true, false, -10, 0),
                "Negative boostChance must never trigger density boost");
        assertFalse(WolfReinforcementSpawnHelper.shouldTriggerDensityBoost(true, false, 25, 25),
                "Roll equal to boostChance must fail strict roll < boostChance check");
        assertFalse(WolfReinforcementSpawnHelper.shouldTriggerDensityBoost(true, false, 25, 99),
                "Roll above boostChance must not trigger");

        assertTrue(WolfReinforcementSpawnHelper.shouldTriggerDensityBoost(true, false, 100, 0));
        assertTrue(WolfReinforcementSpawnHelper.shouldTriggerDensityBoost(true, false, 25, 24));

        // Null and non-natural runtime guard checks
        StubServerLevel level = newStubLevel();
        StubWolf wolf = newStubWolf(new BlockPos(0, 64, 0));
        assertEquals(0, WolfReinforcementSpawnHelper.tryTriggerDensityBoost(null, level, MobSpawnType.NATURAL, null));
        assertEquals(0, WolfReinforcementSpawnHelper.tryTriggerDensityBoost(wolf, null, MobSpawnType.NATURAL, null));
        assertEquals(0, WolfReinforcementSpawnHelper.tryTriggerDensityBoost(wolf, level, MobSpawnType.REINFORCEMENT, null));
        assertEquals(0, WolfReinforcementSpawnHelper.tryTriggerDensityBoost(wolf, level, MobSpawnType.BREEDING, null));
        assertEquals(0, WolfReinforcementSpawnHelper.tryTriggerDensityBoost(wolf, level, MobSpawnType.COMMAND, null));
        assertEquals(0, WolfReinforcementSpawnHelper.tryTriggerDensityBoost(wolf, level, MobSpawnType.NATURAL, new SpawnGroupData() {}));

        level.setClientSide(true);
        assertEquals(0, WolfReinforcementSpawnHelper.tryTriggerDensityBoost(wolf, level, MobSpawnType.NATURAL, null));
    }

    @Test
    @DisplayName("[ERR-20261114-001] Dual-Polarity GameRule Gating: toggles BD_WOLF_SPAWN_DENSITY_BOOST between 0% and 100%")
    void testDualPolarityGameRuleGating() {
        assertNotNull(BetterDogsGameRules.BD_WOLF_SPAWN_DENSITY_BOOST,
                "BD_WOLF_SPAWN_DENSITY_BOOST must be registered");
        assertNotNull(BetterDogsGameRules.BD_WOLF_PACK_CLUSTER_SIZE,
                "BD_WOLF_PACK_CLUSTER_SIZE must be registered");

        Map<GameRules.Key<?>, Integer> intOverrides = new HashMap<>();
        intOverrides.put(BetterDogsGameRules.BD_WOLF_SPAWN_DENSITY_BOOST, 0);
        intOverrides.put(BetterDogsGameRules.BD_WOLF_PACK_CLUSTER_SIZE, 6);

        GameRules stubRules = new GameRules() {
            @Override
            public boolean getBoolean(GameRules.Key<GameRules.BooleanValue> key) {
                if (BetterDogsGameRules.BD_DEBUGGING.equals(key)) {
                    return false;
                }
                return super.getBoolean(key);
            }

            @Override
            public int getInt(GameRules.Key<GameRules.IntegerValue> key) {
                Integer val = intOverrides.get(key);
                if (val != null) {
                    return val;
                }
                return super.getInt(key);
            }
        };

        StubServerLevel level = newStubLevel();
        level.setGameRules(stubRules);
        for (int cx = -1; cx <= 1; cx++) {
            for (int cz = -1; cz <= 1; cz++) {
                level.setChunkLoaded(new BlockPos(cx << 4, 64, cz << 4), true);
            }
        }

        BlockPos origin = new BlockPos(0, 64, 0);
        StubWolf wolf = newStubWolf(origin);

        // 0% polarity: tryTriggerDensityBoost must read 0 and abort before scanning scatter positions
        assertEquals(0, BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_WOLF_SPAWN_DENSITY_BOOST, 100));
        assertEquals(0, WolfReinforcementSpawnHelper.tryTriggerDensityBoost(wolf, level, MobSpawnType.NATURAL, null));
        assertEquals(0, level.heightmapQueries, "0% boost must exit before entering reinforcement scatter loop");

        // 100% polarity: tryTriggerDensityBoost must read 100 and execute 4 wolves * 4 scatter attempts = 16 heightmap queries
        intOverrides.put(BetterDogsGameRules.BD_WOLF_SPAWN_DENSITY_BOOST, 100);
        assertEquals(100, BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_WOLF_SPAWN_DENSITY_BOOST, 100));
        WolfReinforcementSpawnHelper.tryTriggerDensityBoost(wolf, level, MobSpawnType.NATURAL, null);
        assertEquals(16, level.heightmapQueries,
                "100% boost with clusterSize=6 (count=4, attempts=4) must query heightmap 16 times");

        // Zero cluster size polarity under 100% boost: must exit cleanly with 0 queries
        level.heightmapQueries = 0;
        intOverrides.put(BetterDogsGameRules.BD_WOLF_PACK_CLUSTER_SIZE, 0);
        assertEquals(0, WolfReinforcementSpawnHelper.tryTriggerDensityBoost(wolf, level, MobSpawnType.NATURAL, null));
        assertEquals(0, level.heightmapQueries, "Zero cluster size must exit before heightmap queries");

        // Null GameRules fallback consistency: caller fallback matches registered default 100
        StubServerLevel nullRulesLevel = newStubLevel();
        assertEquals(100, BetterDogsGameRules.getInt(nullRulesLevel, BetterDogsGameRules.BD_WOLF_SPAWN_DENSITY_BOOST, 100));
        assertEquals(6, BetterDogsGameRules.getInt(nullRulesLevel, BetterDogsGameRules.BD_WOLF_PACK_CLUSTER_SIZE, 6));
    }

    @Test
    @DisplayName("Pillar 4: Bootstrapped BlockState safety accepts solid ground + air + air and rejects all hazards/fluids/leaves")
    void testBootstrappedBlockStateSafety() {
        BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();

        assertTrue(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, air, air, true, true, true),
                "Grass floor with air foot and air head must pass");
        assertTrue(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(stone, air, air, true, true, true),
                "Stone floor with air foot and air head must pass");

        // Null & structural failures
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(null, air, air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, null, air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, air, null, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, air, air, false, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, air, air, true, false, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, air, air, true, true, false));

        // Fluids at below, foot, or head
        BlockState water = Blocks.WATER.defaultBlockState();
        BlockState lava = Blocks.LAVA.defaultBlockState();
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(water, air, air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, water, air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, air, water, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(lava, air, air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, lava, air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, air, lava, true, true, true));

        // Leaves below
        BlockState oakLeaves = Blocks.OAK_LEAVES.defaultBlockState();
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(oakLeaves, air, air, true, true, true));

        // Hazards: fire, soul fire, magma, campfire, soul campfire, powder snow, sweet berry bush, cactus, cobweb, wither rose
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, Blocks.FIRE.defaultBlockState(), air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, Blocks.SOUL_FIRE.defaultBlockState(), air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(Blocks.MAGMA_BLOCK.defaultBlockState(), air, air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, Blocks.MAGMA_BLOCK.defaultBlockState(), air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(Blocks.CAMPFIRE.defaultBlockState(), air, air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, Blocks.CAMPFIRE.defaultBlockState(), air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, Blocks.SOUL_CAMPFIRE.defaultBlockState(), air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(Blocks.POWDER_SNOW.defaultBlockState(), air, air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, Blocks.POWDER_SNOW.defaultBlockState(), air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, Blocks.SWEET_BERRY_BUSH.defaultBlockState(), air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(Blocks.CACTUS.defaultBlockState(), air, air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, Blocks.CACTUS.defaultBlockState(), air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, Blocks.COBWEB.defaultBlockState(), air, true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, air, Blocks.COBWEB.defaultBlockState(), true, true, true));
        assertFalse(WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, Blocks.WITHER_ROSE.defaultBlockState(), air, true, true, true));
    }

    @Test
    @DisplayName("Pillar 4: Chunk guard, sloped heightmap, overhang +4..-4 scan, and noCollision clearance")
    void testChunkGuardSlopeAndOverhangScan() {
        BlockPos origin = new BlockPos(0, 64, 0);
        BlockPos column = new BlockPos(4, 64, 4);
        StubWolf wolf = newStubWolf(origin);

        // 1. Unloaded chunk must abort before heightmap or block queries
        StubServerLevel unloadedLevel = newStubLevel();
        unloadedLevel.setChunkLoaded(column, false);
        assertNull(WolfReinforcementSpawnHelper.findSafeReinforcementSpawnPos(wolf, unloadedLevel, origin, column));
        assertEquals(0, unloadedLevel.heightmapQueries, "Unloaded chunk must never query heightmap");
        assertEquals(0, unloadedLevel.blockStateQueries, "Unloaded chunk must never query block states");

        // 2. Sloped hill within vertical tolerance (|dy| = 10 <= 12)
        StubServerLevel slopeLevel = newStubLevel();
        slopeLevel.setChunkLoaded(column, true);
        BlockPos slopeSurface = new BlockPos(4, 74, 4);
        slopeLevel.setHeightmapSurface(column.getX(), column.getZ(), slopeSurface);
        slopeLevel.setBlock(slopeSurface.below(), Blocks.GRASS_BLOCK.defaultBlockState());
        slopeLevel.setBlock(slopeSurface, Blocks.AIR.defaultBlockState());
        slopeLevel.setBlock(slopeSurface.above(), Blocks.AIR.defaultBlockState());
        assertEquals(slopeSurface, WolfReinforcementSpawnHelper.findSafeReinforcementSpawnPos(wolf, slopeLevel, origin, column));

        // 3. No-collision rejection even on clean blocks
        slopeLevel.setNoCollisionResult(false);
        assertNull(WolfReinforcementSpawnHelper.findSafeReinforcementSpawnPos(wolf, slopeLevel, origin, column),
                "Colliding bounding box must reject spawn spot");

        // 4. Steep cliff / overhang (heightmap at Y=90 -> |dy|=26 > 12) falls back to local +4..-4 scan at Y=64
        StubServerLevel overhangLevel = newStubLevel();
        overhangLevel.setChunkLoaded(column, true);
        overhangLevel.setHeightmapSurface(column.getX(), column.getZ(), new BlockPos(4, 90, 4));
        overhangLevel.setBlock(new BlockPos(4, 63, 4), Blocks.STONE.defaultBlockState());
        overhangLevel.setBlock(new BlockPos(4, 64, 4), Blocks.AIR.defaultBlockState());
        overhangLevel.setBlock(new BlockPos(4, 65, 4), Blocks.AIR.defaultBlockState());
        assertEquals(new BlockPos(4, 64, 4),
                WolfReinforcementSpawnHelper.findSafeReinforcementSpawnPos(wolf, overhangLevel, origin, column));

        // 5. Sheer cliff with no local floor within +4..-4 returns null rather than teleporting > 12 blocks
        StubServerLevel sheerCliffLevel = newStubLevel();
        sheerCliffLevel.setChunkLoaded(column, true);
        sheerCliffLevel.setHeightmapSurface(column.getX(), column.getZ(), new BlockPos(4, 90, 4));
        assertNull(WolfReinforcementSpawnHelper.findSafeReinforcementSpawnPos(wolf, sheerCliffLevel, origin, column));
    }

    @Test
    @DisplayName("Pillar 5 [PAT-20261113-002]: Mixin-Delegate Equivalence & Zero Client/Yarn Imports")
    void testMixinDelegateEquivalenceAndSidedness() throws IOException {
        Path root = findProjectRoot();
        String basePrefix = Files.exists(root.resolve("common/src/main/java")) ? "common/src/main/java/" : "src/main/java/";

        Path mixinPath = root.resolve(basePrefix + "net/vanillaoutsider/betterdogs/mixin/WolfMixin.java");
        Path helperPath = root.resolve(basePrefix + "net/vanillaoutsider/betterdogs/util/WolfReinforcementSpawnHelper.java");
        assertTrue(Files.exists(mixinPath), "WolfMixin.java must exist at " + mixinPath);
        assertTrue(Files.exists(helperPath), "WolfReinforcementSpawnHelper.java must exist at " + helperPath);

        String mixinSrc = Files.readString(mixinPath, StandardCharsets.UTF_8);
        String helperSrc = Files.readString(helperPath, StandardCharsets.UTF_8);

        assertTrue(mixinSrc.contains("WolfReinforcementSpawnHelper.tryTriggerDensityBoost("),
                "WolfMixin must delegate density boost logic to WolfReinforcementSpawnHelper.tryTriggerDensityBoost");
        int finalizeIdx = mixinSrc.indexOf("finalizeSpawn(");
        int delegateIdx = mixinSrc.indexOf("WolfReinforcementSpawnHelper.tryTriggerDensityBoost(");
        assertTrue(finalizeIdx > 0 && delegateIdx > finalizeIdx,
                "WolfReinforcementSpawnHelper.tryTriggerDensityBoost must be invoked inside finalizeSpawn");

        assertTrue(helperSrc.contains("hasChunkAt"),
                "WolfReinforcementSpawnHelper must verify loaded chunks via hasChunkAt");
        assertTrue(helperSrc.contains("MOTION_BLOCKING_NO_LEAVES"),
                "WolfReinforcementSpawnHelper must query MOTION_BLOCKING_NO_LEAVES heightmap");
        assertTrue(helperSrc.contains("noCollision"),
                "WolfReinforcementSpawnHelper must verify bounding box clearance via noCollision");

        for (String src : new String[]{mixinSrc, helperSrc}) {
            assertFalse(src.contains("net.minecraft.client."), "Zero client imports permitted");
            assertFalse(src.contains("com.mojang.blaze3d."), "Zero Blaze3D imports permitted");
            assertFalse(src.contains("ClientModInitializer"), "Zero ClientModInitializer refs permitted");
            assertFalse(src.contains("net.minecraft.world.World"), "Zero Yarn World references permitted");
            assertFalse(src.contains(".getWorld("), "Zero Yarn getWorld() references permitted");
        }
    }

    @Test
    @DisplayName("Pillar 2 [PAT-20261026-003]: 16-Thread Fixed-Pool Concurrency Stress Test (Java 17)")
    void testConcurrencyStress() throws InterruptedException {
        final int threads = 16;
        final int iterationsPerThread = 1_000;
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger failures = new AtomicInteger(0);

        BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState fire = Blocks.FIRE.defaultBlockState();

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            for (int t = 0; t < threads; t++) {
                final int tid = t;
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        start.await();
                        for (int i = 0; i < iterationsPerThread; i++) {
                            int cluster = (tid * 13 + i) % 160;
                            int expectedCount = cluster <= 0 ? 0 : Math.min(64, cluster / 2 + 1);
                            if (WolfReinforcementSpawnHelper.calculateReinforcementCount(cluster) != expectedCount) {
                                failures.incrementAndGet();
                            }

                            int dy = (i % 30) - 15;
                            boolean expectedTol = Math.abs(dy) <= 12;
                            if (WolfReinforcementSpawnHelper.isWithinVerticalTolerance(64, 64 + dy, 12) != expectedTol) {
                                failures.incrementAndGet();
                            }

                            if (!WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, air, air, true, true, true)) {
                                failures.incrementAndGet();
                            }
                            if (WolfReinforcementSpawnHelper.isSafeSpawnBlockStates(grass, fire, air, true, true, true)) {
                                failures.incrementAndGet();
                            }
                            if (WolfReinforcementSpawnHelper.shouldTriggerDensityBoost(false, false, 100, 0)) {
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

            assertTrue(ready.await(5, TimeUnit.SECONDS), "Worker threads failed to prepare");
            start.countDown();
            assertTrue(done.await(15, TimeUnit.SECONDS), "Concurrency stress test timed out");
        } finally {
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }
        assertEquals(0, failures.get(), "Zero concurrency assertion failures allowed");
    }

    private static final class StubServerLevel extends ServerLevel {
        private boolean clientSide;
        private boolean noCollisionResult;
        private @Nullable GameRules gameRules;
        private Set<Long> loadedChunks;
        private Map<Long, BlockPos> heightmaps;
        private Map<BlockPos, BlockState> blocks;
        int heightmapQueries;
        int blockStateQueries;

        private StubServerLevel() {
            super(null, null, null, null, null, null, null, false, 0L, null, false, null);
        }

        void init() {
            this.clientSide = false;
            this.noCollisionResult = true;
            this.gameRules = null;
            this.loadedChunks = new HashSet<>();
            this.heightmaps = new HashMap<>();
            this.blocks = new HashMap<>();
            this.heightmapQueries = 0;
            this.blockStateQueries = 0;
        }

        void setClientSide(boolean clientSide) {
            this.clientSide = clientSide;
        }

        void setNoCollisionResult(boolean noCollisionResult) {
            this.noCollisionResult = noCollisionResult;
        }

        void setGameRules(@Nullable GameRules gameRules) {
            this.gameRules = gameRules;
        }

        void setChunkLoaded(BlockPos pos, boolean loaded) {
            long key = packXZ(pos.getX() >> 4, pos.getZ() >> 4);
            if (loaded) {
                this.loadedChunks.add(key);
            } else {
                this.loadedChunks.remove(key);
            }
        }

        void setHeightmapSurface(int x, int z, BlockPos surface) {
            this.heightmaps.put(packXZ(x, z), surface);
        }

        void setBlock(BlockPos pos, BlockState state) {
            this.blocks.put(pos.immutable(), state);
        }

        private static long packXZ(int x, int z) {
            return ((long) x << 32) ^ (z & 0xFFFFFFFFL);
        }

        @Override
        public ServerLevel getLevel() {
            return this;
        }

        @Override
        public @Nullable GameRules getGameRules() {
            return this.gameRules;
        }

        @Override
        public boolean isClientSide() {
            return this.clientSide;
        }

        @Override
        public int getMinBuildHeight() {
            return -64;
        }

        @Override
        public int getMaxBuildHeight() {
            return 320;
        }

        @Override
        public boolean hasChunkAt(BlockPos pos) {
            return this.loadedChunks.contains(packXZ(pos.getX() >> 4, pos.getZ() >> 4));
        }

        @Override
        public BlockPos getHeightmapPos(Heightmap.Types type, BlockPos pos) {
            this.heightmapQueries++;
            BlockPos mapped = this.heightmaps.get(packXZ(pos.getX(), pos.getZ()));
            return mapped != null ? mapped : new BlockPos(pos.getX(), this.getMinBuildHeight(), pos.getZ());
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            this.blockStateQueries++;
            BlockState state = this.blocks.get(pos);
            return state != null ? state : Blocks.AIR.defaultBlockState();
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return this.getBlockState(pos).getFluidState();
        }

        @Override
        public boolean noCollision(@Nullable Entity entity, AABB box) {
            return this.noCollisionResult;
        }
    }

    private static final class StubWolf extends Wolf {
        private UUID uuid;
        private BlockPos pos;
        private RandomSource random;

        private StubWolf() {
            super(null, null);
        }

        void init(BlockPos pos) {
            this.uuid = UUID.randomUUID();
            this.pos = pos;
            this.random = RandomSource.create(42L);
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public EntityType<?> getType() {
            return EntityType.WOLF;
        }

        @Override
        public BlockPos blockPosition() {
            return this.pos;
        }

        @Override
        public RandomSource getRandom() {
            return this.random;
        }
    }
}
