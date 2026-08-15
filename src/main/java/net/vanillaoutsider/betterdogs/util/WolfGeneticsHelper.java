// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for calculating offspring personality genetics upon breeding.
 */
public class WolfGeneticsHelper {

    public static WolfPersonality calculateOffspringPersonality(Level level, WolfPersonality parentA, WolfPersonality parentB, RandomSource random) {
        if (parentA == null) {
            parentA = WolfPersonality.NORMAL;
        }
        if (parentB == null) {
            parentB = WolfPersonality.NORMAL;
        }
        if (random == null) {
            return parentA;
        }

        // Case 1: Both parents have the same personality
        if (parentA == parentB) {
            int sameChance = level != null ? BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_BREED_SAME_CHANCE, 80) : 80;
            int otherChance = level != null ? BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_BREED_SAME_OTHER_CHANCE, 10) : 10;
            int roll = random.nextInt(100);

            if (roll < sameChance) {
                return parentA;
            }

            WolfPersonality[] allTypes = WolfPersonality.values();
            for (WolfPersonality other : allTypes) {
                if (other != parentA) {
                    if (roll < sameChance + otherChance) {
                        return other;
                    }
                    sameChance += otherChance;
                }
            }
            return parentA;
        }

        // Case 2: Parents have mixed personalities
        int domChance = level != null ? BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_BREED_MIXED_DOMINANT_CHANCE, 40) : 40;
        int recChance = level != null ? BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_BREED_MIXED_RECESSIVE_CHANCE, 40) : 40;
        int roll = random.nextInt(100);

        if (roll < domChance) {
            return parentA;
        } else if (roll < domChance + recChance) {
            return parentB;
        } else {
            return WolfPersonality.NORMAL;
        }
    }
}
