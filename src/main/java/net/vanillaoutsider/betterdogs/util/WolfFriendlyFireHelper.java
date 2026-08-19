// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

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

        if (!(source.getEntity() instanceof LivingEntity attacker)) {
            return false;
        }

        boolean isOwner = attacker instanceof Player player && wolf.isOwnedBy(player);
        boolean isSneaking = attacker instanceof Player player && (player.isShiftKeyDown() || player.isCrouching());
        boolean isAllyWolf = attacker instanceof Wolf attackerWolf && attackerWolf.isTame()
                && (wolf.isOwnedBy(attackerWolf.getOwner())
                        || (attackerWolf.getOwner() != null && attackerWolf.isOwnedBy(wolf.getOwner())));

        boolean isProjectile = source.is(DamageTypeTags.IS_PROJECTILE) || source.getDirectEntity() != source.getEntity();

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

        // 2. Owner Friendly Fire Cancellation
        if (isOwner && !isSneaking && !isProjectile) {
            boolean friendlyFireEnabled = true;
            if (wolf.level() != null) {
                try {
                    friendlyFireEnabled = DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_FRIENDLY_FIRE);
                } catch (Throwable ignored) {
                    friendlyFireEnabled = true;
                }
            }

            if (friendlyFireEnabled) {
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
        }

        return false;
    }
}
