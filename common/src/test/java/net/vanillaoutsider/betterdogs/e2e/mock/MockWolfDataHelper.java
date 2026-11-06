// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.mock;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersistentData;

/**
 * Mock implementation of IWolfDataHelper simulating data attachment storage.
 */
public class MockWolfDataHelper implements IWolfDataHelper, net.vanillaoutsider.betterdogs.platform.services.IWolfDataHelper {

    private final Map<Wolf, WolfPersistentData> dataStore = new ConcurrentHashMap<>();

    @Override
    public WolfPersistentData getWolfData(Wolf wolf) {
        if (wolf == null) {
            return WolfPersistentData.DEFAULT;
        }
        return this.dataStore.getOrDefault(wolf, WolfPersistentData.DEFAULT);
    }

    @Override
    public void setWolfData(Wolf wolf, WolfPersistentData data) {
        if (wolf != null && data != null) {
            this.dataStore.put(wolf, data);
        }
    }

    public void clear() {
        this.dataStore.clear();
    }

    public int size() {
        return this.dataStore.size();
    }
}
