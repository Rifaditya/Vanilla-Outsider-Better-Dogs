// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.UUID;
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
import org.jetbrains.annotations.Nullable;

/**
 * Dedicated single-purpose helper for preventing accidental friendly fire damage between dogs and owners.
 */
public class WolfFriendlyFireHelper {

    public static boolean shouldCancelDamage(Wolf wolf, DamageSource source) {
        if (wolf == null || !wolf.isTame() || source == null) {
            return false;
        }

        Level level = wolf.level();
        if (level == null) {
            return false;
        }

        // If friendly fire protection is enabled (true, default), prevent accidental owner & packmate hits
        boolean friendlyFireEnabled = BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_FRIENDLY_FIRE, true);
        if (!friendlyFireEnabled) {
            return false;
        }

        Entity rawAttacker = source.getEntity();
        Entity directAttacker = source.getDirectEntity();
        if (!(rawAttacker instanceof LivingEntity) && !(directAttacker instanceof LivingEntity)) {
            return false;
        }

        LivingEntity owner = wolf.getOwner();
        UUID wolfOwnerUuid = PlayChoreographyHelper.getOwnerUUID(wolf);

        // Check if direct attacker or indirect source is the owner (live ref or offline/cross-dimension UUID)
        if ((owner != null && (rawAttacker == owner || directAttacker == owner))
                || (wolfOwnerUuid != null
                        && ((rawAttacker instanceof Player p && wolfOwnerUuid.equals(p.getUUID()))
                                || (directAttacker instanceof Player dp && wolfOwnerUuid.equals(dp.getUUID()))))) {
            return true;
        }

        // Blood Feud bypasses pack friendly fire between rival wolves (betterdogs$hasBloodFeud())
        if ((rawAttacker instanceof Wolf rawWolf && isBloodFeudPair(wolf, rawWolf))
                || (directAttacker instanceof Wolf directWolf && isBloodFeudPair(wolf, directWolf))) {
            return false;
        }

        // Check if attacker is another friendly dog of the same owner
        if ((rawAttacker instanceof Wolf attackerWolf && PackAudienceHelper.isCoOwned(wolf, attackerWolf))
                || (directAttacker instanceof Wolf directWolf && PackAudienceHelper.isCoOwned(wolf, directWolf))) {
            return true;
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

    public static boolean matchesBloodFeudTarget(@Nullable Wolf source, @Nullable Wolf target) {
        return source instanceof WolfExtensions ext && matchesBloodFeudTarget(ext, target);
    }

    public static boolean matchesBloodFeudTarget(@Nullable WolfExtensions ext, @Nullable Wolf target) {
        if (ext == null || target == null || !ext.betterdogs$hasBloodFeud()) {
            return false;
        }
        String feudTarget = ext.betterdogs$getBloodFeudTarget();
        if (feudTarget == null || feudTarget.isEmpty()) {
            return false;
        }
        String targetUuid = target.getStringUUID();
        return targetUuid != null && !targetUuid.isEmpty() && feudTarget.equals(targetUuid);
    }

    private static boolean targetsInBloodFeud(Wolf source, Wolf target) {
        return matchesBloodFeudTarget(source, target);
    }

    public static final float MAX_AGGRESSIVE_BABY_PROVOCATION_DAMAGE = 2.0f;

    /**
     * Evaluates whether incoming damage to a tamed wolf should be cancelled, allowing capped
     * standing-owner provocation taps against AGGRESSIVE puppies (with a 1.0f non-lethal floor)
     * while preserving standard 2-arg friendly fire protection for all other cases.
     */
    public static boolean shouldCancelDamage(Wolf wolf, DamageSource source, float amount) {
        if (wolf == null || !wolf.isTame() || source == null) {
            return false;
        }
        if (isFriendlyFireEnabled(wolf.level()) && isAggressiveBabyProvocation(wolf, source)) {
            float effectiveAmount = Math.min(amount, MAX_AGGRESSIVE_BABY_PROVOCATION_DAMAGE);
            float currentHealth = wolf.getHealth();
            if (effectiveAmount >= currentHealth - 1.0f) {
                if (currentHealth > 1.0f) {
                    wolf.setHealth(1.0f);
                }
                return true;
            }
            return false;
        }
        return shouldCancelDamage(wolf, source);
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
        if (isFriendlyFireEnabled(wolf.level()) && isAggressiveBabyProvocation(wolf, source)) {
            return MAX_AGGRESSIVE_BABY_PROVOCATION_DAMAGE;
        }
        return amount;
    }

    /**
     * Evaluates whether an aggressive puppy's {@code BabyBiteBackGoal} retaliation hit against {@code victim}
     * should be cancelled under friendly-fire and Blood Feud rules.
     */
    public static boolean shouldCancelRetaliationDamage(Wolf attackerPuppy, LivingEntity victim, DamageSource source) {
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
        return shouldCancelDamage(victimWolf, source);
    }

    private static boolean isAggressiveBabyProvocation(Wolf wolf, DamageSource source) {
        if (!wolf.isBaby()) {
            return false;
        }
        if (!(wolf instanceof WolfExtensions ext) || ext.betterdogs$getPersonality() != WolfPersonality.AGGRESSIVE) {
            return false;
        }
        return isStandingOwnerDirectMelee(wolf, source);
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
