// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.Random;

/**
 * Dedicated single-purpose helper for aggressive puppy snap retaliation mechanics.
 */
public final class BabyRetaliationHelper {

    public static final double DEFAULT_SPEED_MODIFIER = 1.25;
    public static final float RETALIATION_DAMAGE = 1.0F;
    public static final int RETALIATION_DURATION_TICKS = 100;

    private BabyRetaliationHelper() {
    }

    /**
     * Checks whether the wolf is eligible to perform baby bite-back retaliation.
     */
    public static boolean isEligible(Wolf wolf) {
        if (wolf == null || !wolf.isBaby() || !wolf.isTame() || wolf.isOrderedToSit() || wolf.isLeashed()) {
            return false;
        }
        if (wolf instanceof WolfExtensions ext) {
            return ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE && !ext.betterdogs$hasBloodFeud();
        }
        return false;
    }

    /**
     * Evaluates probability roll for triggering retaliation when provoked.
     */
    public static boolean shouldTriggerRetaliation(Wolf wolf, LivingEntity attacker, Random random) {
        if (!isEligible(wolf) || attacker == null || attacker == wolf) {
            return false;
        }
        if (wolf.level() == null) {
            return false;
        }
        int chancePercent = DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_BABY_RETALIATE_PERCENT);
        if (chancePercent <= 0) {
            return false;
        }
        if (chancePercent >= 100) {
            return true;
        }
        return random.nextInt(100) < chancePercent;
    }

    /**
     * Triggers the retaliation state on the puppy.
     */
    public static void triggerRetaliation(Wolf wolf, LivingEntity attacker) {
        if (isEligible(wolf) && attacker != null && wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(attacker, WolfExtensions.SocialAction.RETALIATION, RETALIATION_DURATION_TICKS);
        }
    }

    /**
     * Plays sound and animation cues for feisty puppy snap.
     */
    public static void playRetaliationCues(Wolf wolf) {
        if (wolf != null && wolf.level() != null && !wolf.level().isClientSide()) {
            wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(),
                    SoundEvents.WOLF_GROWL_BABY.value(), SoundSource.NEUTRAL, 0.8F, 1.4F);
        }
    }
}
