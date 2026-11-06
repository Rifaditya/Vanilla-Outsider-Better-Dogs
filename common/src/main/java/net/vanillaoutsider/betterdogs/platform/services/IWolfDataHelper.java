// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform.services;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersistentData;

/**
 * Platform abstraction interface for persistent wolf data attachments.
 */
public interface IWolfDataHelper {

    /**
     * Retrieves the persistent data attached to the given wolf, returning default data if absent.
     *
     * @param wolf The wolf entity.
     * @return Persistent data record.
     */
    WolfPersistentData getWolfData(Wolf wolf);

    /**
     * Sets or replaces the persistent data attached to the given wolf.
     *
     * @param wolf The wolf entity.
     * @param data The new persistent data record to attach.
     */
    void setWolfData(Wolf wolf, WolfPersistentData data);
}
