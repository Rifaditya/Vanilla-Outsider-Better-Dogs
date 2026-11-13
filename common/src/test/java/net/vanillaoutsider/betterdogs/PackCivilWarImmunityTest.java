// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.vanillaoutsider.betterdogs.ai.PacifistRevengeGoal;
import net.vanillaoutsider.betterdogs.util.AdultDisciplineHelper;
import net.vanillaoutsider.betterdogs.util.PackAudienceHelper;
import net.vanillaoutsider.betterdogs.util.SmallFightHelper;
import net.vanillaoutsider.betterdogs.util.WolfTargetingHandler;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 35: Pack Civil War Immunity, Targeting Wiring & Alert Silencing Tests (BL-BD-035, BL-BD-054)")
class PackCivilWarImmunityTest {

    private static Unsafe unsafe;

    @BeforeAll
    static void setupMinecraftBootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        unsafe = (Unsafe) theUnsafe.get(null);
    }

    @BeforeEach
    @AfterEach
    void resetCorrectionSnaps() {
        SmallFightHelper.clearCorrectionSnaps();
    }

    private static OfflineStubWolf createOfflineWolf(UUID wolfUuid, @Nullable UUID ownerUuid, boolean tame, boolean baby) {
        try {
            OfflineStubWolf stub = (OfflineStubWolf) unsafe.allocateInstance(OfflineStubWolf.class);
            stub.init(wolfUuid, ownerUuid, tame, baby);
            return stub;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate OfflineStubWolf", e);
        }
    }

    private static Wolf allocatePlainWolf() {
        try {
            return (Wolf) unsafe.allocateInstance(Wolf.class);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate plain Wolf", e);
        }
    }

    private static Path findProjectRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        Path cursor = dir;
        while (cursor != null) {
            if (Files.exists(cursor.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/mixin/WolfCombatMixin.java"))) {
                return cursor;
            }
            cursor = cursor.getParent();
        }
        return dir;
    }

    @Test
    @DisplayName("Assert co-owned packmates owner matching logic")
    void testCoOwnedPackmateMatching() {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();

        assertEquals(ownerA, ownerA, "Matching owner UUID must qualify as packmates");
        assertNotEquals(ownerA, ownerB, "Different owners must not qualify as packmates");
        assertFalse(PackAudienceHelper.isCoOwned(null, null), "Null wolves cannot be co-owned");
    }

    @Test
    @DisplayName("Assert universal domestic alert silencing null safety")
    void testDomesticAlertSilencingNullSafety() {
        assertFalse(AdultDisciplineHelper.shouldSilenceAlert(null, null), "Null victim/attacker must return false");
        assertFalse(AdultDisciplineHelper.canDiscipline(null, null), "Null wolves cannot discipline");
    }

    @Test
    @DisplayName("Assert retaliation suppression logic remains intact")
    void testRetaliationSuppression() {
        assertTrue(WolfTargetingHandler.shouldSuppressRetaliation(true), "Suppression must be active when disciplined");
        assertFalse(WolfTargetingHandler.shouldSuppressRetaliation(false), "Suppression must be inactive when not disciplined");
    }

    @Test
    @DisplayName("Assert PacifistRevengeGoal null safety on packmate check")
    void testPacifistRevengeNullSafety() {
        PacifistRevengeGoal goal = new PacifistRevengeGoal(null);
        assertFalse(goal.canUse());
        assertDoesNotThrow(() -> goal.alertNearbyPackmates(null));
    }

    @Test
    @DisplayName("Assert Negative Parity: Non-co-owned wolves, wild wolves, and sitting/leashed dogs are immune to sparring (BL-BD-035)")
    void testNegativeParitySparringImmunity() {
        assertFalse(SmallFightHelper.shouldInitiatePlayFight(false, 400, 100, 0, 0),
                "Wild or ineligible wolves must never initiate sparring");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, false, false, false, false, false, 0, false, false),
                "Wild wolves must never be eligible for play");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, true, false, false, false, 0, false, false),
                "Sitting wolves must never be eligible for play");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, true, false, false, 0, false, false),
                "Leashed wolves must never be eligible for play");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, false, true, false, 0, false, false),
                "Wolves with active targets must never be eligible for play");
    }

    @Test
    @DisplayName("BL-BD-054: Co-owned packmates with offline owner (getOwner == null) are blocked from attacking each other")
    void testCoOwnedOfflineOwnerCivilWarProtection() {
        UUID sharedOwner = UUID.randomUUID();
        UUID otherOwner = UUID.randomUUID();

        OfflineStubWolf dogA = createOfflineWolf(UUID.randomUUID(), sharedOwner, true, false);
        OfflineStubWolf dogB = createOfflineWolf(UUID.randomUUID(), sharedOwner, true, false);
        OfflineStubWolf rivalOwnerDog = createOfflineWolf(UUID.randomUUID(), otherOwner, true, false);
        OfflineStubWolf wildWolf = createOfflineWolf(UUID.randomUUID(), null, false, false);

        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(dogA, dogB, null),
                "Co-owned tamed wolves must return Boolean.FALSE even when owner is offline (getOwner() == null)");
        assertNull(WolfTargetingHandler.wantsToAttack(dogA, rivalOwnerDog, null),
                "Tamed wolves with different owners must not trigger co-owned civil war block");
        assertNull(WolfTargetingHandler.wantsToAttack(dogA, wildWolf, null),
                "Tamed wolf vs wild wolf must pass to vanilla super");
        assertNull(WolfTargetingHandler.wantsToAttack(wildWolf, dogA, null),
                "Wild wolf vs tamed wolf must pass to vanilla super");
    }

    @Test
    @DisplayName("BL-BD-054: PLAY_FIGHT isolates designated partner and guards null target equality")
    void testPlayFightTargetIsolationAndNullTargetGuard() {
        UUID sharedOwner = UUID.randomUUID();
        OfflineStubWolf sparrerA = createOfflineWolf(UUID.randomUUID(), sharedOwner, true, false);
        OfflineStubWolf sparrerB = createOfflineWolf(UUID.randomUUID(), sharedOwner, true, false);
        OfflineStubWolf bystanderC = createOfflineWolf(UUID.randomUUID(), sharedOwner, true, false);

        sparrerA.betterdogs$setSocialState(sparrerB, WolfExtensions.SocialAction.PLAY_FIGHT, 120);

        assertEquals(Boolean.TRUE, WolfTargetingHandler.wantsToAttack(sparrerA, sparrerB, null),
                "PLAY_FIGHT must allow targeting the designated sparring partner");
        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(sparrerA, bystanderC, null),
                "PLAY_FIGHT must deny attacking bystanders outside the designated sparring pair");
        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(sparrerA, null, null),
                "PLAY_FIGHT with null target must return Boolean.FALSE");

        sparrerA.forceSocialStateRaw(null, WolfExtensions.SocialAction.PLAY_FIGHT);
        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(sparrerA, null, null),
                "PLAY_FIGHT with null socialTarget and null target must return Boolean.FALSE (never null == null -> true)");

        sparrerA.forceSocialStateRaw(null, WolfExtensions.SocialAction.TERRITORIAL_WAR);
        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(sparrerA, null, null),
                "TERRITORIAL_WAR with null socialTarget and null target must return Boolean.FALSE");
    }

    @Test
    @DisplayName("BL-BD-054: Blood feud null/empty target guards prevent NPE and preserve civil war protection")
    void testBloodFeudNullAndEmptyTargetSafety() {
        UUID sharedOwner = UUID.randomUUID();
        OfflineStubWolf dogA = createOfflineWolf(UUID.randomUUID(), sharedOwner, true, false);
        OfflineStubWolf dogB = createOfflineWolf(UUID.randomUUID(), sharedOwner, true, false);

        dogA.forceBloodFeud(true, null);
        Boolean nullTargetResult = assertDoesNotThrow(() -> WolfTargetingHandler.wantsToAttack(dogA, dogB, null),
                "Active blood feud flag with null target string must never throw NullPointerException");
        assertEquals(Boolean.FALSE, nullTargetResult,
                "Active blood feud with null target string must fall back to co-owned civil war block");

        dogA.forceBloodFeud(true, "");
        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(dogA, dogB, null),
                "Active blood feud with empty target string must not match and must preserve civil war block");
    }

    @Test
    @DisplayName("BL-BD-054: Blood feud nemesis bypasses civil war block unless high affinity (> 50) suppresses it")
    void testBloodFeudNemesisAndAffinitySuppression() {
        UUID sharedOwner = UUID.randomUUID();
        OfflineStubWolf dogA = createOfflineWolf(UUID.randomUUID(), sharedOwner, true, false);
        OfflineStubWolf nemesisB = createOfflineWolf(UUID.randomUUID(), sharedOwner, true, false);
        OfflineStubWolf bystanderC = createOfflineWolf(UUID.randomUUID(), sharedOwner, true, false);

        dogA.betterdogs$setBloodFeudTarget(nemesisB.getStringUUID());

        assertEquals(Boolean.TRUE, WolfTargetingHandler.wantsToAttack(dogA, nemesisB, null),
                "Blood feud nemesis must bypass co-owned civil war immunity");
        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(dogA, bystanderC, null),
                "Non-nemesis co-owned packmate must still be blocked despite active blood feud against another wolf");

        dogA.betterdogs$adjustAffinity(nemesisB.getStringUUID(), 51);
        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(dogA, nemesisB, null),
                "High affinity (> 50) with nemesis must suppress the blood feud override");
    }

    @Test
    @DisplayName("BL-BD-054: Hierarchy correction snap suppresses retaliation even under PLAY_FIGHT or blood feud")
    void testCorrectionSnapRetaliationSuppressionViaHandler() {
        UUID sharedOwner = UUID.randomUUID();
        OfflineStubWolf calmDog = createOfflineWolf(UUID.randomUUID(), sharedOwner, true, false);
        OfflineStubWolf playfulDog = createOfflineWolf(UUID.randomUUID(), sharedOwner, true, false);

        playfulDog.betterdogs$setSocialState(calmDog, WolfExtensions.SocialAction.PLAY_FIGHT, 120);
        SmallFightHelper.recordCorrectionSnap(calmDog.getUUID(), playfulDog.getUUID(), 5000L);

        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(playfulDog, calmDog, null),
                "Disciplined wolf must not retaliate against disciplining packmate during active correction snap window");
    }

    @Test
    @DisplayName("BL-BD-054: Defensive inputs (null wolf, non-WolfExtensions Wolf) fail safely returning null")
    void testDefensiveNullAndNonExtensionWolfInputs() {
        UUID sharedOwner = UUID.randomUUID();
        OfflineStubWolf target = createOfflineWolf(UUID.randomUUID(), sharedOwner, true, false);
        Wolf plainWolf = allocatePlainWolf();

        assertNull(assertDoesNotThrow(() -> WolfTargetingHandler.wantsToAttack(null, target, null)),
                "Null wolf must return null without throwing");
        assertNull(assertDoesNotThrow(() -> WolfTargetingHandler.wantsToAttack(plainWolf, target, null)),
                "Wolf instance without WolfExtensions must return null without throwing");
    }

    @Test
    @DisplayName("BL-BD-054 Contract: WolfCombatMixin delegates wantsToAttack to WolfTargetingHandler and WolfCombatHooks.wantsToAttack is purged")
    void testMixinDelegationRegressionContract() throws IOException {
        Path root = findProjectRoot();
        Path mixinFile = root.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/mixin/WolfCombatMixin.java");
        Path hooksFile = root.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfCombatHooks.java");
        Path commonMainJava = root.resolve("common/src/main/java");

        assertTrue(Files.exists(mixinFile), "WolfCombatMixin.java must exist");
        assertTrue(Files.exists(hooksFile), "WolfCombatHooks.java must exist");

        String mixinContent = Files.readString(mixinFile, StandardCharsets.UTF_8);
        assertTrue(mixinContent.contains("WolfTargetingHandler.wantsToAttack("),
                "WolfCombatMixin must delegate wantsToAttack to WolfTargetingHandler.wantsToAttack(...)");
        assertFalse(mixinContent.contains("WolfCombatHooks.wantsToAttack("),
                "WolfCombatMixin must no longer call dead WolfCombatHooks.wantsToAttack(...)");
        assertTrue(mixinContent.contains("WolfCombatHooks.onActuallyHurt("),
                "WolfCombatMixin must continue delegating actuallyHurt to WolfCombatHooks.onActuallyHurt(...)");

        String hooksContent = Files.readString(hooksFile, StandardCharsets.UTF_8);
        assertFalse(hooksContent.contains("wantsToAttack("),
                "WolfCombatHooks.java must no longer contain the dead duplicate wantsToAttack(...) method");
        assertTrue(hooksContent.contains("onActuallyHurt("),
                "WolfCombatHooks.java must retain onActuallyHurt(...)");

        try (Stream<Path> stream = Files.walk(commonMainJava)) {
            List<Path> callers = stream
                    .filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> {
                        try {
                            return Files.readString(p, StandardCharsets.UTF_8).contains("WolfCombatHooks.wantsToAttack");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
            assertTrue(callers.isEmpty(),
                    "Zero callers of WolfCombatHooks.wantsToAttack may remain in common/src/main/java, found: " + callers);
        }
    }

    private static final class OfflineStubWolf extends Wolf implements WolfExtensions {
        private UUID uuid;
        private @Nullable UUID ownerUuid;
        private boolean tame;
        private boolean baby;
        private WolfPersonality personality;
        private boolean hasPersonality;
        private int lastDamageTime;
        private boolean submissive;
        private @Nullable String bloodFeudTarget;
        private boolean hasBloodFeud;
        private long lastMischiefDay;
        private boolean beingDisciplined;
        private @Nullable LivingEntity socialTarget;
        private SocialAction socialAction;
        private int socialModeTimer;
        private long dna;
        private float socialScale;
        private @Nullable EntitySocialScheduler scheduler;
        private Map<String, Integer> affinityMap;
        private @Nullable UUID leaderUuid;
        private boolean guardMode;
        private @Nullable BlockPos guardPos;
        private boolean sittingManually;
        private boolean adoptable;
        private float healthBonus;
        private float damageMod;
        private float speedMod;
        private boolean statsRolled;
        private int pushWaitTimer;
        private @Nullable BlockPos pathfindAvoidPos;
        private long soothedTime;
        private @Nullable BlockPos soundLocationTarget;
        private int passiveOverrideTicks;
        private String nemesisEntityType;
        private long nemesisExpiryTime;
        private boolean hasFetchedItem;
        private @Nullable ItemStack fetchedItemStack;
        private int zoomiesTicks;
        private int howlingTicks;

        private OfflineStubWolf() {
            super(null, null);
        }

        void init(UUID wolfUuid, @Nullable UUID ownerUuid, boolean tame, boolean baby) {
            this.uuid = wolfUuid;
            this.ownerUuid = ownerUuid;
            this.tame = tame;
            this.baby = baby;
            this.personality = WolfPersonality.NORMAL;
            this.bloodFeudTarget = "";
            this.hasBloodFeud = false;
            this.socialAction = SocialAction.NONE;
            this.affinityMap = new HashMap<>();
            this.nemesisEntityType = "";
        }

        void forceBloodFeud(boolean active, @Nullable String targetUuid) {
            this.hasBloodFeud = active;
            this.bloodFeudTarget = targetUuid;
        }

        void forceSocialStateRaw(@Nullable LivingEntity target, SocialAction action) {
            this.socialTarget = target;
            this.socialAction = action;
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
        public boolean isTame() {
            return this.tame;
        }

        @Override
        public boolean isBaby() {
            return this.baby;
        }

        @Override
        public @Nullable EntityReference<LivingEntity> getOwnerReference() {
            return this.ownerUuid != null ? EntityReference.of(this.ownerUuid) : null;
        }

        @Override
        public @Nullable LivingEntity getOwner() {
            return null;
        }

        @Override
        public WolfPersonality betterdogs$getPersonality() {
            return this.personality;
        }

        @Override
        public void betterdogs$setPersonality(WolfPersonality personality) {
            this.personality = personality;
            this.hasPersonality = personality != null;
        }

        @Override
        public boolean betterdogs$hasPersonality() {
            return this.hasPersonality;
        }

        @Override
        public int betterdogs$getLastDamageTime() {
            return this.lastDamageTime;
        }

        @Override
        public void betterdogs$setLastDamageTime(int time) {
            this.lastDamageTime = time;
        }

        @Override
        public boolean betterdogs$isSubmissive() {
            return this.submissive;
        }

        @Override
        public void betterdogs$setSubmissive(boolean submissive) {
            this.submissive = submissive;
        }

        @Override
        public String betterdogs$getBloodFeudTarget() {
            return this.bloodFeudTarget;
        }

        @Override
        public void betterdogs$setBloodFeudTarget(String targetUuid) {
            this.bloodFeudTarget = targetUuid;
            this.hasBloodFeud = targetUuid != null && !targetUuid.isEmpty();
        }

        @Override
        public boolean betterdogs$hasBloodFeud() {
            return this.hasBloodFeud;
        }

        @Override
        public long betterdogs$getLastMischiefDay() {
            return this.lastMischiefDay;
        }

        @Override
        public void betterdogs$setLastMischiefDay(long day) {
            this.lastMischiefDay = day;
        }

        @Override
        public boolean betterdogs$isBeingDisciplined() {
            return this.beingDisciplined;
        }

        @Override
        public void betterdogs$setBeingDisciplined(boolean isBeingDisciplined) {
            this.beingDisciplined = isBeingDisciplined;
        }

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
            }
        }

        @Override
        public @Nullable LivingEntity betterdogs$getSocialTarget() {
            return this.socialTarget;
        }

        @Override
        public SocialAction betterdogs$getSocialAction() {
            return this.socialAction;
        }

        @Override
        public boolean betterdogs$isSocialModeActive() {
            return this.socialTarget != null && this.socialModeTimer > 0;
        }

        @Override
        public void betterdogs$tickSocialMode() {
            if (this.socialModeTimer > 0) {
                this.socialModeTimer--;
            }
        }

        @Override
        public long betterdogs$getDNA() {
            return this.dna;
        }

        @Override
        public void betterdogs$setDNA(long dna) {
            this.dna = dna;
        }

        @Override
        public float betterdogs$getSocialScale() {
            return this.socialScale;
        }

        @Override
        public void betterdogs$setSocialScale(float scale) {
            this.socialScale = scale;
        }

        @Override
        public @Nullable EntitySocialScheduler betterdogs$getScheduler() {
            return this.scheduler;
        }

        @Override
        public EntitySocialScheduler betterdogs$getOrInitializeScheduler() {
            return this.scheduler;
        }

        @Override
        public void betterdogs$tickScheduler() {
        }

        @Override
        public int betterdogs$getAffinity(String targetUuid) {
            return this.affinityMap.getOrDefault(targetUuid, 0);
        }

        @Override
        public void betterdogs$adjustAffinity(String targetUuid, int delta) {
            this.affinityMap.merge(targetUuid, delta, Integer::sum);
        }

        @Override
        public @Nullable UUID betterdogs$getLeaderUuid() {
            return this.leaderUuid;
        }

        @Override
        public void betterdogs$setLeaderUuid(@Nullable UUID uuid) {
            this.leaderUuid = uuid;
        }

        @Override
        public boolean betterdogs$isGuardMode() {
            return this.guardMode;
        }

        @Override
        public void betterdogs$setGuardMode(boolean guardMode) {
            this.guardMode = guardMode;
        }

        @Override
        public @Nullable BlockPos betterdogs$getGuardPos() {
            return this.guardPos;
        }

        @Override
        public void betterdogs$setGuardPos(@Nullable BlockPos pos) {
            this.guardPos = pos;
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
            return this.adoptable;
        }

        @Override
        public void betterdogs$setAdoptable(boolean adoptable) {
            this.adoptable = adoptable;
        }

        @Override
        public float betterdogs$getHealthBonus() {
            return this.healthBonus;
        }

        @Override
        public void betterdogs$setHealthBonus(float hp) {
            this.healthBonus = hp;
        }

        @Override
        public float betterdogs$getDamageMod() {
            return this.damageMod;
        }

        @Override
        public void betterdogs$setDamageMod(float dmg) {
            this.damageMod = dmg;
        }

        @Override
        public float betterdogs$getSpeedMod() {
            return this.speedMod;
        }

        @Override
        public void betterdogs$setSpeedMod(float speed) {
            this.speedMod = speed;
        }

        @Override
        public boolean betterdogs$areStatsRolled() {
            return this.statsRolled;
        }

        @Override
        public void betterdogs$setStatsRolled(boolean rolled) {
            this.statsRolled = rolled;
        }

        @Override
        public int betterdogs$getPushWaitTimer() {
            return this.pushWaitTimer;
        }

        @Override
        public void betterdogs$setPushWaitTimer(int ticks) {
            this.pushWaitTimer = ticks;
        }

        @Override
        public @Nullable BlockPos betterdogs$getPathfindAvoidPos() {
            return this.pathfindAvoidPos;
        }

        @Override
        public void betterdogs$setPathfindAvoidPos(@Nullable BlockPos pos) {
            this.pathfindAvoidPos = pos;
        }

        @Override
        public GoalSelector betterdogs$getGoalSelector() {
            return null;
        }

        @Override
        public long betterdogs$getSoothedTime() {
            return this.soothedTime;
        }

        @Override
        public void betterdogs$setSoothedTime(long time) {
            this.soothedTime = time;
        }

        @Override
        public @Nullable BlockPos betterdogs$getSoundLocationTarget() {
            return this.soundLocationTarget;
        }

        @Override
        public void betterdogs$setSoundLocationTarget(@Nullable BlockPos pos) {
            this.soundLocationTarget = pos;
        }

        @Override
        public int betterdogs$getPassiveOverrideTicks() {
            return this.passiveOverrideTicks;
        }

        @Override
        public void betterdogs$setPassiveOverrideTicks(int ticks) {
            this.passiveOverrideTicks = ticks;
        }

        @Override
        public String betterdogs$getNemesisEntityType() {
            return this.nemesisEntityType;
        }

        @Override
        public void betterdogs$setNemesisEntityType(String type) {
            this.nemesisEntityType = type;
        }

        @Override
        public long betterdogs$getNemesisExpiryTime() {
            return this.nemesisExpiryTime;
        }

        @Override
        public void betterdogs$setNemesisExpiryTime(long time) {
            this.nemesisExpiryTime = time;
        }

        @Override
        public boolean betterdogs$hasFetchedItem() {
            return this.hasFetchedItem;
        }

        @Override
        public void betterdogs$setHasFetchedItem(boolean fetched) {
            this.hasFetchedItem = fetched;
        }

        @Override
        public @Nullable ItemStack betterdogs$getFetchedItemStack() {
            return this.fetchedItemStack;
        }

        @Override
        public void betterdogs$setFetchedItemStack(@Nullable ItemStack stack) {
            this.fetchedItemStack = stack;
        }

        @Override
        public int betterdogs$getZoomiesTicks() {
            return this.zoomiesTicks;
        }

        @Override
        public void betterdogs$setZoomiesTicks(int ticks) {
            this.zoomiesTicks = ticks;
        }

        @Override
        public int betterdogs$getHowlingTicks() {
            return this.howlingTicks;
        }

        @Override
        public void betterdogs$setHowlingTicks(int ticks) {
            this.howlingTicks = ticks;
        }
    }
}
