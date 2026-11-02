// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import java.util.ServiceLoader;
import net.vanillaoutsider.betterdogs.platform.services.IGameRuleHelper;
import net.vanillaoutsider.betterdogs.platform.services.IPlatformHelper;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IGameRuleHelper GAMERULES = load(IGameRuleHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        return loadedService;
    }
}
