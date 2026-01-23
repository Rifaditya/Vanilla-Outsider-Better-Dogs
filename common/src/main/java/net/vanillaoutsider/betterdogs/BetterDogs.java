package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.platform.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterDogs {
    public static final String MOD_ID = "vanilla-outsider-better-dogs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Vanilla Outsider: Better Dogs initializing...");

        // Load Configuration (platform provides config directory)
        BetterDogsConfig.load(Services.getPlatform().getConfigDir());

        // Initialize platform-specific attachments
        Services.getPlatform().initAttachments();

        LOGGER.info("Better Dogs initialized! Wolves have been enhanced.");
    }
}
