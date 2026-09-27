// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.compat.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.Identifiers;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;

public enum WolfHealthProvider implements IEntityComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = new ResourceLocation("vanilla-outsider-better-dogs", "wolf_health");

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (accessor.getEntity() instanceof Wolf wolf) {
            float health = wolf.getHealth();
            float maxHealth = wolf.getMaxHealth();
            float absorption = wolf.getAbsorptionAmount();
            BetterDogsHealthElement.HeartType heartType = wolf.isFullyFrozen() 
                    ? BetterDogsHealthElement.HeartType.FROZEN 
                    : BetterDogsHealthElement.HeartType.NORMAL;

            tooltip.remove(Identifiers.MC_ENTITY_HEALTH);
            tooltip.add(new BetterDogsHealthElement(
                    heartType,
                    maxHealth,
                    health,
                    absorption
            ).tag(Identifiers.MC_ENTITY_HEALTH));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public int getDefaultPriority() {
        return TooltipPosition.TAIL;
    }
}
