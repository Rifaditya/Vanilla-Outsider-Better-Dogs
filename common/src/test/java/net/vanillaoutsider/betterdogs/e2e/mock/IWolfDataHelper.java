// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.mock;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersistentData;

/**
 * Interface contract for Java SPI wolf data attachment helper as defined in PROJECT.md.
 */
public interface IWolfDataHelper extends net.vanillaoutsider.betterdogs.platform.services.IWolfDataHelper {
    WolfPersistentData getWolfData(Wolf wolf);
    void setWolfData(Wolf wolf, WolfPersistentData data);
}
