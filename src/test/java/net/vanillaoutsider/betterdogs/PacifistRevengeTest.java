// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.ai.PacifistRevengeGoal;
import net.vanillaoutsider.betterdogs.util.PackAudienceHelper;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BL-BD-062: Pacifist Defensive Retaliation AI & HurtByTargetGoal Lifecycle Tests")
class PacifistRevengeTest {

    private static Unsafe unsafe;

    @BeforeAll
    static void setupMinecraftBootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        unsafe = (Unsafe) theUnsafe.get(null);
    }

    @Test
    @DisplayName("Assert PacifistRevengeGoal extends HurtByTargetGoal with 24m defense tether and 16m alert constants")
    void testDefensiveInheritanceAndRadiusConstants() {
        assertTrue(HurtByTargetGoal.class.isAssignableFrom(PacifistRevengeGoal.class),
                "PacifistRevengeGoal must extend HurtByTargetGoal rather than Step 25 fleeing Goal");
        assertEquals(24.0D, PacifistRevengeGoal.OWNER_DEFENSE_RADIUS, 0.001D);
        assertEquals(576.0D, PacifistRevengeGoal.OWNER_DEFENSE_RADIUS_SQR, 0.001D);
        assertEquals(16.0D, PacifistRevengeGoal.DEFENSE_ALERT_RADIUS, 0.001D);
    }

    @Test
    @DisplayName("Assert WolfAIMixin registers PacifistRevengeGoal in targetSelector priority 3 (not goalSelector)")
    void testWolfAIMixinTargetSelectorRegistration() throws IOException {
        Path root = findProjectRoot();
        String mixinSource = Files.readString(
                root.resolve("src/main/java/net/vanillaoutsider/betterdogs/mixin/WolfAIMixin.java"),
                StandardCharsets.UTF_8);
        assertTrue(mixinSource.contains("this.targetSelector.addGoal(3, new PacifistRevengeGoal(wolf));"),
                "WolfAIMixin must register PacifistRevengeGoal in targetSelector priority 3");
        assertFalse(mixinSource.contains("this.goalSelector.addGoal(2, new PacifistRevengeGoal(wolf));"),
                "Legacy Step 25 goalSelector registration for PacifistRevengeGoal must be removed");
    }

    @Test
    @DisplayName("Assert start() preserves owner-defense attacker when HurtByTargetGoal.start() would wipe null self-attacker")
    void testOwnerDefenseTargetSurvivesSuperStart() {
        UUID ownerUuid = UUID.randomUUID();
        StubOwnerPlayer owner = createStubPlayer(ownerUuid);
        StubOwnerPlayer attacker = createStubPlayer(UUID.randomUUID());
        StubWolf wolf = createStubWolf(ownerUuid, true, false, WolfPersonality.PACIFIST);
        wolf.attachLiveOwner(owner, 400.0D);
        owner.setLastHurtByMob(attacker);

        PacifistRevengeGoal goal = new PacifistRevengeGoal(wolf);
        assertTrue(goal.canUse(), "Standing Pacifist within 24m must activate when owner is struck");
        assertSame(attacker, wolf.getTarget(), "canUse() must stage the owner attacker on wolf");

        goal.start();
        assertSame(attacker, wolf.getTarget(),
                "start() must preserve owner's attacker instead of letting HurtByTargetGoal.start() wipe target to null");
        assertTrue(goal.canContinueToUse(), "canContinueToUse() must remain active within 24m tether");

        wolf.setDistanceToOwnerSqr(576.01D);
        assertFalse(goal.canContinueToUse(), "canContinueToUse() must disengage beyond 24.0m owner tether");
        goal.stop();
        assertNull(wolf.getTarget(), "stop() must clear wolf target cleanly");
    }

    @Test
    @DisplayName("Assert null safety across all goal lifecycle methods")
    void testNullSafety() {
        PacifistRevengeGoal goal = new PacifistRevengeGoal(null);
        assertFalse(goal.canUse());
        assertFalse(goal.canContinueToUse());
        assertDoesNotThrow(goal::start);
        assertDoesNotThrow(() -> goal.alertNearbyPackmates(null));
        assertDoesNotThrow(goal::stop);
    }

    @Test
    @DisplayName("Pillar 1 Fuzz: 10,000 randomized state combinations match PacifistRevengeGoal oracle invariants")
    void testTenThousandIterationStateFuzzOracle() {
        Random rng = new Random(2603062L);
        double[] distBuckets = new double[] {0.0D, 16.0D, 256.0D, 576.0D, 576.01D, 900.0D, 2304.0D};
        WolfPersonality[] personalities = WolfPersonality.values();

        for (int i = 0; i < 10_000; i++) {
            UUID ownerUuid = UUID.randomUUID();
            boolean tame = rng.nextInt(10) != 0;
            boolean alive = rng.nextInt(10) != 0;
            boolean baby = rng.nextInt(8) == 0;
            boolean orderedSit = rng.nextInt(8) == 0;
            boolean sitPose = rng.nextInt(8) == 0;
            boolean manualSit = rng.nextInt(8) == 0;
            boolean disciplined = rng.nextInt(8) == 0;
            WolfPersonality personality = personalities[rng.nextInt(personalities.length)];
            boolean ownerOnline = rng.nextBoolean();
            double ownerDistSq = distBuckets[rng.nextInt(distBuckets.length)];

            StubWolf wolf = createStubWolf(ownerUuid, tame, baby, personality);
            if (!alive) {
                wolf.setAliveStub(false);
            }
            wolf.setOrderedToSitStub(orderedSit);
            wolf.setSittingPoseStub(sitPose);
            wolf.betterdogs$setSittingManually(manualSit);
            wolf.betterdogs$setBeingDisciplined(disciplined);

            StubOwnerPlayer owner = ownerOnline ? createStubPlayer(ownerUuid) : null;
            wolf.attachLiveOwner(owner, ownerDistSq);

            LivingEntity selfAttacker = pickAttacker(rng, wolf, owner, ownerUuid);
            LivingEntity ownerAttacker = owner != null ? pickAttacker(rng, wolf, owner, ownerUuid) : null;
            wolf.setLastHurtByMob(selfAttacker);
            if (owner != null) {
                owner.setLastHurtByMob(ownerAttacker);
            }

            boolean eligibleDefender = alive && tame && !baby && !orderedSit && !sitPose && !manualSit
                    && personality == WolfPersonality.PACIFIST;
            boolean withinTether = owner == null || ownerDistSq <= PacifistRevengeGoal.OWNER_DEFENSE_RADIUS_SQR;
            boolean selfAttackerValid = isOracleValidTarget(wolf, owner, selfAttacker, disciplined);
            LivingEntity expectedTarget = selfAttackerValid ? selfAttacker
                    : (owner != null && isOracleValidTarget(wolf, owner, ownerAttacker, disciplined)
                            ? ownerAttacker : null);
            boolean expectedCanUse = eligibleDefender && withinTether && expectedTarget != null;

            PacifistRevengeGoal goal = new PacifistRevengeGoal(wolf);
            boolean actualCanUse = assertDoesNotThrow(goal::canUse, "Iteration " + i + " threw in canUse");
            assertEquals(expectedCanUse, actualCanUse, "canUse mismatch at fuzz iteration " + i);

            if (actualCanUse) {
                assertSame(expectedTarget, wolf.getTarget(), "Target mismatch after canUse at iteration " + i);
                assertDoesNotThrow(goal::start, "Iteration " + i + " threw in start");
                assertSame(expectedTarget, wolf.getTarget(), "Target lost during start at iteration " + i);
                assertTrue(goal.canContinueToUse(), "canContinueToUse must be true immediately after start at iteration " + i);
                assertDoesNotThrow(goal::stop, "Iteration " + i + " threw in stop");
                assertNull(wolf.getTarget(), "Target must be cleared after stop at iteration " + i);
            }
        }
    }

    private static @Nullable LivingEntity pickAttacker(Random rng, StubWolf selfWolf,
            @Nullable StubOwnerPlayer owner, UUID ownerUuid) {
        return switch (rng.nextInt(9)) {
            case 0 -> null;
            case 1 -> createStubPlayer(UUID.randomUUID());
            case 2 -> createStubWolf(null, false, false, WolfPersonality.NORMAL);
            case 3 -> createStubWolf(UUID.randomUUID(), true, false, WolfPersonality.NORMAL);
            case 4 -> createStubWolf(ownerUuid, true, false, WolfPersonality.NORMAL);
            case 5 -> createStubCreeper();
            case 6 -> selfWolf;
            case 7 -> owner;
            default -> {
                StubOwnerPlayer dead = createStubPlayer(UUID.randomUUID());
                dead.setAliveStub(false);
                yield dead;
            }
        };
    }

    private static boolean isOracleValidTarget(StubWolf wolf, @Nullable StubOwnerPlayer owner,
            @Nullable LivingEntity candidate, boolean disciplined) {
        if (candidate == null || candidate == wolf || candidate == owner || !candidate.isAlive()) {
            return false;
        }
        if (wolf.isOwnedBy(candidate) || candidate instanceof Creeper || disciplined) {
            return false;
        }
        if (candidate instanceof Wolf otherWolf && otherWolf.isTame()
                && PackAudienceHelper.isCoOwned(wolf, otherWolf)) {
            return false;
        }
        return true;
    }

    private static StubWolf createStubWolf(@Nullable UUID ownerUuid, boolean tame, boolean baby,
            WolfPersonality personality) {
        try {
            StubWolf stub = (StubWolf) unsafe.allocateInstance(StubWolf.class);
            stub.init(UUID.randomUUID(), ownerUuid, tame, baby, personality);
            return stub;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubWolf", e);
        }
    }

    private static StubOwnerPlayer createStubPlayer(UUID uuid) {
        try {
            StubOwnerPlayer stub = (StubOwnerPlayer) unsafe.allocateInstance(StubOwnerPlayer.class);
            stub.init(uuid);
            return stub;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubOwnerPlayer", e);
        }
    }

    private static StubCreeper createStubCreeper() {
        try {
            StubCreeper stub = (StubCreeper) unsafe.allocateInstance(StubCreeper.class);
            stub.init();
            return stub;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubCreeper", e);
        }
    }

    private static Path findProjectRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        Path cursor = dir;
        while (cursor != null) {
            if (Files.exists(cursor.resolve("src/main/java/net/vanillaoutsider/betterdogs/mixin/WolfAIMixin.java"))) {
                return cursor;
            }
            cursor = cursor.getParent();
        }
        return dir;
    }

    private static final class StubCreeper extends Creeper {
        private UUID uuid;

        private StubCreeper() {
            super(null, null);
        }

        void init() {
            this.uuid = UUID.randomUUID();
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public boolean isAlive() {
            return true;
        }
    }

    private static final class StubOwnerPlayer extends ServerPlayer {
        private UUID uuid;
        private boolean alive;
        private @Nullable LivingEntity lastHurtByMob;

        private StubOwnerPlayer() {
            super(null, null, null, null);
        }

        void init(UUID uuid) {
            this.uuid = uuid;
            this.alive = true;
        }

        void setAliveStub(boolean alive) {
            this.alive = alive;
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public boolean isAlive() {
            return this.alive;
        }

        @Override
        public @Nullable LivingEntity getLastHurtByMob() {
            return this.lastHurtByMob;
        }

        @Override
        public void setLastHurtByMob(@Nullable LivingEntity mob) {
            this.lastHurtByMob = mob;
        }

        @Override
        public EntityType<?> getType() {
            return EntityTypes.PLAYER;
        }
    }

    private static final class StubWolf extends Wolf implements WolfExtensions {
        private UUID uuid;
        private @Nullable UUID ownerUuid;
        private boolean tame;
        private boolean baby;
        private boolean alive;
        private boolean orderedToSit;
        private boolean sittingPose;
        private boolean sittingManually;
        private boolean beingDisciplined;
        private WolfPersonality personality;
        private @Nullable LivingEntity liveOwner;
        private double ownerDistanceSq;
        private @Nullable LivingEntity target;
        private @Nullable LivingEntity lastHurtByMob;

        private StubWolf() {
            super(null, null);
        }

        void init(UUID uuid, @Nullable UUID ownerUuid, boolean tame, boolean baby, WolfPersonality personality) {
            this.uuid = uuid;
            this.ownerUuid = ownerUuid;
            this.tame = tame;
            this.baby = baby;
            this.alive = true;
            this.personality = personality;
            this.ownerDistanceSq = 4.0D;
            this.setBoundingBox(new AABB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D));
        }

        void setAliveStub(boolean alive) {
            this.alive = alive;
        }

        void setOrderedToSitStub(boolean sit) {
            this.orderedToSit = sit;
        }

        void setSittingPoseStub(boolean sitPose) {
            this.sittingPose = sitPose;
        }

        void attachLiveOwner(@Nullable LivingEntity owner, double distanceSq) {
            this.liveOwner = owner;
            this.ownerDistanceSq = distanceSq;
        }

        void setDistanceToOwnerSqr(double distanceSq) {
            this.ownerDistanceSq = distanceSq;
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
        public boolean isAlive() {
            return this.alive;
        }

        @Override
        public boolean isTame() {
            return this.tame;
        }

        @Override
        public int getAge() {
            return this.baby ? -24000 : 0;
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
        public double distanceToSqr(Entity entity) {
            return entity == this.liveOwner ? this.ownerDistanceSq : 4.0D;
        }

        @Override
        public @Nullable LivingEntity getTarget() {
            return this.target;
        }

        @Override
        public void setTarget(@Nullable LivingEntity target) {
            this.target = target;
        }

        @Override
        public @Nullable LivingEntity getLastHurtByMob() {
            return this.lastHurtByMob;
        }

        @Override
        public void setLastHurtByMob(@Nullable LivingEntity mob) {
            this.lastHurtByMob = mob;
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
        public int betterdogs$getLastDamageTime() {
            return 0;
        }

        @Override
        public void betterdogs$setLastDamageTime(int time) {
        }

        @Override
        public boolean betterdogs$isSubmissive() {
            return false;
        }

        @Override
        public void betterdogs$setSubmissive(boolean submissive) {
        }

        @Override
        public String betterdogs$getBloodFeudTarget() {
            return "";
        }

        @Override
        public void betterdogs$setBloodFeudTarget(String targetUuid) {
        }

        @Override
        public boolean betterdogs$hasBloodFeud() {
            return false;
        }

        @Override
        public long betterdogs$getLastMischiefDay() {
            return 0L;
        }

        @Override
        public void betterdogs$setLastMischiefDay(long day) {
        }

        @Override
        public boolean betterdogs$isBeingDisciplined() {
            return this.beingDisciplined;
        }

        @Override
        public void betterdogs$setBeingDisciplined(boolean disciplined) {
            this.beingDisciplined = disciplined;
        }

        @Override
        public void betterdogs$setSocialState(@Nullable LivingEntity target, SocialAction action, int maxDurationTicks) {
        }

        @Override
        public @Nullable LivingEntity betterdogs$getSocialTarget() {
            return null;
        }

        @Override
        public SocialAction betterdogs$getSocialAction() {
            return SocialAction.NONE;
        }

        @Override
        public boolean betterdogs$isSocialModeActive() {
            return false;
        }

        @Override
        public void betterdogs$tickSocialMode() {
        }

        @Override
        public long betterdogs$getDNA() {
            return 0L;
        }

        @Override
        public void betterdogs$setDNA(long dna) {
        }

        @Override
        public float betterdogs$getSocialScale() {
            return 1.0F;
        }

        @Override
        public void betterdogs$setSocialScale(float scale) {
        }

        @Override
        public @Nullable EntitySocialScheduler betterdogs$getScheduler() {
            return null;
        }

        @Override
        public EntitySocialScheduler betterdogs$getOrInitializeScheduler() {
            return null;
        }

        @Override
        public void betterdogs$tickScheduler() {
        }

        @Override
        public int betterdogs$getAffinity(String targetUuid) {
            return 0;
        }

        @Override
        public void betterdogs$adjustAffinity(String targetUuid, int delta) {
        }

        @Override
        public @Nullable UUID betterdogs$getLeaderUuid() {
            return null;
        }

        @Override
        public void betterdogs$setLeaderUuid(@Nullable UUID uuid) {
        }

        @Override
        public boolean betterdogs$isGuardMode() {
            return false;
        }

        @Override
        public void betterdogs$setGuardMode(boolean guardMode) {
        }

        @Override
        public @Nullable BlockPos betterdogs$getGuardPos() {
            return null;
        }

        @Override
        public void betterdogs$setGuardPos(@Nullable BlockPos pos) {
        }

        @Override
        public boolean betterdogs$isSittingManually() {
            return this.sittingManually;
        }

        @Override
        public void betterdogs$setSittingManually(boolean sitting) {
            this.sittingManually = sitting;
        }

        @Override
        public boolean betterdogs$isAdoptable() {
            return false;
        }

        @Override
        public void betterdogs$setAdoptable(boolean adoptable) {
        }

        @Override
        public float betterdogs$getHealthBonus() {
            return 0.0F;
        }

        @Override
        public void betterdogs$setHealthBonus(float hp) {
        }

        @Override
        public float betterdogs$getDamageMod() {
            return 1.0F;
        }

        @Override
        public void betterdogs$setDamageMod(float dmg) {
        }

        @Override
        public float betterdogs$getSpeedMod() {
            return 1.0F;
        }

        @Override
        public void betterdogs$setSpeedMod(float speed) {
        }

        @Override
        public boolean betterdogs$areStatsRolled() {
            return false;
        }

        @Override
        public void betterdogs$setStatsRolled(boolean rolled) {
        }

        @Override
        public int betterdogs$getPushWaitTimer() {
            return 0;
        }

        @Override
        public void betterdogs$setPushWaitTimer(int ticks) {
        }

        @Override
        public @Nullable BlockPos betterdogs$getPathfindAvoidPos() {
            return null;
        }

        @Override
        public void betterdogs$setPathfindAvoidPos(@Nullable BlockPos pos) {
        }

        @Override
        public GoalSelector betterdogs$getGoalSelector() {
            return null;
        }

        @Override
        public long betterdogs$getSoothedTime() {
            return 0L;
        }

        @Override
        public void betterdogs$setSoothedTime(long time) {
        }

        @Override
        public @Nullable BlockPos betterdogs$getSoundLocationTarget() {
            return null;
        }

        @Override
        public void betterdogs$setSoundLocationTarget(@Nullable BlockPos pos) {
        }

        @Override
        public int betterdogs$getPassiveOverrideTicks() {
            return 0;
        }

        @Override
        public void betterdogs$setPassiveOverrideTicks(int ticks) {
        }

        @Override
        public String betterdogs$getNemesisEntityType() {
            return "";
        }

        @Override
        public void betterdogs$setNemesisEntityType(String type) {
        }

        @Override
        public long betterdogs$getNemesisExpiryTime() {
            return 0L;
        }

        @Override
        public void betterdogs$setNemesisExpiryTime(long time) {
        }

        @Override
        public boolean betterdogs$hasFetchedItem() {
            return false;
        }

        @Override
        public void betterdogs$setHasFetchedItem(boolean fetched) {
        }

        @Override
        public @Nullable ItemStack betterdogs$getFetchedItemStack() {
            return null;
        }

        @Override
        public void betterdogs$setFetchedItemStack(@Nullable ItemStack stack) {
        }

        @Override
        public int betterdogs$getZoomiesTicks() {
            return 0;
        }

        @Override
        public void betterdogs$setZoomiesTicks(int ticks) {
        }

        @Override
        public int betterdogs$getHowlingTicks() {
            return 0;
        }

        @Override
        public void betterdogs$setHowlingTicks(int ticks) {
        }
    }
}
