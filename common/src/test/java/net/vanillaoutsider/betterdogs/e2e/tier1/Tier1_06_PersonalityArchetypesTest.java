// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier1;

import java.util.UUID;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.personality.CaninePersonalityHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 1 - Feature 06: Extensible Canine Personality Archetypes Tests")
public class Tier1_06_PersonalityArchetypesTest {

    @Test
    @DisplayName("Assert all 6 archetypes exist and have unique IDs and serialized names")
    public void testArchetypeEnumIntegrity() {
        CanineArchetype[] archetypes = CanineArchetype.values();
        assertEquals(6, archetypes.length, "Must define exactly 6 personality archetypes");

        for (CanineArchetype a : archetypes) {
            assertNotNull(a.getSerializedName());
            assertFalse(a.getSerializedName().isEmpty());
            assertTrue(a.getId() >= 0 && a.getId() < 6);
            assertEquals(a, CanineArchetype.fromId(a.getId()));
            assertEquals(a, CanineArchetype.fromSerializedName(a.getSerializedName()));
        }
    }

    @Test
    @DisplayName("Assert archetype attribute modifiers reflect distinct gameplay roles")
    public void testArchetypeAttributeModifiers() {
        // Guardian: High damage, slightly lower speed
        CanineArchetype guardian = CanineArchetype.GUARDIAN;
        assertEquals(1.0, guardian.getHealthModifier(), 1e-4);
        assertEquals(1.2, guardian.getDamageModifier(), 1e-4);
        assertEquals(0.95, guardian.getSpeedModifier(), 1e-4);

        // Playful: High speed, lower damage
        CanineArchetype playful = CanineArchetype.PLAYFUL;
        assertEquals(0.95, playful.getHealthModifier(), 1e-4);
        assertEquals(0.9, playful.getDamageModifier(), 1e-4);
        assertEquals(1.15, playful.getSpeedModifier(), 1e-4);

        // Lazy: Highest health, lowest speed and damage
        CanineArchetype lazy = CanineArchetype.LAZY;
        assertEquals(1.25, lazy.getHealthModifier(), 1e-4);
        assertEquals(0.85, lazy.getDamageModifier(), 1e-4);
        assertEquals(0.85, lazy.getSpeedModifier(), 1e-4);
    }

    @Test
    @DisplayName("Assert deterministic UUID rolling returns consistent archetypes")
    public void testDeterministicUuidRolling() {
        UUID testUuid = UUID.fromString("12345678-1234-1234-1234-123456789abc");
        CanineArchetype firstRoll = CaninePersonalityHelper.rollFromUUID(testUuid, null);
        CanineArchetype secondRoll = CaninePersonalityHelper.rollFromUUID(testUuid, null);

        assertNotNull(firstRoll);
        assertSame(firstRoll, secondRoll, "Same UUID must yield identical archetype across multiple invocations");
    }

    @Test
    @DisplayName("Assert balanced distribution mode (Mode 0) samples all 6 archetypes")
    public void testBalancedDistributionMode() {
        int[] counts = new int[6];
        int sampleSize = 6000;

        for (int i = 0; i < sampleSize; i++) {
            UUID randomUuid = UUID.randomUUID();
            CanineArchetype rolled = CaninePersonalityHelper.rollFromUUID(randomUuid, null);
            counts[rolled.getId()]++;
        }

        // Each archetype should receive roughly 1/6 (1000) of the rolls
        for (int i = 0; i < 6; i++) {
            assertTrue(counts[i] > 600, "Archetype " + i + " must have representative distribution (got " + counts[i] + ")");
        }
    }

    @Test
    @DisplayName("Assert translation keys follow standard namespace conventions")
    public void testTranslationKeysConvention() {
        for (CanineArchetype a : CanineArchetype.values()) {
            assertEquals("betterdogs.archetype." + a.getSerializedName(), a.getTranslationKey());
            assertEquals("betterdogs.archetype." + a.getSerializedName() + ".desc", a.getDescriptionTranslationKey());
        }
    }
}
