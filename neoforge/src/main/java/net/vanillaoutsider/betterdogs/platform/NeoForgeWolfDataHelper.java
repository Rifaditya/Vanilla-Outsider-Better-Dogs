// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.BetterDogsNeoForge;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.platform.services.IWolfDataHelper;

public class NeoForgeWolfDataHelper implements IWolfDataHelper {

    @Override
    public WolfPersistentData getWolfData(Wolf wolf) {
        if (wolf == null || BetterDogsNeoForge.WOLF_DATA == null) {
            return WolfPersistentData.DEFAULT;
        }
        return wolf.getData(BetterDogsNeoForge.WOLF_DATA);
    }

    @Override
    public void setWolfData(Wolf wolf, WolfPersistentData data) {
        if (wolf != null && BetterDogsNeoForge.WOLF_DATA != null) {
            wolf.setData(BetterDogsNeoForge.WOLF_DATA, data != null ? data : WolfPersistentData.DEFAULT);
        }
    }
}
