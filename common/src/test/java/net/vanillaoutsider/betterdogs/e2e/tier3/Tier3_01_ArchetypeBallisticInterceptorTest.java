// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier3;

import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.FireballTrajectoryHelper;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 3 - Pairwise 01: Personality Archetype x Ballistic Interceptor Tests")
public class Tier3_01_ArchetypeBallisticInterceptorTest {

    @Test
    @DisplayName("Assert Guardian and Fearless archetypes have higher damage and defensive intercept efficiency")
    public void testGuardianAndFearlessInterceptionAffinity() {
        CanineArchetype guardian = CanineArchetype.GUARDIAN;
        CanineArchetype fearless = CanineArchetype.FEARLESS;

        assertTrue(guardian.getDamageModifier() > 1.0, "Guardian must deal >1.0x damage");
        assertTrue(fearless.getDamageModifier() > 1.0, "Fearless must deal >1.0x damage");
        assertTrue(fearless.getSpeedModifier() > 1.0, "Fearless must have >1.0x speed for rapid interception response");
    }

    @Test
    @DisplayName("Assert Lazy archetype lower speed interacts with intercept reaction distance")
    public void testLazyArchetypeTrajectoryInterceptPhysics() {
        CanineArchetype lazy = CanineArchetype.LAZY;
        assertEquals(0.85, lazy.getSpeedModifier(), 1e-4, "Lazy dogs move at 85% speed");

        Vec3 dogPos = new Vec3(0, 64, 0);
        Vec3 projPos = new Vec3(0, 64, 20);
        Vec3 projVel = new Vec3(0, 0, -1.0);

        // Scaled reaction ticks accounting for lazy reaction
        double baseReaction = 20.0;
        double effectiveReaction = baseReaction * lazy.getSpeedModifier();

        FireballTrajectoryHelper.TrajectoryInterception interception =
                FireballTrajectoryHelper.computeInterception(dogPos, projPos, projVel, effectiveReaction, 1.75, 85.0);

        // At tau=20, effectiveReaction=17.0, so the lazy dog only intercepts if projectile is closer
        assertFalse(interception.intercepted(), "Lazy dog with reduced reaction window cannot intercept projectile 20 ticks away");

        // When closer (tau=15 ticks):
        FireballTrajectoryHelper.TrajectoryInterception closeInterception =
                FireballTrajectoryHelper.computeInterception(dogPos, new Vec3(0, 64, 15), projVel, effectiveReaction, 1.75, 85.0);
        assertTrue(closeInterception.intercepted(), "Lazy dog intercepts projectile when within effective reaction window");
    }

    @Test
    @DisplayName("Assert Playful archetype high speed grants expanded interception mobility")
    public void testPlayfulArchetypeMobility() {
        CanineArchetype playful = CanineArchetype.PLAYFUL;
        assertEquals(1.15, playful.getSpeedModifier(), 1e-4, "Playful dogs move at 115% speed");

        double baseReaction = 20.0;
        double expandedReaction = baseReaction * playful.getSpeedModifier(); // 23.0 ticks

        Vec3 dogPos = new Vec3(0, 64, 0);
        Vec3 projPos = new Vec3(0, 64, 22);
        Vec3 projVel = new Vec3(0, 0, -1.0);

        FireballTrajectoryHelper.TrajectoryInterception interception =
                FireballTrajectoryHelper.computeInterception(dogPos, projPos, projVel, expandedReaction, 1.75, 85.0);

        assertTrue(interception.intercepted(), "Playful dog with 115% speed window intercepts earlier (at 22 ticks)");
    }
}
