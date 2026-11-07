// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.dasik.social.api.SocialEventRegistry;
import net.minecraft.resources.Identifier;
import net.vanillaoutsider.betterdogs.advancement.CureInbredTrigger;
import net.vanillaoutsider.betterdogs.advancement.GuardWolfPersonalityTrigger;
import net.vanillaoutsider.betterdogs.advancement.InbredWolfTrigger;
import net.vanillaoutsider.betterdogs.advancement.OnPatrolTrigger;
import net.vanillaoutsider.betterdogs.advancement.OutcrossRuntTrigger;
import net.vanillaoutsider.betterdogs.advancement.PutUpForAdoptionTrigger;
import net.vanillaoutsider.betterdogs.advancement.SelfServiceTrigger;
import net.vanillaoutsider.betterdogs.advancement.TameWolfPersonalityTrigger;
import net.vanillaoutsider.betterdogs.advancement.WolfLitterTrigger;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.platform.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Better Dogs Main Common Bootstrap.
 * Protocol: Architectural Protocol 2.1
 */
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

    public static final net.minecraft.sounds.SoundEvent WOLF_HOWL =
            net.minecraft.sounds.SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("betterdogs", "entity.wolf.howl"));

    public static void init() {
        // Register platform-specific content (triggers, sounds)
        Services.PLATFORM.registerContent();
        // Verify Library Version compatibility
        try {
            Class.forName("net.dasik.social.api.config.ConfigHelper");
        } catch (ClassNotFoundException e) {
            net.minecraft.CrashReport report = net.minecraft.CrashReport.forThrowable(e, "Better Dogs: DasikLibrary version mismatch! Requires version 1.7.4 or higher. Please update your mods.");
            throw new net.minecraft.ReportedException(report);
        }

        String version = Services.PLATFORM.getModVersion(MOD_ID);
        if (version == null || version.isEmpty()) {
            version = "5.7.11+26.1.2";
        }
        LOGGER.info("Vanilla Outsider: Better Dogs v{} initializing [Aligned]...", version);

        // Load Configuration
        BetterDogsConfig.load(Services.PLATFORM.getConfigDirectory());

        // Register Game Rules (Hybrid Config)
        net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.register();

        // Register Hive Mind Social Events (V3.1) - Migrated to DasikLibrary
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.WanderlustDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.RetaliationDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.CorrectionDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.SmallFightDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.ZoomiesDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.BeggingDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.FetchDogEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.IdleCuriosityEvent());
        SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.HowlDogEvent());

        // Register Wolf Genetics Config (Engine v1.8.0 Migration)
        java.util.Map<String, net.dasik.social.api.genetics.TraitConfig> wolfTraits = java.util.Map.of(
            "max_health", new net.dasik.social.api.genetics.TraitConfig("max_health", "minecraft:generic.max_health", "ADD_VALUE", 6.0f, 0.6f, -30.0f, 100.0f),
            "attack_damage", new net.dasik.social.api.genetics.TraitConfig("attack_damage", "minecraft:generic.attack_damage", "ADD_MULTIPLIED_TOTAL", 0.20f, 0.6f, -0.8f, 5.0f),
            "movement_speed", new net.dasik.social.api.genetics.TraitConfig("movement_speed", "minecraft:generic.movement_speed", "ADD_MULTIPLIED_TOTAL", 0.15f, 0.6f, -0.6f, 3.0f)
        );

        java.util.Map<String, java.util.Map<String, net.dasik.social.api.genetics.MutationRule>> wolfMutations = java.util.Map.of(
            "normal", java.util.Map.of(
                "max_health", new net.dasik.social.api.genetics.MutationRule("triangular", -2.0f, 10.0f),
                "attack_damage", new net.dasik.social.api.genetics.MutationRule("triangular", -0.05f, 0.25f),
                "movement_speed", new net.dasik.social.api.genetics.MutationRule("triangular", -0.025f, 0.175f)
            ),
            "aggressive", java.util.Map.of(
                "max_health", new net.dasik.social.api.genetics.MutationRule("triangular", -5.0f, 11.0f),
                "attack_damage", new net.dasik.social.api.genetics.MutationRule("triangular", 0.15f, 0.25f),
                "movement_speed", new net.dasik.social.api.genetics.MutationRule("triangular", 0.075f, 0.175f)
            ),
            "pacifist", java.util.Map.of(
                "max_health", new net.dasik.social.api.genetics.MutationRule("triangular", 11.0f, 15.0f),
                "attack_damage", new net.dasik.social.api.genetics.MutationRule("triangular", -0.20f, 0.30f),
                "movement_speed", new net.dasik.social.api.genetics.MutationRule("triangular", -0.15f, 0.20f)
            )
        );

        net.dasik.social.api.genetics.EntityGeneticsRegistry.register(net.minecraft.world.entity.EntityType.WOLF, new net.dasik.social.api.genetics.GeneticsConfig(wolfTraits, wolfMutations));

        LOGGER.info("Better Dogs initialized! Social Hive Mind active (DasikLibrary).");
    }
}
