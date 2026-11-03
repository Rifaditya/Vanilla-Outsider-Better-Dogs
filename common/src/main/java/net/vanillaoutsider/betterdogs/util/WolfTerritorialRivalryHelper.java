// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for resolving wild wolf pack territorial rivalries,
 * calculating alpha dominance scores, evaluating matrix rolls (war vs merge), and performing pack mergers.
 */
public final class WolfTerritorialRivalryHelper {

    private WolfTerritorialRivalryHelper() {}

    /**
     * Calculates dominance score for alpha hierarchy election based on scale, personality, and health.
     */
    public static double calculateDominanceScore(Wolf wolf) {
        if (wolf == null || !wolf.isAlive()) return 0.0;
        double score = 1.0;

        if (wolf instanceof WolfExtensions ext) {
            score = ext.betterdogs$getSocialScale();
            WolfPersonality personality = ext.betterdogs$getPersonality();
            if (personality == WolfPersonality.AGGRESSIVE) {
                score += 0.5;
            } else if (personality == WolfPersonality.PACIFIST) {
                score -= 0.3;
            }
        }

        score += (wolf.getHealth() / wolf.getMaxHealth()) * 0.2;
        return score;
    }

    /**
     * Evaluates territorial rivalry standoff outcome between two rival alphas based on configured GameRules.
     * Returns true if WAR is declared, false if packs agree to peacefully MERGE.
     */
    public static boolean shouldDeclareWar(Level level, Wolf alphaA, Wolf alphaB) {
        if (level == null || alphaA == null || alphaB == null) return false;

        WolfPersonality pA = (alphaA instanceof WolfExtensions extA) ? extA.betterdogs$getPersonality() : WolfPersonality.NORMAL;
        WolfPersonality pB = (alphaB instanceof WolfExtensions extB) ? extB.betterdogs$getPersonality() : WolfPersonality.NORMAL;

        int warChance = 50;

        if (pA == WolfPersonality.AGGRESSIVE && pB == WolfPersonality.AGGRESSIVE) {
            warChance = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_TERR_AA_WAR, 80);
        } else if ((pA == WolfPersonality.AGGRESSIVE && pB == WolfPersonality.NORMAL) || (pA == WolfPersonality.NORMAL && pB == WolfPersonality.AGGRESSIVE)) {
            warChance = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_TERR_AN_WAR, 60);
        } else if ((pA == WolfPersonality.AGGRESSIVE && pB == WolfPersonality.PACIFIST) || (pA == WolfPersonality.PACIFIST && pB == WolfPersonality.AGGRESSIVE)) {
            warChance = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_TERR_AP_WAR, 40);
        } else if (pA == WolfPersonality.NORMAL && pB == WolfPersonality.NORMAL) {
            warChance = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_TERR_NN_WAR, 30);
        } else if ((pA == WolfPersonality.NORMAL && pB == WolfPersonality.PACIFIST) || (pA == WolfPersonality.PACIFIST && pB == WolfPersonality.NORMAL)) {
            warChance = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_TERR_NP_WAR, 10);
        } else if (pA == WolfPersonality.PACIFIST && pB == WolfPersonality.PACIFIST) {
            warChance = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_TERR_PP_WAR, 0);
        }

        return alphaA.getRandom().nextInt(100) < warChance;
    }

    /**
     * Merges a defeated or submitting pack into the victorious alpha's pack.
     */
    public static void mergePacks(Level level, Wolf victoriousAlpha, Wolf defeatedAlpha, List<Wolf> defeatedFollowers) {
        if (victoriousAlpha == null || !victoriousAlpha.isAlive()) return;

        if (victoriousAlpha instanceof WolfExtensions vExt) {
            vExt.betterdogs$setPackLeader(true);
            vExt.betterdogs$setLeaderUUID(null);
        }

        if (defeatedAlpha != null && defeatedAlpha instanceof WolfExtensions dExt) {
            dExt.betterdogs$setPackLeader(false);
            dExt.betterdogs$setLeaderUUID(victoriousAlpha.getUUID());
            defeatedAlpha.setTarget(null);
        }

        if (defeatedFollowers != null) {
            for (Wolf follower : defeatedFollowers) {
                if (follower != null && follower.isAlive() && !follower.isTame() && follower instanceof WolfExtensions fExt) {
                    fExt.betterdogs$setPackLeader(false);
                    fExt.betterdogs$setLeaderUUID(victoriousAlpha.getUUID());
                    follower.setTarget(null);
                }
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                ParticleTypes.HAPPY_VILLAGER,
                victoriousAlpha.getX(),
                victoriousAlpha.getY() + 1.0,
                victoriousAlpha.getZ(),
                7,
                0.5, 0.5, 0.5, 0.0
            );
        }
    }
}
