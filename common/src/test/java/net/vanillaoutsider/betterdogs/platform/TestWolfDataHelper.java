// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.platform.services.IWolfDataHelper;

public class TestWolfDataHelper implements IWolfDataHelper {

    private static final Map<Wolf, WolfPersistentData> DATA_STORE = new WeakHashMap<>();

    @Override
    public WolfPersistentData getWolfData(Wolf wolf) {
        if (wolf == null) {
            return WolfPersistentData.DEFAULT;
        }
        return DATA_STORE.getOrDefault(wolf, WolfPersistentData.DEFAULT);
    }

    @Override
    public void setWolfData(Wolf wolf, WolfPersistentData data) {
        if (wolf != null) {
            DATA_STORE.put(wolf, data != null ? data : WolfPersistentData.DEFAULT);
        }
    }

    public static void clear() {
        DATA_STORE.clear();
    }
}
