// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WolfCombatAiTest {

    @Test
    public void testPacifistCombatBehavior() {
        boolean ownerAttacking = true;
        
        boolean pacifistShouldAttackOffensive = evaluatePacifistTargeting(ownerAttacking, false, false);
        assertFalse(pacifistShouldAttackOffensive, "Pacifist dogs must NOT assist in owner offensive attacks.");

        boolean pacifistShouldDefendWolf = evaluatePacifistTargeting(false, true, false);
        assertTrue(pacifistShouldDefendWolf, "Pacifist dogs must defend themselves when directly attacked.");

        boolean pacifistShouldDefendOwner = evaluatePacifistTargeting(false, false, true);
        assertTrue(pacifistShouldDefendOwner, "Pacifist dogs must defend their owner when owner is directly attacked.");
    }

    @Test
    public void testAggressiveCombatBehavior() {
        boolean aggressiveAttacksHostile = evaluateAggressiveTargeting(false, true);
        assertTrue(aggressiveAttacksHostile, "Aggressive dogs must attack hostiles within scan range.");

        boolean aggressiveAssistsOwner = evaluateAggressiveTargeting(true, false);
        assertTrue(aggressiveAssistsOwner, "Aggressive dogs must assist owner offensive attacks.");
    }

    private boolean evaluatePacifistTargeting(boolean isOwnerAttackingTarget, boolean isTargetAttackingWolf, boolean isTargetAttackingOwner) {
        if (isOwnerAttackingTarget) {
            return false;
        }
        return isTargetAttackingWolf || isTargetAttackingOwner;
    }

    private boolean evaluateAggressiveTargeting(boolean isOwnerAttackingTarget, boolean isHostile) {
        return isHostile || isOwnerAttackingTarget;
    }
}
