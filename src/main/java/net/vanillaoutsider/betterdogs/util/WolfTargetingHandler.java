// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.group.GroupMember;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import org.jspecify.annotations.Nullable;

/**
 * Handles 'wantsToAttack' target evaluation, social combat overrides,
 * 1v1 territorial war constraints, blood feud nemesis targeting, and pack safety.
 */
public class WolfTargetingHandler {

    /**
     * Checks whether this wolf has an active blood feud targeting the given wolf UUID.
     */
    private static boolean matchesBloodFeudTarget(WolfExtensions ext, @Nullable Wolf targetWolf) {
        if (!ext.betterdogs$hasBloodFeud() || targetWolf == null) {
            return false;
        }
        String feudTarget = ext.betterdogs$getBloodFeudTarget();
        return feudTarget != null && !feudTarget.isEmpty() && feudTarget.equals(targetWolf.getStringUUID());
    }

    /**
     * Checks if retaliation targeting should be suppressed for a disciplined wolf against the disciplining packmate.
     */
    public static boolean shouldSuppressRetaliation(Wolf wolf, LivingEntity target) {
        if (wolf == null || !(target instanceof Wolf discipliner)) {
            return false;
        }
        return SmallFightHelper.isRecentCorrectionSnap(wolf, discipliner);
    }

    /**
     * Pure testable logic determining retaliation suppression without entity instantiation.
     */
    public static boolean shouldSuppressRetaliation(boolean isDisciplinedByTarget) {
        return isDisciplinedByTarget;
    }

    /**
     * Handles 'wantsToAttack' logic.
     * 
     * @return Boolean.TRUE to allow (override), Boolean.FALSE to deny (override),
     *         or null to pass to vanilla super.
     */
    public static Boolean wantsToAttack(Wolf wolf, LivingEntity target, LivingEntity owner) {
        if (!(wolf instanceof WolfExtensions ext))
            return null;

        // HIERARCHY CORRECTION SNAP: Disciplined wolf cannot retaliate against disciplining packmate
        if (shouldSuppressRetaliation(wolf, target)) {
            return false;
        }

        // RETALIATION / PLAY FIGHT / TERRITORIAL WAR OVERRIDES
        if (ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.RETALIATION ||
                ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.PLAY_FIGHT ||
                ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.TERRITORIAL_WAR) {
            // If it's a territorial war, ONLY attack the rival leader
            if (ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.TERRITORIAL_WAR) {
                return target != null && target == ext.betterdogs$getSocialTarget();
            }
            // If it's a play fight, ONLY interact with the designated play partner
            if (ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.PLAY_FIGHT) {
                return target != null && target == ext.betterdogs$getSocialTarget();
            }
            return true;
        }

        // Pack Civil War Protection (BL-BD-035): Co-owned tamed wolves NEVER want to attack each other
        if (target instanceof Wolf targetWolf && wolf.isTame() && targetWolf.isTame()) {
            if (PackAudienceHelper.isCoOwned(wolf, targetWolf)) {
                if (!matchesBloodFeudTarget(ext, targetWolf)) {
                    return false;
                }
            }
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
        if (target instanceof Wolf targetWolf && matchesBloodFeudTarget(ext, targetWolf)) {
            // INTEGRATION: High affinity suppresses the feud for this attack
            int affinity = ext.betterdogs$getAffinity(targetWolf.getStringUUID());
            if (affinity > 50) {
                return false;
            }
            return true;
        }

        // Submissive: Cannot attack pack members
        if (ext.betterdogs$isSubmissive()) {
            boolean isPackMember = target instanceof Wolf targetWolf && PackAudienceHelper.isCoOwned(wolf, targetWolf);
            if (isPackMember) {
                return false;
            }
        }

        // Baby wolves cannot attack adult pack members (can still target owner for retaliation)
        if (wolf.isBaby() && target instanceof Wolf targetWolf && targetWolf.isTame() && wolf.isTame()) {
            if (!targetWolf.isBaby() && PackAudienceHelper.isCoOwned(wolf, targetWolf)) {
                return false;
            }
        }

        return null;
    }
}
