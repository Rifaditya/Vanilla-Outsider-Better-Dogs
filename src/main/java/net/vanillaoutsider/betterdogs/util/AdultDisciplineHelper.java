// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;

/**
 * Single-purpose helper managing adult puppy discipline, alert silencing ("The Muzzle"), feud chance calculations, and feedback.
 */
public final class AdultDisciplineHelper {

    private AdultDisciplineHelper() {
    }

    /**
     * Checks if an adult wolf can discipline a misbehaving puppy.
     */
    public static boolean canDiscipline(Wolf adult, Wolf baby) {
        if (adult == null || !adult.isAlive() || adult.isBaby() || !adult.isTame()) {
            return false;
        }
        if (baby == null || !baby.isAlive() || !baby.isBaby() || !baby.isTame()) {
            return false;
        }

        if (adult.isOrderedToSit() || adult.isInSittingPose() || adult.isLeashed() || adult.getTarget() != null) {
            return false;
        }

        // Must share the same owner
        if (adult.getOwner() == null || baby.getOwner() == null) {
            return false;
        }
        return adult.getOwner().equals(baby.getOwner());
    }

    /**
     * Checks whether the interaction between victim and attacker is a hierarchy play correction snap.
     */
    public static boolean isPlayCorrectionSnap(Wolf victim, LivingEntity attacker) {
        if (victim == null || !victim.isAlive() || !victim.isTame()) {
            return false;
        }
        if (!(attacker instanceof Wolf discipliner) || !discipliner.isAlive() || !discipliner.isTame()) {
            return false;
        }
        if (victim.getOwner() == null || discipliner.getOwner() == null || !victim.getOwner().equals(discipliner.getOwner())) {
            return false;
        }
        return SmallFightHelper.isRecentCorrectionSnap(victim, discipliner);
    }

    /**
     * Pure testable logic determining whether a domestic correction snap qualifies for the disciplinary muzzle.
     */
    public static boolean isPlayCorrectionSnap(boolean isVictimTame, boolean isAttackerTame, boolean sameOwner, boolean isCorrectionSnap) {
        return isVictimTame && isAttackerTame && sameOwner && isCorrectionSnap;
    }

    /**
     * "The Muzzle": Checks whether a HurtByTarget alert should be silenced during a domestic dispute or hierarchy correction.
     */
    public static boolean shouldSilenceAlert(Wolf victim, LivingEntity attacker) {
        if (victim == null || !victim.isAlive() || !victim.isTame()) {
            return false;
        }
        if (!(attacker instanceof Wolf discipliner) || !discipliner.isAlive() || !discipliner.isTame()) {
            return false;
        }
        if (victim.getOwner() == null || discipliner.getOwner() == null || !victim.getOwner().equals(discipliner.getOwner())) {
            return false;
        }

        // 1. Adult disciplining baby (legacy baby training)
        if (victim.isBaby() && !discipliner.isBaby()) {
            return true;
        }

        // 2. Hierarchy correction snap during sparring / play fighting
        if (SmallFightHelper.isRecentCorrectionSnap(victim, discipliner)) {
            return true;
        }

        return false;
    }

    /**
     * Calculates the probability of a blood feud arising from discipline based on affinity.
     */
    public static float calculateBloodFeudChance(float baseChance, int affinity) {
        float chance = baseChance;
        if (affinity > 0) {
            // Reduce chance linearly up to 50% at max affinity (100)
            chance *= (1.0f - (affinity / 200.0f));
        } else if (affinity < 0) {
            // Increase chance if they already dislike each other
            chance *= (1.0f + (Math.abs(affinity) / 100.0f));
        }
        return Math.max(0.0f, Math.min(1.0f, chance));
    }

    /**
     * Dispatches sensory feedback for adult puppy correction (growl, whine, and angry villager particles).
     */
    public static void applyDisciplineFeedback(Wolf adult, Wolf baby, ServerLevel serverLevel) {
        if (adult == null || baby == null || serverLevel == null) {
            return;
        }

        // Adult warning growl
        if (adult instanceof Wolf) {
            net.minecraft.sounds.SoundEvent growlSound = ((WolfAccessor) adult).betterdogs$invokeGetSoundSet().growlSound().value();
            serverLevel.playSound(
                    null,
                    adult.getX(),
                    adult.getY(),
                    adult.getZ(),
                    growlSound,
                    SoundSource.NEUTRAL,
                    1.0F,
                    1.0F
            );
        }

        // Puppy submissive whine
        if (baby instanceof Wolf) {
            net.minecraft.sounds.SoundEvent whineSound = ((WolfAccessor) baby).betterdogs$invokeGetSoundSet().whineSound().value();
            serverLevel.playSound(
                    null,
                    baby.getX(),
                    baby.getY(),
                    baby.getZ(),
                    whineSound,
                    SoundSource.NEUTRAL,
                    1.0F,
                    1.3F
            );
        }

        // Angry villager particles at puppy
        WolfParticleHelper.spawnParticles(
                baby,
                ParticleTypes.ANGRY_VILLAGER,
                1.0D,
                0.2D,
                0.2D,
                0.2D,
                0.0D
        );
    }
}
