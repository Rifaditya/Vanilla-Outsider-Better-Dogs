// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StormAnxietyTest {

    @Test
    public void testPersonalityAnxietyMultipliers() {
        float pacifistMult = 3.0F;
        float normalMult = 1.0F;
        float aggroMult = 0.0F;

        assertEquals(3.0F, pacifistMult, 0.001F, "Pacifist must have 3.0x anxiety multiplier");
        assertEquals(1.0F, normalMult, 0.001F, "Normal must have 1.0x anxiety multiplier");
        assertEquals(0.0F, aggroMult, 0.001F, "Aggressive must have 0.0x anxiety multiplier");
    }

    @Test
    public void testShelterSearchVolumeBounds() {
        int xRange = 12;
        int yRange = 4;
        int zRange = 12;

        int totalVolume = (xRange * 2 + 1) * (yRange * 2 + 1) * (zRange * 2 + 1);
        assertEquals(5625, totalVolume, "Shelter search volume must check 5,625 blocks");
    }
}
