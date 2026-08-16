// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.animal.WolfVariants;
import net.vanillaoutsider.betterdogs.util.WolfCoatVariantHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClimateVariantTest {

    @Test
    public void testFreezingTemperatureReturnsSnowyOrAshen() {
        RandomSource random = RandomSource.create(42L);
        for (int i = 0; i < 20; i++) {
            ResourceKey<WolfVariant> key = WolfCoatVariantHelper.getVariantKeyForTemperature(0.0f, random);
            Assertions.assertTrue(key.equals(WolfVariants.SNOWY) || key.equals(WolfVariants.ASHEN),
                "Freezing climate (< 0.15) must return SNOWY or ASHEN variant, got: " + key);
        }
    }

    @Test
    public void testColdTaigaTemperatureReturnsColdVariants() {
        RandomSource random = RandomSource.create(42L);
        List<ResourceKey<WolfVariant>> expected = List.of(WolfVariants.PALE, WolfVariants.ASHEN, WolfVariants.BLACK, WolfVariants.CHESTNUT);
        for (int i = 0; i < 20; i++) {
            ResourceKey<WolfVariant> key = WolfCoatVariantHelper.getVariantKeyForTemperature(0.25f, random);
            Assertions.assertTrue(expected.contains(key),
                "Cold climate (0.15 - 0.45) must return Pale, Ashen, Black, or Chestnut variant, got: " + key);
        }
    }

    @Test
    public void testTemperateForestTemperatureReturnsWoodsOrPale() {
        RandomSource random = RandomSource.create(42L);
        for (int i = 0; i < 20; i++) {
            ResourceKey<WolfVariant> key = WolfCoatVariantHelper.getVariantKeyForTemperature(0.7f, random);
            Assertions.assertTrue(key.equals(WolfVariants.WOODS) || key.equals(WolfVariants.PALE),
                "Temperate climate (0.45 - 0.85) must return WOODS or PALE variant, got: " + key);
        }
    }

    @Test
    public void testWarmJungleTemperatureReturnsRusty() {
        RandomSource random = RandomSource.create(42L);
        for (int i = 0; i < 20; i++) {
            ResourceKey<WolfVariant> key = WolfCoatVariantHelper.getVariantKeyForTemperature(0.95f, random);
            Assertions.assertEquals(WolfVariants.RUSTY, key,
                "Warm climate (0.85 - 1.15) must return RUSTY variant, got: " + key);
        }
    }

    @Test
    public void testHotTemperatureReturnsSpottedOrStriped() {
        RandomSource random = RandomSource.create(42L);
        for (int i = 0; i < 20; i++) {
            ResourceKey<WolfVariant> key = WolfCoatVariantHelper.getVariantKeyForTemperature(2.0f, random);
            Assertions.assertTrue(key.equals(WolfVariants.SPOTTED) || key.equals(WolfVariants.STRIPED),
                "Hot climate (> 1.15) must return SPOTTED or STRIPED variant, got: " + key);
        }
    }
}
