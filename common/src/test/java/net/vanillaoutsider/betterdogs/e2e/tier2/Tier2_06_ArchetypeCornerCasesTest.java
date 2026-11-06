// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier2;

import java.util.UUID;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.personality.CaninePersonalityHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 2 - Boundary 06: Archetype Corner Cases & Mapping Tests")
public class Tier2_06_ArchetypeCornerCasesTest {

    @Test
    @DisplayName("Assert out-of-range IDs fallback gracefully to GUARDIAN")
    public void testOutOfRangeIdFallback() {
        assertSame(CanineArchetype.GUARDIAN, CanineArchetype.fromId(-1));
        assertSame(CanineArchetype.GUARDIAN, CanineArchetype.fromId(6));
        assertSame(CanineArchetype.GUARDIAN, CanineArchetype.fromId(99999));
        assertSame(CanineArchetype.GUARDIAN, CanineArchetype.fromId(Integer.MIN_VALUE));
    }

    @Test
    @DisplayName("Assert null and empty serialized names fallback gracefully to GUARDIAN")
    public void testNullAndEmptyNameFallback() {
        assertSame(CanineArchetype.GUARDIAN, CanineArchetype.fromSerializedName(null));
        assertSame(CanineArchetype.GUARDIAN, CanineArchetype.fromSerializedName(""));
        assertSame(CanineArchetype.GUARDIAN, CanineArchetype.fromSerializedName("non_existent_archetype"));
    }

    @Test
    @DisplayName("Assert case-insensitive resolution of serialized names")
    public void testCaseInsensitiveNameResolution() {
        assertSame(CanineArchetype.GUARDIAN, CanineArchetype.fromSerializedName("GUARDIAN"));
        assertSame(CanineArchetype.PLAYFUL, CanineArchetype.fromSerializedName("PlayFul"));
        assertSame(CanineArchetype.FEARLESS, CanineArchetype.fromSerializedName("fEaRlEsS"));
        assertSame(CanineArchetype.ADVENTURER, CanineArchetype.fromSerializedName("adventurer"));
    }

    @Test
    @DisplayName("Assert defaultFallback() returns GUARDIAN")
    public void testDefaultFallbackMethod() {
        assertSame(CanineArchetype.GUARDIAN, CanineArchetype.defaultFallback());
    }

    @Test
    @DisplayName("Assert splitmix hash uniformity under sequential UUID inputs")
    public void testSequentialUuidDistribution() {
        // Sequentially increasing UUID least significant bits to test bit mixing
        int[] counts = new int[6];
        for (long i = 0; i < 6000; i++) {
            UUID seqUuid = new UUID(42L, i);
            CanineArchetype rolled = CaninePersonalityHelper.rollFromUUID(seqUuid, null);
            counts[rolled.getId()]++;
        }

        // None of the buckets should be starved or exceed 2000
        for (int i = 0; i < 6; i++) {
            assertTrue(counts[i] > 600, "Bucket " + i + " was starved under sequential UUIDs: " + counts[i]);
            assertTrue(counts[i] < 1500, "Bucket " + i + " was clustered under sequential UUIDs: " + counts[i]);
        }
    }
}
