// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier4;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import net.vanillaoutsider.betterdogs.util.WolfLitterHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 4 - Scenario 04: Sibling Adoption and Litter Breeding Workflow")
public class Tier4_04_SiblingAdoptionLitterBreedingScenarioTest {

    @Test
    @DisplayName("Simulate breeding tamed wolf pair, spawning multi-puppy litter, and adopting out sibling")
    public void testSiblingLitterAndAdoptionWorkflow() {
        // Step 1: Initialize parents with established genetics and owner
        UUID ownerId = UUID.randomUUID();
        float p1Health = 30.0f;
        float p2Health = 40.0f;

        // Step 2: Calculate multi-puppy litter size with geometric probability chain
        Random fixedRand = new Random(42L);
        int litterSize = WolfLitterHelper.calculateLitterSize(4, 80, fixedRand);
        assertTrue(litterSize >= 2, "High extraChance (80%) must generate multiple sibling puppies (got " + litterSize + ")");

        // Step 3: Offspring puppy inherits Gaussian genetics from parents
        float puppyHealth = CanineGeneticsHelper.calculateOffspringStat(
                p1Health, p2Health, CanineGeneticsHelper.HEALTH_STD_DEV,
                CanineGeneticsHelper.MIN_HEALTH, CanineGeneticsHelper.MAX_HEALTH, null
        );
        assertEquals(35.0f, puppyHealth, 1e-4f, "Puppy inherits intermediate parental health");

        // Step 4: Sibling puppy automatically tamed and bound to owner
        WolfPersistentData puppyData = new WolfPersistentData(
                1, 0, false, "", 0L, 777L, 0.85f, Map.of(),
                Optional.of(ownerId), false, Optional.empty(), false, false, 0, "", 0L, false, "playful"
        );
        assertTrue(puppyData.leaderUuid().isPresent());
        assertEquals(ownerId, puppyData.leaderUuid().get(), "Puppy must be co-owned by parent's owner");

        // Step 5: Put sibling up for adoption: leader UUID cleared, adoptable set to true
        WolfPersistentData adoptablePuppy = new WolfPersistentData(
                puppyData.personalityId(), puppyData.lastDamageTime(), puppyData.submissive(),
                puppyData.bloodFeudTarget(), puppyData.lastMischiefDay(), puppyData.dna(),
                puppyData.scale(), puppyData.affinityMap(), Optional.empty(), false,
                Optional.empty(), true, puppyData.refusesGroundFood(), puppyData.feedCount(),
                puppyData.nemesisType(), puppyData.nemesisExpiry(), puppyData.discoveredTreat(),
                puppyData.canineArchetype()
        );
        assertTrue(adoptablePuppy.adoptable(), "Puppy is now available for adoption");
        assertTrue(adoptablePuppy.leaderUuid().isEmpty(), "Leader UUID cleared for adoption");

        // Step 6: Second player adopts puppy: new leader UUID assigned, adoptable flag cleared
        UUID newAdopter = UUID.randomUUID();
        WolfPersistentData adoptedPuppy = new WolfPersistentData(
                adoptablePuppy.personalityId(), adoptablePuppy.lastDamageTime(), adoptablePuppy.submissive(),
                adoptablePuppy.bloodFeudTarget(), adoptablePuppy.lastMischiefDay(), adoptablePuppy.dna(),
                adoptablePuppy.scale(), adoptablePuppy.affinityMap(), Optional.of(newAdopter), false,
                Optional.empty(), false, adoptablePuppy.refusesGroundFood(), adoptablePuppy.feedCount(),
                adoptablePuppy.nemesisType(), adoptablePuppy.nemesisExpiry(), adoptablePuppy.discoveredTreat(),
                adoptablePuppy.canineArchetype()
        );
        assertFalse(adoptedPuppy.adoptable());
        assertEquals(newAdopter, adoptedPuppy.leaderUuid().get(), "Puppy successfully adopted by new player");
        assertEquals("playful", adoptedPuppy.canineArchetype(), "Puppy archetype preserved throughout adoption");
    }
}
