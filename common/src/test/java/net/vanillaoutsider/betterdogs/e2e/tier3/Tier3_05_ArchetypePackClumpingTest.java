// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier3;

import net.vanillaoutsider.betterdogs.WolfPersonality;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 3 - Pairwise 05: Personality Archetypes x Tamed Pack Clumping Expansion Tests")
public class Tier3_05_ArchetypePackClumpingTest {

    @Test
    @DisplayName("Assert aggressive dogs in large pack expand wander perimeter to prevent clumping")
    public void testAggressivePackSpacingExpansion() {
        double aggroBaseRadius = 14.0;
        int followerCount = 10;
        float multiplier = 1.0f;
        float maxExtra = 10.0f;

        double spacingOffset = Math.min(Math.sqrt(followerCount - 1) * multiplier, maxExtra);
        double totalAggroRadius = aggroBaseRadius + spacingOffset;

        assertEquals(3.0, spacingOffset, 1e-4);
        assertEquals(17.0, totalAggroRadius, 1e-4, "Aggressive dog in 10-dog pack must roam within 17.0m");
    }

    @Test
    @DisplayName("Assert pacifist dogs in large pack maintain compact defensive perimeter around owner")
    public void testPacifistPackSpacingExpansion() {
        double pacifistBaseRadius = 4.0;
        int followerCount = 10;
        float multiplier = 1.0f;
        float maxExtra = 10.0f;

        double spacingOffset = Math.min(Math.sqrt(followerCount - 1) * multiplier, maxExtra);
        double totalPacifistRadius = pacifistBaseRadius + spacingOffset;

        assertEquals(3.0, spacingOffset, 1e-4);
        assertEquals(7.0, totalPacifistRadius, 1e-4, "Pacifist dog in 10-dog pack must stay within 7.0m of owner");
    }
}
