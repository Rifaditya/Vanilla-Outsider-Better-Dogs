// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.UUID;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
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
        boolean friendlyFireEnabled = BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_FRIENDLY_FIRE, true);
        if (!friendlyFireEnabled) {
            return false;
        }

        Entity attacker = source.getEntity();
        Entity directAttacker = source.getDirectEntity();
        if (!(attacker instanceof LivingEntity) && !(directAttacker instanceof LivingEntity)) {
            return false;
        }

        LivingEntity owner = wolf.getOwner();
        UUID wolfOwnerUuid = PlayChoreographyHelper.getOwnerUUID(wolf);

        // Check if direct attacker or indirect source is the owner (live ref or offline/cross-dimension UUID)
        if ((owner != null && (attacker == owner || directAttacker == owner))
                || (wolfOwnerUuid != null
                        && ((attacker instanceof Player p && wolfOwnerUuid.equals(p.getUUID()))
                                || (directAttacker instanceof Player dp && wolfOwnerUuid.equals(dp.getUUID()))))) {
            return true;
        }

        // Blood Feud bypasses pack friendly fire between rival wolves (betterdogs$hasBloodFeud())
        if ((attacker instanceof Wolf rawWolf && isBloodFeudPair(wolf, rawWolf))
                || (directAttacker instanceof Wolf directWolf && isBloodFeudPair(wolf, directWolf))) {
            return false;
        }

        // Check if attacker is another friendly dog of the same owner
        if (attacker instanceof Wolf attackerWolf && PackAudienceHelper.isCoOwned(wolf, attackerWolf)) {
            return true;
        }
        if (directAttacker instanceof Wolf directWolf && PackAudienceHelper.isCoOwned(wolf, directWolf)) {
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
