// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClientConfigGuiTest {

    @Test
    void testBetterDogsConfigDefaults() {
        BetterDogsConfig config = BetterDogsConfig.get();
        assertNotNull(config);
        assertEquals(0.20, config.globalSpeedBuff, 0.001);
        assertTrue(config.enableStormAnxiety);
        assertTrue(config.enableCliffSafety);
    }
}
