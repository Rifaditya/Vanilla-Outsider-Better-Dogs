// Verified against: BetterDogsTags.java (26.1.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BetterDogsTags {
    public static final TagKey<Item> RAW_FOOD = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "raw_food"));
    public static final TagKey<Item> COOKED_FOOD = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "cooked_food"));
}
