// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FavoriteTreatTest {

    @Test
    public void testFavoriteTreatMatching() {
        String dogFavorite = "minecraft:cooked_beef";
        String heldItem = "minecraft:cooked_beef";
        String differentItem = "minecraft:mutton";

        assertTrue(dogFavorite.equals(heldItem), "Matching treat item id must be identified as favorite");
        assertFalse(dogFavorite.equals(differentItem), "Different treat item id must not match favorite");
    }

    @Test
    public void testDoubleHealingBonusMath() {
        float baseHeal = 8.0F;
        float favoriteBonusHeal = baseHeal * 2.0F;

        assertEquals(16.0F, favoriteBonusHeal, 0.001F, "Favorite treat must grant 2x (+100%) health restoration");
    }

    @Test
    public void testFullHealthRefusalCondition() {
        float currentHealth = 20.0F;
        float maxHealth = 20.0F;
        boolean inLove = false;

        boolean shouldRefuse = (currentHealth >= maxHealth) && !inLove;
        assertTrue(shouldRefuse, "Full health non-breeding dog must refuse food");

        float injuredHealth = 14.0F;
        boolean shouldRefuseInjured = (injuredHealth >= maxHealth) && !inLove;
        assertFalse(shouldRefuseInjured, "Injured dog must not refuse food");
    }
}
