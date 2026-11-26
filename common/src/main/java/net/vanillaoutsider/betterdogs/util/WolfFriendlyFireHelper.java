// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.ai.play.PlayChoreographyHelper;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.UUID;

/**
 * Single-purpose helper managing friendly fire evaluation, melee protection, and lethal health clamping.
 */
public final class WolfFriendlyFireHelper {

    public static final float MAX_AGGRESSIVE_BABY_PROVOCATION_DAMAGE = 2.0f;

    private WolfFriendlyFireHelper() {
    }

    /**
     * Pure testable logic determining whether friendly fire protection should cancel damage.
     */
    public static boolean isFriendlyFireProtected(boolean isTamed, boolean isOwner, boolean isDirectMelee, boolean isSneaking, boolean friendlyFireEnabled) {
        if (!isTamed || !isOwner) {
            return false;
        }
        if (isSneaking) {
            return false; // Sneaking bypasses protection for intentional hits
        }
        if (!isDirectMelee) {
            return false; // Projectiles are allowed
        }
        return friendlyFireEnabled;
    }

    /**
     * Evaluates if damage against a wolf should be cancelled or clamped under friendly fire rules.
     *
     * @return true if damage should be cancelled, false otherwise.
     */
    public static boolean shouldCancelDamage(Wolf wolf, DamageSource source, float amount) {
        if (wolf == null || !wolf.isTame() || source == null) {
            return false;
        }

        Entity rawAttacker = source.getEntity();
        Entity directAttacker = source.getDirectEntity();
        if (!(rawAttacker instanceof LivingEntity) && !(directAttacker instanceof LivingEntity)) {
            return false;
        }

        // Preserve deliberate Social AI (Play Fight / Correction) & Blood Feud exemptions (RETALIATION obeys BD_FRIENDLY_FIRE)
        if ((rawAttacker instanceof WolfExtensions rawExt && rawExt.betterdogs$getSocialTarget() == wolf
                && rawExt.betterdogs$getSocialAction() != WolfExtensions.SocialAction.RETALIATION)
                || (directAttacker instanceof WolfExtensions directExt && directExt.betterdogs$getSocialTarget() == wolf
                && directExt.betterdogs$getSocialAction() != WolfExtensions.SocialAction.RETALIATION)) {
            return false;
        }
        // Blood Feud bypass (betterdogs$hasBloodFeud())
        if ((rawAttacker instanceof Wolf rawWolf && isBloodFeudPair(wolf, rawWolf))
                || (directAttacker instanceof Wolf directWolf && isBloodFeudPair(wolf, directWolf))) {
            return false;
        }

        LivingEntity owner = wolf.getOwner();
        UUID wolfOwnerUuid = PlayChoreographyHelper.getOwnerUUID(wolf);
        Player attackerPlayer = rawAttacker instanceof Player p ? p : (directAttacker instanceof Player p ? p : null);
        boolean isOwner = (owner != null && (rawAttacker == owner || directAttacker == owner))
                || (attackerPlayer != null && wolfOwnerUuid != null && wolfOwnerUuid.equals(attackerPlayer.getUUID()));
        boolean isSneaking = attackerPlayer != null && (attackerPlayer.isShiftKeyDown() || attackerPlayer.isCrouching());
        boolean isAllyWolf = (rawAttacker instanceof Wolf attackerWolf && PackAudienceHelper.isCoOwned(wolf, attackerWolf))
                || (directAttacker instanceof Wolf directWolf && PackAudienceHelper.isCoOwned(wolf, directWolf));

        boolean isProjectile = source.is(DamageTypeTags.IS_PROJECTILE) || source.getDirectEntity() != source.getEntity();

        boolean friendlyFireEnabled = true;
        if (wolf.level() != null && BetterDogsGameRules.BD_FRIENDLY_FIRE != null) {
            try {
                friendlyFireEnabled = BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_FRIENDLY_FIRE, true);
            } catch (Throwable ignored) {
                friendlyFireEnabled = true;
            }
        }
        if (!friendlyFireEnabled) {
            return false;
        }

        boolean isAggressiveBabyProvocation = isOwner && !isSneaking && !isProjectile
                && wolf.isBaby() && wolf instanceof WolfExtensions ext
                && ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE;
        float effectiveAmount = isAggressiveBabyProvocation
                ? Math.min(amount, MAX_AGGRESSIVE_BABY_PROVOCATION_DAMAGE)
                : amount;

        // 1. Lethal Friendly Fire Clamp (Allied dogs or non-sneaking owner)
        if ((isAllyWolf || isOwner) && !isSneaking) {
            float currentHealth = wolf.getHealth();
            if (effectiveAmount >= currentHealth - 1.0f) {
                if (currentHealth > 1.0f) {
                    wolf.setHealth(1.0f);
                }
                return true; // Cancel lethal blow
            }
        }

        // 2. Owner Melee & Co-Owned Packmate Friendly Fire Cancellation
        if (isOwner && !isSneaking && !isProjectile) {
            // Exception: Provocation taps on Aggressive Babies to trigger retaliation behavior
            if (isAggressiveBabyProvocation) {
                return false; // Damage is cleanly clamped to <= 2.0f via @ModifyVariable in WolfCombatMixin
            }
            return true; // Cancel accidental melee hit
        }

        if (isAllyWolf) {
            return true; // Cancel non-lethal accidental packmate hit
        }

        return false;
    }

    /**
     * BL-BD-041: Bidirectional blood feud evaluation ensuring both aggressor and retaliating defender can deal damage.
     */
    public static boolean isBloodFeudPair(Wolf first, Wolf second) {
        if (first == null || second == null) {
            return false;
        }
        return targetsInBloodFeud(first, second) || targetsInBloodFeud(second, first);
    }

    private static boolean targetsInBloodFeud(Wolf source, Wolf target) {
        if (source == null || target == null) {
            return false;
        }
        if (source instanceof WolfExtensions ext && ext.betterdogs$hasBloodFeud()) {
            String feudTarget = ext.betterdogs$getBloodFeudTarget();
            return feudTarget != null && !feudTarget.isEmpty() && feudTarget.equals(target.getStringUUID());
        }
        return false;
    }

    /**
     * Clamps standing-owner direct melee provocation damage against an AGGRESSIVE puppy to
     * {@link #MAX_AGGRESSIVE_BABY_PROVOCATION_DAMAGE} when {@code BD_FRIENDLY_FIRE} is enabled.
     */
    public static float clampProvocationDamage(Wolf wolf, DamageSource source, float amount) {
        if (wolf == null || !wolf.isTame() || !wolf.isBaby() || source == null
                || Float.isNaN(amount) || amount <= MAX_AGGRESSIVE_BABY_PROVOCATION_DAMAGE) {
            return amount;
        }
        if (!(wolf instanceof WolfExtensions ext) || ext.betterdogs$getPersonality() != WolfPersonality.AGGRESSIVE) {
            return amount;
        }
        if (!isStandingOwnerDirectMelee(wolf, source)) {
            return amount;
        }
        return isFriendlyFireEnabled(wolf.level()) ? MAX_AGGRESSIVE_BABY_PROVOCATION_DAMAGE : amount;
    }

    /**
     * Evaluates whether an aggressive puppy's {@code BabyBiteBackGoal} retaliation hit against {@code victim}
     * should be cancelled under friendly-fire and Blood Feud rules.
     */
    public static boolean shouldCancelRetaliationDamage(Wolf attackerPuppy, LivingEntity victim, DamageSource source, float amount) {
        if (attackerPuppy == null || victim == null || source == null) {
            return false;
        }
        if (!(victim instanceof Wolf victimWolf)) {
            return false;
        }
        if (victimWolf == attackerPuppy) {
            return true;
        }
        if (isBloodFeudPair(attackerPuppy, victimWolf)) {
            return false;
        }
        Level ruleLevel = attackerPuppy.level() != null ? attackerPuppy.level() : victimWolf.level();
        if (isFriendlyFireEnabled(ruleLevel) && PackAudienceHelper.isCoOwned(attackerPuppy, victimWolf)) {
            return true;
        }
        return shouldCancelDamage(victimWolf, source, amount);
    }

    private static boolean isStandingOwnerDirectMelee(Wolf wolf, DamageSource source) {
        Entity rawAttacker = source.getEntity();
        Entity directAttacker = source.getDirectEntity();
        LivingEntity owner = wolf.getOwner();
        UUID wolfOwnerUuid = PlayChoreographyHelper.getOwnerUUID(wolf);
        Player attackerPlayer = rawAttacker instanceof Player p ? p : (directAttacker instanceof Player p ? p : null);
        boolean isOwner = (owner != null && (rawAttacker == owner || directAttacker == owner))
                || (attackerPlayer != null && wolfOwnerUuid != null && wolfOwnerUuid.equals(attackerPlayer.getUUID()));
        boolean isSneaking = attackerPlayer != null && (attackerPlayer.isShiftKeyDown() || attackerPlayer.isCrouching());
        boolean isProjectile = source.is(DamageTypeTags.IS_PROJECTILE) || source.getDirectEntity() != source.getEntity();
        return isOwner && !isSneaking && !isProjectile;
    }

    private static boolean isFriendlyFireEnabled(Level level) {
        if (level == null || BetterDogsGameRules.BD_FRIENDLY_FIRE == null) {
            return true;
        }
        try {
            return BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_FRIENDLY_FIRE, true);
        } catch (Throwable ignored) {
            return true;
        }
    }
}
