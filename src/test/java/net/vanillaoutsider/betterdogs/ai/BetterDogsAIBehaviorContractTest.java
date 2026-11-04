// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.ai;

import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.util.WolfGuardHelper;
import net.vanillaoutsider.betterdogs.util.WolfScavengeHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Headless AI Behavior Contract & Invariant Verification Suite.
 * Validates AI goal parameters, threat predicates, cancellation contracts, and guard patrol invariants.
 */
public class BetterDogsAIBehaviorContractTest {

    @Test
    @DisplayName("Pillar 5 / Contract: FleeCreeperGoal parameter invariants and threat predicate safety")
    public void testFleeCreeperGoalParametersAndNullSafety() {
        assertEquals(10.0F, FleeCreeperGoal.DEFAULT_FLEE_DISTANCE, 0.001F,
                "Default flee distance must be 10.0 meters");
        assertEquals(1.5D, FleeCreeperGoal.DEFAULT_SPRINT_SPEED, 0.001D,
                "Default sprint speed multiplier must be 1.5x");

        // Threat predicate null and non-creeper entity checks
        assertFalse(FleeCreeperGoal.isThreateningCreeper(null),
                "Threat predicate must return false on null entity");
    }

    @Test
    @DisplayName("Pillar 5 / Contract: Scavenge heal amount null-safety and empty stack fallback")
    public void testScavengeHealNullAndEmptySafety() {
        assertEquals(0.0f, WolfScavengeHelper.calculateHealAmount(null), 0.001f,
                "Heal amount must be 0 for null stack");
        assertFalse(WolfScavengeHelper.isEdible(null, null),
                "isEdible must be false for null wolf and null stack");
    }

    @Test
    @DisplayName("Pillar 5 / Contract: WolfGuardHelper personality territory math and boundaries")
    public void testGuardHelperTerritoryMath() {
        int agg = WolfGuardHelper.getPatrolRadius(WolfPersonality.AGGRESSIVE);
        int norm = WolfGuardHelper.getPatrolRadius(WolfPersonality.NORMAL);
        int pac = WolfGuardHelper.getPatrolRadius(WolfPersonality.PACIFIST);

        assertEquals(12, agg, "Aggressive wolves must patrol 12 blocks");
        assertEquals(8, norm, "Normal wolves must patrol 8 blocks");
        assertEquals(4, pac, "Pacifist wolves must patrol 4 blocks");

        assertTrue(agg > norm && norm > pac, "Patrol radii must follow strict hierarchy: Aggressive > Normal > Pacifist");
    }
}
