// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum InbredStatusProvider implements IEntityComponentProvider {
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
    public Identifier getUid() {
        return UID;
    }
}
