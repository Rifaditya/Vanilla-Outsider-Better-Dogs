// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.util.WolfInteractionHelper;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
import net.vanillaoutsider.betterdogs.WolfPersistentData;

public enum WolfInfoProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (config.get(getUid())) {
            if (accessor.getEntity() instanceof Wolf wolf) {
                
                // Add Favorite Treat info if tamed
                if (wolf.isTame()) {
                    var treat = WolfInteractionHelper.getFavoriteTreat(wolf);
                    if (treat != null) {
                        boolean discovered = accessor.getServerData().getBoolean("betterdogs:discovered_treat").orElse(false);
                        boolean hideSetting = config.get(Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "hide_undiscovered_treat"));
                        
                        if (hideSetting && !discovered) {
                            tooltip.add(Component.translatable("betterdogs.jade.treat", "???"));
                        } else {
                            tooltip.add(Component.translatable("betterdogs.jade.treat", Component.translatable(treat.getDescriptionId())));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof Wolf wolf) {
            tag.putBoolean("betterdogs:discovered_treat", WolfPersistentData.hasDiscoveredTreat(wolf));
        }
    }

    @Override
    public Identifier getUid() {
        return Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "wolf_info");
    }
}
