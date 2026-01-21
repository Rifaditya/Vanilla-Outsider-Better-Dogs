package net.vanillaoutsider.betterdogs;

import net.fabricmc.api.ModInitializer;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
// import me.shedaniel.autoconfig.AutoConfig;
// import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterDogs implements ModInitializer {
    public static final String MOD_ID = "vanilla-outsider-better-dogs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Vanilla Outsider: Better Dogs initializing...");

        // Register Config
        // AutoConfig.register(BetterDogsConfig.class, GsonConfigSerializer::new);

        // Initialize wolf data persistence
        WolfPersistentData.Attachments.init();

        LOGGER.info("Better Dogs initialized! Wolves have been enhanced.");
    }
}
