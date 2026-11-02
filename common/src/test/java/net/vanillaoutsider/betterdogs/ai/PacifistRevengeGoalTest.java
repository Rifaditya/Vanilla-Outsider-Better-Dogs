// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import net.vanillaoutsider.betterdogs.WolfPersonality;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PacifistRevengeGoalTest {

    @Test
    public void testPersonalityDefensePolicy() {
        WolfPersonality pacifist = WolfPersonality.PACIFIST;
        WolfPersonality aggressive = WolfPersonality.AGGRESSIVE;
        WolfPersonality normal = WolfPersonality.NORMAL;

        assertEquals("pacifist", pacifist.getId());
        assertEquals("aggressive", aggressive.getId());
        assertEquals("normal", normal.getId());

        assertNotEquals(pacifist, aggressive);
        assertNotEquals(pacifist, normal);
    }
}
