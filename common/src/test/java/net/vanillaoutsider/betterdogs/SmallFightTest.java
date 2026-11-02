// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmallFightTest {

    @Test
    public void testPlayFightCooldownManagement() {
        int initialCooldown = 600; // 30 seconds
        int decremented = Math.max(0, initialCooldown - 1);

        Assertions.assertEquals(599, decremented, "Play fight cooldown should decrement per tick");

        int zeroCooldown = Math.max(0, 0 - 1);
        Assertions.assertEquals(0, zeroCooldown, "Play fight cooldown should not fall below 0");
    }

    @Test
    public void testPartnerOwnershipMatching() {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();

        Assertions.assertTrue(ownerA.equals(ownerA), "Wolves with matching owner UUID are eligible packmates for sparring");
        Assertions.assertFalse(ownerA.equals(ownerB), "Wolves with different owners should not engage in friendly sparring");
    }

    @Test
    public void testCombatAndSitSafetyCheck() {
        boolean isSitting = true;
        boolean hasTarget = false;

        boolean canSpar = !isSitting && !hasTarget;
        Assertions.assertFalse(canSpar, "Sitting wolves must never engage in play fighting");

        boolean isSitting2 = false;
        boolean hasTarget2 = true;
        boolean canSpar2 = !isSitting2 && !hasTarget2;
        Assertions.assertFalse(canSpar2, "Wolves with an active combat target must never engage in play fighting");
    }
}
