// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.compat.jade;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;

public enum WolfHealthProvider implements IEntityComponentProvider {
    INSTANCE;

    private static final Identifier UID = Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "wolf_health");

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (accessor.getEntity() instanceof Wolf wolf) {
            float health = wolf.getHealth();
            float maxHealth = wolf.getMaxHealth();
            float absorption = wolf.getAbsorptionAmount();
            BetterDogsHealthElement.HeartType heartType = wolf.isFullyFrozen() 
                    ? BetterDogsHealthElement.HeartType.FROZEN 
                    : BetterDogsHealthElement.HeartType.NORMAL;

            boolean replaced = tooltip.replace(JadeIds.MC_ENTITY_HEALTH, oldRowLists -> {
                java.util.List<java.util.List<snownee.jade.api.ui.IElement>> newList = new java.util.ArrayList<>();
                java.util.List<snownee.jade.api.ui.IElement> newRow = new java.util.ArrayList<>();

                if (!oldRowLists.isEmpty()) {
                    for (snownee.jade.api.ui.IElement oldElement : oldRowLists.get(0)) {
                        if (oldElement != null && JadeIds.MC_ENTITY_HEALTH.equals(oldElement.getTag())) {
                            newRow.add(new BetterDogsHealthElement(
                                    heartType,
                                    maxHealth,
                                    health,
                                    absorption
                            ).tag(JadeIds.MC_ENTITY_HEALTH));
                        } else {
                            newRow.add(oldElement);
                        }
                    }
                }

                newList.add(newRow);
                return newList;
            });

            if (!replaced) {
                tooltip.remove(JadeIds.MC_ENTITY_HEALTH);
                tooltip.add(new BetterDogsHealthElement(
                        heartType,
                        maxHealth,
                        health,
                        absorption
                ).tag(JadeIds.MC_ENTITY_HEALTH));
            }
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }

    @Override
    public int getDefaultPriority() {
        return TooltipPosition.TAIL;
    }
}
