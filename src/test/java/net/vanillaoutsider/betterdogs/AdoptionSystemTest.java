// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfAdoptionHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AdoptionSystemTest {

    @Test
    @DisplayName("Verify WolfAdoptionHelper Null Safety and Predicates")
    public void testAdoptionNullSafety() {
        Assertions.assertFalse(WolfAdoptionHelper.canListForAdoption(null, null, null));
        Assertions.assertFalse(WolfAdoptionHelper.canCancelAdoption(null, null, null));
        Assertions.assertFalse(WolfAdoptionHelper.canAdopt(null, null, null));
        Assertions.assertDoesNotThrow(() -> WolfAdoptionHelper.tryHandleAdoption(null, null, null));
        Assertions.assertDoesNotThrow(() -> WolfAdoptionHelper.tickAdoptionAmbientParticles(null));
    }
}
