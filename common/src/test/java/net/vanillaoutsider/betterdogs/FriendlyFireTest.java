// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.11
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FriendlyFireTest {

    @BeforeAll
    public static void setupMinecraftBootstrap() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

    private static Path findProjectRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (dir != null) {
            if (Files.exists(dir.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfFriendlyFireHelper.java"))) {
                return dir;
            }
            dir = dir.getParent();
        }
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

    @Test
    @DisplayName("Verify Friendly Fire Null Safety")
    public void testNullSafety() {
        Assertions.assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(null, null));
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(null, null));
    }

    @Test
    @DisplayName("BL-BD-041: Assert bidirectional Blood Feud exemption, symmetry, and bystander protection")
    public void testBloodFeudBidirectionalExemption() {
        UUID ownerId = UUID.randomUUID();
        UUID dogAId = UUID.randomUUID();
        UUID dogBId = UUID.randomUUID();
        UUID dogCId = UUID.randomUUID();

        PackCivilWarImmunityTest.OfflineStubWolf dogA = PackCivilWarImmunityTest.createOfflineWolf(dogAId, ownerId, true, false);
        PackCivilWarImmunityTest.OfflineStubWolf dogB = PackCivilWarImmunityTest.createOfflineWolf(dogBId, ownerId, true, false);
        PackCivilWarImmunityTest.OfflineStubWolf dogC = PackCivilWarImmunityTest.createOfflineWolf(dogCId, ownerId, true, false);

        // Dog A initiates blood feud against Dog B
        dogA.betterdogs$setBloodFeudTarget(dogBId.toString());

        // 1. Bidirectional Feud Pair Symmetry (Crucial fix for defender retaliation)
        Assertions.assertTrue(WolfFriendlyFireHelper.isBloodFeudPair(dogA, dogB), "Aggressor -> Victim must be in feud pair");
        Assertions.assertTrue(WolfFriendlyFireHelper.isBloodFeudPair(dogB, dogA), "Defending Victim -> Aggressor must be in feud pair (bidirectional)");

        // 2. Anti-Cross-Bleed Protection (Anti-Goal 2: Bystander co-owned dogs remain fully protected)
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogA, dogC), "Aggressor with bystander must NOT be in feud pair");
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogC, dogA), "Bystander with aggressor must NOT be in feud pair");
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogB, dogC), "Victim with bystander must NOT be in feud pair");

        // 3. Null & Empty Safety (PAT-20261114-001)
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(null, null), "Null wolves must return false");
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogA, null), "Null target must return false");
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(null, dogB), "Null source must return false");

        // Empty feud target
        dogA.betterdogs$setBloodFeudTarget("");
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogA, dogB), "Empty feud target must return false");
    }

    @Test
    @DisplayName("BL-BD-063: Verify WolfFriendlyFireHelper & BetterDogsGameRules positive polarity and server fallback contract")
    public void testFriendlyFireSourceContracts() throws IOException {
        Path root = findProjectRoot();
        String helperSource = Files.readString(
                root.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfFriendlyFireHelper.java"),
                StandardCharsets.UTF_8);
        String rulesSource = Files.readString(
                root.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/registry/BetterDogsGameRules.java"),
                StandardCharsets.UTF_8);

        Assertions.assertTrue(helperSource.contains("BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_FRIENDLY_FIRE, true)"),
                "WolfFriendlyFireHelper must read BD_FRIENDLY_FIRE with default-true protection fallback");
        Assertions.assertFalse(helperSource.contains("BetterDogsGameRules.BD_FRIENDLY_FIRE, false"),
                "WolfFriendlyFireHelper must never fall back to false for BD_FRIENDLY_FIRE");
        Assertions.assertFalse(helperSource.contains("!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_FRIENDLY_FIRE"),
                "WolfFriendlyFireHelper must not negate getBoolean directly");

        int gateIdx = helperSource.indexOf("if (!friendlyFireEnabled)");
        int feudIdx = helperSource.indexOf("betterdogs$hasBloodFeud()");
        int coOwnedIdx = helperSource.indexOf("PackAudienceHelper.isCoOwned");
        Assertions.assertTrue(gateIdx > 0 && feudIdx > gateIdx && coOwnedIdx > feudIdx,
                "Friendly-fire gate must run before Blood Feud exemption, which must precede PackAudienceHelper.isCoOwned");

        Assertions.assertTrue(helperSource.contains("isBloodFeudPair("),
                "WolfFriendlyFireHelper must invoke isBloodFeudPair");

        Assertions.assertTrue(rulesSource.contains("level instanceof ServerLevel serverLevel"),
                "BetterDogsGameRules.getBoolean must query ServerLevel.getGameRules() directly before fallback");
        Assertions.assertTrue(rulesSource.contains("catch (Throwable t)"),
                "BetterDogsGameRules.getBoolean must preserve caller fallback cleanly on Throwable");
    }

    @Test
    @DisplayName("BL-BD-050: Verify WolfMixin @ModifyVariable hurtServer damage clamp and BabyBiteBackGoal retaliation contract")
    public void testWolfMixinModifyVariableAndBabyBiteBackContracts() throws IOException {
        Path root = findProjectRoot();
        String wolfMixinSource = Files.readString(
                root.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/mixin/WolfMixin.java"),
                StandardCharsets.UTF_8);
        String goalSource = Files.readString(
                root.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/ai/BabyBiteBackGoal.java"),
                StandardCharsets.UTF_8);

        Assertions.assertTrue(wolfMixinSource.contains("@ModifyVariable(method = \"hurtServer\", at = @At(\"HEAD\"), argsOnly = true, ordinal = 0)"),
                "WolfMixin must declare @ModifyVariable on hurtServer amount at HEAD");
        Assertions.assertTrue(wolfMixinSource.contains("WolfFriendlyFireHelper.clampProvocationDamage((Wolf) (Object) this, source, amount)"),
                "WolfMixin @ModifyVariable must delegate to WolfFriendlyFireHelper.clampProvocationDamage");
        Assertions.assertTrue(wolfMixinSource.contains("WolfFriendlyFireHelper.shouldCancelDamage(wolf, source, amount)"),
                "WolfMixin hurtServer must pass amount into 3-arg WolfFriendlyFireHelper.shouldCancelDamage");
        Assertions.assertTrue(goalSource.contains("WolfFriendlyFireHelper.shouldCancelRetaliationDamage(this.wolf, this.target, source)"),
                "BabyBiteBackGoal must filter retaliation hits via shouldCancelRetaliationDamage(this.wolf, this.target, source)");
    }

    @Test
    @DisplayName("BL-BD-050: Assert aggressive baby provocation clamp, zero free-heal & low-HP floor across BD_FRIENDLY_FIRE polarity")
    public void testAggressiveBabyProvocationClampAndDualPolarity() {
        PackCivilWarImmunityTest.ensureBootstrap();
        try {
            if (BetterDogsGameRules.BD_FRIENDLY_FIRE == null) {
                BetterDogsGameRules.register();
            }
        } catch (Throwable ignored) {
        }
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();

        PackCivilWarImmunityTest.OfflineStubWolf aggroBaby = PackCivilWarImmunityTest.createOfflineWolf(UUID.randomUUID(), ownerA, true, true);
        aggroBaby.betterdogs$setPersonality(WolfPersonality.AGGRESSIVE);
        aggroBaby.setStubMaxHealth(40.0f);
        PackCivilWarImmunityTest.OfflineStubWolf normalBaby = PackCivilWarImmunityTest.createOfflineWolf(UUID.randomUUID(), ownerA, true, true);
        normalBaby.betterdogs$setPersonality(WolfPersonality.NORMAL);
        PackCivilWarImmunityTest.OfflineStubWolf aggroAdult = PackCivilWarImmunityTest.createOfflineWolf(UUID.randomUUID(), ownerA, true, false);
        aggroAdult.betterdogs$setPersonality(WolfPersonality.AGGRESSIVE);

        PackCivilWarImmunityTest.StubServerLevel level = PackCivilWarImmunityTest.createStubServerLevel(
                100L, List.of(aggroBaby, normalBaby, aggroAdult));
        aggroBaby.attachLevel(level);
        normalBaby.attachLevel(level);
        aggroAdult.attachLevel(level);

        PackCivilWarImmunityTest.StubServerPlayer standingOwner = PackCivilWarImmunityTest.createStubPlayer(ownerA, false);
        PackCivilWarImmunityTest.StubServerPlayer crouchingOwner = PackCivilWarImmunityTest.createStubPlayer(ownerA, true);
        PackCivilWarImmunityTest.StubServerPlayer rivalPlayer = PackCivilWarImmunityTest.createStubPlayer(ownerB, false);

        DamageSource ownerMelee = new DamageSource(PackCivilWarImmunityTest.mobDamageType, standingOwner, standingOwner);
        DamageSource crouchMelee = new DamageSource(PackCivilWarImmunityTest.mobDamageType, crouchingOwner, crouchingOwner);
        DamageSource ownerProjectile = new DamageSource(PackCivilWarImmunityTest.mobDamageType, null, standingOwner);
        DamageSource rivalMelee = new DamageSource(PackCivilWarImmunityTest.mobDamageType, rivalPlayer, rivalPlayer);

        // 1. BD_FRIENDLY_FIRE = true: heavy hits clamp to 2.0f without pre-healing
        GameRuleMap trueMap = GameRuleMap.of();
        trueMap.set(BetterDogsGameRules.BD_FRIENDLY_FIRE, true);
        level.setGameRules(PackCivilWarImmunityTest.createStubGameRules(trueMap));

        Assertions.assertEquals(2.0f, WolfFriendlyFireHelper.clampProvocationDamage(aggroBaby, ownerMelee, 20.0f), 1e-5f);
        Assertions.assertEquals(2.0f, WolfFriendlyFireHelper.clampProvocationDamage(aggroBaby, ownerMelee, 11.0f), 1e-5f);
        Assertions.assertEquals(2.0f, WolfFriendlyFireHelper.clampProvocationDamage(aggroBaby, ownerMelee, Float.MAX_VALUE), 1e-5f);
        Assertions.assertEquals(1.5f, WolfFriendlyFireHelper.clampProvocationDamage(aggroBaby, ownerMelee, 1.5f), 1e-5f);
        Assertions.assertTrue(Float.isNaN(WolfFriendlyFireHelper.clampProvocationDamage(aggroBaby, ownerMelee, Float.NaN)));

        // Non-qualifying variants remain unclamped
        Assertions.assertEquals(20.0f, WolfFriendlyFireHelper.clampProvocationDamage(aggroBaby, crouchMelee, 20.0f), 1e-5f);
        Assertions.assertEquals(20.0f, WolfFriendlyFireHelper.clampProvocationDamage(aggroBaby, ownerProjectile, 20.0f), 1e-5f);
        Assertions.assertEquals(20.0f, WolfFriendlyFireHelper.clampProvocationDamage(aggroBaby, rivalMelee, 20.0f), 1e-5f);
        Assertions.assertEquals(20.0f, WolfFriendlyFireHelper.clampProvocationDamage(normalBaby, ownerMelee, 20.0f), 1e-5f);
        Assertions.assertEquals(20.0f, WolfFriendlyFireHelper.clampProvocationDamage(aggroAdult, ownerMelee, 20.0f), 1e-5f);

        // Full HP (8.0f & 40.0f) and wounded armored baseline (5.0f): shouldCancelDamage returns false and NEVER increases HP
        aggroBaby.setHealth(8.0f);
        Assertions.assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(aggroBaby, ownerMelee, 20.0f));
        Assertions.assertEquals(8.0f, aggroBaby.getHealth(), 1e-5f, "Full HP (8.0f) puppy must not be pre-healed");

        aggroBaby.setHealth(40.0f);
        Assertions.assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(aggroBaby, ownerMelee, 20.0f));
        Assertions.assertEquals(40.0f, aggroBaby.getHealth(), 1e-5f, "Full HP (40.0f) puppy must not be pre-healed");

        aggroBaby.setHealth(5.0f);
        Assertions.assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(aggroBaby, ownerMelee, 20.0f));
        Assertions.assertEquals(5.0f, aggroBaby.getHealth(), 1e-5f, "Wounded puppy (e.g. wearing WOLF_ARMOR) must gain zero free HP");

        // Low-HP aggressive puppy (<= 3.0f) clamps cleanly to 1.0f non-lethal floor when BD_FRIENDLY_FIRE=true
        aggroBaby.setHealth(2.5f);
        Assertions.assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(aggroBaby, ownerMelee, 20.0f));
        Assertions.assertEquals(1.0f, aggroBaby.getHealth(), 1e-5f, "Low-HP aggressive puppy must clamp to 1.0f non-lethal floor");

        aggroBaby.setHealth(3.0f);
        Assertions.assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(aggroBaby, ownerMelee, 20.0f));
        Assertions.assertEquals(1.0f, aggroBaby.getHealth(), 1e-5f, "3.0f HP puppy taking clamped 2.0f provocation hit must clamp to 1.0f");

        // Non-aggressive baby & adult remain protected when BD_FRIENDLY_FIRE=true
        Assertions.assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(normalBaby, ownerMelee, 20.0f));
        Assertions.assertTrue(WolfFriendlyFireHelper.shouldCancelDamage(aggroAdult, ownerMelee, 20.0f));

        // 2. BD_FRIENDLY_FIRE = false: zero clamping, zero cancellation
        GameRuleMap falseMap = GameRuleMap.of();
        falseMap.set(BetterDogsGameRules.BD_FRIENDLY_FIRE, false);
        level.setGameRules(PackCivilWarImmunityTest.createStubGameRules(falseMap));

        Assertions.assertEquals(20.0f, WolfFriendlyFireHelper.clampProvocationDamage(aggroBaby, ownerMelee, 20.0f), 1e-5f);
        aggroBaby.setHealth(2.5f);
        Assertions.assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(aggroBaby, ownerMelee, 20.0f));
        Assertions.assertEquals(2.5f, aggroBaby.getHealth(), 1e-5f, "BD_FRIENDLY_FIRE=false must not clamp HP");
    }

    @Test
    @DisplayName("BL-BD-050: Assert shouldCancelRetaliationDamage target filtering across BD_FRIENDLY_FIRE true/false")
    public void testShouldCancelRetaliationDamageDualPolarity() {
        PackCivilWarImmunityTest.ensureBootstrap();
        try {
            if (BetterDogsGameRules.BD_FRIENDLY_FIRE == null) {
                BetterDogsGameRules.register();
            }
        } catch (Throwable ignored) {
        }
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();

        PackCivilWarImmunityTest.OfflineStubWolf aggroPuppy = PackCivilWarImmunityTest.createOfflineWolf(UUID.randomUUID(), ownerA, true, true);
        aggroPuppy.betterdogs$setPersonality(WolfPersonality.AGGRESSIVE);
        PackCivilWarImmunityTest.OfflineStubWolf packmate = PackCivilWarImmunityTest.createOfflineWolf(UUID.randomUUID(), ownerA, true, false);
        PackCivilWarImmunityTest.OfflineStubWolf feudPackmate = PackCivilWarImmunityTest.createOfflineWolf(UUID.randomUUID(), ownerA, true, false);
        PackCivilWarImmunityTest.OfflineStubWolf rivalWolf = PackCivilWarImmunityTest.createOfflineWolf(UUID.randomUUID(), ownerB, true, false);
        PackCivilWarImmunityTest.OfflineStubWolf wildWolf = PackCivilWarImmunityTest.createOfflineWolf(UUID.randomUUID(), null, false, false);

        aggroPuppy.betterdogs$setBloodFeudTarget(feudPackmate.getStringUUID());

        PackCivilWarImmunityTest.StubServerLevel level = PackCivilWarImmunityTest.createStubServerLevel(
                200L, List.of(aggroPuppy, packmate, feudPackmate, rivalWolf, wildWolf));
        aggroPuppy.attachLevel(level);
        packmate.attachLevel(level);
        feudPackmate.attachLevel(level);
        rivalWolf.attachLevel(level);
        wildWolf.attachLevel(level);

        PackCivilWarImmunityTest.StubServerPlayer ownerPlayer = PackCivilWarImmunityTest.createStubPlayer(ownerA, false);
        Creeper hostileCreeper = PackCivilWarImmunityTest.createOfflineCreeper();
        DamageSource biteSource = new DamageSource(PackCivilWarImmunityTest.mobDamageType, aggroPuppy, aggroPuppy);

        GameRuleMap trueMap = GameRuleMap.of();
        trueMap.set(BetterDogsGameRules.BD_FRIENDLY_FIRE, true);
        level.setGameRules(PackCivilWarImmunityTest.createStubGameRules(trueMap));

        Assertions.assertFalse(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(aggroPuppy, ownerPlayer, biteSource));
        Assertions.assertFalse(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(aggroPuppy, hostileCreeper, biteSource));
        Assertions.assertFalse(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(aggroPuppy, rivalWolf, biteSource));
        Assertions.assertFalse(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(aggroPuppy, wildWolf, biteSource));
        Assertions.assertFalse(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(aggroPuppy, feudPackmate, biteSource));
        Assertions.assertTrue(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(aggroPuppy, aggroPuppy, biteSource));
        Assertions.assertTrue(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(aggroPuppy, packmate, biteSource),
                "Co-owned non-feud packmate must be protected when BD_FRIENDLY_FIRE=true");

        GameRuleMap falseMap = GameRuleMap.of();
        falseMap.set(BetterDogsGameRules.BD_FRIENDLY_FIRE, false);
        level.setGameRules(PackCivilWarImmunityTest.createStubGameRules(falseMap));

        Assertions.assertFalse(WolfFriendlyFireHelper.shouldCancelRetaliationDamage(aggroPuppy, packmate, biteSource),
                "Co-owned packmate must take retaliation damage when BD_FRIENDLY_FIRE=false");
    }
}
