// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.group.GroupMember;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Handles 'wantsToAttack' target evaluation, social combat overrides,
 * 1v1 territorial war constraints, blood feud nemesis targeting, and pack safety.
 */
public class WolfTargetingHandler {

    /**
     * Handles 'wantsToAttack' logic.
     * 
     * @return Boolean.TRUE to allow (override), Boolean.FALSE to deny (override),
     *         or null to pass to vanilla super.
     */
    public static Boolean wantsToAttack(Wolf wolf, LivingEntity target, LivingEntity owner) {
        if (!(wolf instanceof WolfExtensions ext))
            return null;

        // RETALIATION / PLAY FIGHT / TERRITORIAL WAR OVERRIDES
        if (ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.RETALIATION ||
                ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.PLAY_FIGHT ||
                ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.TERRITORIAL_WAR) {
            // If it's a territorial war, ONLY attack the rival leader
            if (ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.TERRITORIAL_WAR) {
                return target == ext.betterdogs$getSocialTarget();
            }
            return true;
        }

        // Territorial War: Member constraints (1v1 Leader protection)
        if (wolf instanceof GroupMember member) {
            LivingEntity leader = member.getLeader();
            if (leader instanceof WolfExtensions leaderExt && leaderExt.betterdogs$getSocialAction() == WolfExtensions.SocialAction.TERRITORIAL_WAR) {
                LivingEntity rivalLeader = leaderExt.betterdogs$getSocialTarget();
                // 1. Members cannot target rival leader
                if (target == rivalLeader) return false;
                // 2. Rival members cannot target our leader
                if (target == leader) return false;
            }
        }

        // Blood Feud: ALLOW attacking nemesis (bypass all protection)
        if (target instanceof Wolf targetWolf && ext.betterdogs$hasBloodFeud()) {
            if (ext.betterdogs$getBloodFeudTarget().equals(targetWolf.getStringUUID())) {
                // INTEGRATION: High affinity suppresses the feud for this attack
                int affinity = ext.betterdogs$getAffinity(targetWolf.getStringUUID());
                if (affinity > 50) {
                    return false;
                }
                return true;
            }
        }

        // Submissive: Cannot attack pack members
        if (ext.betterdogs$isSubmissive()) {
            boolean isPackMember = target instanceof Wolf targetWolf && targetWolf.isTame()
                    && targetWolf.getOwner() != null && targetWolf.getOwner().equals(owner);
            if (isPackMember) {
                return false;
            }
        }

        // Baby wolves cannot attack adult pack members (can still target owner for retaliation)
        if (wolf.isBaby() && target instanceof Wolf targetWolf && targetWolf.isTame() && wolf.isTame()) {
            if (!targetWolf.isBaby() && targetWolf.getOwner() != null
                    && targetWolf.getOwner().equals(wolf.getOwner())) {
                return false;
            }
        }

        return null;
    }
}
