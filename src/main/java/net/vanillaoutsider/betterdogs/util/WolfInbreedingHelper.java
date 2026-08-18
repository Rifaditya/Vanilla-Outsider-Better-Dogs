// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.genetics.GeneticsEngine;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.BetterDogs;

import java.util.UUID;

/**
 * Single-purpose helper managing inbreeding lineage checks, runt defect penalties,
 * smoke particle dispatch, and breeding advancement triggers.
 */
public final class WolfInbreedingHelper {

    public static final float RUNT_SCALE_MULTIPLIER = 0.70f;

    private WolfInbreedingHelper() {
    }

    /**
     * Pure math helper to calculate the scale of an inbred runt puppy.
     */
    public static float calculateRuntScale(float baseScale, float runtMultiplier) {
        if (baseScale <= 0.0f || runtMultiplier <= 0.0f) {
            return 0.70f;
        }
        return Math.max(0.30f, baseScale * runtMultiplier);
    }

    /**
     * Pure lineage relationship checker comparing 3-generation parent UUIDs and entity UUIDs.
     */
    public static boolean isLineageRelated(UUID p1Parent1, UUID p1Parent2, UUID p2Parent1, UUID p2Parent2, UUID p1Uuid, UUID p2Uuid) {
        // Direct parent-offspring relationship
        if (p1Uuid != null) {
            if (p1Uuid.equals(p2Parent1) || p1Uuid.equals(p2Parent2)) {
                return true;
            }
        }
        if (p2Uuid != null) {
            if (p2Uuid.equals(p1Parent1) || p2Uuid.equals(p1Parent2)) {
                return true;
            }
        }

        // Sibling or half-sibling relationship (shared parent)
        if (p1Parent1 != null) {
            if (p1Parent1.equals(p2Parent1) || p1Parent1.equals(p2Parent2)) {
                return true;
            }
        }
        if (p1Parent2 != null) {
            if (p1Parent2.equals(p2Parent1) || p1Parent2.equals(p2Parent2)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Processes breeding lineage, applies runt scale penalties, dispatches particles, and fires advancements.
     */
    public static void processBreedingLineage(Wolf baby, Wolf parent1, Wolf parent2, ServerLevel level) {
        if (baby == null || parent1 == null || parent2 == null || level == null) {
            return;
        }

        var babyGen = GeneticsEngine.getGenetics(baby);
        var p1Gen = GeneticsEngine.getGenetics(parent1);
        var p2Gen = GeneticsEngine.getGenetics(parent2);

        if (babyGen.inbred()) {
            var babyScaleAttr = baby.getAttribute(Attributes.SCALE);
            if (babyScaleAttr != null) {
                float currentScale = (float) babyScaleAttr.getBaseValue();
                babyScaleAttr.setBaseValue(calculateRuntScale(currentScale, RUNT_SCALE_MULTIPLIER));
            }
            playRuntBirthFeedback(baby, level);

            ServerPlayer player = parent1.getLoveCause();
            if (player == null) {
                player = parent2.getLoveCause();
            }
            if (player != null) {
                BetterDogs.INBRED_WOLF.trigger(player);
            }
        } else if (p1Gen.inbred() || p2Gen.inbred()) {
            ServerPlayer player = parent1.getLoveCause();
            if (player == null) {
                player = parent2.getLoveCause();
            }
            if (player != null) {
                BetterDogs.OUTCROSS_RUNT.trigger(player);
            }
        }
    }

    /**
     * Dispatches dark smoke particles at the runt puppy's position upon birth.
     */
    public static void playRuntBirthFeedback(Wolf baby, ServerLevel level) {
        if (baby == null || level == null || level.isClientSide()) {
            return;
        }

        level.sendParticles(
                ParticleTypes.SMOKE,
                baby.getX(),
                baby.getY() + 0.3,
                baby.getZ(),
                5,
                0.2,
                0.2,
                0.2,
                0.02
        );
    }
}
