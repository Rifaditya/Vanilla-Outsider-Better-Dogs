// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.ai.play.PlayChoreographyHelper;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.UUID;

/**
 * Single-purpose helper managing friendly fire evaluation, melee protection, and lethal health clamping.
 */
public final class WolfFriendlyFireHelper {

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

        // Preserve deliberate Social AI (Play Fight / Correction / Retaliation) & Blood Feud exemptions
        if ((rawAttacker instanceof WolfExtensions rawExt && rawExt.betterdogs$getSocialTarget() == wolf)
                || (directAttacker instanceof WolfExtensions directExt && directExt.betterdogs$getSocialTarget() == wolf)) {
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

        // 1. Lethal Friendly Fire Clamp (Allied dogs or non-sneaking owner)
        if ((isAllyWolf || isOwner) && !isSneaking) {
            float currentHealth = wolf.getHealth();
            if (amount >= currentHealth - 1.0f) {
                if (currentHealth > 1.0f) {
                    wolf.setHealth(1.0f);
                }
                return true; // Cancel lethal blow
            }
        }

        // 2. Owner Melee & Co-Owned Packmate Friendly Fire Cancellation
        if (isOwner && !isSneaking && !isProjectile) {
            // Exception: Provocation taps on Aggressive Babies to trigger retaliation behavior
            if (wolf.isBaby() && wolf instanceof WolfExtensions ext
                    && ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE) {
                if (amount > 2.0f) {
                    wolf.setHealth(wolf.getHealth() + (amount - 2.0f));
                }
                return false; // Let provocation hit land
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
}
