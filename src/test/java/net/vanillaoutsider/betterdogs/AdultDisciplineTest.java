// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.AdultDisciplineHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 32: Adult Puppy Discipline & Target Silencing Tests")
class AdultDisciplineTest {

    @Test
    @DisplayName("Assert affinity modulation on blood feud probability")
    void testBloodFeudAffinityModulation() {
        float baseChance = 0.20F;

        // Max affinity (+100) reduces feud probability by 50%
        float highAffinityChance = AdultDisciplineHelper.calculateBloodFeudChance(baseChance, 100);
        assertEquals(0.10F, highAffinityChance, 0.001F, "Max affinity should halve feud chance");

        // Negative affinity (-50) increases feud probability by 50%
        float negativeAffinityChance = AdultDisciplineHelper.calculateBloodFeudChance(baseChance, -50);
        assertEquals(0.30F, negativeAffinityChance, 0.001F, "Negative affinity should increase feud chance");

        // Neutral affinity (0) preserves base chance
        float neutralChance = AdultDisciplineHelper.calculateBloodFeudChance(baseChance, 0);
        assertEquals(baseChance, neutralChance, 0.001F, "Neutral affinity should preserve base chance");

        // Boundary clamp validation
        assertEquals(1.0F, AdultDisciplineHelper.calculateBloodFeudChance(1.5F, -100), 0.001F, "Chance must clamp at 1.0");
        assertEquals(0.0F, AdultDisciplineHelper.calculateBloodFeudChance(-0.5F, 100), 0.001F, "Chance must clamp at 0.0");
    }

    @Test
    @DisplayName("Assert strict null safety across discipline and silencing helper methods")
    void testNullSafety() {
        assertFalse(AdultDisciplineHelper.canDiscipline(null, null));
        assertFalse(AdultDisciplineHelper.shouldSilenceAlert(null, null));
        assertDoesNotThrow(() -> AdultDisciplineHelper.applyDisciplineFeedback(null, null, null));
    }
}
