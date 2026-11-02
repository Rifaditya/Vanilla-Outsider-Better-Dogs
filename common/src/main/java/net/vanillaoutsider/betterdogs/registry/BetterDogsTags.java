// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BetterDogsTags {
    public static final TagKey<Item> RAW_FOOD = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("betterdogs", "raw_food"));
    public static final TagKey<Item> COOKED_FOOD = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("betterdogs", "cooked_food"));
    public static final TagKey<Item> COMMAND_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("betterdogs", "command_items"));
}
