// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Single-purpose helper managing tactical low-health disengagement checks,
 * personality flee weights, escape vectors, and audiovisual retreat feedback.
 */
public final class WolfFleeHelper {

    private static final float DEFAULT_HEALTH_THRESHOLD = 0.30f;

    private WolfFleeHelper() {
    }

    /**
     * Checks if the wolf's health is below the 30% Max HP threshold.
     */
    public static boolean isLowHealth(Wolf wolf) {
        if (wolf == null) {
            return false;
        }
        return isLowHealthMath(wolf.getHealth(), wolf.getMaxHealth(), DEFAULT_HEALTH_THRESHOLD);
    }

    /**
     * Pure math helper for low-health threshold evaluation.
     */
    public static boolean isLowHealthMath(float currentHealth, float maxHealth, float thresholdMultiplier) {
        if (maxHealth <= 0.0f || currentHealth <= 0.0f) {
            return false;
        }
        return currentHealth < (maxHealth * thresholdMultiplier);
    }

    /**
     * Retrieves the flee probability (0-100%) based on the wolf's personality.
     */
    public static int getPersonalityFleeChance(Wolf wolf) {
        if (wolf == null || wolf.level() == null) {
            return 50;
        }

        WolfExtensions ext = (WolfExtensions) wolf;
        WolfPersonality personality = ext.betterdogs$hasPersonality()
                ? ext.betterdogs$getPersonality()
                : WolfPersonality.NORMAL;

        return switch (personality) {
            case PACIFIST -> DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_PACI_FLEE_CHANCE);
            case NORMAL -> DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_NORMAL_FLEE_CHANCE);
            case AGGRESSIVE -> DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_AGGRO_FLEE_CHANCE);
        };
    }

    /**
     * Pure math helper to evaluate a deterministic roll against a flee chance threshold.
     */
    public static boolean shouldFleeWithRoll(int roll, int chance) {
        if (chance <= 0) {
            return false;
        }
        if (chance >= 100) {
            return true;
        }
        return roll >= 0 && roll < chance;
    }

    /**
     * Checks whether the wolf should trigger tactical low-health disengagement.
     */
    public static boolean shouldFlee(Wolf wolf) {
        if (wolf == null || wolf.isOrderedToSit()) {
            return false;
        }

        if (!DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_FLEE_LOW_HEALTH)) {
            return false;
        }

        if (!isLowHealth(wolf)) {
            return false;
        }

        int chance = getPersonalityFleeChance(wolf);
        return shouldFleeWithRoll(wolf.getRandom().nextInt(100), chance);
    }

    /**
     * Calculates an escape position away from the primary threat (attacker or current target).
     */
    public static Vec3 calculateEscapePosition(Wolf wolf, LivingEntity avoidTarget, int horizontalRange, int verticalRange) {
        if (wolf == null) {
            return null;
        }

        Vec3 escapePos = null;
        if (avoidTarget != null) {
            escapePos = DefaultRandomPos.getPosAway(wolf, horizontalRange, verticalRange, avoidTarget.position());
        }
        if (escapePos == null) {
            escapePos = DefaultRandomPos.getPos(wolf, horizontalRange, verticalRange);
        }

        return escapePos;
    }

    /**
     * Plays the low-health retreat whine sound and spawns sweat droplet particles on the server.
     */
    public static void playDisengagementFeedback(Wolf wolf) {
        if (wolf == null || wolf.level().isClientSide()) {
            return;
        }

        if (wolf.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SPLASH,
                    wolf.getX(),
                    wolf.getY() + 0.5,
                    wolf.getZ(),
                    3,
                    0.2,
                    0.2,
                    0.2,
                    0.05
            );
            serverLevel.playSound(
                    null,
                    wolf.getX(),
                    wolf.getY(),
                    wolf.getZ(),
                    SoundEvents.WOLF_WHINE_BABY,
                    SoundSource.NEUTRAL,
                    0.8F,
                    (wolf.isBaby() ? 1.0F : 0.7F) + wolf.getRandom().nextFloat() * 0.2F
            );
        }
    }
}
