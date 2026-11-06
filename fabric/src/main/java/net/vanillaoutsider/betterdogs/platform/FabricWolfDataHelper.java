// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.BetterDogsFabric;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.platform.services.IWolfDataHelper;

public class FabricWolfDataHelper implements IWolfDataHelper {

    @Override
    public WolfPersistentData getWolfData(Wolf wolf) {
        if (wolf == null) {
            return WolfPersistentData.DEFAULT;
        }
        return wolf.getAttachedOrCreate(BetterDogsFabric.WOLF_DATA, () -> WolfPersistentData.DEFAULT);
    }

    @Override
    public void setWolfData(Wolf wolf, WolfPersistentData data) {
        if (wolf != null) {
            wolf.setAttached(BetterDogsFabric.WOLF_DATA, data != null ? data : WolfPersistentData.DEFAULT);
        }
    }
}
