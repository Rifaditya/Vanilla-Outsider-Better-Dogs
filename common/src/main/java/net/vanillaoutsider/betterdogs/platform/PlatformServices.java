package net.vanillaoutsider.betterdogs.platform;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersistentData;

import java.nio.file.Path;

/**
 * Platform abstraction interface for multi-loader support.
 * Implementations exist in fabric/ and neoforge/ modules.
 */
public interface PlatformServices {

    /**
     * Get the config directory for the current platform.
     */
    Path getConfigDir();

    /**
     * Get wolf persistent data from entity.
     */
    WolfPersistentData getWolfData(Wolf wolf);

    /**
     * Set wolf persistent data on entity.
     */
    void setWolfData(Wolf wolf, WolfPersistentData data);

    /**
     * Initialize platform-specific attachments.
     */
    void initAttachments();
}
