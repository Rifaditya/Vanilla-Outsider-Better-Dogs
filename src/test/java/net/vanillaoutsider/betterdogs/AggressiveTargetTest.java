// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import java.lang.reflect.Field;
import java.util.UUID;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.AggressiveTargetGoal;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 26: Aggressive Autonomous Stalking AI Tests")
class AggressiveTargetTest {

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
    static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private static StubWolf createStubWolf(boolean guardMode, @Nullable BlockPos guardPos, WolfPersonality personality) {
        try {
            StubWolf wolf = (StubWolf) UNSAFE.allocateInstance(StubWolf.class);
            wolf.init(UUID.randomUUID(), guardMode, guardPos, personality);
            return wolf;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubWolf", e);
        }
    }

    private static StubZombie createStubZombie(double x, double y, double z) {
        try {
            StubZombie zombie = (StubZombie) UNSAFE.allocateInstance(StubZombie.class);
            zombie.init(x, y, z);
            return zombie;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubZombie", e);
        }
    }

    @Test
    @DisplayName("Assert 16-block detection perimeter math (d <= 16.0m, d^2 <= 256.0)")
    void testDetectionPerimeterMath() {
        double range = AggressiveTargetGoal.DEFAULT_DETECT_RANGE;
        assertEquals(16.0D, range, 0.001D);

        double rangeSq = range * range;
        assertEquals(256.0D, rangeSq, 0.001D);

        assertTrue(100.0D <= rangeSq); // 10 blocks away
        assertTrue(256.0D <= rangeSq); // Exactly 16 blocks away
        assertFalse(256.01D <= rangeSq); // Beyond 16 blocks
    }

    @Test
    @DisplayName("Assert guard mode expanded perimeter (24m for Aggressive, 16m for Normal)")
    void testGuardModePerimeter() {
        double aggroRange = AggressiveTargetGoal.GUARD_AGGRO_RANGE;
        double normalRange = AggressiveTargetGoal.GUARD_NORMAL_RANGE;

        assertEquals(24.0D, aggroRange, 0.001D);
        assertEquals(16.0D, normalRange, 0.001D);

        double aggroSq = aggroRange * aggroRange;
        assertEquals(576.0D, aggroSq, 0.001D);
    }

    @Test
    @DisplayName("Assert personality enum validity for Aggressive filtering")
    void testPersonalityFiltering() {
        assertEquals(WolfPersonality.AGGRESSIVE, WolfPersonality.valueOf("AGGRESSIVE"));
        assertNotEquals(WolfPersonality.AGGRESSIVE, WolfPersonality.PACIFIST);
        assertNotEquals(WolfPersonality.AGGRESSIVE, WolfPersonality.NORMAL);
    }

    @Test
    @DisplayName("Assert null safety across target eligibility helpers")
    void testTargetEligibilityNullSafety() {
        assertFalse(AggressiveTargetGoal.isEligibleTarget(null, null));
    }

    @Test
    @DisplayName("BL-BD-048: Leashed wolves suppress AggressiveTargetGoal eligibility, canUse, and canContinueToUse")
    void testLeashedSuppressionAcrossGuardingAndNonGuardingWolves() {
        BlockPos guardPos = new BlockPos(10, 64, 10);
        StubZombie nearbyTarget = createStubZombie(12.5, 64.0, 10.5);

        // 1. Guarding wolf: unleashed eligible vs leashed suppressed
        StubWolf guardingWolf = createStubWolf(true, guardPos, WolfPersonality.AGGRESSIVE);
        guardingWolf.setTarget(nearbyTarget);
        AggressiveTargetGoal guardTargetGoal = new AggressiveTargetGoal(guardingWolf);

        guardingWolf.setLeashedStub(false);
        assertTrue(AggressiveTargetGoal.isEligibleTarget(guardingWolf, nearbyTarget),
                "Unleashed guarding wolf must find nearby hostile target eligible");

        guardingWolf.setLeashedStub(true);
        assertFalse(AggressiveTargetGoal.isEligibleTarget(guardingWolf, nearbyTarget),
                "Leashed guarding wolf must reject target in isEligibleTarget");
        assertFalse(guardTargetGoal.canUse(),
                "Leashed guarding wolf must return false in AggressiveTargetGoal.canUse()");
        assertFalse(guardTargetGoal.canContinueToUse(),
                "Leashed guarding wolf must return false in AggressiveTargetGoal.canContinueToUse()");

        // 2. Non-guarding wolf: leashed suppressed
        StubWolf roamingWolf = createStubWolf(false, null, WolfPersonality.AGGRESSIVE);
        roamingWolf.setTarget(nearbyTarget);
        AggressiveTargetGoal roamingTargetGoal = new AggressiveTargetGoal(roamingWolf);

        roamingWolf.setLeashedStub(false);
        assertTrue(AggressiveTargetGoal.isEligibleTarget(roamingWolf, nearbyTarget),
                "Unleashed non-guarding wolf without owner anchor fallback must find target eligible");

        roamingWolf.setLeashedStub(true);
        assertFalse(AggressiveTargetGoal.isEligibleTarget(roamingWolf, nearbyTarget),
                "Leashed non-guarding wolf must reject target in isEligibleTarget");
        assertFalse(roamingTargetGoal.canUse(),
                "Leashed non-guarding wolf must return false in AggressiveTargetGoal.canUse()");
        assertFalse(roamingTargetGoal.canContinueToUse(),
                "Leashed non-guarding wolf must return false in AggressiveTargetGoal.canContinueToUse()");
    }

    private static final class StubNavigation extends GroundPathNavigation {
        private StubNavigation() { super(null, null); }
        @Override public boolean isDone() { return true; }
        @Override public void stop() {}
    }

    private static final class StubZombie extends Zombie {
        private double sx;
        private double sy;
        private double sz;

        private StubZombie() {
            super(null, null);
        }

        void init(double x, double y, double z) {
            this.sx = x;
            this.sy = y;
            this.sz = z;
        }

        @Override public boolean isAlive() { return true; }
        @Override public EntityType<? extends Zombie> getType() { return EntityTypes.ZOMBIE; }
        @Override public double distanceToSqr(double x, double y, double z) {
            double dx = this.sx - x;
            double dy = this.sy - y;
            double dz = this.sz - z;
            return dx * dx + dy * dy + dz * dz;
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
        private boolean leashed;
        private boolean sittingPose;
        private boolean orderedToSit;
        private boolean guardMode;
        private @Nullable BlockPos guardPos;
        private @Nullable LivingEntity target;
        private WolfPersonality personality;
        private RandomSource random;
        private StubNavigation stubNav;

        private StubWolf() {
            super(null, null);
        }

        void init(UUID uuid, boolean guardMode, @Nullable BlockPos guardPos, WolfPersonality personality) {
            this.uuid = uuid;
            this.guardMode = guardMode;
            this.guardPos = guardPos;
            this.personality = personality;
            this.random = RandomSource.create(12345L);
            try {
                this.stubNav = (StubNavigation) UNSAFE.allocateInstance(StubNavigation.class);
            } catch (InstantiationException e) {
                throw new IllegalStateException("Failed to allocate StubNavigation", e);
            }
            this.setBoundingBox(new AABB(10.0, 64.0, 10.0, 10.6, 64.85, 10.6));
            try {
                ENTITY_POSITION_FIELD.set(this, new Vec3(10.5, 64.0, 10.5));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to initialize Entity.position", e);
            }
        }

        void setLeashedStub(boolean v) { this.leashed = v; }

        @Override public UUID getUUID() { return this.uuid; }
        @Override public String getStringUUID() { return this.uuid.toString(); }
        @Override public EntityType<?> getType() { return EntityTypes.WOLF; }
        @Override public @Nullable Level level() { return null; }
        @Override public RandomSource getRandom() { return this.random; }
        @Override public float getPathfindingMalus(PathType pathType) { return 0.0f; }
        @Override public void setPathfindingMalus(PathType pathType, float cost) {}
        @Override public double getAttributeValue(Holder<Attribute> attribute) { return 16.0D; }
        @Override public boolean isTame() { return true; }
        @Override public int getAge() { return 0; }
        @Override public boolean isLeashed() { return this.leashed; }
        @Override public boolean isInSittingPose() { return this.sittingPose; }
        @Override public void setInSittingPose(boolean pose) { this.sittingPose = pose; }
        @Override public boolean isOrderedToSit() { return this.orderedToSit; }
        @Override public void setOrderedToSit(boolean ordered) { this.orderedToSit = ordered; }
        @Override public @Nullable LivingEntity getTarget() { return this.target; }
        @Override public void setTarget(@Nullable LivingEntity target) { this.target = target; }
        @Override public PathNavigation getNavigation() { return this.stubNav; }
        @Override public @Nullable LivingEntity getOwner() { return null; }

        @Override public WolfPersonality betterdogs$getPersonality() { return this.personality; }
        @Override public void betterdogs$setPersonality(WolfPersonality p) { this.personality = p; }
        @Override public boolean betterdogs$hasPersonality() { return this.personality != null; }
        @Override public boolean betterdogs$isGuardMode() { return this.guardMode; }
        @Override public void betterdogs$setGuardMode(boolean g) { this.guardMode = g; }
        @Override public @Nullable BlockPos betterdogs$getGuardPos() { return this.guardPos; }
        @Override public void betterdogs$setGuardPos(@Nullable BlockPos pos) { this.guardPos = pos; }
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
