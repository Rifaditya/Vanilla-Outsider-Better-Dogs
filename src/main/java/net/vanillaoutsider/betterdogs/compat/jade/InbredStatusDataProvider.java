// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum InbredStatusDataProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    private static final Identifier UID = Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "inbred_status");

    @Override
    public void appendServerData(CompoundTag data, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof Wolf wolf) {
            data.putBoolean("betterdogs:inbred", WolfPersistentData.isPersistedInbred(wolf));
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
