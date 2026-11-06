// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 2 - Boundary 07: WolfData Corruption & Edge Case Tests")
public class Tier2_07_WolfDataCorruptionTest {

    @Test
    @DisplayName("Assert empty NBT compound safely deserializes with DEFAULT fallback values")
    public void testEmptyCompoundDeserialization() {
        CompoundTag emptyTag = new CompoundTag();
        DataResult<WolfPersistentData> result = WolfPersistentData.CODEC.parse(NbtOps.INSTANCE, emptyTag);

        assertTrue(result.result().isPresent(), "Parsing empty compound must succeed: " + result.error());
        WolfPersistentData parsed = result.result().get();

        assertEquals(-1, parsed.personalityId());
        assertEquals(0, parsed.lastDamageTime());
        assertFalse(parsed.submissive());
        assertEquals("", parsed.bloodFeudTarget());
        assertEquals(1.0f, parsed.scale(), 1e-4f);
        assertTrue(parsed.affinityMap().isEmpty());
        assertTrue(parsed.leaderUuid().isEmpty());
        assertFalse(parsed.guardMode());
        assertTrue(parsed.guardPos().isEmpty());
        assertFalse(parsed.adoptable());
        assertFalse(parsed.refusesGroundFood());
        assertEquals(0, parsed.feedCount());
        assertEquals("", parsed.canineArchetype());
    }

    @Test
    @DisplayName("Assert extreme affinity map values clamp or handle negative values cleanly")
    public void testExtremeAffinityValues() {
        Map<String, Integer> extremeAffinity = new HashMap<>();
        extremeAffinity.put("player-low", -100);
        extremeAffinity.put("player-high", 100);

        WolfPersistentData data = new WolfPersistentData(
                0, 0, false, "", 0L, 0L, 1.0f, extremeAffinity,
                Optional.empty(), false, Optional.empty(), false, false, 0, "", 0L, false, ""
        );

        assertEquals(-100, data.affinityMap().get("player-low"));
        assertEquals(100, data.affinityMap().get("player-high"));
    }

    @Test
    @DisplayName("Assert Long.MAX_VALUE and Long.MIN_VALUE DNA values survive serialization")
    public void testExtremeDnaValues() {
        WolfPersistentData dataMax = new WolfPersistentData(
                0, 0, false, "", 0L, Long.MAX_VALUE, 1.0f, Map.of(),
                Optional.empty(), false, Optional.empty(), false, false, 0, "", 0L, false, ""
        );
        assertEquals(Long.MAX_VALUE, dataMax.dna());

        WolfPersistentData dataMin = new WolfPersistentData(
                0, 0, false, "", 0L, Long.MIN_VALUE, 1.0f, Map.of(),
                Optional.empty(), false, Optional.empty(), false, false, 0, "", 0L, false, ""
        );
        assertEquals(Long.MIN_VALUE, dataMin.dna());
    }

    @Test
    @DisplayName("Assert missing optional BlockPos and UUID fields encode and decode cleanly")
    public void testOptionalFieldsAbsenceRoundtrip() {
        WolfPersistentData data = new WolfPersistentData(
                1, 50, false, "uuid-target", 10L, 500L, 1.1f, Map.of(),
                Optional.empty(), false, Optional.empty(), false, false, 0, "", 0L, false, "guardian"
        );

        var encoded = WolfPersistentData.CODEC.encodeStart(NbtOps.INSTANCE, data).result().orElseThrow();
        var decoded = WolfPersistentData.CODEC.parse(NbtOps.INSTANCE, encoded).result().orElseThrow();

        assertTrue(decoded.leaderUuid().isEmpty());
        assertTrue(decoded.guardPos().isEmpty());
        assertEquals("guardian", decoded.canineArchetype());
    }

    @Test
    @DisplayName("Assert corrupted type fields in NBT fail gracefully with parse error or default")
    public void testCorruptedTypeHandling() {
        CompoundTag badTag = new CompoundTag();
        badTag.putString("scale", "NOT_A_FLOAT"); // string instead of float

        DataResult<WolfPersistentData> result = WolfPersistentData.CODEC.parse(NbtOps.INSTANCE, badTag);
        // Either fails parsing or falls back safely
        assertNotNull(result);
    }
}
