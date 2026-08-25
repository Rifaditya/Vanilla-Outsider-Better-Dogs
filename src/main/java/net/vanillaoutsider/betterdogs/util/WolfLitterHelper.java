// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.gamerules.GameRules;
import net.vanillaoutsider.betterdogs.BetterDogs;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.Random;

/**
 * Single-purpose helper for calculating multi-puppy litter sizes, spawning sibling puppies,
 * and firing litter size advancement triggers.
 */
public final class WolfLitterHelper {

    private WolfLitterHelper() {
    }

    /**
     * Pure math helper to calculate total litter size using a geometric probability chain (RandomSource).
     */
    public static int calculateLitterSize(int maxSize, int extraChance, RandomSource random) {
        if (maxSize <= 1 || extraChance <= 0 || random == null) {
            return 1;
        }

        int litterSize = 1;
        for (int i = 1; i < maxSize; i++) {
            if (random.nextInt(100) < extraChance) {
                litterSize++;
            } else {
                break;
            }
        }
        return Math.min(litterSize, maxSize);
    }

    /**
     * Pure math helper to calculate total litter size using a geometric probability chain (Random).
     */
    public static int calculateLitterSize(int maxSize, int extraChance, Random random) {
        if (maxSize <= 1 || extraChance <= 0 || random == null) {
            return 1;
        }

        int litterSize = 1;
        for (int i = 1; i < maxSize; i++) {
            if (random.nextInt(100) < extraChance) {
                litterSize++;
            } else {
                break;
            }
        }
        return Math.min(litterSize, maxSize);
    }

    /**
     * Processes breeding events for tamed wolves to spawn extra sibling puppies with independent genetics.
     */
    public static void processBreedingLitter(ServerLevel level, Animal parent1, Animal partner) {
        if (level == null || !(parent1 instanceof Wolf wolf) || !(partner instanceof Wolf partnerWolf)) {
            return;
        }

        // Only target tamed wolves
        if (!wolf.isTame()) {
            return;
        }

        int maxSize = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_WOLF_LITTER_MAX_SIZE);
        int extraChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_WOLF_LITTER_EXTRA_CHANCE);

        int litterSize = calculateLitterSize(maxSize, extraChance, wolf.getRandom());

        // Spawn extra puppies beyond the first vanilla offspring
        for (int i = 1; i < litterSize; i++) {
            AgeableMob extraOffspring = parent1.getBreedOffspring(level, partner);
            if (extraOffspring != null) {
                extraOffspring.setBaby(true);
                extraOffspring.snapTo(parent1.getX(), parent1.getY(), parent1.getZ(), 0.0f, 0.0f);

                level.addFreshEntityWithPassengers(extraOffspring);

                // Visual feedback: Hearts
                level.broadcastEntityEvent(parent1, (byte) 18);

                // Extra XP per puppy if mob drops enabled
                if (level.getGameRules().get(GameRules.MOB_DROPS)) {
                    level.addFreshEntity(new ExperienceOrb(level, parent1.getX(), parent1.getY(), parent1.getZ(), wolf.getRandom().nextInt(7) + 1));
                }

                WolfDebugLogger.log(wolf, "Breeding", "Extra puppy born! Litter size incremented for tamed wolf pair.");
            }
        }

        // Fire advancement trigger if multi-puppy litter produced
        if (litterSize >= 2) {
            ServerPlayer player = wolf.getLoveCause();
            if (player == null) {
                player = partnerWolf.getLoveCause();
            }
            if (player != null) {
                BetterDogs.WOLF_LITTER.trigger(player, litterSize);
            }
        }
    }
}
