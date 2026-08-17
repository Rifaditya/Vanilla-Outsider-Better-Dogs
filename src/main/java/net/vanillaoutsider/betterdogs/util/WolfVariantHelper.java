// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariants;
import net.minecraft.world.level.biome.Biome;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

public class WolfVariantHelper {

    public static void applyClimateVariant(Wolf wolf, ServerLevel level) {
        if (!DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_DYNAMIC_CLIMATE_VARIANTS)) {
            return;
        }

        WolfAccessor accessor = (WolfAccessor) wolf;
        Holder<WolfVariant> currentHolder = accessor.betterdogs$invokeGetVariant();
        ResourceKey<WolfVariant> currentKey = currentHolder != null ? currentHolder.unwrapKey().orElse(null) : null;

        // PRIORITY 1 SAFEGUARD: If a third-party mod or custom datapack assigned a non-vanilla custom variant, yield!
        if (currentKey != null && !currentKey.identifier().getNamespace().equals("minecraft")) {
            return;
        }

        // PRIORITY 2 SAFEGUARD: If Vanilla matched a specific specialized non-fallback variant, yield!
        if (currentKey != null) {
            Identifier id = currentKey.identifier();
            if (id.equals(WolfVariants.SNOWY.identifier()) ||
                id.equals(WolfVariants.ASHEN.identifier()) ||
                id.equals(WolfVariants.RUSTY.identifier()) ||
                id.equals(WolfVariants.STRIPED.identifier()) ||
                id.equals(WolfVariants.BLACK.identifier()) ||
                id.equals(WolfVariants.SPOTTED.identifier())) {
                return;
            }
        }

        // PRIORITY 3: Fallback resolution for un-mapped modded biomes or default Pale/Woods fallback.
        Holder<Biome> biomeHolder = level.getBiome(wolf.blockPosition());
        Biome biome = biomeHolder.value();
        float temp = biome.getBaseTemperature();
        boolean hasPrecipitation = biome.hasPrecipitation();

        String biomePath = biomeHolder.unwrapKey().map(k -> k.identifier().getPath().toLowerCase()).orElse("");

        ResourceKey<WolfVariant> targetVariant = null;

        // Climate Physics Resolution Matrix
        if (temp < 0.15f || biomePath.contains("snow") || biomePath.contains("ice") || biomePath.contains("frozen") || biomePath.contains("tundra")) {
            targetVariant = WolfVariants.SNOWY;
        } else if (temp >= 1.0f && (!hasPrecipitation || biomePath.contains("desert") || biomePath.contains("badlands") || biomePath.contains("mesa") || biomePath.contains("savanna") || biomePath.contains("scrub"))) {
            if (biomePath.contains("badlands") || biomePath.contains("mesa") || biomePath.contains("red")) {
                targetVariant = WolfVariants.STRIPED;
            } else {
                targetVariant = WolfVariants.ASHEN;
            }
        } else if ((temp >= 0.8f && biomePath.contains("jungle")) || biomePath.contains("rainforest") || biomePath.contains("tropic")) {
            targetVariant = WolfVariants.RUSTY;
        } else if (biomePath.contains("old_growth") || biomePath.contains("dark") || biomePath.contains("dense") || biomePath.contains("ominous")) {
            targetVariant = WolfVariants.BLACK;
        } else if (biomePath.contains("taiga") || biomePath.contains("spruce") || biomePath.contains("conifer")) {
            targetVariant = WolfVariants.CHESTNUT;
        } else if (biomePath.contains("grove") || biomePath.contains("meadow")) {
            targetVariant = WolfVariants.SPOTTED;
        }

        if (targetVariant != null) {
            ResourceKey<WolfVariant> finalKey = targetVariant;
            level.registryAccess().lookup(Registries.WOLF_VARIANT).flatMap(reg -> reg.get(finalKey)).ifPresent(accessor::betterdogs$invokeSetVariant);
        }
    }
}
