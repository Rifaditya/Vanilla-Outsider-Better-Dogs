// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum InbredStatusProvider implements IEntityComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = new ResourceLocation("vanilla-outsider-better-dogs", "inbred_status");

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        try {
            if (config != null && config.get(getUid())) {
                if (accessor != null && accessor.getEntity() instanceof Wolf wolf && wolf.isTame()) {
                    if (accessor.getServerData() != null && accessor.getServerData().getBoolean("betterdogs:inbred")) {
                        tooltip.add(Component.translatable("betterdogs.jade.inbred").withStyle(ChatFormatting.RED));
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
