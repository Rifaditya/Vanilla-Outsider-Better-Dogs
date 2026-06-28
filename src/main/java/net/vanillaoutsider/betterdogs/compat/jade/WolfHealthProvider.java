// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum WolfHealthProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (accessor.getEntity() instanceof Wolf wolf) {
            // Provide accurate health considering Better Dogs attribute modifiers
            float health = wolf.getHealth();
            float maxHealth = wolf.getMaxHealth();
            float absorption = wolf.getAbsorptionAmount();
            net.minecraft.client.gui.Hud.HeartType heartType = wolf.isFullyFrozen() ? net.minecraft.client.gui.Hud.HeartType.FROZEN : net.minecraft.client.gui.Hud.HeartType.NORMAL;

            tooltip.replace(snownee.jade.api.JadeIds.MC_ENTITY_HEALTH, oldRowLists -> {
                java.util.List<java.util.List<net.minecraft.client.gui.layouts.LayoutElement>> newList = new java.util.ArrayList<>();
                java.util.List<net.minecraft.client.gui.layouts.LayoutElement> newRow = new java.util.ArrayList<>();
                
                if (!oldRowLists.isEmpty()) {
                    for (net.minecraft.client.gui.layouts.LayoutElement oldElement : oldRowLists.get(0)) {
                        if (oldElement instanceof snownee.jade.api.ui.Element e && snownee.jade.api.JadeIds.MC_ENTITY_HEALTH.equals(e.getTag())) {
                            newRow.add(new BetterDogsHealthElement(
                                    heartType,
                                    maxHealth,
                                    health,
                                    absorption
                            ).tag(snownee.jade.api.JadeIds.MC_ENTITY_HEALTH));
                        } else {
                            newRow.add(oldElement);
                        }
                    }
                }
                
                newList.add(newRow);
                return newList;
            });
        }
    }

    @Override
    public Identifier getUid() {
        return Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "wolf_health");
    }

    @Override
    public int getDefaultPriority() {
        return snownee.jade.api.TooltipPosition.TAIL; // 10000, ensures this runs LAST to guarantee replace() works
    }
}
