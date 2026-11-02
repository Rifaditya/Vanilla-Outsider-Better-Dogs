// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.vanillaoutsider.betterdogs.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CoreMechanicsSuiteTest {

    @Test
    @DisplayName("Mechanic 1: Petting & Affection Timing Invariants")
    public void testPettingAffectionTiming() {
        assertEquals(12000L, WolfPettingHelper.SOOTHE_DURATION_TICKS, "Soothe duration must be 12,000 ticks (10 minutes)");
        assertFalse(WolfPettingHelper.canPet(null, null, null, null), "Null inputs must reject petting");
        assertFalse(WolfPettingHelper.isSoothed(null), "Null wolf must not be marked as soothed");
    }

    @Test
    @DisplayName("Mechanic 2: Genetics Personality & Scale Distribution")
    public void testGeneticsAndScaleDistribution() {
        RandomSource random = RandomSource.create(42L);

        // Same parent personality inheritance (~80%)
        int sameCount = 0;
        int trials = 500;
        for (int i = 0; i < trials; i++) {
            WolfPersonality result = WolfGeneticsHelper.calculateOffspringPersonality(null, WolfPersonality.AGGRESSIVE, WolfPersonality.AGGRESSIVE, random);
            if (result == WolfPersonality.AGGRESSIVE) {
                sameCount++;
            }
        }
        double ratio = (double) sameCount / trials;
        assertTrue(ratio >= 0.70 && ratio <= 0.90, "Expected ~80% inheritance, got: " + ratio);

        // Scale inheritance within clamped bounds
        float scale = WolfScaleGeneticsHelper.calculateOffspringScale(null, 1.0f, 1.0f, random);
        assertTrue(scale >= 0.70f && scale <= 1.45f, "Scale must remain clamped between 0.70 and 1.45, got: " + scale);
    }

    @Test
    @DisplayName("Mechanic 3: Dog Food & Treats Classification")
    public void testDogFoodClassification() {
        assertFalse(DogFoodHelper.isRawMeat(null), "Null stack must not be raw meat");
        assertFalse(DogFoodHelper.isCookedMeat(null), "Null stack must not be cooked meat");
        assertEquals(2.0F, DogFoodHelper.calculateHealAmount(null), 0.001F, "Null item stack must fall back to 2.0F healing");
        assertEquals(2.0F, DogFoodHelper.calculateHealAmount(ItemStack.EMPTY), 0.001F, "Empty item stack must fall back to 2.0F healing");
    }

    @Test
    @DisplayName("Mechanic 4: Adoption & Ownership Transfer State Machine")
    public void testAdoptionStateMachine() {
        assertFalse(WolfAdoptionHelper.canListForAdoption(null, null, null), "Null inputs must reject adoption listing");
        assertFalse(WolfAdoptionHelper.canCancelAdoption(null, null, null), "Null inputs must reject adoption cancellation");
        assertFalse(WolfAdoptionHelper.canAdopt(null, null, null), "Null inputs must reject adoption");
    }

    @Test
    @DisplayName("Mechanic 5: Thermal Hazard & Danger Avoidance Boundaries")
    public void testHazardAvoidanceBoundaries() {
        assertFalse(WolfHazardHelper.isThermalHazard(null), "Null block state must not be thermal hazard");
        assertFalse(WolfHazardHelper.isDirectHazard(null, null), "Null level or pos must not be direct hazard");
        assertFalse(WolfHazardHelper.isHazardNear(null, null, 5, 2), "Null level or pos must not detect nearby hazards");
    }
}
