package net.vanillaoutsider.betterdogs.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BetterDogsTags {
    public static final TagKey<Item> RAW_FOOD = TagKey.create(Registries.ITEM, Identifier.parse("vanilla-outsider-better-dogs:raw_food"));
    public static final TagKey<Item> COOKED_FOOD = TagKey.create(Registries.ITEM, Identifier.parse("vanilla-outsider-better-dogs:cooked_food"));
}
