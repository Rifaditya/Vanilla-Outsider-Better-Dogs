// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.mock;

import java.nio.file.Path;

/**
 * Interface contract for Java SPI platform helper as defined in PROJECT.md.
 */
public interface IPlatformHelper extends net.vanillaoutsider.betterdogs.platform.services.IPlatformHelper {
    String getPlatformName();
    boolean isModLoaded(String modId);
    boolean isDevelopmentEnvironment();
    Path getConfigDirectory();
    String getModVersion(String modId);
}
