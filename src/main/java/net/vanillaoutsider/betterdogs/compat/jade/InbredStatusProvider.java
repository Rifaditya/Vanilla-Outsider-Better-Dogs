// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;

public enum InbredStatusProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;

    private static final Identifier UID = Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "inbred_status");

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (config.get(getUid())) {
            if (accessor.getEntity() instanceof Wolf wolf && wolf.isTame()) {
                if (accessor.getServerData().getBoolean("betterdogs:inbred").orElse(false)) {
                    tooltip.add(Component.translatable("betterdogs.jade.inbred").withStyle(net.minecraft.ChatFormatting.RED));
                }
            }
        }
    }

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
