// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.UUID;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.ai.play.PlayChoreographyHelper;
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

        // If friendly fire protection is enabled (true, default), prevent accidental owner & packmate hits
        if (BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_FRIENDLY_FIRE, true)) {
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

            // Blood Feud bypasses pack friendly fire between rival wolves
            if (rawAttacker instanceof net.vanillaoutsider.betterdogs.WolfExtensions ext && ext.betterdogs$hasBloodFeud()) {
                if (wolf.getStringUUID().equals(ext.betterdogs$getBloodFeudTarget())) {
                    return false;
                }
            }
            if (directAttacker instanceof net.vanillaoutsider.betterdogs.WolfExtensions directExt && directExt.betterdogs$hasBloodFeud()) {
                if (wolf.getStringUUID().equals(directExt.betterdogs$getBloodFeudTarget())) {
                    return false;
                }
            }

            // Check if attacker is another friendly dog of the same owner
            if ((rawAttacker instanceof Wolf attackerWolf && PackAudienceHelper.isCoOwned(wolf, attackerWolf))
                    || (directAttacker instanceof Wolf directWolf && PackAudienceHelper.isCoOwned(wolf, directWolf))) {
                return true;
            }
        }

        return false;
    }
}
