// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.ai.FleeCreeperGoal;
import net.vanillaoutsider.betterdogs.util.ParticleDensity;
import net.vanillaoutsider.betterdogs.util.WolfParticleHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 27: Creeper Threat Repulsion AI Tests")
class CreeperEvasionTest {

    @Test
    @DisplayName("Assert 10-block Creeper evasion perimeter math (d <= 10.0m, d^2 <= 100.0)")
    void testEvasionPerimeterMath() {
        float distance = FleeCreeperGoal.DEFAULT_FLEE_DISTANCE;
        assertEquals(10.0F, distance, 0.001F);

        double maxDistSq = (double) (distance * distance);
        assertEquals(100.0D, maxDistSq, 0.001D);

        assertTrue(36.0D <= maxDistSq); // 6 blocks away
        assertTrue(100.0D <= maxDistSq); // Exactly 10 blocks away
        assertFalse(100.01D <= maxDistSq); // Beyond 10 blocks
    }

    @Test
    @DisplayName("Assert 1.5x evasion sprint speed multiplier")
    void testEvasionSprintSpeed() {
        assertEquals(1.5D, FleeCreeperGoal.DEFAULT_SPRINT_SPEED, 0.001D);
    }

    @Test
    @DisplayName("Assert ParticleDensity 4-tier enum parsing and count scaling (0, 1, 3, 6)")
    void testParticleDensityScaling() {
        assertEquals(ParticleDensity.NONE, ParticleDensity.fromInt(0));
        assertEquals(ParticleDensity.LOW, ParticleDensity.fromInt(1));
        assertEquals(ParticleDensity.MEDIUM, ParticleDensity.fromInt(2));
        assertEquals(ParticleDensity.HIGH, ParticleDensity.fromInt(3));

        assertEquals(0, ParticleDensity.NONE.getDefaultCount());
        assertEquals(1, ParticleDensity.LOW.getDefaultCount());
        assertEquals(3, ParticleDensity.MEDIUM.getDefaultCount());
        assertEquals(6, ParticleDensity.HIGH.getDefaultCount());

        assertEquals(ParticleDensity.NONE, ParticleDensity.fromString("none"));
        assertEquals(ParticleDensity.LOW, ParticleDensity.fromString("low"));
        assertEquals(ParticleDensity.MEDIUM, ParticleDensity.fromString("medium"));
        assertEquals(ParticleDensity.HIGH, ParticleDensity.fromString("high"));
        assertEquals(ParticleDensity.MEDIUM, ParticleDensity.fromString("invalid"));
        assertEquals(ParticleDensity.MEDIUM, ParticleDensity.fromString(null));
    }

    @Test
    @DisplayName("Assert null safety across Creeper evasion helpers")
    void testEvasionNullSafety() {
        assertFalse(FleeCreeperGoal.isThreateningCreeper(null));
        assertEquals(ParticleDensity.MEDIUM, WolfParticleHelper.getDensity(null));
        assertEquals(3, WolfParticleHelper.getScaledCount(null, ParticleDensity.MEDIUM));
        assertDoesNotThrow(() -> WolfParticleHelper.spawnParticles(null, null, 0, 0, 0, 0, 0));
    }
}
