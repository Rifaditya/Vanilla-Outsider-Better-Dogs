// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.Item;
import net.vanillaoutsider.betterdogs.util.DogTreatHelper;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum WolfInfoProvider implements IEntityComponentProvider {
    INSTANCE;

    public static final Identifier HIDE_UNDISCOVERED_TREAT = Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "hide_undiscovered_treat");
    private static final Identifier UID = Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "wolf_info");

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        try {
            if (config != null && config.get(getUid())) {
                if (accessor != null && accessor.getEntity() instanceof Wolf wolf) {
                    // Add Favorite Treat info if tamed
                    if (wolf.isTame()) {
                        Item treat = DogTreatHelper.getFavoriteTreat(wolf);
                        if (treat != null) {
                            boolean discovered = false;
                            if (accessor.getServerData() != null) {
                                discovered = accessor.getServerData().getBoolean("betterdogs:discovered_treat").orElse(false);
                            }

                            boolean hideSetting = true;
                            try {
                                hideSetting = config.get(HIDE_UNDISCOVERED_TREAT);
                            } catch (Exception ignored) {
                            }

                            if (hideSetting && !discovered) {
                                tooltip.add(Component.translatable("betterdogs.jade.treat", "???"));
                            } else {
                                tooltip.add(Component.translatable("betterdogs.jade.treat", Component.translatable(treat.getDescriptionId())));
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
