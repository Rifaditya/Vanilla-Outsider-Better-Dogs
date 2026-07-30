// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: GameTestHelper.java, Wolf.java, DasikAnimalGeneticsAPI.java (26.3+)
package net.vanillaoutsider.betterdogs.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.dasik.social.api.genetics.DasikAnimalGeneticsAPI;
import net.dasik.social.api.genetics.EntityGenetics;
import net.dasik.social.api.genetics.GeneticsEngine;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class BetterDogsGameTests {

    public static void testWolfSpawnAndInit(GameTestHelper helper) {
        BlockPos spawnPos = new BlockPos(1, 2, 1);
        Wolf wolf = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);

        helper.assertTrue(wolf != null, "Wolf entity failed to spawn");
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setPersonality(WolfPersonality.NORMAL);
            helper.assertTrue(ext.betterdogs$hasPersonality(), "Wolf personality failed to initialize");
        }

        helper.succeed();
    }

    public static void testGuardModeToggle(GameTestHelper helper) {
        BlockPos spawnPos = new BlockPos(2, 2, 2);
        Wolf wolf = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);

        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setGuardMode(true);
            ext.betterdogs$setGuardPos(spawnPos);

            helper.assertTrue(ext.betterdogs$isGuardMode(), "Guard mode toggle failed");
            helper.assertTrue(spawnPos.equals(ext.betterdogs$getGuardPos()), "Guard position anchor failed");
        }

        helper.succeed();
    }

    public static void testStateCleanupHooks(GameTestHelper helper) {
        BlockPos spawnPos = new BlockPos(3, 2, 3);
        Wolf wolf = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);

        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSoundLocationTarget(spawnPos);
            ext.betterdogs$setPassiveOverrideTicks(100);
            ext.betterdogs$clearTransientState();

            helper.assertTrue(ext.betterdogs$getSoundLocationTarget() == null, "Sound location target cleanup failed");
            helper.assertTrue(ext.betterdogs$getPassiveOverrideTicks() == 0, "Passive override ticks cleanup failed");
        }

        helper.succeed();
    }

    // ========== Genetics Scale Clamping Tests ==========

    public static void testGeneticsScaleClamping(GameTestHelper helper) {
        Wolf wolf = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        helper.assertTrue(wolf != null, "Wolf entity failed to spawn for scale test");

        // Test default scale (no genetics set yet)
        float defaultScale = DasikAnimalGeneticsAPI.getScale(wolf);
        helper.assertTrue(defaultScale == 1.0f, "Default scale should be 1.0f, got: " + defaultScale);

        // Test normal scale set
        DasikAnimalGeneticsAPI.setScale(wolf, 1.5f);
        float normalScale = DasikAnimalGeneticsAPI.getScale(wolf);
        helper.assertTrue(normalScale == 1.5f, "Normal scale should be 1.5f, got: " + normalScale);

        // Test lower bound clamping (below 0.5f)
        DasikAnimalGeneticsAPI.setScale(wolf, 0.1f);
        float clampedLow = DasikAnimalGeneticsAPI.getScale(wolf);
        helper.assertTrue(clampedLow == 0.5f, "Scale should clamp to 0.5f, got: " + clampedLow);

        // Test upper bound clamping (above 2.0f)
        DasikAnimalGeneticsAPI.setScale(wolf, 5.0f);
        float clampedHigh = DasikAnimalGeneticsAPI.getScale(wolf);
        helper.assertTrue(clampedHigh == 2.0f, "Scale should clamp to 2.0f, got: " + clampedHigh);

        // Test NaN sanitization
        DasikAnimalGeneticsAPI.setScale(wolf, Float.NaN);
        float nanResult = DasikAnimalGeneticsAPI.getScale(wolf);
        helper.assertTrue(nanResult == 1.0f, "NaN scale should sanitize to 1.0f, got: " + nanResult);

        // Test Infinity sanitization
        DasikAnimalGeneticsAPI.setScale(wolf, Float.POSITIVE_INFINITY);
        float infResult = DasikAnimalGeneticsAPI.getScale(wolf);
        helper.assertTrue(infResult == 1.0f, "Infinity scale should sanitize to 1.0f, got: " + infResult);

        // Test isRunt (scale < 0.85f)
        DasikAnimalGeneticsAPI.setScale(wolf, 0.7f);
        helper.assertTrue(DasikAnimalGeneticsAPI.isRunt(wolf), "Wolf with scale 0.7f should be a runt");

        // Test isGiant (scale > 1.15f)
        DasikAnimalGeneticsAPI.setScale(wolf, 1.5f);
        helper.assertTrue(DasikAnimalGeneticsAPI.isGiant(wolf), "Wolf with scale 1.5f should be a giant");

        helper.succeed();
    }

    // ========== Kinship & Inbreeding Risk Tests ==========

    public static void testKinshipAndInbreedingRisk(GameTestHelper helper) {
        Wolf parent1 = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        Wolf parent2 = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        Wolf offspring = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        Wolf unrelated = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);

        helper.assertTrue(parent1 != null && parent2 != null && offspring != null && unrelated != null,
                "Failed to spawn wolves for kinship test");

        // Set up offspring with parent UUIDs
        GeneticsEngine.setGenetics(offspring, new EntityGenetics(
                Optional.of(parent1.getUUID()),
                Optional.of(parent2.getUUID()),
                false,
                true,
                Map.of()
        ));

        // Test isParentOf
        helper.assertTrue(DasikAnimalGeneticsAPI.isParentOf(parent1, offspring),
                "parent1 should be parent of offspring");
        helper.assertTrue(DasikAnimalGeneticsAPI.isParentOf(parent2, offspring),
                "parent2 should be parent of offspring");
        helper.assertTrue(!DasikAnimalGeneticsAPI.isParentOf(unrelated, offspring),
                "unrelated should NOT be parent of offspring");

        // Test predictInbreedingRiskPercent: parent-offspring = 100%
        int parentOffspringRisk = DasikAnimalGeneticsAPI.predictInbreedingRiskPercent(parent1, offspring);
        helper.assertTrue(parentOffspringRisk == 100,
                "Parent-offspring risk should be 100%, got: " + parentOffspringRisk);

        // Create sibling with same parents
        Wolf sibling = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        helper.assertTrue(sibling != null, "Failed to spawn sibling wolf");
        GeneticsEngine.setGenetics(sibling, new EntityGenetics(
                Optional.of(parent1.getUUID()),
                Optional.of(parent2.getUUID()),
                false,
                true,
                Map.of()
        ));

        // Test areSiblings
        helper.assertTrue(DasikAnimalGeneticsAPI.areSiblings(offspring, sibling),
                "offspring and sibling should be siblings");
        helper.assertTrue(!DasikAnimalGeneticsAPI.areSiblings(offspring, unrelated),
                "offspring and unrelated should NOT be siblings");

        // Test predictInbreedingRiskPercent: full siblings = 100%
        int siblingRisk = DasikAnimalGeneticsAPI.predictInbreedingRiskPercent(offspring, sibling);
        helper.assertTrue(siblingRisk == 100,
                "Full sibling risk should be 100%, got: " + siblingRisk);

        // Test predictInbreedingRiskPercent: unrelated = 0%
        int unrelatedRisk = DasikAnimalGeneticsAPI.predictInbreedingRiskPercent(parent1, unrelated);
        helper.assertTrue(unrelatedRisk == 0,
                "Unrelated risk should be 0%, got: " + unrelatedRisk);

        // Test half-sibling (shares one parent) = 50%
        Wolf halfSibling = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        helper.assertTrue(halfSibling != null, "Failed to spawn half-sibling wolf");
        GeneticsEngine.setGenetics(halfSibling, new EntityGenetics(
                Optional.of(parent1.getUUID()),
                Optional.of(UUID.randomUUID()),
                false,
                true,
                Map.of()
        ));

        int halfSiblingRisk = DasikAnimalGeneticsAPI.predictInbreedingRiskPercent(offspring, halfSibling);
        helper.assertTrue(halfSiblingRisk == 50,
                "Half-sibling risk should be 50%, got: " + halfSiblingRisk);

        helper.succeed();
    }

    // ========== Dynamic Trait Mutation & Reset Tests ==========

    public static void testTraitMutationAndReset(GameTestHelper helper) {
        Wolf wolf = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        helper.assertTrue(wolf != null, "Wolf entity failed to spawn for trait test");

        // Test setTrait
        DasikAnimalGeneticsAPI.setTrait(wolf, "max_health", 5.0f);
        float healthTrait = DasikAnimalGeneticsAPI.getTrait(wolf, "max_health", 0.0f);
        helper.assertTrue(healthTrait == 5.0f, "setTrait max_health should be 5.0f, got: " + healthTrait);

        // Test modifyTrait (adds delta)
        DasikAnimalGeneticsAPI.modifyTrait(wolf, "max_health", 3.0f);
        float modifiedHealth = DasikAnimalGeneticsAPI.getTrait(wolf, "max_health", 0.0f);
        helper.assertTrue(modifiedHealth == 8.0f, "modifyTrait max_health should be 8.0f (5+3), got: " + modifiedHealth);

        // Test modifyTrait with negative delta
        DasikAnimalGeneticsAPI.modifyTrait(wolf, "max_health", -2.0f);
        float reducedHealth = DasikAnimalGeneticsAPI.getTrait(wolf, "max_health", 0.0f);
        helper.assertTrue(reducedHealth == 6.0f, "modifyTrait max_health should be 6.0f (8-2), got: " + reducedHealth);

        // Set multiple traits to verify reset clears all
        DasikAnimalGeneticsAPI.setTrait(wolf, "attack_damage", 2.0f);
        DasikAnimalGeneticsAPI.setTrait(wolf, "movement_speed", 0.05f);

        // Verify traits exist before reset
        helper.assertTrue(DasikAnimalGeneticsAPI.hasGenetics(wolf), "Wolf should have genetics before reset");

        // Test resetGenetics
        DasikAnimalGeneticsAPI.resetGenetics(wolf);
        helper.assertTrue(!DasikAnimalGeneticsAPI.hasGenetics(wolf), "Wolf should NOT have genetics after reset");

        // Verify traits are cleared
        float resetHealth = DasikAnimalGeneticsAPI.getTrait(wolf, "max_health", 0.0f);
        helper.assertTrue(resetHealth == 0.0f, "max_health should be 0.0f after reset, got: " + resetHealth);

        float resetDamage = DasikAnimalGeneticsAPI.getTrait(wolf, "attack_damage", 0.0f);
        helper.assertTrue(resetDamage == 0.0f, "attack_damage should be 0.0f after reset, got: " + resetDamage);

        helper.succeed();
    }

    // ========== Guard Mode Position Anchoring Tests ==========

    public static void testGuardModePosAnchoring(GameTestHelper helper) {
        Wolf wolf = EntityTypes.WOLF.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        helper.assertTrue(wolf != null, "Wolf entity failed to spawn for guard pos test");

        if (wolf instanceof WolfExtensions ext) {
            // Test initial state: guard mode off, no pos
            helper.assertTrue(!ext.betterdogs$isGuardMode(), "Guard mode should be off initially");
            helper.assertTrue(ext.betterdogs$getGuardPos() == null, "Guard pos should be null initially");

            // Test setting guard position
            BlockPos anchor1 = new BlockPos(100, 64, 200);
            ext.betterdogs$setGuardMode(true);
            ext.betterdogs$setGuardPos(anchor1);

            helper.assertTrue(ext.betterdogs$isGuardMode(), "Guard mode should be on after set");
            helper.assertTrue(anchor1.equals(ext.betterdogs$getGuardPos()),
                    "Guard pos should match anchor1");

            // Test changing guard position
            BlockPos anchor2 = new BlockPos(-50, 80, 300);
            ext.betterdogs$setGuardPos(anchor2);
            helper.assertTrue(anchor2.equals(ext.betterdogs$getGuardPos()),
                    "Guard pos should match anchor2 after update");

            // Test clearing guard mode
            ext.betterdogs$setGuardMode(false);
            helper.assertTrue(!ext.betterdogs$isGuardMode(), "Guard mode should be off after clear");
        }

        helper.succeed();
    }
}
