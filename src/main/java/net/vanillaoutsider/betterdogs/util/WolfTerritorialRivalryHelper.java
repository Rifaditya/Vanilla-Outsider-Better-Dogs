// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.dasik.social.api.group.GroupMember;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.Random;

/**
 * Dedicated single-purpose helper for resolving wild wolf pack territorial rivalries,
 * calculating alpha dominance scores, evaluating matrix rolls (war vs merge vs retreat), and performing pack mergers.
 */
public final class WolfTerritorialRivalryHelper {

    public static final double DEFAULT_TERRITORY_RADIUS = 96.0;
    public static final int DEFAULT_WAR_DURATION_TICKS = 1200; // 60 seconds

    private WolfTerritorialRivalryHelper() {
    }

    public enum RivalryOutcome {
        WAR,
        MERGE,
        RETREAT
    }

    /**
     * Calculates dominance score for alpha hierarchy election based on scale, personality, and health.
     */
    public static double calculateDominanceScore(Wolf wolf) {
        if (wolf == null || !wolf.isAlive()) {
            return 0.0;
        }
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
     * Compares dominance between two personalities.
     */
    public static boolean isMoreDominant(Wolf wolfA, Wolf wolfB) {
        if (wolfA == null || wolfB == null) {
            return wolfA != null;
        }
        double scoreA = calculateDominanceScore(wolfA);
        double scoreB = calculateDominanceScore(wolfB);
        if (Math.abs(scoreA - scoreB) > 0.001) {
            return scoreA > scoreB;
        }
        return wolfA.getId() > wolfB.getId();
    }

    /**
     * Evaluates territorial rivalry standoff outcome between two rival alphas based on configured GameRules.
     */
    public static RivalryOutcome evaluateOutcome(Level level, Wolf alphaA, Wolf alphaB, Random random) {
        if (level == null || alphaA == null || alphaB == null || random == null) {
            return RivalryOutcome.RETREAT;
        }

        WolfPersonality pA = (alphaA instanceof WolfExtensions extA) ? extA.betterdogs$getPersonality() : WolfPersonality.NORMAL;
        WolfPersonality pB = (alphaB instanceof WolfExtensions extB) ? extB.betterdogs$getPersonality() : WolfPersonality.NORMAL;

        int warChance = 0;
        int mergeChance = 0;

        if (pA == WolfPersonality.AGGRESSIVE && pB == WolfPersonality.AGGRESSIVE) {
            warChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TERR_AA_WAR);
            mergeChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TERR_AA_MERGE);
        } else if ((pA == WolfPersonality.AGGRESSIVE && pB == WolfPersonality.NORMAL) ||
                (pA == WolfPersonality.NORMAL && pB == WolfPersonality.AGGRESSIVE)) {
            warChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TERR_AN_WAR);
            mergeChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TERR_AN_MERGE);
        } else if ((pA == WolfPersonality.AGGRESSIVE && pB == WolfPersonality.PACIFIST) ||
                (pA == WolfPersonality.PACIFIST && pB == WolfPersonality.AGGRESSIVE)) {
            warChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TERR_AP_WAR);
            mergeChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TERR_AP_MERGE);
        } else if (pA == WolfPersonality.NORMAL && pB == WolfPersonality.NORMAL) {
            warChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TERR_NN_WAR);
            mergeChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TERR_NN_MERGE);
        } else if ((pA == WolfPersonality.NORMAL && pB == WolfPersonality.PACIFIST) ||
                (pA == WolfPersonality.PACIFIST && pB == WolfPersonality.NORMAL)) {
            warChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TERR_NP_WAR);
            mergeChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TERR_NP_MERGE);
        } else if (pA == WolfPersonality.PACIFIST && pB == WolfPersonality.PACIFIST) {
            warChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TERR_PP_WAR);
            mergeChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TERR_PP_MERGE);
        }

        int roll = random.nextInt(100);
        if (roll < warChance) {
            return RivalryOutcome.WAR;
        } else if (roll < (warChance + mergeChance)) {
            return RivalryOutcome.MERGE;
        } else {
            return RivalryOutcome.RETREAT;
        }
    }

    /**
     * Starts a territorial war state between two rival alphas.
     */
    public static void startWar(Wolf alphaA, Wolf alphaB) {
        if (alphaA instanceof WolfExtensions extA) {
            extA.betterdogs$setSocialState(alphaB, WolfExtensions.SocialAction.TERRITORIAL_WAR, DEFAULT_WAR_DURATION_TICKS);
        }
        if (alphaB instanceof WolfExtensions extB) {
            extB.betterdogs$setSocialState(alphaA, WolfExtensions.SocialAction.TERRITORIAL_WAR, DEFAULT_WAR_DURATION_TICKS);
        }
    }

    /**
     * Merges the defeated alpha and all of its followers into the victorious alpha's pack.
     */
    public static void mergePacks(Wolf victoriousAlpha, Wolf defeatedAlpha, double searchRadius) {
        if (victoriousAlpha == null || !victoriousAlpha.isAlive()) {
            return;
        }

        if (defeatedAlpha != null && defeatedAlpha instanceof GroupMember dMember) {
            if (defeatedAlpha.level() instanceof ServerLevel serverLevel) {
                for (Wolf other : serverLevel.getEntitiesOfClass(Wolf.class, defeatedAlpha.getBoundingBox().inflate(searchRadius))) {
                    if (other != null && ((GroupMember) other).getLeader() == defeatedAlpha) {
                        ((GroupMember) other).setLeader(victoriousAlpha);
                        if (other instanceof WolfExtensions oExt) {
                            oExt.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
                        }
                    }
                }
            }
            dMember.setLeader(victoriousAlpha);
            defeatedAlpha.setTarget(null);
            if (defeatedAlpha instanceof WolfExtensions dExt) {
                dExt.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
            }
        }

        if (victoriousAlpha.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    victoriousAlpha.getX(),
                    victoriousAlpha.getY() + 1.0,
                    victoriousAlpha.getZ(),
                    5,
                    0.5, 0.5, 0.5, 0.0
            );
        }
    }
}
