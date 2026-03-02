package net.vanillaoutsider.betterdogs.registry

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object BetterDogsTags {
    val RAW_FOOD: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.parse("vanilla-outsider-better-dogs:raw_food"))
    val COOKED_FOOD: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.parse("vanilla-outsider-better-dogs:cooked_food"))
}
