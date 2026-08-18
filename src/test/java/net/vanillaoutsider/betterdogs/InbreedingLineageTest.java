// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfInbreedingHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 18: Lineage Tracking & Inbreeding Defects Tests")
class InbreedingLineageTest {

    @Test
    @DisplayName("Assert direct parent-offspring relationship detection")
    void testDirectParentOffspringRelationship() {
        UUID fatherUuid = UUID.randomUUID();
        UUID motherUuid = UUID.randomUUID();
        UUID childUuid = UUID.randomUUID();

        // Father breeding with Daughter (Daughter has fatherUuid as parent1)
        assertTrue(WolfInbreedingHelper.isLineageRelated(null, null, fatherUuid, motherUuid, fatherUuid, childUuid),
                "Breeding with offspring must be recognized as inbreeding");

        // Daughter breeding with Father
        assertTrue(WolfInbreedingHelper.isLineageRelated(fatherUuid, motherUuid, null, null, childUuid, fatherUuid),
                "Offspring breeding with parent must be recognized as inbreeding");
    }

    @Test
    @DisplayName("Assert sibling and half-sibling relationship detection")
    void testSiblingAndHalfSiblingRelationship() {
        UUID sharedFather = UUID.randomUUID();
        UUID mother1 = UUID.randomUUID();
        UUID mother2 = UUID.randomUUID();

        UUID child1 = UUID.randomUUID();
        UUID child2 = UUID.randomUUID();

        // Full siblings (same father and mother)
        assertTrue(WolfInbreedingHelper.isLineageRelated(sharedFather, mother1, sharedFather, mother1, child1, child2),
                "Full siblings must be recognized as inbreeding");

        // Half siblings (same father, different mothers)
        assertTrue(WolfInbreedingHelper.isLineageRelated(sharedFather, mother1, sharedFather, mother2, child1, child2),
                "Half siblings sharing father must be recognized as inbreeding");

        // Completely unrelated wolves
        UUID unrelatedFather = UUID.randomUUID();
        assertFalse(WolfInbreedingHelper.isLineageRelated(sharedFather, mother1, unrelatedFather, mother2, child1, child2),
                "Unrelated wolves must not be flagged as inbreeding");
    }

    @Test
    @DisplayName("Assert runt scale calculation math and clamping")
    void testRuntScaleMath() {
        // Standard offspring scale 1.0f -> runt scale 0.70f
        assertEquals(0.70f, WolfInbreedingHelper.calculateRuntScale(1.0f, 0.70f), 0.001f);

        // Large offspring scale 1.20f -> runt scale 0.84f
        assertEquals(0.84f, WolfInbreedingHelper.calculateRuntScale(1.20f, 0.70f), 0.001f);

        // Small offspring scale 0.50f -> 0.35f
        assertEquals(0.35f, WolfInbreedingHelper.calculateRuntScale(0.50f, 0.70f), 0.001f);

        // Lower clamp (0.30f minimum)
        assertEquals(0.30f, WolfInbreedingHelper.calculateRuntScale(0.30f, 0.70f), 0.001f);

        // Degenerate inputs
        assertEquals(0.70f, WolfInbreedingHelper.calculateRuntScale(0.0f, 0.70f), 0.001f);
        assertEquals(0.70f, WolfInbreedingHelper.calculateRuntScale(1.0f, 0.0f), 0.001f);
    }

    @Test
    @DisplayName("Assert strict null safety across helper methods")
    void testNullSafety() {
        assertFalse(WolfInbreedingHelper.isLineageRelated(null, null, null, null, null, null));
        assertDoesNotThrow(() -> WolfInbreedingHelper.processBreedingLineage(null, null, null, null));
        assertDoesNotThrow(() -> WolfInbreedingHelper.playRuntBirthFeedback(null, null));
    }
}
