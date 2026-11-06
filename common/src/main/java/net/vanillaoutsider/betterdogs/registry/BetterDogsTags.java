// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BetterDogsTags {
    public static final TagKey<Item> RAW_FOOD = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "raw_food"));
    public static final TagKey<Item> COOKED_FOOD = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "cooked_food"));
    public static final TagKey<Item> COMMAND_ITEMS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "command_items"));
    public static final TagKey<Item> FETCH_ITEMS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "fetch_items"));
    public static final TagKey<Item> TREATS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "treats"));

    public static final TagKey<Block> CURIOSITY_BLOCKS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "curiosity_blocks"));
    public static final TagKey<Block> SEATS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "seats"));
    public static final TagKey<Block> COMMON_CHAIRS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "chairs"));
}
