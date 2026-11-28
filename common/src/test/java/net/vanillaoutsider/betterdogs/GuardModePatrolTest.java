// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import java.lang.reflect.Field;
import java.util.UUID;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.WolfGuardGoal;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.util.WolfGuardHelper;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

public class GuardModePatrolTest {

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

    private static StubWolf createStubWolf(boolean tame, boolean guardMode, @Nullable BlockPos guardPos, WolfPersonality personality) {
        try {
            StubWolf wolf = (StubWolf) UNSAFE.allocateInstance(StubWolf.class);
            wolf.init(UUID.randomUUID(), tame, guardMode, guardPos, personality);
            return wolf;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubWolf", e);
        }
    }

    @Test
    @DisplayName("Verify Personality Guard Patrol Radii (Aggressive 12m, Normal 8m, Pacifist 4m)")
    public void testGuardRadii() {
        Assertions.assertEquals(12, WolfGuardHelper.getPatrolRadius(WolfPersonality.AGGRESSIVE), "Aggressive wolves must patrol 12 blocks");
        Assertions.assertEquals(8, WolfGuardHelper.getPatrolRadius(WolfPersonality.NORMAL), "Normal wolves must patrol 8 blocks");
        Assertions.assertEquals(4, WolfGuardHelper.getPatrolRadius(WolfPersonality.PACIFIST), "Pacifist wolves must patrol 4 blocks");
        Assertions.assertEquals(8, WolfGuardHelper.getPatrolRadius(null), "Null personality must fallback to 8 blocks");
    }

    @Test
    @DisplayName("Verify Guard Leash Boundaries and Distance Math")
    public void testGuardLeashBoundaries() {
        BlockPos post = new BlockPos(100, 64, -200);
        int aggressiveRadius = WolfGuardHelper.getPatrolRadius(WolfPersonality.AGGRESSIVE);
        double maxDistSq = aggressiveRadius * aggressiveRadius; // 144.0

        double insideDistSq = 100.0;
        double outsideDistSq = 200.0;

        Assertions.assertTrue(insideDistSq <= maxDistSq, "10 blocks distance is within 12m territory");
        Assertions.assertFalse(outsideDistSq <= maxDistSq, "15 blocks distance is outside 12m territory");
    }

    @Test
    @DisplayName("Verify WolfGuardHelper Null Safety")
    public void testGuardHelperNullSafety() {
        Assertions.assertFalse(WolfGuardHelper.canToggleGuard(null, null, null));
        Assertions.assertDoesNotThrow(() -> WolfGuardHelper.toggleGuardMode(null, null));
    }

    @Test
    @DisplayName("BL-BD-048: Leashed guard dog suspends WolfGuardGoal patrol, preserves guard post, and resumes on unleash")
    public void testLeashedGuardGoalSuspendsPatrolAndPreservesGuardPost() {
        BlockPos guardPost = new BlockPos(10, 64, 10);
        StubWolf wolf = createStubWolf(true, true, guardPost, WolfPersonality.AGGRESSIVE);
        WolfGuardGoal goal = new WolfGuardGoal(wolf);

        // 1. Guarding + unleashed -> patrol allowed
        wolf.setLeashedStub(false);
        Assertions.assertTrue(goal.canUse(), "Unleashed guard dog must allow WolfGuardGoal.canUse()");
        Assertions.assertTrue(goal.canContinueToUse(), "Unleashed guard dog must allow WolfGuardGoal.canContinueToUse()");

        // 2. Guarding + leashed -> patrol suspended, guardMode & guardPos preserved
        wolf.setLeashedStub(true);
        Assertions.assertFalse(goal.canUse(), "Leashed guard dog must suppress WolfGuardGoal.canUse()");
        Assertions.assertFalse(goal.canContinueToUse(), "Leashed guard dog must suppress WolfGuardGoal.canContinueToUse()");
        Assertions.assertTrue(wolf.betterdogs$isGuardMode(), "Leashing must preserve guardMode == true");
        Assertions.assertEquals(guardPost, wolf.betterdogs$getGuardPos(), "Leashing must preserve guardPos");

        // 3. stop() clears navigation without wiping guard post
        wolf.stubNav.done = false;
        goal.stop();
        Assertions.assertTrue(wolf.stubNav.isDone(), "WolfGuardGoal.stop() must clear navigation");
        Assertions.assertTrue(wolf.betterdogs$isGuardMode(), "WolfGuardGoal.stop() must preserve guardMode");
        Assertions.assertEquals(guardPost, wolf.betterdogs$getGuardPos(), "WolfGuardGoal.stop() must preserve guardPos");

        // 4. Unleashed again -> patrol resumes around preserved post
        wolf.setLeashedStub(false);
        Assertions.assertTrue(goal.canUse(), "Unleashed guard dog must immediately resume patrol");
        Assertions.assertTrue(goal.canContinueToUse(), "Unleashed guard dog must continue patrol");
    }

    @Test
    @DisplayName("BL-BD-048: Watchdog alarm polarity matrix for leashed Pacifist guard dogs")
    public void testWatchdogAlarmPolarityMatrix() {
        Assertions.assertTrue(
                WolfGuardHelper.shouldRunLeashedWatchdogAlarm(true, true, false, true, WolfPersonality.PACIFIST),
                "Tame, guarding, standing, leashed PACIFIST dog must run stationary watchdog alarm");
        Assertions.assertFalse(
                WolfGuardHelper.shouldRunLeashedWatchdogAlarm(true, true, false, true, WolfPersonality.AGGRESSIVE),
                "AGGRESSIVE guard dog must not run pacifist watchdog alarm");
        Assertions.assertFalse(
                WolfGuardHelper.shouldRunLeashedWatchdogAlarm(true, true, false, true, WolfPersonality.NORMAL),
                "NORMAL guard dog must not run pacifist watchdog alarm");
        Assertions.assertFalse(
                WolfGuardHelper.shouldRunLeashedWatchdogAlarm(true, true, false, false, WolfPersonality.PACIFIST),
                "Unleashed PACIFIST guard dog uses WolfGuardGoal alarm, not leashed fallback");
        Assertions.assertFalse(
                WolfGuardHelper.shouldRunLeashedWatchdogAlarm(true, true, true, true, WolfPersonality.PACIFIST),
                "Manually sitting PACIFIST guard dog must not run watchdog alarm");
        Assertions.assertFalse(
                WolfGuardHelper.shouldRunLeashedWatchdogAlarm(true, false, false, true, WolfPersonality.PACIFIST),
                "Non-guarding leashed PACIFIST dog must not run watchdog alarm");
        Assertions.assertFalse(
                WolfGuardHelper.shouldRunLeashedWatchdogAlarm(false, true, false, true, WolfPersonality.PACIFIST),
                "Untamed wolf must never run watchdog alarm");
        Assertions.assertFalse(
                WolfGuardHelper.shouldRunLeashedWatchdogAlarm(true, true, false, true, null),
                "Null personality must return false");
    }

    private static final class StubNavigation extends GroundPathNavigation {
        boolean done = true;

        private StubNavigation() {
            super(null, null);
        }

        @Override public boolean isDone() { return this.done; }
        @Override public void stop() { this.done = true; }
        @Override public boolean moveTo(double x, double y, double z, double speed) { this.done = false; return true; }
        @Override public boolean moveTo(Entity entity, double speed) { this.done = false; return true; }
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
        private boolean tame;
        private boolean leashed;
        private boolean sittingPose;
        private boolean orderedToSit;
        private boolean guardMode;
        private boolean sittingManually;
        private @Nullable BlockPos guardPos;
        private @Nullable LivingEntity target;
        private WolfPersonality personality;
        private RandomSource random;
        StubNavigation stubNav;
        private StubLookControl stubLook;
        private StubJumpControl stubJump;

        private StubWolf() {
            super(null, null);
        }

        void init(UUID uuid, boolean tame, boolean guardMode, @Nullable BlockPos guardPos, WolfPersonality personality) {
            this.uuid = uuid;
            this.tame = tame;
            this.guardMode = guardMode;
            this.guardPos = guardPos;
            this.personality = personality;
            this.random = RandomSource.create(12345L);
            try {
                this.stubNav = (StubNavigation) UNSAFE.allocateInstance(StubNavigation.class);
                this.stubNav.done = true;
                this.stubLook = (StubLookControl) UNSAFE.allocateInstance(StubLookControl.class);
                this.stubJump = (StubJumpControl) UNSAFE.allocateInstance(StubJumpControl.class);
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

        void setLeashedStub(boolean v) { this.leashed = v; }

        @Override public UUID getUUID() { return this.uuid; }
        @Override public String getStringUUID() { return this.uuid.toString(); }
        @Override public EntityType<?> getType() { return EntityType.WOLF; }
        @Override public @Nullable Level level() { return null; }
        @Override public RandomSource getRandom() { return this.random; }
        @Override public Component getName() { return Component.literal("StubWolf"); }
        @Override public SoundSource getSoundSource() { return SoundSource.NEUTRAL; }
        @Override public float getPathfindingMalus(PathType pathType) { return 0.0f; }
        @Override public void setPathfindingMalus(PathType pathType, float cost) {}
        @Override public boolean isTame() { return this.tame; }
        @Override public boolean isLeashed() { return this.leashed; }
        @Override public boolean isInSittingPose() { return this.sittingPose; }
        @Override public void setInSittingPose(boolean pose) { this.sittingPose = pose; }
        @Override public boolean isOrderedToSit() { return this.orderedToSit; }
        @Override public void setOrderedToSit(boolean ordered) { this.orderedToSit = ordered; }
        @Override public @Nullable LivingEntity getTarget() { return this.target; }
        @Override public void setTarget(@Nullable LivingEntity target) { this.target = target; }
        @Override public PathNavigation getNavigation() { return this.stubNav; }
        @Override public LookControl getLookControl() { return this.stubLook; }
        @Override public JumpControl getJumpControl() { return this.stubJump; }
        @Override public @Nullable EntityReference<LivingEntity> getOwnerReference() { return null; }
        @Override public @Nullable LivingEntity getOwner() { return null; }

        @Override public WolfPersonality betterdogs$getPersonality() { return this.personality; }
        @Override public void betterdogs$setPersonality(WolfPersonality p) { this.personality = p; }
        @Override public boolean betterdogs$hasPersonality() { return this.personality != null; }
        @Override public CanineArchetype betterdogs$getCanineArchetype() { return CanineArchetype.GUARDIAN; }
        @Override public void betterdogs$setCanineArchetype(CanineArchetype a) {}
        @Override public boolean betterdogs$isGuardMode() { return this.guardMode; }
        @Override public void betterdogs$setGuardMode(boolean g) { this.guardMode = g; }
        @Override public @Nullable BlockPos betterdogs$getGuardPos() { return this.guardPos; }
        @Override public void betterdogs$setGuardPos(@Nullable BlockPos pos) { this.guardPos = pos; }
        @Override public boolean betterdogs$isSittingManually() { return this.sittingManually; }
        @Override public void betterdogs$setSittingManually(boolean s) { this.sittingManually = s; }
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
