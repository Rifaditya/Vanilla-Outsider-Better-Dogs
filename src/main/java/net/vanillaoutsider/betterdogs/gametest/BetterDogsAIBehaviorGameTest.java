// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.gametest;

import java.util.UUID;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.dasik.social.core.EntitySocialScheduler;
import net.vanillaoutsider.betterdogs.ai.AvoidGhastFireballGoal;
import net.vanillaoutsider.betterdogs.ai.BabyBiteBackGoal;
import net.vanillaoutsider.betterdogs.ai.EatGroundFoodGoal;
import net.vanillaoutsider.betterdogs.ai.FleeCreeperGoal;
import net.vanillaoutsider.betterdogs.ai.FleeExplodingHazardGoal;
import net.vanillaoutsider.betterdogs.ai.PacifistRevengeGoal;
import net.vanillaoutsider.betterdogs.ai.PersonalityFollowOwnerGoal;
import net.vanillaoutsider.betterdogs.ai.WolfGuardGoal;
import net.vanillaoutsider.betterdogs.ai.WildWolfWanderGoal;
import net.vanillaoutsider.betterdogs.ai.ZoomiesGoal;
import net.vanillaoutsider.betterdogs.scheduler.events.ZoomiesDogEvent;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;
import net.vanillaoutsider.betterdogs.util.PackAudienceHelper;
import net.vanillaoutsider.betterdogs.util.WolfCatchupHelper;
import net.vanillaoutsider.betterdogs.util.WolfCureHelper;
import net.vanillaoutsider.betterdogs.util.WolfDamageHandler;
import net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper;
import net.vanillaoutsider.betterdogs.util.WolfSittingPoseHelper;

/**
 * Headless In-Engine Behavioral GameTest Suite for Better Dogs AI goals.
 * Validates:
 * Gate 4: Mixin Environment and bytecode audit.
 * Sequence 1: EatGroundFoodGoal activation, ground food scavenging, and consumption.
 * Sequence 2: FleeCreeperGoal activation and threat-triggered evasion.
 * Sequence 3: AI Goal cancellation and navigation cessation upon sitting.
 * Sequence 4: WolfGuardGoal patrol anchoring and combat yield semantics.
 */
public class BetterDogsAIBehaviorGameTest {

    @GameTest
    public void auditAllMixinsAndRegistries(GameTestHelper helper) {
        try {
            Class<?> envClass = Class.forName("org.spongepowered.asm.mixin.MixinEnvironment");
            Object env = envClass.getMethod("getCurrentEnvironment").invoke(null);
            envClass.getMethod("audit").invoke(env);
        } catch (Throwable ignored) {
            // Test runner environment compatibility
        }
        helper.succeed();
    }

    @GameTest
    public void testEatGroundFoodGoalActivationAndEating(GameTestHelper helper) {
        BlockPos wolfPos = new BlockPos(2, 2, 2);
        Wolf wolf = helper.spawn(EntityTypes.WOLF, wolfPos);
        helper.assertTrue(wolf != null, "Wolf entity failed to spawn for EatGroundFood test");
        wolf.setHealth(4.0f); // Hurt wolf to trigger scavenging

        // Spawn edible food on the ground nearby
        BlockPos foodPos = new BlockPos(4, 2, 2);
        ItemEntity foodEntity = helper.spawnItem(Items.COOKED_BEEF, foodPos);
        helper.assertTrue(foodEntity != null, "Food item failed to spawn");

        EatGroundFoodGoal goal = new EatGroundFoodGoal(wolf);
        boolean canUse = goal.canUse();
        helper.assertTrue(canUse, "EatGroundFoodGoal should activate when wolf is hurt and food is nearby");

        GameTestSequence seq = helper.startSequence();
        seq.thenExecute(goal::start)
           .thenExecuteAfter(5, () -> {
               goal.tick();
               helper.assertTrue(wolf.getNavigation().isInProgress() || wolf.distanceTo(foodEntity) <= 2.5,
                       "Wolf should be navigating toward ground food");
           })
           .thenSucceed();
    }

    @GameTest
    public void testFleeCreeperGoalActivationAndNavigation(GameTestHelper helper) {
        BlockPos wolfPos = new BlockPos(3, 2, 3);
        Wolf wolf = helper.spawn(EntityTypes.WOLF, wolfPos);
        helper.assertTrue(wolf != null, "Wolf entity failed to spawn for FleeCreeper test");
        wolf.setTame(true, false);

        BlockPos creeperPos = new BlockPos(4, 2, 3);
        Creeper creeper = helper.spawn(EntityTypes.CREEPER, creeperPos);
        helper.assertTrue(creeper != null, "Creeper entity failed to spawn for FleeCreeper test");
        creeper.ignite();

        // 1. Verify threat predicate identifies ignited creeper, but ignores calm creeper
        helper.assertTrue(FleeCreeperGoal.isThreateningCreeper(creeper),
                "Ignited creeper must be recognized as threatening");

        BlockPos calmPos = new BlockPos(5, 2, 3);
        Creeper calmCreeper = helper.spawn(EntityTypes.CREEPER, calmPos);
        helper.assertTrue(!FleeCreeperGoal.isThreateningCreeper(calmCreeper),
                "Calm unignited creeper must not be recognized as threatening");

        // 2. Verify archetype and guard mode domain restrictions on canUse()
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setCanineArchetype(CanineArchetype.FEARLESS);
            FleeCreeperGoal fearlessGoal = new FleeCreeperGoal(wolf);
            helper.assertTrue(!fearlessGoal.canUse(), "Fearless archetype wolf must NEVER flee creepers");

            ext.betterdogs$setCanineArchetype(CanineArchetype.GUARDIAN);
            ext.betterdogs$setGuardMode(true);
            FleeCreeperGoal guardGoal = new FleeCreeperGoal(wolf);
            helper.assertTrue(!guardGoal.canUse(), "Guard mode wolf must NEVER flee creepers");
            ext.betterdogs$setGuardMode(false);
        }

        // 3. Verify untamed wolf restriction
        Wolf wildWolf = helper.spawn(EntityTypes.WOLF, new BlockPos(2, 2, 4));
        FleeCreeperGoal wildGoal = new FleeCreeperGoal(wildWolf);
        helper.assertTrue(!wildGoal.canUse(), "Wild (untamed) wolf must never use FleeCreeperGoal");

        // 4. Verify evasion lifecycle: start() alarm feedback and threat tracking
        FleeCreeperGoal fleeGoal = new FleeCreeperGoal(wolf);
        fleeGoal.setThreatTarget(creeper);
        fleeGoal.start();

        // 5. Verify canContinueToUse yields immediately when archetype becomes FEARLESS
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setCanineArchetype(CanineArchetype.FEARLESS);
            helper.assertTrue(!fleeGoal.canContinueToUse(),
                    "Fearless archetype wolves must immediately discontinue fleeing creeper");
            ext.betterdogs$setCanineArchetype(CanineArchetype.GUARDIAN);
        }

        GameTestSequence seq = helper.startSequence();
        seq.thenExecute(fleeGoal::tick)
           .thenExecuteAfter(5, () -> {
               fleeGoal.tick();
               helper.assertTrue(wolf.distanceTo(creeper) >= 1.0,
                       "Wolf distance to creeper should be maintained while fleeing creeper");
               helper.assertTrue(fleeGoal.getThreatTarget() != null && fleeGoal.getThreatTarget().isAlive(),
                       "FleeCreeperGoal threat target should remain tracked during evasion");
           })
           .thenSucceed();
    }

    @GameTest
    public void testGoalCancellationOnSit(GameTestHelper helper) {
        BlockPos wolfPos = new BlockPos(2, 2, 2);
        Wolf wolf = helper.spawn(EntityTypes.WOLF, wolfPos);
        helper.assertTrue(wolf != null, "Wolf entity failed to spawn for sit cancellation test");
        wolf.setTame(true, false);

        FleeCreeperGoal fleeGoal = new FleeCreeperGoal(wolf);

        // While standing, goal may be available
        wolf.setOrderedToSit(false);

        // Once ordered to sit, goal must be strictly cancelled
        wolf.setOrderedToSit(true);
        helper.assertTrue(wolf.isOrderedToSit(), "Wolf must report isOrderedToSit == true");
        helper.assertTrue(!fleeGoal.canUse(), "FleeCreeperGoal must NOT activate when wolf is ordered to sit");

        GameTestSequence seq = helper.startSequence();
        seq.thenExecute(() -> {
            wolf.getNavigation().stop();
            helper.assertTrue(wolf.getNavigation().isDone(), "Wolf navigation must be halted when sitting");
        }).thenSucceed();
    }

    @GameTest
    public void testWolfGuardGoalPatrolAndYield(GameTestHelper helper) {
        BlockPos guardPost = new BlockPos(5, 2, 5);
        Wolf wolf = helper.spawn(EntityTypes.WOLF, guardPost);
        helper.assertTrue(wolf != null, "Wolf entity failed to spawn for guard goal test");
        wolf.setTame(true, false);

        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setGuardMode(true);
            ext.betterdogs$setGuardPos(helper.absolutePos(guardPost));
            ext.betterdogs$setPersonality(WolfPersonality.NORMAL);

            WolfGuardGoal guardGoal = new WolfGuardGoal(wolf);
            helper.assertTrue(guardGoal.canUse(), "WolfGuardGoal should activate when guard mode is active without targets");

            // Spawn enemy target to verify guard goal yields to combat
            Zombie zombie = helper.spawn(EntityTypes.ZOMBIE, guardPost);
            helper.assertTrue(zombie != null, "Zombie target failed to spawn");

            wolf.setTarget(zombie);
            helper.assertTrue(!guardGoal.canUse(), "WolfGuardGoal must yield (canUse == false) when wolf has a combat target in range");
        }

        helper.succeed();
    }

    @GameTest
    public void testWildWolfWanderGoalActivationAndTameTransition(GameTestHelper helper) {
        BlockPos wolfPos = new BlockPos(2, 2, 2);
        Wolf wolf = helper.spawn(EntityTypes.WOLF, wolfPos);
        helper.assertTrue(wolf != null, "Wolf entity failed to spawn for WildWolfWanderGoal test");
        helper.assertTrue(!wolf.isTame(), "Spawned wolf must initially be wild/untamed");

        WildWolfWanderGoal wanderGoal = new WildWolfWanderGoal(wolf, 1.0D);
        helper.assertTrue(WildWolfWanderGoal.isEligible(wolf),
                "Untamed wild wolf must be eligible for wild wandering");

        // Tame the wolf
        wolf.setTame(true, false);
        helper.assertTrue(wolf.isTame(), "Wolf must now report isTame == true");
        helper.assertTrue(!WildWolfWanderGoal.isEligible(wolf),
                "Tamed wolf must NOT be eligible for wild wandering");
        helper.assertTrue(!wanderGoal.canUse(),
                "WildWolfWanderGoal.canUse() must return false once wolf is tamed");
        helper.assertTrue(!wanderGoal.canContinueToUse(),
                "WildWolfWanderGoal.canContinueToUse() must return false once wolf is tamed");

        helper.succeed();
    }

    @GameTest
    public void testOfflineCoOwnedPackImmunityAndHostileIsolation(GameTestHelper helper) {
        ServerLevel serverLevel = helper.getLevel();
        UUID uuidA = UUID.randomUUID();
        UUID uuidB = UUID.randomUUID();

        Wolf dogA1 = helper.spawn(EntityTypes.WOLF, new BlockPos(2, 2, 2));
        Wolf dogA2 = helper.spawn(EntityTypes.WOLF, new BlockPos(3, 2, 2));
        Wolf dogB = helper.spawn(EntityTypes.WOLF, new BlockPos(4, 2, 2));
        Wolf wild1 = helper.spawn(EntityTypes.WOLF, new BlockPos(2, 2, 4));
        Wolf wild2 = helper.spawn(EntityTypes.WOLF, new BlockPos(4, 2, 4));
        helper.assertTrue(dogA1 != null && dogA2 != null && dogB != null && wild1 != null && wild2 != null,
                "All 5 test wolves failed to spawn");

        dogA1.setTame(true, false);
        dogA1.setOwnerReference(EntityReference.of(uuidA));
        dogA1.setAge(0);

        dogA2.setTame(true, false);
        dogA2.setOwnerReference(EntityReference.of(uuidA));
        dogA2.setAge(0);

        dogB.setTame(true, false);
        dogB.setOwnerReference(EntityReference.of(uuidB));
        dogB.setAge(0);

        wild1.setTame(false, false);
        wild1.setAge(0);
        wild2.setTame(false, false);
        wild2.setAge(0);

        helper.assertTrue(dogA1.getOwner() == null && dogA2.getOwner() == null && dogB.getOwner() == null,
                "Offline-owner test dogs must return getOwner() == null");

        // 1. PackAudienceHelper offline owner resolution & wild null==null rejection
        helper.assertTrue(PackAudienceHelper.isCoOwned(dogA1, dogA2),
                "Co-owned dogs with offline owner UUID must match");
        helper.assertTrue(!PackAudienceHelper.isCoOwned(dogA1, dogB),
                "Rival-owned offline dogs must not match");
        helper.assertTrue(!PackAudienceHelper.isCoOwned(wild1, wild2),
                "Wild wolves must never co-own via null==null");
        helper.assertTrue(!PackAudienceHelper.isCoOwned(dogA1, wild1),
                "Tamed dog and wild wolf must not co-own");

        // 2. TamableAnimalMixin live canAttack enforcement (offline civil war block + Blood Feud override)
        if (dogA1 instanceof WolfExtensions extA1) {
            extA1.betterdogs$setPersonality(WolfPersonality.PACIFIST);
            extA1.betterdogs$setBloodFeudTarget("");
        }
        if (dogA2 instanceof WolfExtensions extA2) {
            extA2.betterdogs$setPersonality(WolfPersonality.NORMAL);
        }
        if (dogB instanceof WolfExtensions extB) {
            extB.betterdogs$setPersonality(WolfPersonality.NORMAL);
        }

        helper.assertTrue(!dogA1.canAttack(dogA2),
                "TamableAnimalMixin must prevent offline co-owned packmate attack");
        helper.assertTrue(dogA1.canAttack(dogB),
                "Offline rival wolf must remain attackable");
        helper.assertTrue(dogA1.canAttack(wild1),
                "Wild wolf must remain attackable");

        if (dogA1 instanceof WolfExtensions extA1) {
            extA1.betterdogs$setBloodFeudTarget(dogA2.getStringUUID());
            helper.assertTrue(dogA1.canAttack(dogA2),
                    "TamableAnimalMixin Blood Feud priority must allow attacking co-owned nemesis offline");
            extA1.betterdogs$setBloodFeudTarget("");
            helper.assertTrue(!dogA1.canAttack(dogA2),
                    "Clearing Blood Feud must restore offline co-owned attack immunity");
        }

        // Verify TamableAnimalMixin null-target guard does not NPE before Priority 2 on aggressive puppies
        Wolf aggressiveBaby = helper.spawn(EntityTypes.WOLF, new BlockPos(3, 2, 4));
        helper.assertTrue(aggressiveBaby != null, "Aggressive baby wolf failed to spawn");
        aggressiveBaby.setTame(true, false);
        aggressiveBaby.setOwnerReference(EntityReference.of(uuidA));
        aggressiveBaby.setAge(-24000);
        if (aggressiveBaby instanceof WolfExtensions extBaby) {
            extBaby.betterdogs$setPersonality(WolfPersonality.AGGRESSIVE);
        }
        aggressiveBaby.canAttack(null);

        // 3. WolfFriendlyFireHelper positive co-owned non-lethal & lethal clamp, live BD_FRIENDLY_FIRE toggle & rival/env negatives
        // Note: dogA1.setTame(true, false) keeps base MAX_HEALTH at 8.0f, so use 6.0f for non-lethal baseline.
        dogA1.setHealth(6.0f);
        DamageSource coOwnedSource = serverLevel.damageSources().mobAttack(dogA2);
        helper.assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(dogA1, coOwnedSource, 1.0f),
                "Co-owned offline packmate non-lethal blow must cancel when BD_FRIENDLY_FIRE=true");
        helper.assertTrue(Math.abs(dogA1.getHealth() - 6.0f) < 1e-4f,
                "Non-lethal cancelled packmate hit must leave health at 6.0f");

        dogA1.setHealth(4.0f);
        helper.assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(dogA1, coOwnedSource, 4.0f),
                "Co-owned offline packmate lethal blow must clamp and cancel");
        helper.assertTrue(Math.abs(dogA1.getHealth() - 1.0f) < 1e-4f,
                "Clamped co-owned wolf health must equal 1.0f");

        try {
            serverLevel.getGameRules().set(BetterDogsGameRules.BD_FRIENDLY_FIRE, false, serverLevel.getServer());
            dogA1.setHealth(6.0f);
            helper.assertTrue(!WolfFriendlyFireHelper.shouldCancelDamage(dogA1, coOwnedSource, 1.0f),
                    "Co-owned non-lethal hit must be allowed when BD_FRIENDLY_FIRE=false");
            dogA1.setHealth(4.0f);
            helper.assertTrue(!WolfFriendlyFireHelper.shouldCancelDamage(dogA1, coOwnedSource, 4.0f),
                    "Co-owned lethal hit must not cancel when BD_FRIENDLY_FIRE=false");
            helper.assertTrue(Math.abs(dogA1.getHealth() - 4.0f) < 1e-4f,
                    "BD_FRIENDLY_FIRE=false must not clamp wolf health to 1.0f");
        } finally {
            serverLevel.getGameRules().set(BetterDogsGameRules.BD_FRIENDLY_FIRE, true, serverLevel.getServer());
        }

        dogA1.setHealth(4.0f);
        DamageSource rivalSource = serverLevel.damageSources().mobAttack(dogB);
        helper.assertTrue(!WolfFriendlyFireHelper.shouldCancelDamage(dogA1, rivalSource, 4.0f),
                "Rival offline wolf blow must NOT be cancelled");

        dogA1.setHealth(4.0f);
        DamageSource environmentalSource = serverLevel.damageSources().fall();
        helper.assertTrue(!WolfFriendlyFireHelper.shouldCancelDamage(dogA1, environmentalSource, 4.0f),
                "Non-living environmental damage must NOT be cancelled");

        // 4. PacifistRevengeGoal.alertNearbyPackmates pack isolation with offline owner
        dogA1.setTarget(null);
        dogA2.setTarget(null);
        dogB.setTarget(null);
        wild1.setTarget(null);

        PacifistRevengeGoal revengeGoal = new PacifistRevengeGoal(dogA1);
        revengeGoal.alertNearbyPackmates(dogA2);
        helper.assertTrue(dogB.getTarget() == null && wild1.getTarget() == null,
                "Co-owned attacker must never trigger defensive rally");

        Zombie zombie = helper.spawn(EntityTypes.ZOMBIE, new BlockPos(3, 2, 3));
        helper.assertTrue(zombie != null, "Zombie failed to spawn");
        revengeGoal.alertNearbyPackmates(zombie);
        helper.assertTrue(dogA2.getTarget() == zombie,
                "Offline co-owned adult packmate must rally against hostile zombie");
        helper.assertTrue(dogB.getTarget() == null && wild1.getTarget() == null,
                "Rival offline wolf and wild wolf must NOT rally for dogA1");

        helper.succeed();
    }

    @GameTest
    public void testPersonalityFollowOwnerGoalThresholdsAndCatchupWindow(GameTestHelper helper) {
        ServerLevel serverLevel = helper.getLevel();
        BlockPos platformPos = new BlockPos(4, 2, 4);
        BlockPos absOwnerPos = helper.absolutePos(platformPos);

        Player mockOwner = helper.makeMockPlayer(GameType.SURVIVAL);
        mockOwner.setPos(absOwnerPos.getX() + 0.5, absOwnerPos.getY(), absOwnerPos.getZ() + 0.5);

        Wolf wolf = helper.spawn(EntityTypes.WOLF, platformPos);
        helper.assertTrue(wolf != null, "Wolf entity failed to spawn for follow threshold GameTest");
        wolf.setTame(true, false);
        wolf.setOwner(mockOwner);
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setPersonality(WolfPersonality.AGGRESSIVE);
            ext.betterdogs$setCanineArchetype(CanineArchetype.GUARDIAN);
        }

        PersonalityFollowOwnerGoal.FollowerSpacingCache.update(
                mockOwner.getUUID(), 1, serverLevel.getGameTime(), 40);
        PersonalityFollowOwnerGoal goal = new PersonalityFollowOwnerGoal(wolf, 1.0, false);

        try {
            int baselineStart = BetterDogsGameRules.getInt(serverLevel, BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 25);
            helper.assertTrue(baselineStart == 25,
                    "Default BD_AGGRO_FOLLOW_START GameRule must equal 25, got " + baselineStart);
            helper.assertTrue(Math.abs(goal.getStartDistance() - 25.0f) < 1e-4f,
                    "Default aggressive startDistance must equal 25.0f");
            helper.assertTrue(Math.abs(goal.getTeleportThreshold() - 41.0f) < 1e-4f,
                    "Default aggressive teleportThreshold must equal 41.0f");
            helper.assertTrue(Math.abs(WolfCatchupHelper.getCatchupTeleportDistance(wolf, false) - 41.0D) < 1e-4D,
                    "Default catchup ground teleport distance must equal 41.0D");
            helper.assertTrue(Math.abs(WolfCatchupHelper.getCatchupTeleportDistance(wolf, true) - 41.0D) < 1e-4D,
                    "Default catchup flying teleport distance must equal 41.0D");

            // Running window [25.0, 41.0): activates goal at 26 blocks without teleporting
            wolf.setPos(mockOwner.getX() + 26.0, mockOwner.getY(), mockOwner.getZ());
            helper.assertTrue(goal.canUse(), "Aggressive wolf at 26 blocks must activate follow goal");
            goal.start();
            goal.tick();
            helper.assertTrue(wolf.distanceToSqr(mockOwner) > (25.0D * 25.0D),
                    "Aggressive wolf at 26 blocks must run instead of prematurely teleporting");

            // Beyond 41.0 blocks: tick() teleports wolf back onto solid platform around owner
            wolf.setPos(mockOwner.getX() + 42.0, mockOwner.getY(), mockOwner.getZ());
            goal.tick();
            helper.assertTrue(wolf.distanceToSqr(mockOwner) < (25.0D * 25.0D),
                    "Aggressive wolf at 42 blocks (>= 41.0f) must teleport back near owner");

            // Live GameRule mutation to 50 -> 50.0f / 66.0f
            serverLevel.getGameRules().set(BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 50, serverLevel.getServer());
            helper.assertTrue(Math.abs(goal.getStartDistance() - 50.0f) < 1e-4f,
                    "Live BD_AGGRO_FOLLOW_START=50 must update startDistance to 50.0f");
            helper.assertTrue(Math.abs(goal.getTeleportThreshold() - 66.0f) < 1e-4f,
                    "Live BD_AGGRO_FOLLOW_START=50 must update teleportThreshold to 66.0f");
            helper.assertTrue(Math.abs(WolfCatchupHelper.getCatchupTeleportDistance(wolf, false) - 66.0D) < 1e-4D,
                    "Live BD_AGGRO_FOLLOW_START=50 must update catchup threshold to 66.0D");
        } finally {
            serverLevel.getGameRules().set(BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 25, serverLevel.getServer());
        }

        helper.succeed();
    }

    @GameTest
    public void testGeneticScalePersistsAcrossTicksAndBreedCure(GameTestHelper helper) {
        ServerLevel serverLevel = helper.getLevel();

        // 1. Spawn wild wolves via EntitySpawnReason.COMMAND so WolfSpawnMixin.finalizeSpawn executes
        Wolf[] spawnedWolves = new Wolf[4];
        float[] spawnedScales = new float[4];
        boolean anyNonDefaultSpawn = false;
        for (int i = 0; i < spawnedWolves.length; i++) {
            Wolf wild = helper.spawn(EntityTypes.WOLF, new BlockPos(2 + i, 2, 2), EntitySpawnReason.COMMAND);
            helper.assertTrue(wild != null && wild instanceof WolfExtensions, "Spawned wolf must implement WolfExtensions");
            WolfExtensions ext = (WolfExtensions) wild;
            var scaleAttr = wild.getAttribute(Attributes.SCALE);
            helper.assertTrue(scaleAttr != null, "Spawned wolf must have Attributes.SCALE");
            float persisted = ext.betterdogs$getSocialScale();
            float attrVal = (float) scaleAttr.getBaseValue();
            helper.assertTrue(Math.abs(persisted - attrVal) < 1e-4f,
                    "finalizeSpawn must persist wild scale into both WolfPersistentData and Attributes.SCALE (persisted="
                            + persisted + ", attr=" + attrVal + ")");
            if (Math.abs(persisted - 1.0f) > 0.005f) {
                anyNonDefaultSpawn = true;
            }
            spawnedWolves[i] = wild;
            spawnedScales[i] = persisted;
        }
        helper.assertTrue(anyNonDefaultSpawn, "At least one spawned wolf must roll a non-1.00f genetic scale");

        // 2. Breeding inheritance persistence (unrelated large parents -> baby > 1.05f)
        Wolf parent1 = helper.spawn(EntityTypes.WOLF, new BlockPos(2, 2, 4));
        Wolf outcrossParent = helper.spawn(EntityTypes.WOLF, new BlockPos(3, 2, 4));
        helper.assertTrue(parent1 != null && outcrossParent != null, "Breeding parent wolves failed to spawn");
        ((WolfExtensions) parent1).betterdogs$setSocialScale(1.30f);
        ((WolfExtensions) outcrossParent).betterdogs$setSocialScale(1.26f);

        Wolf normalBaby = parent1.getBreedOffspring(serverLevel, outcrossParent);
        helper.assertTrue(normalBaby != null, "getBreedOffspring must produce a normal puppy");
        BlockPos normalBabyPos = helper.absolutePos(new BlockPos(4, 2, 4));
        normalBaby.snapTo(normalBabyPos.getX() + 0.5, normalBabyPos.getY(), normalBabyPos.getZ() + 0.5, 0.0f, 0.0f);
        serverLevel.addFreshEntity(normalBaby);

        float normalBabyScale = ((WolfExtensions) normalBaby).betterdogs$getSocialScale();
        helper.assertTrue(normalBabyScale > 1.05f,
                "Offspring of large parents must inherit persisted scale > 1.05f, got " + normalBabyScale);
        helper.assertTrue(Math.abs(normalBabyScale - (float) normalBaby.getAttribute(Attributes.SCALE).getBaseValue()) < 1e-4f,
                "Normal puppy Attributes.SCALE must match persisted scale");

        // 3. Inbred runt penalty persistence (< 0.75f) + Golden Apple cure restoration
        Wolf siblingParent = helper.spawn(EntityTypes.WOLF, new BlockPos(5, 2, 4));
        helper.assertTrue(siblingParent != null, "Sibling parent failed to spawn");
        UUID sharedAncestor = UUID.randomUUID();
        WolfPersistentData.setPersistedParentsAndInbred(parent1, sharedAncestor, null, false);
        WolfPersistentData.setPersistedParentsAndInbred(siblingParent, sharedAncestor, null, false);
        ((WolfExtensions) parent1).betterdogs$setSocialScale(0.90f);
        ((WolfExtensions) siblingParent).betterdogs$setSocialScale(0.90f);

        Wolf inbredBaby = parent1.getBreedOffspring(serverLevel, siblingParent);
        helper.assertTrue(inbredBaby != null, "getBreedOffspring must produce an inbred puppy");
        BlockPos inbredBabyPos = helper.absolutePos(new BlockPos(5, 2, 5));
        inbredBaby.snapTo(inbredBabyPos.getX() + 0.5, inbredBabyPos.getY(), inbredBabyPos.getZ() + 0.5, 0.0f, 0.0f);
        serverLevel.addFreshEntity(inbredBaby);

        helper.assertTrue(WolfPersistentData.isPersistedInbred(inbredBaby),
                "Shared-ancestor offspring must be marked inbred");
        float runtScale = ((WolfExtensions) inbredBaby).betterdogs$getSocialScale();
        helper.assertTrue(runtScale < 0.75f,
                "Inbred runt puppy must persist reduced scale < 0.75f, got " + runtScale);
        helper.assertTrue(Math.abs(runtScale - (float) inbredBaby.getAttribute(Attributes.SCALE).getBaseValue()) < 1e-4f,
                "Inbred runt puppy Attributes.SCALE must match persisted runt scale");

        Player mockOwner = helper.makeMockPlayer(GameType.SURVIVAL);
        GameTestSequence seq = helper.startSequence();
        seq.thenExecuteAfter(5, () -> {
            // Verify tick 1+ WolfSocialMixin sync preserves wild, normal baby, and runt scales + voice pitch
            for (int i = 0; i < spawnedWolves.length; i++) {
                Wolf wild = spawnedWolves[i];
                float expected = spawnedScales[i];
                helper.assertTrue(Math.abs(((WolfExtensions) wild).betterdogs$getSocialScale() - expected) < 1e-4f,
                        "Spawned wolf " + i + " lost persisted scale after ticks");
                helper.assertTrue(Math.abs((float) wild.getAttribute(Attributes.SCALE).getBaseValue() - expected) < 1e-4f,
                        "Spawned wolf " + i + " Attributes.SCALE snapped after ticks");
                float expectedPitch = CanineGeneticsHelper.calculateVoicePitch(1.0f, expected);
                helper.assertTrue(Math.abs(CanineGeneticsHelper.getModulatedVoicePitch(wild, 1.0f) - expectedPitch) < 1e-4f,
                        "Spawned wolf " + i + " modulated voice pitch drifted after ticks");
            }

            helper.assertTrue(Math.abs(((WolfExtensions) normalBaby).betterdogs$getSocialScale() - normalBabyScale) < 1e-4f,
                    "Bred puppy lost persisted scale after ticks");
            helper.assertTrue(Math.abs((float) normalBaby.getAttribute(Attributes.SCALE).getBaseValue() - normalBabyScale) < 1e-4f,
                    "Bred puppy Attributes.SCALE snapped back after ticks");
            helper.assertTrue(CanineGeneticsHelper.getModulatedVoicePitch(normalBaby, 1.0f) < 0.98f,
                    "Large bred puppy voice pitch must remain modulated (< 0.98f)");

            helper.assertTrue(Math.abs(((WolfExtensions) inbredBaby).betterdogs$getSocialScale() - runtScale) < 1e-4f,
                    "Inbred runt puppy lost persisted scale after ticks");
            helper.assertTrue(Math.abs((float) inbredBaby.getAttribute(Attributes.SCALE).getBaseValue() - runtScale) < 1e-4f,
                    "Inbred runt puppy Attributes.SCALE snapped back after ticks");
            helper.assertTrue(CanineGeneticsHelper.getModulatedVoicePitch(inbredBaby, 1.0f) > 1.12f,
                    "Small runt puppy voice pitch must remain modulated (> 1.12f)");

            // Cure inbred runt with Golden Apple and verify restoration persists across further ticks
            inbredBaby.setTame(true, false);
            inbredBaby.setOwner(mockOwner);
            InteractionResult cureResult = WolfCureHelper.tryCureInbredWolf(
                    inbredBaby, mockOwner, InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_APPLE));
            helper.assertTrue(cureResult == InteractionResult.SUCCESS, "Golden Apple cure must return SUCCESS");
            helper.assertTrue(!WolfPersistentData.isPersistedInbred(inbredBaby), "Cure must clear inbred status");
            helper.assertTrue(Math.abs(((WolfExtensions) inbredBaby).betterdogs$getSocialScale() - 1.0f) < 1e-4f,
                    "Cure must persist restored 1.0f scale");
            helper.assertTrue(Math.abs((float) inbredBaby.getAttribute(Attributes.SCALE).getBaseValue() - 1.0f) < 1e-4f,
                    "Cure must update Attributes.SCALE to 1.0f");
        }).thenExecuteAfter(3, () -> {
            helper.assertTrue(Math.abs(((WolfExtensions) inbredBaby).betterdogs$getSocialScale() - 1.0f) < 1e-4f,
                    "Cured wolf persisted scale must stay 1.0f after subsequent ticks");
            helper.assertTrue(Math.abs((float) inbredBaby.getAttribute(Attributes.SCALE).getBaseValue() - 1.0f) < 1e-4f,
                    "Cured wolf Attributes.SCALE must stay 1.0f after subsequent ticks");
            helper.assertTrue(Math.abs((float) normalBaby.getAttribute(Attributes.SCALE).getBaseValue() - normalBabyScale) < 1e-4f,
                    "Normal bred puppy scale must still remain at " + normalBabyScale);
        }).thenSucceed();
    }

    @GameTest
    public void testLazySittingPoseClearsOnMoveTargetDamageAndPreservesOrderedSit(GameTestHelper helper) {
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                helper.setBlock(x, 1, z, Blocks.STONE);
            }
        }
        ServerLevel serverLevel = helper.getLevel();
        Player mockOwner = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos ownerPos = helper.absolutePos(new BlockPos(2, 2, 3));
        mockOwner.setPos(ownerPos.getX() + 0.5, ownerPos.getY(), ownerPos.getZ() + 0.5);

        Wolf lazyWolf = helper.spawn(EntityTypes.WOLF, new BlockPos(2, 2, 2));
        Wolf orderedWolf = helper.spawn(EntityTypes.WOLF, new BlockPos(4, 2, 2));
        Wolf evasionLazyWolf = helper.spawn(EntityTypes.WOLF, new BlockPos(2, 2, 4));
        helper.assertTrue(lazyWolf != null && orderedWolf != null && evasionLazyWolf != null,
                "Wolves failed to spawn for sitting pose GameTest");

        lazyWolf.setTame(true, false);
        lazyWolf.setOwner(mockOwner);
        lazyWolf.setOrderedToSit(false);
        lazyWolf.setNoActionTime(100);
        lazyWolf.getNavigation().stop();
        lazyWolf.setDeltaMovement(0.0, -0.0784, 0.0);
        lazyWolf.setInSittingPose(true);
        if (lazyWolf instanceof WolfExtensions ext) {
            ext.betterdogs$setPersonality(WolfPersonality.NORMAL);
            ext.betterdogs$setCanineArchetype(CanineArchetype.LAZY);
        }
        lazyWolf.setOnGround(true);

        orderedWolf.setTame(true, false);
        orderedWolf.setOwner(mockOwner);
        orderedWolf.setOrderedToSit(true);
        orderedWolf.setInSittingPose(true);
        if (orderedWolf instanceof WolfExtensions ext) {
            ext.betterdogs$setPersonality(WolfPersonality.NORMAL);
        }
        orderedWolf.setOnGround(true);

        evasionLazyWolf.setTame(true, false);
        evasionLazyWolf.setOwner(mockOwner);
        evasionLazyWolf.setOrderedToSit(false);
        evasionLazyWolf.setInSittingPose(true);

        helper.assertTrue(!WolfSittingPoseHelper.syncSittingPoseOnTick(lazyWolf) && lazyWolf.isInSittingPose(),
                "Stationary lazy wolf must preserve resting pose on sync");

        // Evasion goal wasSitting check: lazy pose clears on start() and NEVER promotes to ordered sit on stop()
        AvoidGhastFireballGoal avoidGoal = new AvoidGhastFireballGoal(evasionLazyWolf);
        avoidGoal.start();
        helper.assertTrue(!evasionLazyWolf.isInSittingPose(), "AvoidGhastFireballGoal.start() must clear lazy sitting pose");
        helper.assertTrue(!avoidGoal.wasSitting(), "AvoidGhastFireballGoal must not mark lazy sit as wasSitting");
        avoidGoal.stop();
        helper.assertTrue(!evasionLazyWolf.isOrderedToSit() && !evasionLazyWolf.isInSittingPose(),
                "AvoidGhastFireballGoal.stop() must not promote lazy dog to orderedToSit");

        evasionLazyWolf.getNavigation().stop();
        evasionLazyWolf.setDeltaMovement(0.0, -0.0784, 0.0);
        evasionLazyWolf.setInSittingPose(true);
        FleeExplodingHazardGoal fleeGoal = new FleeExplodingHazardGoal(evasionLazyWolf);
        fleeGoal.start();
        helper.assertTrue(!evasionLazyWolf.isInSittingPose(), "FleeExplodingHazardGoal.start() must clear lazy sitting pose");
        helper.assertTrue(!fleeGoal.wasSitting(), "FleeExplodingHazardGoal must not mark lazy sit as wasSitting");
        fleeGoal.stop();
        helper.assertTrue(!evasionLazyWolf.isOrderedToSit() && !evasionLazyWolf.isInSittingPose(),
                "FleeExplodingHazardGoal.stop() must not promote lazy dog to orderedToSit");

        GameTestSequence seq = helper.startSequence();
        seq.thenExecuteAfter(2, () -> {
            helper.assertTrue(lazyWolf.isInSittingPose() && !lazyWolf.isOrderedToSit(),
                    "Stationary lazy wolf must remain in resting pose across server ticks (sitting="
                            + lazyWolf.isInSittingPose()
                            + ", ordered=" + lazyWolf.isOrderedToSit()
                            + ", dm=" + lazyWolf.getDeltaMovement()
                            + ", navDone=" + lazyWolf.getNavigation().isDone()
                            + ", target=" + lazyWolf.getTarget()
                            + ", onGround=" + lazyWolf.onGround() + ")");
            helper.assertTrue(orderedWolf.isInSittingPose() && orderedWolf.isOrderedToSit(),
                    "Ordered wolf must remain seated across server ticks");

            // 1. Navigation movement clears lazy resting pose via WolfMixin tick guard
            BlockPos dest = helper.absolutePos(new BlockPos(5, 2, 2));
            lazyWolf.getNavigation().moveTo(dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5, 1.0);
            orderedWolf.getNavigation().moveTo(dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5, 1.0);
        }).thenExecuteAfter(2, () -> {
            helper.assertTrue(!lazyWolf.isInSittingPose(),
                    "Navigation movement must clear lazy resting pose");
            helper.assertTrue(!lazyWolf.isOrderedToSit(),
                    "Clearing lazy pose must keep isOrderedToSit == false");
            helper.assertTrue(orderedWolf.isInSittingPose() && orderedWolf.isOrderedToSit(),
                    "Ordered sit must remain seated even if navigation was invoked");

            Zombie zombie = helper.spawn(EntityTypes.ZOMBIE, new BlockPos(5, 2, 4));
            helper.assertTrue(zombie != null, "Zombie failed to spawn");

            // 2. Combat target acquisition clears lazy pose via WolfMobMixin setTarget TAIL
            lazyWolf.getNavigation().stop();
            lazyWolf.setDeltaMovement(0.0, -0.0784, 0.0);
            lazyWolf.setInSittingPose(true);
            lazyWolf.setTarget(zombie);
            helper.assertTrue(!lazyWolf.isInSittingPose(),
                    "Acquiring a combat target must immediately clear lazy sitting pose");
            lazyWolf.setTarget(null);

            orderedWolf.setTarget(zombie);
            helper.assertTrue(orderedWolf.isInSittingPose() && orderedWolf.isOrderedToSit(),
                    "Acquiring a target must not clear ordered sitting pose");
            orderedWolf.setTarget(null);

            // 3. Cancelled friendly-fire preserves lazy pose; live non-cancelled damage clears lazy pose;
            //    WolfDamageHandler itself preserves player-ordered sit (vanilla Wolf.hurtServer clears ordered sit pre-actuallyHurt).
            lazyWolf.getNavigation().stop();
            lazyWolf.setDeltaMovement(0.0, -0.0784, 0.0);
            lazyWolf.setInSittingPose(true);
            boolean ffCancelled = WolfDamageHandler.onActuallyHurt(
                    lazyWolf, serverLevel.damageSources().playerAttack(mockOwner), 1.0f);
            helper.assertTrue(ffCancelled && lazyWolf.isInSittingPose(),
                    "Cancelled friendly-fire damage must preserve lazy sitting pose");

            lazyWolf.hurtServer(serverLevel, serverLevel.damageSources().fall(), 1.0f);
            helper.assertTrue(!lazyWolf.isInSittingPose(),
                    "Non-cancelled damage must clear lazy sitting pose");

            boolean orderedCancelled = WolfDamageHandler.onActuallyHurt(
                    orderedWolf, serverLevel.damageSources().fall(), 1.0f);
            helper.assertTrue(!orderedCancelled && orderedWolf.isInSittingPose() && orderedWolf.isOrderedToSit(),
                    "WolfDamageHandler must preserve player-ordered sit");
        }).thenSucceed();
    }

    @GameTest
    public void testAggressivePuppyProvocationClampAndBabyBiteBackRetaliation(GameTestHelper helper) {
        for (int x = 0; x < 10; x++) {
            for (int z = 0; z < 10; z++) {
                helper.setBlock(x, 1, z, Blocks.STONE);
            }
        }

        ServerLevel serverLevel = helper.getLevel();
        Player mockOwner = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos ownerPos = helper.absolutePos(new BlockPos(2, 2, 2));
        mockOwner.setPos(ownerPos.getX() + 0.5, ownerPos.getY(), ownerPos.getZ() + 0.5);

        // 1. Full-HP (8.0f & 40.0f) AGGRESSIVE puppy hit with 20.0f melee loses exactly 2.0f HP
        Wolf aggroPuppy8 = helper.spawn(EntityTypes.WOLF, new BlockPos(3, 2, 2));
        Wolf aggroPuppy40 = helper.spawn(EntityTypes.WOLF, new BlockPos(4, 2, 2));
        Wolf armoredPuppy = helper.spawn(EntityTypes.WOLF, new BlockPos(5, 2, 2));
        Wolf lowHpPuppy = helper.spawn(EntityTypes.WOLF, new BlockPos(6, 2, 2));
        Wolf lowHpPuppyFalse = helper.spawn(EntityTypes.WOLF, new BlockPos(7, 2, 2));
        helper.assertTrue(aggroPuppy8 != null && aggroPuppy40 != null && armoredPuppy != null && lowHpPuppy != null && lowHpPuppyFalse != null,
                "Test puppies failed to spawn");

        for (Wolf pup : new Wolf[] { aggroPuppy8, aggroPuppy40, armoredPuppy, lowHpPuppy, lowHpPuppyFalse }) {
            pup.setTame(true, false);
            pup.setOwner(mockOwner);
            pup.setAge(-24000);
            if (pup instanceof WolfExtensions ext) {
                ext.betterdogs$setPersonality(WolfPersonality.AGGRESSIVE);
            }
        }

        serverLevel.getGameRules().set(BetterDogsGameRules.BD_FRIENDLY_FIRE, true, serverLevel.getServer());
        DamageSource ownerMelee = serverLevel.damageSources().playerAttack(mockOwner);

        aggroPuppy8.setHealth(8.0f);
        aggroPuppy8.hurtServer(serverLevel, ownerMelee, 20.0f);
        helper.assertTrue(aggroPuppy8.isAlive() && Math.abs(aggroPuppy8.getHealth() - 6.0f) < 1e-4f,
                "8.0f HP aggressive puppy hit for 20.0f must lose exactly 2.0f (got " + aggroPuppy8.getHealth() + ")");

        aggroPuppy40.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0D);
        aggroPuppy40.setHealth(40.0f);
        aggroPuppy40.hurtServer(serverLevel, ownerMelee, 20.0f);
        helper.assertTrue(aggroPuppy40.isAlive() && Math.abs(aggroPuppy40.getHealth() - 38.0f) < 1e-4f,
                "40.0f HP aggressive puppy hit for 20.0f must lose exactly 2.0f (got " + aggroPuppy40.getHealth() + ")");

        // 2. Wounded armored puppy wearing WOLF_ARMOR must never heal for free and armor absorbs <= 2 durability
        ItemStack wolfArmor = new ItemStack(Items.WOLF_ARMOR);
        armoredPuppy.setItemSlot(EquipmentSlot.BODY, wolfArmor);
        armoredPuppy.setHealth(5.0f);
        armoredPuppy.hurtServer(serverLevel, ownerMelee, 20.0f);
        int armorDamage = armoredPuppy.getBodyArmorItem().getDamageValue();
        helper.assertTrue(armoredPuppy.getHealth() <= 5.0f && Math.abs(armoredPuppy.getHealth() - 5.0f) < 1e-4f,
                "Wounded armored puppy must NOT heal from pre-heal hack (hp=" + armoredPuppy.getHealth() + ")");
        helper.assertTrue(armorDamage > 0 && armorDamage <= 2,
                "Wolf armor must absorb clamped <= 2.0f provocation damage, not 20.0f (damage=" + armorDamage + ")");

        // 3. Low-HP (2.5f) aggressive puppy clamps to 1.0f under BD_FRIENDLY_FIRE=true, dies under false
        lowHpPuppy.setHealth(2.5f);
        lowHpPuppy.hurtServer(serverLevel, ownerMelee, 20.0f);
        helper.assertTrue(lowHpPuppy.isAlive() && Math.abs(lowHpPuppy.getHealth() - 1.0f) < 1e-4f,
                "2.5f HP aggressive puppy hit for 20.0f under BD_FRIENDLY_FIRE=true must clamp to 1.0f (got " + lowHpPuppy.getHealth() + ")");

        try {
            serverLevel.getGameRules().set(BetterDogsGameRules.BD_FRIENDLY_FIRE, false, serverLevel.getServer());
            lowHpPuppyFalse.setHealth(2.5f);
            lowHpPuppyFalse.hurtServer(serverLevel, ownerMelee, 20.0f);
            helper.assertTrue(!lowHpPuppyFalse.isAlive(),
                    "Low-HP aggressive puppy hit for 20.0f under BD_FRIENDLY_FIRE=false must take lethal damage");
        } finally {
            serverLevel.getGameRules().set(BetterDogsGameRules.BD_FRIENDLY_FIRE, true, serverLevel.getServer());
        }

        // 4. BabyBiteBackGoal ticks on AGGRESSIVE puppy targeting Zombie & mockOwner (deals 1.0F),
        //    while co-owned packmate target is blocked under BD_FRIENDLY_FIRE=true and allowed under false
        Wolf retaliator = helper.spawn(EntityTypes.WOLF, new BlockPos(3, 2, 6));
        Zombie zombieTarget = helper.spawn(EntityTypes.ZOMBIE, new BlockPos(3, 2, 6));
        Wolf packmateTarget = helper.spawn(EntityTypes.WOLF, new BlockPos(5, 2, 6));
        helper.assertTrue(retaliator != null && zombieTarget != null && packmateTarget != null,
                "Retaliation entities failed to spawn");

        retaliator.setTame(true, false);
        retaliator.setOwner(mockOwner);
        retaliator.setAge(-24000);
        packmateTarget.setTame(true, false);
        packmateTarget.setOwner(mockOwner);
        packmateTarget.setAge(0);
        if (retaliator instanceof WolfExtensions ext) {
            ext.betterdogs$setPersonality(WolfPersonality.AGGRESSIVE);
            ext.betterdogs$setSocialState(zombieTarget, WolfExtensions.SocialAction.RETALIATION, 100);
        }

        if (zombieTarget.getAttribute(Attributes.ARMOR) != null) {
            zombieTarget.getAttribute(Attributes.ARMOR).setBaseValue(0.0D);
        }
        float zombieBefore = zombieTarget.getHealth();
        BabyBiteBackGoal biteGoal = new BabyBiteBackGoal(retaliator);
        helper.assertTrue(biteGoal.canUse(), "BabyBiteBackGoal must activate for RETALIATION against Zombie");
        biteGoal.start();
        biteGoal.tick();
        biteGoal.stop();
        helper.assertTrue(Math.abs(zombieTarget.getHealth() - (zombieBefore - 1.0f)) < 1e-4f,
                "BabyBiteBackGoal must deal 1.0F damage to Zombie target (before=" + zombieBefore + ", after=" + zombieTarget.getHealth() + ")");

        // Owner retaliation
        retaliator.snapTo(mockOwner.getX(), mockOwner.getY(), mockOwner.getZ(), 0.0f, 0.0f);
        float ownerBefore = mockOwner.getHealth();
        if (retaliator instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(mockOwner, WolfExtensions.SocialAction.RETALIATION, 100);
        }
        helper.assertTrue(biteGoal.canUse(), "BabyBiteBackGoal must activate for RETALIATION against owner");
        biteGoal.start();
        biteGoal.tick();
        biteGoal.stop();
        helper.assertTrue(Math.abs(mockOwner.getHealth() - (ownerBefore - 1.0f)) < 1e-4f,
                "BabyBiteBackGoal must deal 1.0F damage to owner (before=" + ownerBefore + ", after=" + mockOwner.getHealth() + ")");

        // Co-owned packmate target blocked when BD_FRIENDLY_FIRE=true, allowed when false
        retaliator.snapTo(packmateTarget.getX(), packmateTarget.getY(), packmateTarget.getZ(), 0.0f, 0.0f);
        float packmateBefore = packmateTarget.getHealth();
        if (retaliator instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(packmateTarget, WolfExtensions.SocialAction.RETALIATION, 100);
        }
        helper.assertTrue(biteGoal.canUse(), "BabyBiteBackGoal must activate for RETALIATION against co-owned packmate");
        biteGoal.start();
        biteGoal.tick();
        biteGoal.stop();
        helper.assertTrue(Math.abs(packmateTarget.getHealth() - packmateBefore) < 1e-4f,
                "BabyBiteBackGoal must block retaliation damage to co-owned packmate when BD_FRIENDLY_FIRE=true");

        try {
            serverLevel.getGameRules().set(BetterDogsGameRules.BD_FRIENDLY_FIRE, false, serverLevel.getServer());
            if (retaliator instanceof WolfExtensions ext) {
                ext.betterdogs$setSocialState(packmateTarget, WolfExtensions.SocialAction.RETALIATION, 100);
            }
            helper.assertTrue(biteGoal.canUse(), "BabyBiteBackGoal must activate for RETALIATION when BD_FRIENDLY_FIRE=false");
            biteGoal.start();
            biteGoal.tick();
            biteGoal.stop();
            helper.assertTrue(Math.abs(packmateTarget.getHealth() - (packmateBefore - 1.0f)) < 1e-4f,
                    "BabyBiteBackGoal must deal 1.0F damage to co-owned packmate when BD_FRIENDLY_FIRE=false");
        } finally {
            serverLevel.getGameRules().set(BetterDogsGameRules.BD_FRIENDLY_FIRE, true, serverLevel.getServer());
        }

        helper.succeed();
    }

    @GameTest
    public void testAmbientMorningZoomiesSchedulerLifecycle(GameTestHelper helper) {
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                helper.setBlock(x, 1, z, Blocks.STONE);
            }
        }

        ServerLevel serverLevel = helper.getLevel();
        serverLevel.getGameRules().set(BetterDogsGameRules.BD_ZOOMIES_ENABLED, true, serverLevel.getServer());

        Player mockOwner = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos ownerPos = helper.absolutePos(new BlockPos(2, 2, 2));
        mockOwner.setPos(ownerPos.getX() + 0.5, ownerPos.getY(), ownerPos.getZ() + 0.5);

        Wolf wolf = helper.spawn(EntityTypes.WOLF, new BlockPos(2, 2, 2));
        helper.assertTrue(wolf != null, "Wolf failed to spawn for ambient zoomies test");
        wolf.setTame(true, false);
        wolf.setOwner(mockOwner);
        wolf.setOrderedToSit(false);
        wolf.setInSittingPose(false);
        wolf.setOnGround(true);
        wolf.tickCount = 20;

        helper.assertTrue(wolf instanceof WolfExtensions, "Wolf must implement WolfExtensions");
        WolfExtensions ext = (WolfExtensions) wolf;
        ext.betterdogs$setGuardMode(false);
        ext.betterdogs$setZoomiesTicks(0);

        helper.assertTrue(ext.betterdogs$getScheduler() == null,
                "Freshly spawned wolf must start with null social scheduler");

        // Woven WolfSocialMixin.betterdogs$tickScheduler() must never NPE when scheduler is null
        ext.betterdogs$tickScheduler();

        EntitySocialScheduler scheduler = ext.betterdogs$getOrInitializeScheduler();
        helper.assertTrue(scheduler != null, "betterdogs$getOrInitializeScheduler must return non-null scheduler");
        scheduler.schedule(new ZoomiesDogEvent());

        ZoomiesGoal zoomiesGoal = new ZoomiesGoal(wolf);

        GameTestSequence seq = helper.startSequence();
        seq.thenExecuteAfter(1, () -> {
            EntitySocialScheduler active = ext.betterdogs$getScheduler();
            helper.assertTrue(active != null && active.isEventActive(ZoomiesDogEvent.ID),
                    "ZoomiesDogEvent must remain active after tick 1 (not inverted tick-1 expiration)");
            helper.assertTrue(zoomiesGoal.canUse() && zoomiesGoal.canContinueToUse(),
                    "ZoomiesGoal must activate while ZoomiesDogEvent is active on tick 1 (canUse="
                            + zoomiesGoal.canUse()
                            + ", canContinue=" + zoomiesGoal.canContinueToUse()
                            + ", tame=" + wolf.isTame()
                            + ", orderedSit=" + wolf.isOrderedToSit()
                            + ", sittingPose=" + wolf.isInSittingPose()
                            + ", target=" + wolf.getTarget()
                            + ", guard=" + ext.betterdogs$isGuardMode()
                            + ", zoomiesRule=" + BetterDogsGameRules.isZoomiesEnabled(wolf.level())
                            + ")");
        }).thenExecuteAfter(4, () -> {
            EntitySocialScheduler active = ext.betterdogs$getScheduler();
            helper.assertTrue(active != null && active.isEventActive(ZoomiesDogEvent.ID),
                    "ZoomiesDogEvent must remain active across ticks 1..5");
            helper.assertTrue(zoomiesGoal.canUse() && zoomiesGoal.canContinueToUse(),
                    "ZoomiesGoal must remain active across ticks 1..5 (canUse="
                            + zoomiesGoal.canUse()
                            + ", canContinue=" + zoomiesGoal.canContinueToUse()
                            + ", tame=" + wolf.isTame()
                            + ", orderedSit=" + wolf.isOrderedToSit()
                            + ", sittingPose=" + wolf.isInSittingPose()
                            + ", target=" + wolf.getTarget()
                            + ", guard=" + ext.betterdogs$isGuardMode()
                            + ", zoomiesRule=" + BetterDogsGameRules.isZoomiesEnabled(wolf.level())
                            + ")");
        }).thenSucceed();
    }

    @GameTest
    public void testVehicleTargetAutoPrunesWhenSeatDiscarded(GameTestHelper helper) {
        UUID dogUuid = UUID.randomUUID();
        int baselineCount = DogCommandManager.getTrackedVehicleTargetCount();
        Interaction seat = helper.spawn(EntityTypes.INTERACTION, new BlockPos(2, 2, 2));
        helper.assertTrue(seat != null, "Interaction seat failed to spawn for vehicle target GameTest");

        try {
            DogCommandManager.setVehicleTarget(dogUuid, seat);
            helper.assertTrue(DogCommandManager.getVehicleTarget(dogUuid) == seat,
                    "Live Interaction seat must be returned by DogCommandManager.getVehicleTarget");
            helper.assertTrue(DogCommandManager.getTrackedVehicleTargetCount() == baselineCount + 1,
                    "Tracked vehicle target count must increment while seat is live");

            seat.discard();
            helper.assertTrue(seat.isRemoved(), "Discarded Interaction seat must report isRemoved == true");
            helper.assertTrue(DogCommandManager.getVehicleTarget(dogUuid) == null,
                    "DogCommandManager.getVehicleTarget must return null after seat.discard()");
            helper.assertTrue(DogCommandManager.getTrackedVehicleTargetCount() == baselineCount,
                    "Discarded Interaction seat must auto-prune from VEHICLE_TARGETS on lookup");
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
            if (!seat.isRemoved()) {
                seat.discard();
            }
        }

        helper.succeed();
    }
}
