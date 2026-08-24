// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterDogs {

    public static final String MOD_ID = "vanilla-outsider-better-dogs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("[Better Dogs 1.21.11] Initializing Core Personality & DNA System...");
        BetterDogsGameRules.init();
        LOGGER.info("[Better Dogs 1.21.11] Registered 80+ GameRules.");
        net.vanillaoutsider.betterdogs.registry.BetterDogsSoundEvents.registerSoundEvents();
        LOGGER.info("[Better Dogs 1.21.11] Registered custom SoundEvents.");
        net.vanillaoutsider.betterdogs.world.BetterDogsSpawning.registerSpawns();
        LOGGER.info("[Better Dogs 1.21.11] Registered expanded biome spawns.");
    }
}
