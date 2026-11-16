// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform.services;

public interface IPlatformHelper {
    /**
     * Gets the name of the current platform (e.g. "Fabric" or "NeoForge").
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given ID is loaded.
     */
    boolean isModLoaded(String modId);

    /**
     * Checks if the game is currently running in a development environment.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Checks if the connected client has the Better Dogs mod installed.
     * Always returns true for local singleplayer integrated server connections.
     */
    default boolean hasBetterDogsClient(net.minecraft.server.level.ServerPlayer player) {
        return false;
    }
}
