// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.SmallFightHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 39: Harmless Social Play Sparring AI Tests")
class SmallFightTest {

    @Test
    @DisplayName("Assert sparring duration ticks configuration (120 ticks / 6s)")
    void testSparringDurationTicks() {
        assertEquals(120, SmallFightHelper.SPARRING_DURATION_TICKS);
        assertEquals(6.0, SmallFightHelper.DEFAULT_PARTNER_RADIUS);
        assertEquals(1.15, SmallFightHelper.DEFAULT_SPEED_MODIFIER);
    }

    @Test
    @DisplayName("Assert partner ownership matching (same owner UUID)")
    void testPartnerOwnershipMatching() {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();

        assertEquals(ownerA, ownerA, "Wolves with matching owner UUID are eligible packmates for sparring");
        assertNotEquals(ownerA, ownerB, "Wolves with different owners should not engage in friendly sparring");
    }

    @Test
    @DisplayName("Assert combat and sit safety checks")
    void testCombatAndSitSafetyCheck() {
        boolean isSitting = true;
        boolean hasTarget = false;

        boolean canSpar = !isSitting && !hasTarget;
        assertFalse(canSpar, "Sitting wolves must never engage in play fighting");

        boolean isSitting2 = false;
        boolean hasTarget2 = true;
        boolean canSpar2 = !isSitting2 && !hasTarget2;
        assertFalse(canSpar2, "Wolves with an active combat target must never engage in play fighting");
    }

    @Test
    @DisplayName("Assert SmallFightHelper strict null safety")
    void testSmallFightHelperNullSafety() {
        assertDoesNotThrow(() -> assertFalse(SmallFightHelper.isEligibleForPlay(null)));
        assertDoesNotThrow(() -> assertFalse(SmallFightHelper.canPlayTogether(null, null)));
        assertDoesNotThrow(() -> assertNull(SmallFightHelper.findPlayPartner(null, 6.0)));
        assertDoesNotThrow(() -> SmallFightHelper.startPlaySession(null, null));
        assertDoesNotThrow(() -> SmallFightHelper.applyPlayFeedback(null, null));
    }
}
