// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
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
}
