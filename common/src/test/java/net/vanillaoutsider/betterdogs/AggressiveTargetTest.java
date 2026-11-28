// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.sensing.Sensing;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.vanillaoutsider.betterdogs.ai.AggressiveTargetGoal;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

public class AggressiveTargetTest {

    private static final Unsafe unsafe = initUnsafe();
    private static long entityPositionOffset;

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
    static void setupMinecraftBootstrap() throws Exception {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
        try {
            if (BetterDogsGameRules.BD_AGGRO_DETECT_RANGE == null) {
                BetterDogsGameRules.register();
            }
        } catch (Throwable ignored) {
        }
        Field posField = Entity.class.getDeclaredField("position");
        entityPositionOffset = unsafe.objectFieldOffset(posField);
    }

    private static GameRules createAggroGameRules() {
        GameRules.Key<GameRules.IntegerValue> aggroRangeKey = BetterDogsGameRules.BD_AGGRO_DETECT_RANGE;
        return new GameRules() {
            @Override
            public int getInt(GameRules.Key<GameRules.IntegerValue> key) {
                if (aggroRangeKey != null && aggroRangeKey.equals(key)) {
                    return 16;
                }
                return super.getInt(key);
            }
        };
    }

    @Test
    public void testLeashedWolfBlockedAcrossIsEligibleTargetCanUseAndCanContinueToUse() {
        GameRules rules = createAggroGameRules();

        UUID ownerUuid = UUID.randomUUID();
        StubOwnerPlayer owner = createStubPlayer(ownerUuid, new Vec3(0.0, 64.0, 0.0));
        StubMonster zombie = createStubMonster(new Vec3(4.0, 64.0, 0.0));
        StubServerLevel level = createStubServerLevel(rules, List.of(zombie));

        StubWolf wolf = createStubWolf(UUID.randomUUID(), ownerUuid, true, WolfPersonality.AGGRESSIVE);
        wolf.attachLevel(level);
        wolf.attachLiveOwner(owner);

        AggressiveTargetGoal goal = new AggressiveTargetGoal(wolf);

        // Positive baseline: unleashed aggressive tamed wolf with nearby monster is eligible & canUse()
        assertTrue(AggressiveTargetGoal.isEligibleTarget(wolf, zombie),
                "Unleashed aggressive wolf must treat nearby Monster as eligible target");
        assertTrue(goal.canUse(),
                "Unleashed aggressive wolf must activate AggressiveTargetGoal when eligible Monster is near owner");
        goal.start();
        assertSame(zombie, wolf.getTarget(), "AggressiveTargetGoal.start() must assign target to wolf");
        assertTrue(goal.canContinueToUse(),
                "Unleashed aggressive wolf must continue targeting eligible Monster");

        // Negative leash polarity: leashing blocks isEligibleTarget, canUse, and canContinueToUse immediately
        wolf.setLeashedStub(true);
        assertFalse(AggressiveTargetGoal.isEligibleTarget(wolf, zombie),
                "isEligibleTarget must return false when wolf is leashed");
        assertFalse(goal.canUse(),
                "canUse must return false when wolf is leashed");
        assertFalse(goal.canContinueToUse(),
                "canContinueToUse must return false immediately when wolf is leashed mid-chase");

        goal.stop();
        assertNull(wolf.getTarget(), "AggressiveTargetGoal.stop() must clear wolf target");

        // Unleashing restores eligibility
        wolf.setLeashedStub(false);
        assertTrue(AggressiveTargetGoal.isEligibleTarget(wolf, zombie),
                "Unleashing must restore target eligibility");
        assertTrue(goal.canUse(),
                "Unleashing must restore AggressiveTargetGoal.canUse()");
    }

    @Test
    public void testCreeperAndGhastStayExcludedAndNullWolfSafe() {
        StubCreeper creeper = createStubCreeper();
        StubGhast ghast = createStubGhast();
        StubWolf wolf = createStubWolf(UUID.randomUUID(), UUID.randomUUID(), true, WolfPersonality.AGGRESSIVE);

        assertFalse(AggressiveTargetGoal.isEligibleTarget(wolf, creeper),
                "AggressiveTargetGoal must never target Creepers");
        assertFalse(AggressiveTargetGoal.isEligibleTarget(wolf, ghast),
                "AggressiveTargetGoal must never target Ghasts");
        assertFalse(AggressiveTargetGoal.isEligibleTarget(wolf, null),
                "AggressiveTargetGoal must safely reject null target");
    }

    private static StubServerLevel createStubServerLevel(GameRules rules, List<Monster> monsters) {
        try {
            StubServerLevel level = (StubServerLevel) unsafe.allocateInstance(StubServerLevel.class);
            level.init(rules, monsters);
            return level;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubServerLevel", e);
        }
    }

    private static StubOwnerPlayer createStubPlayer(UUID uuid, Vec3 pos) {
        try {
            StubOwnerPlayer player = (StubOwnerPlayer) unsafe.allocateInstance(StubOwnerPlayer.class);
            player.init(uuid, pos);
            return player;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubOwnerPlayer", e);
        }
    }

    private static StubMonster createStubMonster(Vec3 pos) {
        try {
            StubMonster monster = (StubMonster) unsafe.allocateInstance(StubMonster.class);
            monster.init(pos);
            return monster;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubMonster", e);
        }
    }

    private static StubCreeper createStubCreeper() {
        try {
            StubCreeper creeper = (StubCreeper) unsafe.allocateInstance(StubCreeper.class);
            creeper.init();
            return creeper;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubCreeper", e);
        }
    }

    private static StubGhast createStubGhast() {
        try {
            StubGhast ghast = (StubGhast) unsafe.allocateInstance(StubGhast.class);
            ghast.init();
            return ghast;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubGhast", e);
        }
    }

    private static StubWolf createStubWolf(UUID uuid, UUID ownerUuid, boolean tame, WolfPersonality personality) {
        try {
            StubWolf wolf = (StubWolf) unsafe.allocateInstance(StubWolf.class);
            wolf.init(uuid, ownerUuid, tame, personality);
            return wolf;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubWolf", e);
        }
    }

    private static final class StubServerLevel extends ServerLevel {
        private GameRules gameRules;
        private List<Monster> monsters;

        private StubServerLevel() {
            super(null, null, null, null, null, null, null, false, 0L, null, false, null);
        }

        void init(GameRules gameRules, List<Monster> monsters) {
            this.gameRules = gameRules;
            this.monsters = monsters;
        }

        @Override
        public boolean isClientSide() {
            return false;
        }

        @Override
        public GameRules getGameRules() {
            return this.gameRules;
        }

        @Override
        public <T extends Entity> List<T> getEntitiesOfClass(Class<T> clazz, AABB aabb, Predicate<? super T> predicate) {
            List<T> matched = new ArrayList<>();
            if (this.monsters != null) {
                for (Monster m : this.monsters) {
                    if (clazz.isInstance(m)) {
                        T cast = clazz.cast(m);
                        if (predicate.test(cast)) {
                            matched.add(cast);
                        }
                    }
                }
            }
            return matched;
        }
    }

    private static final class StubOwnerPlayer extends ServerPlayer {
        private UUID uuid;

        private StubOwnerPlayer() {
            super(null, null, null, null);
        }

        void init(UUID uuid, Vec3 pos) {
            this.uuid = uuid;
            unsafe.putObject(this, entityPositionOffset, pos);
            this.setBoundingBox(new AABB(pos.x - 0.3, pos.y, pos.z - 0.3, pos.x + 0.3, pos.y + 1.8, pos.z + 0.3));
        }

        @Override public UUID getUUID() { return this.uuid; }
        @Override public boolean isAlive() { return true; }
        @Override public EntityType<?> getType() { return EntityType.PLAYER; }
        @Override public @Nullable PlayerTeam getTeam() { return null; }
    }

    private static final class StubMonster extends Monster {
        private UUID uuid;

        private StubMonster() {
            super(null, null);
        }

        void init(Vec3 pos) {
            this.uuid = UUID.randomUUID();
            unsafe.putObject(this, entityPositionOffset, pos);
            this.setBoundingBox(new AABB(pos.x - 0.3, pos.y, pos.z - 0.3, pos.x + 0.3, pos.y + 1.95, pos.z + 0.3));
        }

        @Override public UUID getUUID() { return this.uuid; }
        @Override public boolean isAlive() { return true; }
        @Override public boolean isInvisible() { return false; }
        @Override public boolean isDiscrete() { return false; }
        @Override public boolean isShiftKeyDown() { return false; }
        @Override public double getVisibilityPercent(@Nullable Entity lookingEntity) { return 1.0D; }
        @Override public boolean isInvulnerable() { return false; }
        @Override public boolean isAttackable() { return true; }
        @Override public boolean skipAttackInteraction(Entity entity) { return false; }
        @Override public boolean canBeSeenAsEnemy() { return true; }
        @Override public boolean canBeSeenByAnyone() { return true; }
        @Override public @Nullable PlayerTeam getTeam() { return null; }
        @Override public EntityType<?> getType() { return EntityType.ZOMBIE; }
        @Override public BlockPos blockPosition() { return BlockPos.containing(this.position()); }
    }

    private static final class StubCreeper extends Creeper {
        private StubCreeper() {
            super(null, null);
        }

        void init() {}

        @Override public boolean isAlive() { return true; }
    }

    private static final class StubGhast extends Ghast {
        private StubGhast() {
            super(null, null);
        }

        void init() {}

        @Override public boolean isAlive() { return true; }
    }

    private static final class StubSensing extends Sensing {
        private StubSensing() {
            super(null);
        }

        @Override
        public boolean hasLineOfSight(Entity entity) {
            return true;
        }
    }

    private static final class ZeroRandomSource implements RandomSource {
        @Override public RandomSource fork() { return this; }
        @Override public net.minecraft.world.level.levelgen.PositionalRandomFactory forkPositional() { return null; }
        @Override public void setSeed(long seed) {}
        @Override public int nextInt() { return 0; }
        @Override public int nextInt(int bound) { return 0; }
        @Override public long nextLong() { return 0L; }
        @Override public boolean nextBoolean() { return false; }
        @Override public float nextFloat() { return 0.0f; }
        @Override public double nextDouble() { return 0.0; }
        @Override public double nextGaussian() { return 0.0; }
    }

    private static final class StubWolf extends Wolf implements WolfExtensions {
        private UUID uuid;
        private @Nullable UUID ownerUuid;
        private @Nullable LivingEntity liveOwner;
        private boolean tame;
        private boolean alive;
        private boolean orderedToSit;
        private boolean leashed;
        private WolfPersonality personality;
        private @Nullable Level level;
        private @Nullable LivingEntity target;
        private StubSensing stubSensing;
        private RandomSource zeroRandom;

        private StubWolf() {
            super(null, null);
        }

        void init(UUID uuid, @Nullable UUID ownerUuid, boolean tame, WolfPersonality personality) {
            this.uuid = uuid;
            this.ownerUuid = ownerUuid;
            this.tame = tame;
            this.alive = true;
            this.orderedToSit = false;
            this.leashed = false;
            this.personality = personality;
            this.zeroRandom = new ZeroRandomSource();
            try {
                this.stubSensing = (StubSensing) unsafe.allocateInstance(StubSensing.class);
            } catch (InstantiationException e) {
                throw new IllegalStateException("Failed to allocate StubSensing", e);
            }
            Vec3 pos = new Vec3(0.5, 64.0, 0.5);
            unsafe.putObject(this, entityPositionOffset, pos);
            this.setBoundingBox(new AABB(0.0, 64.0, 0.0, 0.6, 64.85, 0.6));
        }

        void attachLevel(@Nullable Level level) { this.level = level; }
        void attachLiveOwner(@Nullable LivingEntity owner) { this.liveOwner = owner; }
        void setLeashedStub(boolean leashed) { this.leashed = leashed; }

        @Override public UUID getUUID() { return this.uuid; }
        @Override public String getStringUUID() { return this.uuid.toString(); }
        @Override public EntityType<?> getType() { return EntityType.WOLF; }
        @Override public Level level() { return this.level; }
        @Override public RandomSource getRandom() { return this.zeroRandom; }
        @Override public BlockPos blockPosition() { return BlockPos.containing(this.position()); }
        @Override public boolean hasRestriction() { return false; }
        @Override public BlockPos getRestrictCenter() { return BlockPos.ZERO; }
        @Override public float getRestrictRadius() { return -1.0f; }
        @Override public boolean isWithinRestriction() { return true; }
        @Override public boolean isWithinRestriction(BlockPos pos) { return true; }
        @Override public Component getName() { return Component.literal("StubAggressiveWolf"); }
        @Override public SoundSource getSoundSource() { return SoundSource.NEUTRAL; }
        @Override public boolean isAlive() { return this.alive; }
        @Override public boolean isTame() { return this.tame; }
        @Override public boolean isOrderedToSit() { return this.orderedToSit; }
        @Override public boolean isLeashed() { return this.leashed; }
        @Override public boolean mayBeLeashed() { return this.leashed; }
        @Override public boolean isDiscrete() { return false; }
        @Override public boolean isShiftKeyDown() { return false; }
        @Override public boolean isInvisible() { return false; }
        @Override public boolean isSpectator() { return false; }
        @Override public @Nullable PlayerTeam getTeam() { return null; }
        @Override public Sensing getSensing() { return this.stubSensing; }
        @Override public boolean hasLineOfSight(Entity entity) { return true; }
        @Override public boolean canAttack(LivingEntity target) { return target != null && target.isAlive(); }
        @Override public double getAttributeValue(Holder<Attribute> attribute) {
            return Attributes.FOLLOW_RANGE.equals(attribute) ? 16.0D : 1.0D;
        }
        @Override public @Nullable LivingEntity getTarget() { return this.target; }
        @Override public void setTarget(@Nullable LivingEntity target) { this.target = target; }
        @Override public @Nullable UUID getOwnerUUID() { return this.ownerUuid; }
        @Override public @Nullable LivingEntity getOwner() { return this.liveOwner; }
        @Override public boolean isOwnedBy(LivingEntity entity) {
            return entity != null && (entity == this.liveOwner || (this.ownerUuid != null && this.ownerUuid.equals(entity.getUUID())));
        }

        @Override public WolfPersonality betterdogs$getPersonality() { return this.personality; }
        @Override public void betterdogs$setPersonality(WolfPersonality p) { this.personality = p; }
        @Override public boolean betterdogs$hasPersonality() { return this.personality != null; }
        @Override public CanineArchetype betterdogs$getCanineArchetype() { return CanineArchetype.GUARDIAN; }
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
        @Override public boolean betterdogs$isGuarding() { return false; }
        @Override public void betterdogs$setGuarding(boolean guarding) {}
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
    }
}
