// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
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
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BL-BD-074: SocialEvent Expiration, Dead/Null Target Guards & Howl Timer Reset Tests")
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
            WolfExtensions.SocialAction expectedActiveAction) {
        StubServerLevel level = createStubServerLevel();
        StubWolf wolf = createStubWolf(level, true);
        EntitySocialScheduler scheduler = wolf.betterdogs$getOrInitializeScheduler();

        scheduler.schedule(eventFactory.get());
        assertExactActiveAndExpiryWindow(scheduler, wolf, expectedId, expectedDurationTicks, expectedActiveAction);
    }

    private static void assertExactActiveAndExpiryWindow(
            EntitySocialScheduler scheduler,
            StubWolf wolf,
            String expectedId,
            int expectedDurationTicks,
            WolfExtensions.SocialAction expectedActiveAction) {
        for (int tick = 1; tick < expectedDurationTicks; tick++) {
            scheduler.tick();
            assertTrue(scheduler.isEventActive(expectedId),
                    expectedId + " must remain active on tick " + tick + "/" + (expectedDurationTicks - 1));
            assertFalse(scheduler.isIdle(),
                    "Scheduler must not be idle while " + expectedId + " is active on tick " + tick);
            if (expectedActiveAction != WolfExtensions.SocialAction.NONE) {
                assertEquals(expectedActiveAction, wolf.betterdogs$getSocialAction(),
                        expectedId + " must hold social action " + expectedActiveAction + " on tick " + tick);
            }
        }

        scheduler.tick();
        assertFalse(scheduler.isEventActive(expectedId),
                expectedId + " must expire on tick " + expectedDurationTicks);
        assertTrue(scheduler.isIdle(),
                "Scheduler must become idle after " + expectedId + " expires on tick " + expectedDurationTicks);
        assertEquals(WolfExtensions.SocialAction.NONE, wolf.betterdogs$getSocialAction(),
                "SocialAction must reset to NONE after " + expectedId + " expires");
        assertFalse(wolf.betterdogs$isSocialModeActive(),
                "SocialMode must be inactive after " + expectedId + " expires");
    }

    @Test
    @DisplayName("All 8 SocialEvent classes remain active for ticks 1..N-1 and expire cleanly on tick N")
    void testAllEightSocialEventsRunFullAuthoredDuration() {
        StubServerLevel sharedLevel = createStubServerLevel();
        StubWolf liveTarget = createStubWolf(sharedLevel, true);

        assertExactDurationLifecycle(BeggingDogEvent::new, BeggingDogEvent.ID, 400, WolfExtensions.SocialAction.NONE);
        assertExactDurationLifecycle(() -> new CorrectionDogEvent(liveTarget), CorrectionDogEvent.ID, 60,
                WolfExtensions.SocialAction.DISCIPLINE);
        assertExactDurationLifecycle(FetchDogEvent::new, FetchDogEvent.ID, 600, WolfExtensions.SocialAction.NONE);
        assertExactDurationLifecycle(HowlDogEvent::new, HowlDogEvent.ID, 60, WolfExtensions.SocialAction.NONE);
        assertExactDurationLifecycle(IdleCuriosityEvent::new, IdleCuriosityEvent.ID, 300,
                WolfExtensions.SocialAction.NONE);
        assertExactDurationLifecycle(() -> new RetaliationDogEvent(liveTarget), RetaliationDogEvent.ID, 100,
                WolfExtensions.SocialAction.RETALIATION);
        assertExactDurationLifecycle(SmallFightDogEvent::new, SmallFightDogEvent.ID, 200,
                WolfExtensions.SocialAction.NONE);
        assertExactDurationLifecycle(WanderlustDogEvent::new, WanderlustDogEvent.ID, 600,
                WolfExtensions.SocialAction.NONE);
    }

    @Test
    @DisplayName("HowlDogEvent resets timer on onStart and supports consecutive reuse after preemption and expiry")
    void testHowlDogEventTimerResetAndConsecutiveReuse() {
        StubServerLevel level = createStubServerLevel();
        StubWolf wolf = createStubWolf(level, true);
        EntitySocialScheduler scheduler = wolf.betterdogs$getOrInitializeScheduler();

        HowlDogEvent reusableHowl = new HowlDogEvent();

        // Run 1: preempted mid-flight after 30 ticks by a higher-priority event
        scheduler.schedule(reusableHowl);
        for (int tick = 1; tick <= 30; tick++) {
            scheduler.tick();
            assertTrue(scheduler.isEventActive(HowlDogEvent.ID),
                    "HowlDogEvent must be active on tick " + tick + " before preemption");
        }

        SocialEvent preempter = new HighPriorityDummyEvent();
        scheduler.schedule(preempter);
        scheduler.tick(); // preempter tick 1 (howl preempted)
        assertFalse(scheduler.isEventActive(HowlDogEvent.ID), "HowlDogEvent must be preempted");
        scheduler.tick(); // preempter tick 2 (expires)
        assertTrue(scheduler.isIdle(), "Scheduler must become idle after preempter expires");

        // Run 2: reuse the same HowlDogEvent instance -> must still run full 60 ticks
        scheduler.schedule(reusableHowl);
        assertExactActiveAndExpiryWindow(scheduler, wolf, HowlDogEvent.ID, 60, WolfExtensions.SocialAction.NONE);

        // Run 3: simulate dirty timer via direct tick() without onEnd(), then re-schedule via onStart()
        reusableHowl.tick(new TickContext(wolf, 200L, 1.0f, FastRandom.INSTANCE));
        reusableHowl.tick(new TickContext(wolf, 201L, 1.0f, FastRandom.INSTANCE));
        scheduler.schedule(reusableHowl);
        assertExactActiveAndExpiryWindow(scheduler, wolf, HowlDogEvent.ID, 60, WolfExtensions.SocialAction.NONE);
    }

    @Test
    @DisplayName("Negative parity: null and dead targets expire CorrectionDogEvent and RetaliationDogEvent on tick 1")
    void testNullAndDeadTargetsExpireImmediately() {
        StubServerLevel level = createStubServerLevel();
        StubWolf wolf = createStubWolf(level, true);
        StubWolf deadTarget = createStubWolf(level, false);
        EntitySocialScheduler scheduler = wolf.betterdogs$getOrInitializeScheduler();

        // CorrectionDogEvent(null)
        scheduler.schedule(new CorrectionDogEvent(null));
        scheduler.tick();
        assertFalse(scheduler.isEventActive(CorrectionDogEvent.ID),
                "CorrectionDogEvent(null) must expire on tick 1");
        assertTrue(scheduler.isIdle(), "Scheduler must be idle after CorrectionDogEvent(null) tick 1");
        assertEquals(WolfExtensions.SocialAction.NONE, wolf.betterdogs$getSocialAction());

        // CorrectionDogEvent(deadTarget)
        scheduler.schedule(new CorrectionDogEvent(deadTarget));
        scheduler.tick();
        assertFalse(scheduler.isEventActive(CorrectionDogEvent.ID),
                "CorrectionDogEvent with dead target must expire on tick 1");
        assertTrue(scheduler.isIdle(), "Scheduler must be idle after dead-target CorrectionDogEvent tick 1");
        assertEquals(WolfExtensions.SocialAction.NONE, wolf.betterdogs$getSocialAction());

        // RetaliationDogEvent(null)
        scheduler.schedule(new RetaliationDogEvent(null));
        scheduler.tick();
        assertFalse(scheduler.isEventActive(RetaliationDogEvent.ID),
                "RetaliationDogEvent(null) must expire on tick 1");
        assertTrue(scheduler.isIdle(), "Scheduler must be idle after RetaliationDogEvent(null) tick 1");
        assertEquals(WolfExtensions.SocialAction.NONE, wolf.betterdogs$getSocialAction());

        // RetaliationDogEvent(deadTarget)
        scheduler.schedule(new RetaliationDogEvent(deadTarget));
        scheduler.tick();
        assertFalse(scheduler.isEventActive(RetaliationDogEvent.ID),
                "RetaliationDogEvent with dead target must expire on tick 1");
        assertTrue(scheduler.isIdle(), "Scheduler must be idle after dead-target RetaliationDogEvent tick 1");
        assertEquals(WolfExtensions.SocialAction.NONE, wolf.betterdogs$getSocialAction());

        // Target dies mid-event -> must exit on next tick and clear SocialAction
        StubWolf dyingTarget = createStubWolf(level, true);
        scheduler.schedule(new RetaliationDogEvent(dyingTarget));
        for (int tick = 1; tick <= 5; tick++) {
            scheduler.tick();
            assertTrue(scheduler.isEventActive(RetaliationDogEvent.ID));
        }
        dyingTarget.setAliveStub(false);
        scheduler.tick();
        assertFalse(scheduler.isEventActive(RetaliationDogEvent.ID),
                "RetaliationDogEvent must exit immediately when target dies mid-event");
        assertTrue(scheduler.isIdle());
        assertEquals(WolfExtensions.SocialAction.NONE, wolf.betterdogs$getSocialAction());
    }

    @Test
    @DisplayName("Higher-priority RetaliationDogEvent preempts IdleCuriosityEvent and clears state on expiry")
    void testRetaliationPreemptsIdleCuriosityAndExpiresCleanly() {
        StubServerLevel level = createStubServerLevel();
        StubWolf wolf = createStubWolf(level, true);
        StubWolf aggressor = createStubWolf(level, true);
        EntitySocialScheduler scheduler = wolf.betterdogs$getOrInitializeScheduler();

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
            assertEquals(WolfExtensions.SocialAction.RETALIATION, wolf.betterdogs$getSocialAction());
        }

        scheduler.tick(); // tick 100
        assertFalse(scheduler.isEventActive(RetaliationDogEvent.ID),
                "RetaliationDogEvent must expire cleanly on tick 100");
        assertTrue(scheduler.isIdle(), "Scheduler must become idle after preemption + retaliation expiry");
        assertEquals(WolfExtensions.SocialAction.NONE, wolf.betterdogs$getSocialAction());
        assertFalse(wolf.betterdogs$isSocialModeActive());
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
            super(null, null, null, null, null, null, false, 0L, null, false);
        }

        void init() {
            this.random = RandomSource.create(12345L);
            this.gameTime = 100L;
        }

        @Override public boolean isClientSide() { return false; }
        @Override public long getGameTime() { return this.gameTime; }
        @Override public RandomSource getRandom() { return this.random; }
        @Override public List<ServerPlayer> players() { return Collections.emptyList(); }
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
        private @Nullable LivingEntity socialTarget;
        private SocialAction socialAction;
        private int socialModeTimer;
        private @Nullable EntitySocialScheduler scheduler;
        private Map<String, Integer> affinityMap;

        private StubWolf() {
            super(null, null);
        }

        void init(UUID uuid, Level level, boolean alive) {
            this.uuid = uuid;
            this.level = level;
            this.alive = alive;
            this.random = RandomSource.create(67890L);
            this.socialAction = SocialAction.NONE;
            this.affinityMap = new HashMap<>();
            this.setBoundingBox(new AABB(0.0, 64.0, 0.0, 0.6, 64.85, 0.6));
            try {
                ENTITY_POSITION_FIELD.set(this, new Vec3(0.5, 64.0, 0.5));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to initialize Entity.position", e);
            }
        }

        void setAliveStub(boolean alive) { this.alive = alive; }

        @Override public UUID getUUID() { return this.uuid; }
        @Override public String getStringUUID() { return this.uuid.toString(); }
        @Override public EntityType<?> getType() { return EntityTypes.WOLF; }
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
        @Override public SocialScheduler dasik$getScheduler() { return this.scheduler; }

        // WolfExtensions
        @Override public WolfPersonality betterdogs$getPersonality() { return WolfPersonality.NORMAL; }
        @Override public void betterdogs$setPersonality(WolfPersonality p) {}
        @Override public boolean betterdogs$hasPersonality() { return true; }
        @Override public CanineArchetype betterdogs$getCanineArchetype() { return CanineArchetype.GUARDIAN; }
        @Override public void betterdogs$setCanineArchetype(CanineArchetype a) {}
        @Override public boolean betterdogs$isPerformingScoot() { return false; }
        @Override public boolean betterdogs$isGuardMode() { return false; }
        @Override public void betterdogs$setGuardMode(boolean g) {}
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
                this.setTarget(null);
            }
        }

        @Override public @Nullable LivingEntity betterdogs$getSocialTarget() { return this.socialTarget; }
        @Override public SocialAction betterdogs$getSocialAction() { return this.socialAction; }
        @Override public boolean betterdogs$isSocialModeActive() {
            return this.socialTarget != null && this.socialTarget.isAlive() && this.socialModeTimer > 0;
        }
        @Override public void betterdogs$tickSocialMode() {
            if (this.socialModeTimer <= 1) {
                if (this.socialModeTimer > 0 || this.socialTarget != null) {
                    betterdogs$setSocialState(null, SocialAction.NONE, 0);
                }
                return;
            }
            this.socialModeTimer--;
        }

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
        @Override public int betterdogs$getAffinity(String targetUuid) {
            return this.affinityMap.getOrDefault(targetUuid, 0);
        }
        @Override public void betterdogs$adjustAffinity(String targetUuid, int delta) {
            this.affinityMap.merge(targetUuid, delta, Integer::sum);
        }
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
        @Override public int betterdogs$getZoomiesTicks() { return 0; }
        @Override public void betterdogs$setZoomiesTicks(int ticks) {}
        @Override public int betterdogs$getHowlingTicks() { return 0; }
        @Override public void betterdogs$setHowlingTicks(int ticks) {}
    }
}
