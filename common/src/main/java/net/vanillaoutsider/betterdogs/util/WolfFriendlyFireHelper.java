// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

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

        // If friendly fire is enabled (true), do NOT cancel damage (vanilla behavior)
        if (!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_FRIENDLY_FIRE, false)) {
            LivingEntity owner = wolf.getOwner();
            if (owner == null) {
                return false;
            }

            Entity attacker = source.getEntity();
            Entity directAttacker = source.getDirectEntity();

            // Check if direct attacker or indirect source is the owner
            if (attacker == owner || directAttacker == owner) {
                return true;
            }

            // Blood Feud bypasses pack friendly fire between rival wolves
            if (attacker instanceof net.vanillaoutsider.betterdogs.WolfExtensions ext && ext.betterdogs$hasBloodFeud()) {
                if (ext.betterdogs$getBloodFeudTarget().equals(wolf.getStringUUID())) {
                    return false;
                }
            }
            if (directAttacker instanceof net.vanillaoutsider.betterdogs.WolfExtensions directExt && directExt.betterdogs$hasBloodFeud()) {
                if (directExt.betterdogs$getBloodFeudTarget().equals(wolf.getStringUUID())) {
                    return false;
                }
            }

            // Check if attacker is another friendly dog of the same owner
            if (attacker instanceof Wolf attackerWolf && attackerWolf.isTame() && attackerWolf.isOwnedBy(owner)) {
                return true;
            }
            if (directAttacker instanceof Wolf directWolf && directWolf.isTame() && directWolf.isOwnedBy(owner)) {
                return true;
            }
        }

        return false;
    }
}
