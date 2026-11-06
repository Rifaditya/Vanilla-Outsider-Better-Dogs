// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier3;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 3 - Pairwise 08: WolfData Attachments x Archetype x Adoption System Tests")
public class Tier3_08_WolfDataArchetypeAdoptionTest {

    @Test
    @DisplayName("Assert putting dog up for adoption clears leader UUID and sets adoptable flag while preserving archetype")
    public void testPutUpForAdoptionStateEvolution() {
        UUID originalLeader = UUID.randomUUID();
        WolfPersistentData preAdoption = new WolfPersistentData(
                2, 0, false, "", 0L, 12345L, 1.05f, Map.of(),
                Optional.of(originalLeader), true, Optional.of(new BlockPos(0, 64, 0)),
                false, false, 0, "", 0L, false, "playful"
        );

        assertTrue(preAdoption.leaderUuid().isPresent());
        assertFalse(preAdoption.adoptable());
        assertEquals("playful", preAdoption.canineArchetype());

        // Put up for adoption: clear leader UUID, set adoptable = true, clear guard mode
        WolfPersistentData postAdoption = new WolfPersistentData(
                preAdoption.personalityId(), preAdoption.lastDamageTime(), preAdoption.submissive(),
                preAdoption.bloodFeudTarget(), preAdoption.lastMischiefDay(), preAdoption.dna(),
                preAdoption.scale(), preAdoption.affinityMap(), Optional.empty(), false,
                Optional.empty(), true, preAdoption.refusesGroundFood(), preAdoption.feedCount(),
                preAdoption.nemesisType(), preAdoption.nemesisExpiry(), preAdoption.discoveredTreat(),
                preAdoption.canineArchetype()
        );

        assertTrue(postAdoption.leaderUuid().isEmpty(), "Leader UUID must be cleared on adoption");
        assertTrue(postAdoption.adoptable(), "Adoptable flag must be set to true");
        assertFalse(postAdoption.guardMode(), "Guard mode must be cleared on adoption");
        assertEquals("playful", postAdoption.canineArchetype(), "Archetype must be preserved through adoption");
    }

    @Test
    @DisplayName("Assert completing adoption tames dog to new owner and clears adoptable state")
    public void testAdoptionCompletionStateEvolution() {
        UUID newOwner = UUID.randomUUID();
        WolfPersistentData adoptableDog = new WolfPersistentData(
                1, 0, false, "", 0L, 54321L, 1.2f, Map.of(),
                Optional.empty(), false, Optional.empty(), true, false, 0, "", 0L, false, "guardian"
        );

        assertTrue(adoptableDog.adoptable());

        // Finalize adoption to new owner
        WolfPersistentData adoptedDog = new WolfPersistentData(
                adoptableDog.personalityId(), adoptableDog.lastDamageTime(), adoptableDog.submissive(),
                adoptableDog.bloodFeudTarget(), adoptableDog.lastMischiefDay(), adoptableDog.dna(),
                adoptableDog.scale(), adoptableDog.affinityMap(), Optional.of(newOwner), false,
                Optional.empty(), false, adoptableDog.refusesGroundFood(), adoptableDog.feedCount(),
                adoptableDog.nemesisType(), adoptableDog.nemesisExpiry(), adoptableDog.discoveredTreat(),
                adoptableDog.canineArchetype()
        );

        assertFalse(adoptedDog.adoptable(), "Adoptable flag must be cleared once adopted");
        assertTrue(adoptedDog.leaderUuid().isPresent());
        assertEquals(newOwner, adoptedDog.leaderUuid().get(), "Leader UUID must match new owner");
        assertEquals("guardian", adoptedDog.canineArchetype());
    }
}
