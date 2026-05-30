// Mod Initializer (Fabric)
package net.vanillaoutsider.betterdogs;

import net.dasik.social.api.SocialEventRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.vanillaoutsider.betterdogs.advancement.TameWolfPersonalityTrigger;
import net.vanillaoutsider.betterdogs.advancement.GuardWolfPersonalityTrigger;
import net.vanillaoutsider.betterdogs.advancement.InbredWolfTrigger;
import net.vanillaoutsider.betterdogs.advancement.OutcrossRuntTrigger;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                .orElse("4.6.0+A-26.2");
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

        LOGGER.info("Better Dogs initialized! Social Hive Mind active (DasikLibrary).");
    }
}
