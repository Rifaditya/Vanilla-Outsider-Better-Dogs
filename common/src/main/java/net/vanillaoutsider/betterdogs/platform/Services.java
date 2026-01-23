package net.vanillaoutsider.betterdogs.platform;

import java.util.ServiceLoader;

/**
 * Service loader for platform-specific implementations.
 */
public class Services {
    
    private static PlatformServices platformServices;

    public static PlatformServices getPlatform() {
        if (platformServices == null) {
            platformServices = ServiceLoader.load(PlatformServices.class)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No PlatformServices implementation found!"));
        }
        return platformServices;
    }

    /**
     * Manually set the platform services (called by loader modules).
     */
    public static void setPlatform(PlatformServices services) {
        platformServices = services;
    }
}
