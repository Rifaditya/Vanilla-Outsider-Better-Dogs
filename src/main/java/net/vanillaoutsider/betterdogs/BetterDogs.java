// Verified against: BetterDogs.java (26.1.2+)
package net.vanillaoutsider.betterdogs;

import net.dasik.social.api.SocialEventRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Better Dogs Main Entrypoint.
 * Protocol: Architectural Protocol 2.1
 */
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.vanillaoutsider.betterdogs.advancement.TameWolfPersonalityTrigger;
import net.vanillaoutsider.betterdogs.advancement.GuardWolfPersonalityTrigger;
import net.vanillaoutsider.betterdogs.advancement.InbredWolfTrigger;
import net.vanillaoutsider.betterdogs.advancement.OutcrossRuntTrigger;
import net.vanillaoutsider.betterdogs.advancement.CureInbredTrigger;
import net.vanillaoutsider.betterdogs.advancement.WolfLitterTrigger;
import net.vanillaoutsider.betterdogs.advancement.PutUpForAdoptionTrigger;
import net.vanillaoutsider.betterdogs.advancement.OnPatrolTrigger;
import net.vanillaoutsider.betterdogs.advancement.SelfServiceTrigger;

/**
 * Better Dogs Main Entrypoint.
 * Protocol: Architectural Protocol 2.1
 */
public class BetterDogs implements ModInitializer {
    public static final String MOD_ID = "vanilla-outsider-better-dogs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final AttachmentType<WolfPersistentData> WOLF_DATA = AttachmentRegistry.createPersistent(
            Identifier.parse("betterdogs:wolf_data"),
            WolfPersistentData.CODEC);

    public static final TameWolfPersonalityTrigger TAME_WOLF_PERSONALITY = Registry.register(
            BuiltInRegistries.TRIGGER_TYPES,
            Identifier.fromNamespaceAndPath(MOD_ID, "tame_wolf_personality"),
            new TameWolfPersonalityTrigger()
    );

    public static final GuardWolfPersonalityTrigger GUARD_WOLF_PERSONALITY = Registry.register(
            BuiltInRegistries.TRIGGER_TYPES,
            Identifier.fromNamespaceAndPath(MOD_ID, "guard_wolf_personality"),
            new GuardWolfPersonalityTrigger()
    );

    public static final InbredWolfTrigger INBRED_WOLF = Registry.register(
            BuiltInRegistries.TRIGGER_TYPES,
            Identifier.fromNamespaceAndPath(MOD_ID, "inbred_wolf"),
            new InbredWolfTrigger()
    );

    public static final OutcrossRuntTrigger OUTCROSS_RUNT = Registry.register(
            BuiltInRegistries.TRIGGER_TYPES,
            Identifier.fromNamespaceAndPath(MOD_ID, "outcross_runt"),
            new OutcrossRuntTrigger()
    );

    public static final CureInbredTrigger CURE_INBRED = Registry.register(
            BuiltInRegistries.TRIGGER_TYPES,
            Identifier.fromNamespaceAndPath(MOD_ID, "cure_inbred"),
            new CureInbredTrigger()
    );

    public static final WolfLitterTrigger WOLF_LITTER = Registry.register(
            BuiltInRegistries.TRIGGER_TYPES,
            Identifier.fromNamespaceAndPath(MOD_ID, "wolf_litter"),
            new WolfLitterTrigger()
    );

    public static final PutUpForAdoptionTrigger PUT_UP_FOR_ADOPTION = Registry.register(
            BuiltInRegistries.TRIGGER_TYPES,
            Identifier.fromNamespaceAndPath(MOD_ID, "put_up_for_adoption"),
            new PutUpForAdoptionTrigger()
    );

    public static final OnPatrolTrigger ON_PATROL = Registry.register(
            BuiltInRegistries.TRIGGER_TYPES,
            Identifier.fromNamespaceAndPath(MOD_ID, "on_patrol"),
            new OnPatrolTrigger()
    );

    public static final SelfServiceTrigger SELF_SERVICE = Registry.register(
            BuiltInRegistries.TRIGGER_TYPES,
            Identifier.fromNamespaceAndPath(MOD_ID, "self_service"),
            new SelfServiceTrigger()
    );

    public static final net.minecraft.sounds.SoundEvent WOLF_HOWL = Registry.register(
            BuiltInRegistries.SOUND_EVENT,
            Identifier.fromNamespaceAndPath("betterdogs", "entity.wolf.howl"),
            net.minecraft.sounds.SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("betterdogs", "entity.wolf.howl"))
    );

    @Override
    public void onInitialize() {
        init();
    }

    public static void init() {
        // Verify Library Version compatibility
        try {
            Class.forName("net.dasik.social.api.config.ConfigHelper");
        } catch (ClassNotFoundException e) {
            net.minecraft.CrashReport report = net.minecraft.CrashReport.forThrowable(e, "Better Dogs: DasikLibrary version mismatch! Requires version 1.7.4 or higher. Please update your mods.");
            throw new net.minecraft.ReportedException(report);
        }

        String version = FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("3.8.0+A-26.1.2");
        LOGGER.info("Vanilla Outsider: Better Dogs v{} initializing [Aligned]...", version);

        // Load Configuration
        BetterDogsConfig.load(FabricLoader.getInstance().getConfigDir());

        // Register Game Rules (Hybrid Config)
        net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.register();

        // Initialize platform-specific attachments
        @SuppressWarnings("unused")
        var ignored = WOLF_DATA;

        // Register Commands
        net.vanillaoutsider.betterdogs.command.BetterDogsCommand.register();

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

        net.vanillaoutsider.betterdogs.util.DogCommandManager.registerEvents();
        LOGGER.info("Better Dogs initialized! Social Hive Mind active (DasikLibrary).");
    }
}
