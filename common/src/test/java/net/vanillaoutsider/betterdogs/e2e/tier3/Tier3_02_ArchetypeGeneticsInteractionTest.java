// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier3;

import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 3 - Pairwise 02: Personality Archetype x Physical Genetics Interaction Tests")
public class Tier3_02_ArchetypeGeneticsInteractionTest {

    @Test
    @DisplayName("Assert compound health scaling: Lazy archetype modifier compounding with maximum inherited health")
    public void testLazyArchetypeMaxHealthCompound() {
        CanineArchetype lazy = CanineArchetype.LAZY;
        float maxGeneticHealth = CanineGeneticsHelper.MAX_HEALTH; // 60.0f
        double totalEffectiveHealth = maxGeneticHealth * lazy.getHealthModifier(); // 60.0 * 1.25 = 75.0 HP

        assertEquals(75.0, totalEffectiveHealth, 1e-4, "Lazy archetype on max genetic health must scale to 75.0 HP");
    }

    @Test
    @DisplayName("Assert compound speed scaling: Playful archetype modifier compounding with maximum inherited speed")
    public void testPlayfulArchetypeMaxSpeedCompound() {
        CanineArchetype playful = CanineArchetype.PLAYFUL;
        float maxGeneticSpeed = CanineGeneticsHelper.MAX_SPEED; // 0.45f
        double totalEffectiveSpeed = maxGeneticSpeed * playful.getSpeedModifier(); // 0.45 * 1.15 = 0.5175

        assertEquals(0.5175, totalEffectiveSpeed, 1e-4, "Playful archetype on max genetic speed must scale to 0.5175");
    }

    @Test
    @DisplayName("Assert Glutton archetype durability: 115% health modifier on minimum genetic health")
    public void testGluttonArchetypeMinHealthCompound() {
        CanineArchetype glutton = CanineArchetype.GLUTTON;
        float minGeneticHealth = CanineGeneticsHelper.MIN_HEALTH; // 10.0f
        double totalEffectiveHealth = minGeneticHealth * glutton.getHealthModifier(); // 10.0 * 1.15 = 11.5 HP

        assertEquals(11.5, totalEffectiveHealth, 1e-4, "Glutton archetype must elevate minimum genetic health to 11.5 HP");
    }
}
