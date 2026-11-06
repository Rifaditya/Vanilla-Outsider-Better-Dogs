// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum WolfInfoDataProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    private static final Identifier UID = Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "wolf_info");

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof Wolf wolf) {
            tag.putBoolean("betterdogs:discovered_treat", WolfPersistentData.hasDiscoveredTreat(wolf));
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
