// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.SocialScheduler;
import net.dasik.social.api.TickContext;
import net.dasik.social.core.EntitySocialScheduler;
import net.dasik.social.util.FastRandom;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.scheduler.events.BeggingDogEvent;
import net.vanillaoutsider.betterdogs.scheduler.events.CorrectionDogEvent;
import net.vanillaoutsider.betterdogs.scheduler.events.FetchDogEvent;
import net.vanillaoutsider.betterdogs.scheduler.events.HowlDogEvent;
import net.vanillaoutsider.betterdogs.scheduler.events.IdleCuriosityEvent;
import net.vanillaoutsider.betterdogs.scheduler.events.RetaliationDogEvent;
import net.vanillaoutsider.betterdogs.scheduler.events.SmallFightDogEvent;
import net.vanillaoutsider.betterdogs.scheduler.events.WanderlustDogEvent;
import net.vanillaoutsider.betterdogs.scheduler.events.ZoomiesDogEvent;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BL-BD-074: SocialEvent Expiration, Dead/Null Target Guards & Howl Timer Reset Tests (1.21.1)")
class SocialEventExpirationTest {

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
    }

    private static StubServerLevel createStubServerLevel() {
        try {
            StubServerLevel level = (StubServerLevel) unsafe.allocateInstance(StubServerLevel.class);
            level.init();
            return level;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubServerLevel", e);
        }
    }

    private static StubWolf createStubWolf(StubServerLevel level, boolean alive) {
        try {
            StubWolf wolf = (StubWolf) unsafe.allocateInstance(StubWolf.class);
            wolf.init(UUID.randomUUID(), level, alive);
            return wolf;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubWolf", e);
        }
    }

    private static void assertExactDurationLifecycle(
            Supplier<SocialEvent> eventFactory,
            String expectedId,
            int expectedDurationTicks,
            @Nullable LivingEntity expectedRetaliationTarget) {
        StubServerLevel level = createStubServerLevel();
        StubWolf wolf = createStubWolf(level, true);
        EntitySocialScheduler scheduler = wolf.getOrInitializeScheduler();

        scheduler.schedule(eventFactory.get());
        assertExactActiveAndExpiryWindow(scheduler, wolf, expectedId, expectedDurationTicks, expectedRetaliationTarget);
    }

    private static void assertExactActiveAndExpiryWindow(
            EntitySocialScheduler scheduler,
            StubWolf wolf,
            String expectedId,
            int expectedDurationTicks,
            @Nullable LivingEntity expectedRetaliationTarget) {
        for (int tick = 1; tick < expectedDurationTicks; tick++) {
            scheduler.tick();
            assertTrue(scheduler.isEventActive(expectedId),
                    expectedId + " must remain active on tick " + tick + "/" + (expectedDurationTicks - 1));
            assertFalse(scheduler.isIdle(),
                    "Scheduler must not be idle while " + expectedId + " is active on tick " + tick);
            if (expectedRetaliationTarget != null) {
                assertSame(expectedRetaliationTarget, wolf.betterdogs$getRetaliationTarget(),
                        expectedId + " must hold retaliation target on tick " + tick);
            }
        }

        scheduler.tick();
        assertFalse(scheduler.isEventActive(expectedId),
                expectedId + " must expire on tick " + expectedDurationTicks);
        assertTrue(scheduler.isIdle(),
                "Scheduler must become idle after " + expectedId + " expires on tick " + expectedDurationTicks);
        if (expectedRetaliationTarget != null) {
            assertNull(wolf.betterdogs$getRetaliationTarget(),
                    "Retaliation target must clear to null after " + expectedId + " expires");
            assertEquals(0, wolf.betterdogs$getRetaliationTicks(),
                    "Retaliation ticks must clear to 0 after " + expectedId + " expires");
        }
    }

    @Test
    @DisplayName("All 9 SocialEvent classes remain active for ticks 1..N-1 and expire cleanly on tick N")
    void testAllNineSocialEventsRunFullAuthoredDuration() {
        StubServerLevel sharedLevel = createStubServerLevel();
        StubWolf liveTarget = createStubWolf(sharedLevel, true);

        assertExactDurationLifecycle(BeggingDogEvent::new, BeggingDogEvent.ID, 400, null);
        assertExactDurationLifecycle(() -> new CorrectionDogEvent(liveTarget), CorrectionDogEvent.ID, 60, liveTarget);
        assertExactDurationLifecycle(FetchDogEvent::new, FetchDogEvent.ID, 600, null);
        assertExactDurationLifecycle(HowlDogEvent::new, HowlDogEvent.ID, 60, null);
        assertExactDurationLifecycle(IdleCuriosityEvent::new, IdleCuriosityEvent.ID, 300, null);
        assertExactDurationLifecycle(() -> new RetaliationDogEvent(liveTarget), RetaliationDogEvent.ID, 100, liveTarget);
        assertExactDurationLifecycle(SmallFightDogEvent::new, SmallFightDogEvent.ID, 200, null);
        assertExactDurationLifecycle(WanderlustDogEvent::new, WanderlustDogEvent.ID, 600, null);
        assertExactDurationLifecycle(ZoomiesDogEvent::new, ZoomiesDogEvent.ID, 240, null);
    }

    @Test
    @DisplayName("Events clear and initialize extension timers cleanly on start and end")
    void testEventExtensionTimerHooks() {
        StubServerLevel level = createStubServerLevel();
        StubWolf wolf = createStubWolf(level, true);
        EntitySocialScheduler scheduler = wolf.getOrInitializeScheduler();

        scheduler.schedule(new ZoomiesDogEvent());
        scheduler.tick();
        assertEquals(240, wolf.betterdogs$getZoomiesTicks());
        for (int t = 2; t <= 240; t++) {
            scheduler.tick();
        }
        assertTrue(scheduler.isIdle());
        assertEquals(0, wolf.betterdogs$getZoomiesTicks());

        scheduler.schedule(new WanderlustDogEvent());
        scheduler.tick();
        assertEquals(600, wolf.betterdogs$getWanderlustTicks());
        for (int t = 2; t <= 600; t++) {
            scheduler.tick();
        }
        assertTrue(scheduler.isIdle());
        assertEquals(0, wolf.betterdogs$getWanderlustTicks());

        scheduler.schedule(new SmallFightDogEvent());
        scheduler.tick();
        assertEquals(200, wolf.betterdogs$getPlayFightCooldown());
        for (int t = 2; t <= 200; t++) {
            scheduler.tick();
        }
        assertTrue(scheduler.isIdle());
    }

    @Test
    @DisplayName("HowlDogEvent resets timer on onStart and supports consecutive reuse after preemption and expiry")
    void testHowlDogEventTimerResetAndConsecutiveReuse() {
        StubServerLevel level = createStubServerLevel();
        StubWolf wolf = createStubWolf(level, true);
        EntitySocialScheduler scheduler = wolf.getOrInitializeScheduler();

        HowlDogEvent reusableHowl = new HowlDogEvent();

        // Run 1: preempted mid-flight after 30 ticks by a higher-priority event
        scheduler.schedule(reusableHowl);
        for (int tick = 1; tick <= 30; tick++) {
            scheduler.tick();
            assertTrue(scheduler.isEventActive(HowlDogEvent.ID),
                    "HowlDogEvent must be active on tick " + tick + " before preemption");
        }
        assertEquals(60, wolf.betterdogs$getHowlingTicks());

        SocialEvent preempter = new HighPriorityDummyEvent();
        scheduler.schedule(preempter);
        scheduler.tick(); // preempter tick 1 (howl preempted)
        assertFalse(scheduler.isEventActive(HowlDogEvent.ID), "HowlDogEvent must be preempted");
        scheduler.tick(); // preempter tick 2 (expires)
        assertTrue(scheduler.isIdle(), "Scheduler must become idle after preempter expires");

        // Run 2: reuse the same HowlDogEvent instance -> must still run full 60 ticks
        scheduler.schedule(reusableHowl);
        assertExactActiveAndExpiryWindow(scheduler, wolf, HowlDogEvent.ID, 60, null);

        // Run 3: simulate dirty timer via direct tick() without onEnd(), then re-schedule via onStart()
        reusableHowl.tick(new TickContext(wolf, 200L, 1.0f, FastRandom.INSTANCE));
        reusableHowl.tick(new TickContext(wolf, 201L, 1.0f, FastRandom.INSTANCE));
        scheduler.schedule(reusableHowl);
        assertExactActiveAndExpiryWindow(scheduler, wolf, HowlDogEvent.ID, 60, null);
    }

    @Test
    @DisplayName("Negative parity: null and dead targets expire CorrectionDogEvent and RetaliationDogEvent on tick 1")
    void testNullAndDeadTargetsExpireImmediately() {
        StubServerLevel level = createStubServerLevel();
        StubWolf wolf = createStubWolf(level, true);
        StubWolf deadTarget = createStubWolf(level, false);
        EntitySocialScheduler scheduler = wolf.getOrInitializeScheduler();

        // CorrectionDogEvent(null)
        scheduler.schedule(new CorrectionDogEvent(null));
        scheduler.tick();
        assertFalse(scheduler.isEventActive(CorrectionDogEvent.ID),
                "CorrectionDogEvent(null) must expire on tick 1");
        assertTrue(scheduler.isIdle(), "Scheduler must be idle after CorrectionDogEvent(null) tick 1");
        assertNull(wolf.betterdogs$getRetaliationTarget());
        assertEquals(0, wolf.betterdogs$getRetaliationTicks());

        // CorrectionDogEvent(deadTarget)
        scheduler.schedule(new CorrectionDogEvent(deadTarget));
        scheduler.tick();
        assertFalse(scheduler.isEventActive(CorrectionDogEvent.ID),
                "CorrectionDogEvent with dead target must expire on tick 1");
        assertTrue(scheduler.isIdle(), "Scheduler must be idle after dead-target CorrectionDogEvent tick 1");
        assertNull(wolf.betterdogs$getRetaliationTarget());
        assertEquals(0, wolf.betterdogs$getRetaliationTicks());

        // RetaliationDogEvent(null)
        scheduler.schedule(new RetaliationDogEvent(null));
        scheduler.tick();
        assertFalse(scheduler.isEventActive(RetaliationDogEvent.ID),
                "RetaliationDogEvent(null) must expire on tick 1");
        assertTrue(scheduler.isIdle(), "Scheduler must be idle after RetaliationDogEvent(null) tick 1");
        assertNull(wolf.betterdogs$getRetaliationTarget());
        assertEquals(0, wolf.betterdogs$getRetaliationTicks());

        // RetaliationDogEvent(deadTarget)
        scheduler.schedule(new RetaliationDogEvent(deadTarget));
        scheduler.tick();
        assertFalse(scheduler.isEventActive(RetaliationDogEvent.ID),
                "RetaliationDogEvent with dead target must expire on tick 1");
        assertTrue(scheduler.isIdle(), "Scheduler must be idle after dead-target RetaliationDogEvent tick 1");
        assertNull(wolf.betterdogs$getRetaliationTarget());
        assertEquals(0, wolf.betterdogs$getRetaliationTicks());

        // Mid-event target death on CorrectionDogEvent
        StubWolf dyingCorrectionTarget = createStubWolf(level, true);
        scheduler.schedule(new CorrectionDogEvent(dyingCorrectionTarget));
        for (int tick = 1; tick <= 5; tick++) {
            scheduler.tick();
            assertTrue(scheduler.isEventActive(CorrectionDogEvent.ID));
        }
        dyingCorrectionTarget.setAliveStub(false);
        scheduler.tick();
        assertFalse(scheduler.isEventActive(CorrectionDogEvent.ID),
                "CorrectionDogEvent must exit immediately when target dies mid-event");
        assertTrue(scheduler.isIdle());
        assertNull(wolf.betterdogs$getRetaliationTarget());
        assertEquals(0, wolf.betterdogs$getRetaliationTicks());

        // Mid-event target death on RetaliationDogEvent
        StubWolf dyingRetaliationTarget = createStubWolf(level, true);
        scheduler.schedule(new RetaliationDogEvent(dyingRetaliationTarget));
        for (int tick = 1; tick <= 5; tick++) {
            scheduler.tick();
            assertTrue(scheduler.isEventActive(RetaliationDogEvent.ID));
        }
        dyingRetaliationTarget.setAliveStub(false);
        scheduler.tick();
        assertFalse(scheduler.isEventActive(RetaliationDogEvent.ID),
                "RetaliationDogEvent must exit immediately when target dies mid-event");
        assertTrue(scheduler.isIdle());
        assertNull(wolf.betterdogs$getRetaliationTarget());
        assertEquals(0, wolf.betterdogs$getRetaliationTicks());
    }

    @Test
    @DisplayName("Higher-priority RetaliationDogEvent preempts IdleCuriosityEvent and clears state on expiry")
    void testRetaliationPreemptsIdleCuriosityAndExpiresCleanly() {
        StubServerLevel level = createStubServerLevel();
        StubWolf wolf = createStubWolf(level, true);
        StubWolf aggressor = createStubWolf(level, true);
        EntitySocialScheduler scheduler = wolf.getOrInitializeScheduler();

        scheduler.schedule(new IdleCuriosityEvent());
        for (int tick = 1; tick <= 10; tick++) {
            scheduler.tick();
            assertTrue(scheduler.isEventActive(IdleCuriosityEvent.ID),
                    "IdleCuriosityEvent must remain active before preemption on tick " + tick);
        }

        scheduler.schedule(new RetaliationDogEvent(aggressor));
        for (int tick = 1; tick < 100; tick++) {
            scheduler.tick();
            assertFalse(scheduler.isEventActive(IdleCuriosityEvent.ID),
                    "IdleCuriosityEvent must be preempted by RetaliationDogEvent");
            assertTrue(scheduler.isEventActive(RetaliationDogEvent.ID),
                    "RetaliationDogEvent must remain active on tick " + tick + "/99");
            assertSame(aggressor, wolf.betterdogs$getRetaliationTarget());
        }

        scheduler.tick(); // tick 100
        assertFalse(scheduler.isEventActive(RetaliationDogEvent.ID),
                "RetaliationDogEvent must expire cleanly on tick 100");
        assertTrue(scheduler.isIdle(), "Scheduler must become idle after preemption + retaliation expiry");
        assertNull(wolf.betterdogs$getRetaliationTarget());
        assertEquals(0, wolf.betterdogs$getRetaliationTicks());
    }

    private static final class HighPriorityDummyEvent implements SocialEvent {
        private int tickCount = 0;

        @Override public String getId() { return "high_priority_dummy"; }
        @Override public int getPriorityValue() { return 25; }
        @Override public String getTrackId() { return "main"; }
        @Override public boolean canPreempt(SocialEvent other) { return other.getPriorityValue() < 25; }
        @Override public void onStart(TickContext context) { this.tickCount = 0; }
        @Override public boolean tick(TickContext context) {
            this.tickCount++;
            return this.tickCount < 2;
        }
        @Override public void onEnd(SocialEntity entity, EndReason reason) {}
    }

    private static final class StubServerLevel extends ServerLevel {
        private RandomSource random;
        private long gameTime;

        private StubServerLevel() {
            super(null, null, null, null, null, null, null, false, 0L, null, false, null);
        }

        void init() {
            this.random = RandomSource.create(12345L);
            this.gameTime = 100L;
        }

        @Override public boolean isClientSide() { return false; }
        @Override public long getGameTime() { return this.gameTime; }
        @Override public RandomSource getRandom() { return this.random; }
        @Override public List<ServerPlayer> players() { return Collections.emptyList(); }
        @Override public void playSound(@Nullable Player player, double x, double y, double z, SoundEvent sound,
                                        SoundSource category, float volume, float pitch) {}
        @Override public <T extends Entity> List<T> getEntitiesOfClass(Class<T> entityClass, AABB area) {
            return Collections.emptyList();
        }
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
        private boolean alive;
        private Level level;
        private RandomSource random;
        private @Nullable LivingEntity target;
        private @Nullable LivingEntity retaliationTarget;
        private int retaliationTicks;
        private int howlingTicks;
        private int playFightCooldown;
        private int wanderlustTicks;
        private int zoomiesTicks;
        private @Nullable EntitySocialScheduler scheduler;

        private StubWolf() {
            super(null, null);
        }

        void init(UUID uuid, Level level, boolean alive) {
            this.uuid = uuid;
            this.level = level;
            this.alive = alive;
            this.random = RandomSource.create(67890L);
            this.setBoundingBox(new AABB(0.0, 64.0, 0.0, 0.6, 64.85, 0.6));
            try {
                ENTITY_POSITION_FIELD.set(this, new Vec3(0.5, 64.0, 0.5));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to initialize Entity.position", e);
            }
        }

        void setAliveStub(boolean alive) { this.alive = alive; }

        EntitySocialScheduler getOrInitializeScheduler() {
            if (this.scheduler == null) {
                this.scheduler = new EntitySocialScheduler(this);
            }
            return this.scheduler;
        }

        @Override public UUID getUUID() { return this.uuid; }
        @Override public String getStringUUID() { return this.uuid.toString(); }
        @Override public EntityType<?> getType() { return EntityType.WOLF; }
        @Override public Level level() { return this.level; }
        @Override public RandomSource getRandom() { return this.random; }
        @Override public boolean isAlive() { return this.alive; }
        @Override public @Nullable LivingEntity getTarget() { return this.target; }
        @Override public void setTarget(@Nullable LivingEntity target) { this.target = target; }

        // SocialEntity
        @Override public long dasik$getDNA() { return 1L; }
        @Override public String dasik$getSpeciesId() { return "wolf"; }
        @Override public LivingEntity dasik$asEntity() { return this; }
        @Override public float dasik$getSocialScale() { return 1.0f; }
        @Override public @Nullable SocialScheduler dasik$getScheduler() { return this.scheduler; }

        // WolfExtensions
        @Override public WolfPersonality betterdogs$getPersonality() { return WolfPersonality.NORMAL; }
        @Override public void betterdogs$setPersonality(WolfPersonality p) {}
        @Override public boolean betterdogs$hasPersonality() { return true; }
        @Override public float betterdogs$getSocialScale() { return 1.0f; }
        @Override public void betterdogs$setSocialScale(float scale) {}
        @Override public long betterdogs$getDnaSeed() { return 1L; }
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
        @Override public boolean betterdogs$isGuarding() { return false; }
        @Override public void betterdogs$setGuarding(boolean guarding) {}
        @Override public @Nullable BlockPos betterdogs$getGuardPos() { return null; }
        @Override public void betterdogs$setGuardPos(@Nullable BlockPos pos) {}
        @Override public boolean betterdogs$isUpForAdoption() { return false; }
        @Override public void betterdogs$setUpForAdoption(boolean adoption) {}
        @Override public long betterdogs$getLastGiftDay() { return 0L; }
        @Override public void betterdogs$setLastGiftDay(long day) {}
        @Override public int betterdogs$getZoomiesTicks() { return this.zoomiesTicks; }
        @Override public void betterdogs$setZoomiesTicks(int ticks) { this.zoomiesTicks = ticks; }
        @Override public boolean betterdogs$hasFetchedStick() { return false; }
        @Override public void betterdogs$setHasFetchedStick(boolean fetched) {}
        @Override public int betterdogs$getCalmTicks() { return 0; }
        @Override public void betterdogs$setCalmTicks(int ticks) {}
        @Override public int betterdogs$getHowlingTicks() { return this.howlingTicks; }
        @Override public void betterdogs$setHowlingTicks(int ticks) { this.howlingTicks = ticks; }
        @Override public int betterdogs$getFeedCount() { return 0; }
        @Override public void betterdogs$setFeedCount(int count) {}
        @Override public @Nullable LivingEntity betterdogs$getRetaliationTarget() { return this.retaliationTarget; }
        @Override public void betterdogs$setRetaliationTarget(@Nullable LivingEntity target, int ticks) {
            this.retaliationTarget = target;
            this.retaliationTicks = ticks;
        }
        @Override public int betterdogs$getRetaliationTicks() { return this.retaliationTicks; }
        @Override public String betterdogs$getBloodFeudTarget() { return ""; }
        @Override public void betterdogs$setBloodFeudTarget(String targetUuid) {}
        @Override public boolean betterdogs$hasBloodFeud() { return false; }
        @Override public int betterdogs$getPlayFightCooldown() { return this.playFightCooldown; }
        @Override public void betterdogs$setPlayFightCooldown(int ticks) { this.playFightCooldown = ticks; }
        @Override public @Nullable UUID betterdogs$getLeaderUUID() { return null; }
        @Override public void betterdogs$setLeaderUUID(@Nullable UUID uuid) {}
        @Override public boolean betterdogs$isPackLeader() { return false; }
        @Override public void betterdogs$setPackLeader(boolean isLeader) {}
        @Override public int betterdogs$getWanderlustTicks() { return this.wanderlustTicks; }
        @Override public void betterdogs$setWanderlustTicks(int ticks) { this.wanderlustTicks = ticks; }
        @Override public CanineArchetype betterdogs$getCanineArchetype() { return CanineArchetype.GUARDIAN; }
    }
}
