// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.WolfGuardGoal;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.util.WolfGuardHelper;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

public class GuardModePatrolTest {

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
    }

    @Test
    public void testGuardRadii() {
        assertEquals(12, WolfGuardHelper.getPatrolRadius(WolfPersonality.AGGRESSIVE));
        assertEquals(4, WolfGuardHelper.getPatrolRadius(WolfPersonality.PACIFIST));
        assertEquals(8, WolfGuardHelper.getPatrolRadius(WolfPersonality.NORMAL));
    }

    @Test
    public void testGuardPersistence() {
        CompoundTag tag = new CompoundTag();
        BlockPos post = new BlockPos(100, 64, -200);

        // Guarding active
        WolfPersistentData.writeToNbt(tag, WolfPersonality.AGGRESSIVE, 1.0f, 12345L, "minecraft:beef", 5000L, "minecraft:creeper", 10000L, null, null, false, true, post);
        assertTrue(WolfPersistentData.readIsGuardingFromNbt(tag));
        assertEquals(post, WolfPersistentData.readGuardPosFromNbt(tag));

        // Guarding deactivated
        WolfPersistentData.writeToNbt(tag, WolfPersonality.AGGRESSIVE, 1.0f, 12345L, "minecraft:beef", 5000L, "minecraft:creeper", 10000L, null, null, false, false, null);
        assertFalse(WolfPersistentData.readIsGuardingFromNbt(tag));
    }

    @Test
    public void testLeashInterruptsGuardPatrolWithoutClearingGuardAnchorAndStopsNavigation() {
        BlockPos guardPost = new BlockPos(10, 64, 10);
        StubWolf wolf = createStubWolf(UUID.randomUUID(), true, WolfPersonality.AGGRESSIVE, guardPost);
        WolfGuardGoal goal = new WolfGuardGoal(wolf);

        // 1. Unleashed standing guard dog can start and continue patrolling
        assertTrue(goal.canUse(), "Unleashed guarding dog must be able to start patrolling");
        assertTrue(goal.canContinueToUse(), "Unleashed guarding dog must continue patrolling");
        goal.start();
        goal.tick();
        assertFalse(wolf.stubNav.isDone(), "Patrol tick must initiate navigation movement");

        // 2. Leashing mid-patrol blocks canUse/canContinueToUse while preserving guard anchor
        wolf.setLeashedStub(true);
        assertFalse(goal.canUse(), "Leashed dog must not start guard patrol");
        assertFalse(goal.canContinueToUse(), "Leashed dog must immediately cease active guard patrol");
        assertTrue(wolf.betterdogs$isGuarding(), "Leashing must preserve betterdogs$isGuarding() state");
        assertEquals(guardPost, wolf.betterdogs$getGuardPos(), "Leashing must preserve guard anchor position");

        // 3. Goal stop() clears active patrol navigation
        int stopCallsBefore = wolf.stubNav.stopCallCount;
        goal.stop();
        assertEquals(stopCallsBefore + 1, wolf.stubNav.stopCallCount, "WolfGuardGoal.stop() must call navigation.stop()");
        assertTrue(wolf.stubNav.isDone(), "Navigation must be marked done after WolfGuardGoal.stop()");

        // 4. Unleashing restores patrol eligibility cleanly
        wolf.setLeashedStub(false);
        assertTrue(goal.canUse(), "Unleashing must allow guard patrol to resume automatically");
        assertTrue(goal.canContinueToUse(), "Unleashing must allow guard patrol continuation");
    }

    private static StubWolf createStubWolf(UUID uuid, boolean tame, WolfPersonality personality, BlockPos guardPos) {
        try {
            StubWolf wolf = (StubWolf) unsafe.allocateInstance(StubWolf.class);
            wolf.init(uuid, tame, personality, guardPos);
            return wolf;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubWolf", e);
        }
    }

    private static final class StubNavigation extends GroundPathNavigation {
        boolean done = true;
        int stopCallCount = 0;

        private StubNavigation() {
            super(null, null);
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public void stop() {
            this.stopCallCount++;
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
        private boolean alive;
        private boolean sittingPose;
        private boolean leashed;
        private boolean guarding;
        private @Nullable BlockPos guardPos;
        private WolfPersonality personality;
        private RandomSource random;
        StubNavigation stubNav;
        private StubLookControl stubLook;
        private StubJumpControl stubJump;

        private StubWolf() {
            super(null, null);
        }

        void init(UUID uuid, boolean tame, WolfPersonality personality, BlockPos guardPos) {
            this.uuid = uuid;
            this.tame = tame;
            this.alive = true;
            this.sittingPose = false;
            this.leashed = false;
            this.guarding = true;
            this.guardPos = guardPos;
            this.personality = personality;
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
                ENTITY_POSITION_FIELD.set(this, new Vec3(guardPos.getX() + 0.5, guardPos.getY(), guardPos.getZ() + 0.5));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to initialize Entity.position", e);
            }
        }

        void setLeashedStub(boolean leashed) { this.leashed = leashed; }

        @Override public UUID getUUID() { return this.uuid; }
        @Override public String getStringUUID() { return this.uuid.toString(); }
        @Override public EntityType<?> getType() { return EntityType.WOLF; }
        @Override public Level level() { return null; }
        @Override public RandomSource getRandom() { return this.random; }
        @Override public BlockPos blockPosition() { return this.guardPos != null ? this.guardPos : BlockPos.ZERO; }
        @Override public float getPathfindingMalus(PathType pathType) { return 0.0f; }
        @Override public void setPathfindingMalus(PathType pathType, float cost) {}
        @Override public Component getName() { return Component.literal("StubGuardWolf"); }
        @Override public SoundSource getSoundSource() { return SoundSource.NEUTRAL; }
        @Override public boolean isAlive() { return this.alive; }
        @Override public boolean isTame() { return this.tame; }
        @Override public boolean isInSittingPose() { return this.sittingPose; }
        @Override public boolean isLeashed() { return this.leashed; }
        @Override public boolean mayBeLeashed() { return this.leashed; }
        @Override public PathNavigation getNavigation() { return this.stubNav; }
        @Override public LookControl getLookControl() { return this.stubLook; }
        @Override public JumpControl getJumpControl() { return this.stubJump; }
        @Override public @Nullable EntityReference<LivingEntity> getOwnerReference() { return null; }
        @Override public @Nullable LivingEntity getOwner() { return null; }
        @Override public boolean isOwnedBy(LivingEntity entity) { return false; }

        @Override public WolfPersonality betterdogs$getPersonality() { return this.personality; }
        @Override public void betterdogs$setPersonality(WolfPersonality p) { this.personality = p; }
        @Override public boolean betterdogs$hasPersonality() { return this.personality != null; }
        @Override public CanineArchetype betterdogs$getCanineArchetype() { return CanineArchetype.GUARDIAN; }
        @Override public boolean betterdogs$isGuarding() { return this.guarding; }
        @Override public void betterdogs$setGuarding(boolean g) { this.guarding = g; }
        @Override public @Nullable BlockPos betterdogs$getGuardPos() { return this.guardPos; }
        @Override public void betterdogs$setGuardPos(@Nullable BlockPos pos) { this.guardPos = pos; }
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
    }
}
