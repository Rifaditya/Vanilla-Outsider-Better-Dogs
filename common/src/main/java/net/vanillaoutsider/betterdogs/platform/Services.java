// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import java.util.ServiceLoader;
import net.vanillaoutsider.betterdogs.platform.services.IPlatformHelper;
import net.vanillaoutsider.betterdogs.platform.services.IWolfDataHelper;

/**
 * Service locator initializing Java SPI providers via {@link ServiceLoader}.
 */
public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IWolfDataHelper WOLF_DATA = load(IWolfDataHelper.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service provider for " + clazz.getName()));
    }
}
