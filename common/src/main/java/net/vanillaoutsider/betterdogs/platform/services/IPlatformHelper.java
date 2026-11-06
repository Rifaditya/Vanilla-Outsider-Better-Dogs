// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform.services;

import java.nio.file.Path;

/**
 * Platform abstraction interface for environment, mod list, and path queries.
 */
public interface IPlatformHelper {

    /**
     * Gets the name of the current loader platform (e.g., "Fabric" or "NeoForge").
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given ID is loaded in the current runtime.
     *
     * @param modId The mod identifier.
     * @return true if loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Checks if the game is currently executing in a development environment.
     *
     * @return true if running in IDE/development, false if production release.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the path to the game's configuration directory.
     *
     * @return Path representing config/.
     */
    Path getConfigDirectory();

    /**
     * Gets the formatted version string of the specified mod ID.
     *
     * @param modId The mod identifier.
     * @return Version string, or empty string if not found.
     */
    String getModVersion(String modId);
}
