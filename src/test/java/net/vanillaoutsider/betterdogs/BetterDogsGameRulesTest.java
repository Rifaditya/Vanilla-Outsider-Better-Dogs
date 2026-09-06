// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BetterDogsGameRulesTest {

    @Test
    public void testNullSafeAccessors() {
        boolean boolVal = BetterDogsGameRules.getBoolean(null, null, true);
        assertTrue(boolVal, "Null level and key should return fallback boolean value true");

        boolean boolValFalse = BetterDogsGameRules.getBoolean(null, null, false);
        assertFalse(boolValFalse, "Null level and key should return fallback boolean value false");

        int intVal = BetterDogsGameRules.getInt(null, null, 64);
        assertEquals(64, intVal, "Null level and key should return fallback integer value");
    }

    @Test
    public void testWolfScaleConfigValidation() {
        net.vanillaoutsider.betterdogs.config.BetterDogsConfig config = new net.vanillaoutsider.betterdogs.config.BetterDogsConfig();

        // Clamping below minimum
        config.wolfMinScale = -0.50;
        config.wolfMaxScale = -0.10;
        config.validate();
        assertEquals(0.01, config.getWolfMinScale(), 1e-6, "wolfMinScale should be clamped to 0.01 minimum");
        assertEquals(0.01, config.getWolfMaxScale(), 1e-6, "wolfMaxScale should be clamped to 0.01 minimum");

        // Freedom above maximum: massive scales remain intact
        config.wolfMinScale = 50.0;
        config.wolfMaxScale = 100.0;
        config.validate();
        assertEquals(50.0, config.getWolfMinScale(), 1e-6, "wolfMinScale should remain intact without artificial upper caps");
        assertEquals(100.0, config.getWolfMaxScale(), 1e-6, "wolfMaxScale should remain intact without artificial upper caps");

        // Inversion check: min > max
        config.wolfMinScale = 2.50;
        config.wolfMaxScale = 1.50;
        config.validate();
        assertTrue(config.getWolfMinScale() <= config.getWolfMaxScale(), "wolfMinScale should not exceed wolfMaxScale");
        assertEquals(1.50, config.getWolfMinScale(), 1e-6, "wolfMinScale should be clamped to wolfMaxScale");
    }
}
