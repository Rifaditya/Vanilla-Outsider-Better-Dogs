// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.11
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;

/**
 * Dedicated single-purpose helper for domestic pack dispute and hierarchy correction alert silencing ("The Muzzle").
 */
public final class AdultDisciplineHelper {

    private AdultDisciplineHelper() {
    }

    /**
     * Checks whether the interaction between victim and attacker is a hierarchy play correction snap.
     */
    public static boolean isPlayCorrectionSnap(Wolf victim, LivingEntity attacker) {
        if (victim == null || !victim.isAlive() || !victim.isTame()) {
            return false;
        }
        if (!(attacker instanceof Wolf discipliner) || !discipliner.isAlive() || !discipliner.isTame()) {
            return false;
        }
        if (!PackAudienceHelper.isCoOwned(victim, discipliner)) {
            return false;
        }
        return SmallFightHelper.isRecentCorrectionSnap(victim, discipliner);
    }

    /**
     * Pure testable logic determining whether a domestic correction snap qualifies for the disciplinary muzzle.
     */
    public static boolean isPlayCorrectionSnap(boolean isVictimTame, boolean isAttackerTame, boolean sameOwner, boolean isCorrectionSnap) {
        return isVictimTame && isAttackerTame && sameOwner && isCorrectionSnap;
    }

    /**
     * "The Muzzle": Checks whether a HurtByTarget alert should be silenced during a domestic dispute or hierarchy correction.
     * All domestic packmate disputes (baby training, play correction, or accidental hits between co-owned dogs) must be silenced (BL-BD-035).
     */
    public static boolean shouldSilenceAlert(Wolf victim, LivingEntity attacker) {
        if (victim == null || !victim.isAlive() || !victim.isTame()) {
            return false;
        }
        if (!(attacker instanceof Wolf discipliner) || !discipliner.isAlive() || !discipliner.isTame()) {
            return false;
        }
        if (!PackAudienceHelper.isCoOwned(victim, discipliner)) {
            return false;
        }

        // All domestic packmate disputes must be completely silenced to prevent pack civil war
        return true;
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
}
