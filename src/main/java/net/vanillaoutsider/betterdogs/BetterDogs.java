package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterDogs {
    public static final String MOD_ID = "vanilla-outsider-better-dogs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final AttachmentType<WolfPersistentData> WOLF_DATA = AttachmentRegistry.createPersistent(
            Identifier.parse("betterdogs:wolf_data"),
            WolfPersistentData.CODEC);

    public static void init() {
        LOGGER.info("Vanilla Outsider: Better Dogs initializing [Fabric Only]...");

        // Load Configuration
        BetterDogsConfig.load(FabricLoader.getInstance().getConfigDir());
        
        // Register Game Rules (Hybrid Config)
        net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.register();

        // Initialize platform-specific attachments
        @SuppressWarnings("unused")
        var ignored = WOLF_DATA;
        
        // Register Hive Mind Social Events (V3.1)
        net.vanillaoutsider.social.core.SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.WanderlustDogEvent());
        net.vanillaoutsider.social.core.SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.RetaliationDogEvent());
        net.vanillaoutsider.social.core.SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.CorrectionDogEvent());
        net.vanillaoutsider.social.core.SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.SmallFightDogEvent());
        net.vanillaoutsider.social.core.SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.ZoomiesDogEvent());
        net.vanillaoutsider.social.core.SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.BeggingDogEvent());
        net.vanillaoutsider.social.core.SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.FetchDogEvent());
        net.vanillaoutsider.social.core.SocialEventRegistry.register(new net.vanillaoutsider.betterdogs.scheduler.events.IdleCuriosityEvent());

        LOGGER.info("Better Dogs initialized! Social Hive Mind active.");
    }
}
