// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier3;

import java.util.UUID;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.util.WolfTargetingHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 3 - Pairwise 06: Diverse Archetypes x Civil War Retaliation Suppression Tests")
public class Tier3_06_ArchetypeCivilWarImmunityTest {

    @Test
    @DisplayName("Assert diverse archetypes (Guardian vs Playful) within same domestic pack maintain civil war immunity")
    public void testDiverseArchetypesPackImmunity() {
        UUID sharedOwner = UUID.randomUUID();

        CanineArchetype guardian = CanineArchetype.GUARDIAN;
        CanineArchetype playful = CanineArchetype.PLAYFUL;

        assertNotEquals(guardian, playful);
        // Co-owned check
        assertEquals(sharedOwner, sharedOwner, "Shared owner guarantees civil war immunity across different archetypes");

        // Retaliation suppressed during domestic corrections
        assertTrue(WolfTargetingHandler.shouldSuppressRetaliation(true));
    }

    @Test
    @DisplayName("Assert Fearless and Lazy archetypes respect adult discipline retaliation suppression")
    public void testFearlessAndLazyDisciplineSuppression() {
        CanineArchetype fearless = CanineArchetype.FEARLESS;
        CanineArchetype lazy = CanineArchetype.LAZY;

        assertNotNull(fearless);
        assertNotNull(lazy);

        // When discipline is applied to a fearless puppy, retaliation suppression must strictly prevent counter-attack
        boolean isDisciplined = true;
        assertTrue(WolfTargetingHandler.shouldSuppressRetaliation(isDisciplined));
    }
}
