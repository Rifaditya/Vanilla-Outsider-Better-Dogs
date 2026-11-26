// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialScheduler;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.ZoomiesGoal;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.scheduler.events.ZoomiesDogEvent;
import net.vanillaoutsider.betterdogs.util.WolfZoomiesHelper;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BL-BD-051: Ambient Social Scheduler Catch-22, Null Safety & Morning Zoomies Tests")
class AmbientSocialSchedulerTest {

    private static final Unsafe unsafe = initUnsafe();
    private static long deterministicZoomiesTriggerSeed;

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
        if (BetterDogsGameRules.BD_ZOOMIES_ENABLED == null) {
            BetterDogsGameRules.register();
        }
        deterministicZoomiesTriggerSeed = findZoomiesTriggerSeed();
    }

    private static long findZoomiesTriggerSeed() {
        for (long i = 1L; i <= 100_000L; i++) {
            long seed = i * 0x9E3779B97F4A7C15L;
            if (RandomSource.create(seed).nextFloat() < 0.01f) {
                return seed;
            }
        }
        throw new IllegalStateException("Failed to locate deterministic seed < 0.01f");
    }

    private static GameRules createZoomiesGameRules(boolean zoomiesEnabled) {
        GameRules rules = new GameRules(List.of(
                BetterDogsGameRules.BD_ZOOMIES_ENABLED,
                BetterDogsGameRules.BD_ZOOMIES_DURATION_TICKS,
                BetterDogsGameRules.BD_DEBUGGING
        ));
        rules.set(BetterDogsGameRules.BD_ZOOMIES_ENABLED, zoomiesEnabled, null);
        rules.set(BetterDogsGameRules.BD_DEBUGGING, false, null);
        return rules;
    }

    private static StubServerLevel createStubServerLevel(GameRules rules, long clockTime) {
        try {
            StubServerLevel level = (StubServerLevel) unsafe.allocateInstance(StubServerLevel.class);
            level.init(rules, clockTime);
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

    private static StubWolf createStubWolf(@Nullable UUID ownerUuid, boolean tame) {
        try {
            StubWolf wolf = (StubWolf) unsafe.allocateInstance(StubWolf.class);
            wolf.init(UUID.randomUUID(), ownerUuid, tame, WolfPersonality.NORMAL, CanineArchetype.PLAYFUL);
            return wolf;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubWolf", e);
        }
    }

    private static Path findMainJavaRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        Path cursor = dir;
        while (cursor != null) {
            if (Files.exists(cursor.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfZoomiesHelper.java"))) {
                return cursor.resolve("common/src/main/java");
            }
            if (Files.exists(cursor.resolve("src/main/java/net/vanillaoutsider/betterdogs/util/WolfZoomiesHelper.java"))) {
                return cursor.resolve("src/main/java");
            }
            cursor = cursor.getParent();
        }
        return dir.resolve("src/main/java");
    }

    @Test
    @DisplayName("Test 1: Morning zoomies triggers with null scheduler, runs 240-tick lifecycle & drives ZoomiesGoal")
    void testPositiveMorningZoomiesTriggerAnd240TickLifecycle() {
        StubServerLevel level = createStubServerLevel(createZoomiesGameRules(true), 100L);
        StubWolf wolf = createStubWolf(UUID.randomUUID(), true);
        wolf.attachLevel(level);
        wolf.tickCount = 20;
        wolf.setOnGroundStub(true);
        wolf.setOrderedToSitStub(false);
        wolf.setSittingPoseStub(false);
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);

        assertNull(wolf.betterdogs$getScheduler(), "Idle wolf should start with null scheduler");
        assertTrue(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "Morning zoomies must trigger with null scheduler, morning clock, standing tamed wolf & < 0.01f seed");

        EntitySocialScheduler scheduler = wolf.betterdogs$getOrInitializeScheduler();
        scheduler.schedule(new ZoomiesDogEvent());
        ZoomiesGoal goal = new ZoomiesGoal(wolf);

        for (int tick = 1; tick <= 239; tick++) {
            scheduler.tick();
            assertTrue(scheduler.isEventActive(ZoomiesDogEvent.ID),
                    "ZoomiesDogEvent must remain active on tick " + tick + "/239");
            assertFalse(scheduler.isIdle(),
                    "Scheduler must not report idle while zoomies is active on tick " + tick);
            assertTrue(goal.canUse(), "ZoomiesGoal.canUse() must be true on tick " + tick);
            assertTrue(goal.canContinueToUse(), "ZoomiesGoal.canContinueToUse() must be true on tick " + tick);
        }

        scheduler.tick(); // 240th tick
        assertFalse(scheduler.isEventActive(ZoomiesDogEvent.ID),
                "ZoomiesDogEvent must expire on tick 240");
        assertTrue(scheduler.isIdle(),
                "Scheduler must become idle after ZoomiesDogEvent completes on tick 240");
        assertFalse(goal.canUse(), "ZoomiesGoal.canUse() must return false after tick 240 expiry");
        assertFalse(goal.canContinueToUse(), "ZoomiesGoal.canContinueToUse() must return false after tick 240 expiry");
    }

    @Test
    @DisplayName("Test 2: Dual-polarity bd_zoomies_enabled GameRule (true triggers, false blocks)")
    void testDualPolarityZoomiesGameRule() {
        StubWolf wolf = createStubWolf(UUID.randomUUID(), true);
        wolf.tickCount = 20;
        wolf.setOnGroundStub(true);
        wolf.setOrderedToSitStub(false);
        wolf.setSittingPoseStub(false);

        StubServerLevel enabledLevel = createStubServerLevel(createZoomiesGameRules(true), 500L);
        wolf.attachLevel(enabledLevel);
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertTrue(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "bd_zoomies_enabled = true must allow morning zoomies");

        StubServerLevel disabledLevel = createStubServerLevel(createZoomiesGameRules(false), 500L);
        wolf.attachLevel(disabledLevel);
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "bd_zoomies_enabled = false must block morning zoomies even on deterministic < 0.01f seed");
    }

    @Test
    @DisplayName("Test 3: Negative parity boundaries block morning zoomies across all invalid states")
    void testNegativeParityBoundaries() {
        StubServerLevel morningLevel = createStubServerLevel(createZoomiesGameRules(true), 2999L);
        StubWolf wolf = createStubWolf(UUID.randomUUID(), true);
        wolf.attachLevel(morningLevel);
        wolf.tickCount = 20;
        wolf.setOnGroundStub(true);

        // Baseline sanity at clock 2999 (< 3000) and negative clock floorMod (-23900 % 24000 -> 100)
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertTrue(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null));
        morningLevel.setClockTime(-23900L);
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertTrue(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "Math.floorMod must handle negative world clock time safely");
        morningLevel.setClockTime(2999L);

        // 1. Null wolf / null level
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(null, null));
        wolf.attachLevel(null);
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null));
        wolf.attachLevel(morningLevel);

        // 2. Client-side level
        wolf.attachLevel(createStubClientLevel());
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "Client-side level must never trigger ambient morning zoomies");
        wolf.attachLevel(morningLevel);

        // 3. Non-multiple-of-20 tick
        wolf.tickCount = 21;
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "Non-multiple-of-20 tick must throttle ambient polling");
        wolf.tickCount = 20;

        // 4. Untamed wolf
        StubWolf wildWolf = createStubWolf(null, false);
        wildWolf.attachLevel(morningLevel);
        wildWolf.tickCount = 20;
        wildWolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wildWolf, null),
                "Untamed wolf must never trigger morning zoomies");

        // 5. Airborne
        wolf.setOnGroundStub(false);
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "Airborne wolf must never trigger morning zoomies");
        wolf.setOnGroundStub(true);

        // 6. Ordered sitting
        wolf.setOrderedToSitStub(true);
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "Ordered sitting wolf must never trigger morning zoomies");
        wolf.setOrderedToSitStub(false);

        // 7. Lazy resting sitting pose
        wolf.setSittingPoseStub(true);
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "Wolf in sitting pose must never trigger morning zoomies");
        wolf.setSittingPoseStub(false);

        // 8. Combat target
        StubWolf enemy = createStubWolf(null, false);
        wolf.setTarget(enemy);
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "Combat-engaged wolf must never trigger morning zoomies");
        wolf.setTarget(null);

        // 9. Guard mode
        wolf.setGuardModeStub(true);
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "Guard-mode wolf must never trigger morning zoomies");
        wolf.setGuardModeStub(false);

        // 10. Active treat zoomies
        wolf.betterdogs$setZoomiesTicks(40);
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "Wolf already in treat zoomies must never trigger ambient morning zoomies");
        wolf.betterdogs$setZoomiesTicks(0);

        // 11. Afternoon / non-morning time (>= 3000)
        morningLevel.setClockTime(3000L);
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "TimeOfDay == 3000 must not trigger morning zoomies");
        morningLevel.setClockTime(27000L);
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, null),
                "Afternoon wrapped TimeOfDay (27000 mod 24000 == 3000) must not trigger morning zoomies");
        morningLevel.setClockTime(100L);

        // 12. Already-active ZoomiesDogEvent in scheduler
        EntitySocialScheduler activeScheduler = wolf.betterdogs$getOrInitializeScheduler();
        activeScheduler.schedule(new ZoomiesDogEvent());
        activeScheduler.tick();
        assertTrue(activeScheduler.isEventActive(ZoomiesDogEvent.ID));
        wolf.setRandomSeed(deterministicZoomiesTriggerSeed);
        assertFalse(WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies(wolf, activeScheduler),
                "Already-active zoomies event must block duplicate ambient schedule");
    }

    @Test
    @DisplayName("Test 4: Source/Mixin contract parity (post-isIdle poll, helper delegation, no BeggingDogEvent, GPLv3)")
    void testSourceAndMixinContractParity() throws IOException {
        Path mainJava = findMainJavaRoot();
        String clientPkg = "net." + "minecraft." + "client.";

        String mixinSrc = Files.readString(
                mainJava.resolve("net/vanillaoutsider/betterdogs/mixin/WolfSocialMixin.java"),
                StandardCharsets.UTF_8);
        assertFalse(mixinSrc.contains("BeggingDogEvent"),
                "WolfSocialMixin must remove dead BeggingDogEvent import and polling");
        assertFalse(mixinSrc.contains("betterdogs$lastBeggingTime"),
                "WolfSocialMixin must remove unused betterdogs$lastBeggingTime field");
        assertTrue(mixinSrc.contains("WolfZoomiesHelper.shouldTriggerAmbientMorningZoomies("),
                "WolfSocialMixin must delegate ambient morning zoomies check to WolfZoomiesHelper");
        assertTrue(mixinSrc.contains("betterdogs$getOrInitializeScheduler().schedule(new ZoomiesDogEvent())"),
                "WolfSocialMixin must lazy-initialize scheduler only when ambient trigger fires");

        int idxIsIdle = mixinSrc.indexOf("betterdogs$socialScheduler.isIdle()");
        int idxNullReset = mixinSrc.indexOf("betterdogs$socialScheduler = null;");
        int idxPollCall = mixinSrc.indexOf("betterdogs$pollAmbientEvents();");
        assertTrue(idxIsIdle >= 0 && idxNullReset > idxIsIdle && idxPollCall > idxNullReset,
                "betterdogs$pollAmbientEvents() must execute strictly AFTER isIdle() null-cleanup");

        String eventSrc = Files.readString(
                mainJava.resolve("net/vanillaoutsider/betterdogs/scheduler/events/ZoomiesDogEvent.java"),
                StandardCharsets.UTF_8);
        assertTrue(eventSrc.contains("return this.tickCount < 240;"),
                "ZoomiesDogEvent.tick() must return this.tickCount < 240");
        assertFalse(eventSrc.contains("return this.tickCount >= 240;"),
                "ZoomiesDogEvent.tick() must not contain inverted >= 240 condition");

        String[] touchedFiles = {
                "net/vanillaoutsider/betterdogs/util/WolfZoomiesHelper.java",
                "net/vanillaoutsider/betterdogs/mixin/WolfSocialMixin.java",
                "net/vanillaoutsider/betterdogs/scheduler/events/ZoomiesDogEvent.java"
        };
        for (String rel : touchedFiles) {
            List<String> lines = Files.readAllLines(mainJava.resolve(rel), StandardCharsets.UTF_8);
            assertEquals("// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3", lines.get(0).trim(),
                    "Exact GPLv3 header required on line 1 in " + rel);
            String joined = String.join("\n", lines);
            assertFalse(joined.contains(clientPkg), "Zero client imports allowed in " + rel);
            assertFalse(joined.contains("com.mojang.blaze3d."), "Zero Blaze3D imports allowed in " + rel);
        }
    }

    private static final class StubNavigation extends GroundPathNavigation {
        private StubNavigation() {
            super(null, null);
        }

        @Override public boolean isDone() { return true; }
        @Override public void stop() {}
        @Override public boolean moveTo(double x, double y, double z, double speed) { return true; }
        @Override public boolean moveTo(Entity entity, double speed) { return true; }
        @Override public boolean isStableDestination(BlockPos pos) { return false; }
    }

    private static final class StubLookControl extends LookControl {
        private StubLookControl() { super(null); }
        @Override public void setLookAt(Entity entity, float maxYaw, float maxPitch) {}
    }

    private static final class StubJumpControl extends JumpControl {
        private StubJumpControl() { super(null); }
        @Override public void jump() {}
    }

    private static final class StubServerLevel extends ServerLevel {
        private GameRules gameRules;
        private long clockTime;

        private StubServerLevel() {
            super(null, null, null, null, null, null, false, 0L, null, false);
        }

        void init(GameRules gameRules, long clockTime) {
            this.gameRules = gameRules;
            this.clockTime = clockTime;
        }

        void setClockTime(long clockTime) {
            this.clockTime = clockTime;
        }

        @Override public GameRules getGameRules() { return this.gameRules; }
        @Override public boolean isClientSide() { return false; }
        @Override public long getGameTime() { return 100L; }
        @Override public long getDefaultClockTime() { return this.clockTime; }
        @Override public FluidState getFluidState(BlockPos pos) { return Fluids.EMPTY.defaultFluidState(); }
        @Override public BlockState getBlockState(BlockPos pos) {
            return pos.getY() < 64 ? Blocks.STONE.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }
        @Override public void playSound(@Nullable Entity source, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch) {}
        @Override public <T extends ParticleOptions> int sendParticles(T particle, double x, double y, double z, int count, double dx, double dy, double dz, double speed) { return count; }
        @Override public int getMinY() { return -64; }
        @Override public int getHeight() { return 384; }
    }

    private static final class StubClientLevel extends ServerLevel {
        private StubClientLevel() {
            super(null, null, null, null, null, null, false, 0L, null, false);
        }

        @Override public boolean isClientSide() { return true; }
    }

    private static final class StubWolf extends Wolf implements WolfExtensions, SocialEntity {
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
        private boolean tame;
        private boolean onGround;
        private boolean orderedToSit;
        private boolean sittingPose;
        private boolean guardMode;
        private int zoomiesTicks;
        private @Nullable Level level;
        private @Nullable LivingEntity target;
        private WolfPersonality personality;
        private CanineArchetype archetype;
        private RandomSource random;
        private @Nullable EntitySocialScheduler scheduler;
        private StubNavigation stubNav;
        private StubLookControl stubLook;
        private StubJumpControl stubJump;

        private StubWolf() {
            super(null, null);
        }

        void init(UUID uuid, @Nullable UUID ownerUuid, boolean tame,
                  WolfPersonality personality, CanineArchetype archetype) {
            this.uuid = uuid;
            this.ownerUuid = ownerUuid;
            this.tame = tame;
            this.onGround = true;
            this.personality = personality;
            this.archetype = archetype;
            this.random = RandomSource.create(12345L);
            try {
                this.stubNav = (StubNavigation) unsafe.allocateInstance(StubNavigation.class);
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
        void setOrderedToSitStub(boolean v) { this.orderedToSit = v; }
        void setSittingPoseStub(boolean v) { this.sittingPose = v; }
        void setGuardModeStub(boolean v) { this.guardMode = v; }
        void setOnGroundStub(boolean v) { this.onGround = v; }
        void setRandomSeed(long seed) { this.random = RandomSource.create(seed); }

        @Override public UUID getUUID() { return this.uuid; }
        @Override public String getStringUUID() { return this.uuid.toString(); }
        @Override public EntityType<?> getType() { return EntityTypes.WOLF; }
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
        @Override public boolean isAlive() { return true; }
        @Override public boolean isTame() { return this.tame; }
        @Override public int getAge() { return 0; }
        @Override public boolean onGround() { return this.onGround; }
        @Override public boolean isOrderedToSit() { return this.orderedToSit; }
        @Override public void setOrderedToSit(boolean ordered) { this.orderedToSit = ordered; }
        @Override public boolean isInSittingPose() { return this.sittingPose; }
        @Override public void setInSittingPose(boolean pose) { this.sittingPose = pose; }
        @Override public PathNavigation getNavigation() { return this.stubNav; }
        @Override public LookControl getLookControl() { return this.stubLook; }
        @Override public JumpControl getJumpControl() { return this.stubJump; }
        @Override public @Nullable LivingEntity getTarget() { return this.target; }
        @Override public void setTarget(@Nullable LivingEntity target) { this.target = target; }
        @Override public @Nullable EntityReference<LivingEntity> getOwnerReference() {
            return this.ownerUuid != null ? EntityReference.of(this.ownerUuid) : null;
        }
        @Override public @Nullable LivingEntity getOwner() { return null; }

        // SocialEntity
        @Override public long dasik$getDNA() { return 1L; }
        @Override public String dasik$getSpeciesId() { return "wolf"; }
        @Override public LivingEntity dasik$asEntity() { return this; }
        @Override public float dasik$getSocialScale() { return 1.0f; }
        @Override public SocialScheduler dasik$getScheduler() { return this.scheduler; }

        // WolfExtensions
        @Override public WolfPersonality betterdogs$getPersonality() { return this.personality; }
        @Override public void betterdogs$setPersonality(WolfPersonality p) { this.personality = p; }
        @Override public boolean betterdogs$hasPersonality() { return this.personality != null; }
        @Override public CanineArchetype betterdogs$getCanineArchetype() { return this.archetype; }
        @Override public void betterdogs$setCanineArchetype(CanineArchetype a) { this.archetype = a; }
        @Override public boolean betterdogs$isPerformingScoot() { return false; }
        @Override public boolean betterdogs$isGuardMode() { return this.guardMode; }
        @Override public void betterdogs$setGuardMode(boolean g) { this.guardMode = g; }
        @Override public @Nullable BlockPos betterdogs$getGuardPos() { return null; }
        @Override public void betterdogs$setGuardPos(@Nullable BlockPos pos) {}
        @Override public boolean betterdogs$isSittingManually() { return false; }
        @Override public void betterdogs$setSittingManually(boolean s) {}
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
        @Override public void betterdogs$setBeingDisciplined(boolean b) {}
        @Override public void betterdogs$setSocialState(@Nullable LivingEntity target, SocialAction action, int maxDurationTicks) {}
        @Override public @Nullable LivingEntity betterdogs$getSocialTarget() { return null; }
        @Override public SocialAction betterdogs$getSocialAction() { return SocialAction.NONE; }
        @Override public boolean betterdogs$isSocialModeActive() { return false; }
        @Override public void betterdogs$tickSocialMode() {}
        @Override public long betterdogs$getDNA() { return 1L; }
        @Override public void betterdogs$setDNA(long dna) {}
        @Override public float betterdogs$getSocialScale() { return 1.0f; }
        @Override public void betterdogs$setSocialScale(float scale) {}
        @Override public @Nullable EntitySocialScheduler betterdogs$getScheduler() { return this.scheduler; }
        @Override public EntitySocialScheduler betterdogs$getOrInitializeScheduler() {
            if (this.scheduler == null) {
                this.scheduler = new EntitySocialScheduler(this);
            }
            return this.scheduler;
        }
        @Override public void betterdogs$tickScheduler() {}
        @Override public int betterdogs$getAffinity(String targetUuid) { return 0; }
        @Override public void betterdogs$adjustAffinity(String targetUuid, int delta) {}
        @Override public @Nullable UUID betterdogs$getLeaderUuid() { return null; }
        @Override public void betterdogs$setLeaderUuid(@Nullable UUID uuid) {}
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
        @Override public int betterdogs$getZoomiesTicks() { return this.zoomiesTicks; }
        @Override public void betterdogs$setZoomiesTicks(int ticks) { this.zoomiesTicks = ticks; }
        @Override public int betterdogs$getHowlingTicks() { return 0; }
        @Override public void betterdogs$setHowlingTicks(int ticks) {}
    }
}
