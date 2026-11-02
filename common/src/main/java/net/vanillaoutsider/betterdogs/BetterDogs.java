// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.dasik.social.api.SocialEventRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.vanillaoutsider.betterdogs.advancement.CureInbredTrigger;
import net.vanillaoutsider.betterdogs.advancement.GuardWolfPersonalityTrigger;
import net.vanillaoutsider.betterdogs.advancement.InbredWolfTrigger;
import net.vanillaoutsider.betterdogs.advancement.OnPatrolTrigger;
import net.vanillaoutsider.betterdogs.advancement.OutcrossRuntTrigger;
import net.vanillaoutsider.betterdogs.advancement.PutUpForAdoptionTrigger;
import net.vanillaoutsider.betterdogs.advancement.SelfServiceTrigger;
import net.vanillaoutsider.betterdogs.advancement.TameWolfPersonalityTrigger;
import net.vanillaoutsider.betterdogs.advancement.WolfLitterTrigger;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterDogs {

    public static final String MOD_ID = "vanilla-outsider-better-dogs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final TameWolfPersonalityTrigger TAME_WOLF_PERSONALITY = new TameWolfPersonalityTrigger();
    public static final GuardWolfPersonalityTrigger GUARD_WOLF_PERSONALITY = new GuardWolfPersonalityTrigger();
    public static final InbredWolfTrigger INBRED_WOLF = new InbredWolfTrigger();
    public static final OutcrossRuntTrigger OUTCROSS_RUNT = new OutcrossRuntTrigger();
    public static final CureInbredTrigger CURE_INBRED = new CureInbredTrigger();
    public static final WolfLitterTrigger WOLF_LITTER = new WolfLitterTrigger();
    public static final PutUpForAdoptionTrigger PUT_UP_FOR_ADOPTION = new PutUpForAdoptionTrigger();
    public static final OnPatrolTrigger ON_PATROL = new OnPatrolTrigger();
    public static final SelfServiceTrigger SELF_SERVICE = new SelfServiceTrigger();

    public static void init() {
        LOGGER.info("[Better Dogs 1.21.1] Initializing Core Personality & DNA System...");
        net.vanillaoutsider.betterdogs.config.BetterDogsConfig.load(net.dasik.social.platform.Services.PLATFORM.getConfigDir());
        BetterDogsGameRules.init();
        LOGGER.info("[Better Dogs 1.21.1] Registered 80+ GameRules.");

        // Register Hive Mind Social Events (V3.1) - Migrated to DasikLibrary
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.WanderlustDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.RetaliationDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.CorrectionDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.SmallFightDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.ZoomiesDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.FetchDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.BeggingDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.HowlDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.IdleCuriosityEvent());

        LOGGER.info("[Better Dogs 1.21.1] Core systems initialized.");
    }

    public static void registerCriteriaTriggers() {
        CriteriaTriggers.register("betterdogs:tame_wolf_personality", TAME_WOLF_PERSONALITY);
        CriteriaTriggers.register("betterdogs:guard_wolf_personality", GUARD_WOLF_PERSONALITY);
        CriteriaTriggers.register("betterdogs:inbred_wolf", INBRED_WOLF);
        CriteriaTriggers.register("betterdogs:outcross_runt", OUTCROSS_RUNT);
        CriteriaTriggers.register("betterdogs:cure_inbred", CURE_INBRED);
        CriteriaTriggers.register("betterdogs:wolf_litter", WOLF_LITTER);
        CriteriaTriggers.register("betterdogs:put_up_for_adoption", PUT_UP_FOR_ADOPTION);
        CriteriaTriggers.register("betterdogs:on_patrol", ON_PATROL);
        CriteriaTriggers.register("betterdogs:self_service", SELF_SERVICE);
    }
}
