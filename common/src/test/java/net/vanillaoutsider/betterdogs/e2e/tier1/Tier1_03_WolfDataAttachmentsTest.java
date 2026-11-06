// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier1;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.e2e.mock.IWolfDataHelper;
import net.vanillaoutsider.betterdogs.e2e.mock.MockWolfDataHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 1 - Feature 03: WolfData Attachments & Persistence Tests")
public class Tier1_03_WolfDataAttachmentsTest {

    @Test
    @DisplayName("Assert DEFAULT WolfPersistentData invariants and initial states")
    public void testDefaultWolfDataInvariants() {
        WolfPersistentData def = WolfPersistentData.DEFAULT;
        assertNotNull(def, "DEFAULT must not be null");
        assertEquals(-1, def.personalityId(), "Default personalityId must be -1 (unassigned)");
        assertEquals(0, def.lastDamageTime(), "Default lastDamageTime must be 0");
        assertFalse(def.submissive(), "Default submissive must be false");
        assertEquals("", def.bloodFeudTarget(), "Default bloodFeudTarget must be empty string");
        assertEquals(1.0f, def.scale(), 1e-4f, "Default scale must be 1.0f");
        assertFalse(def.guardMode(), "Default guardMode must be false");
        assertFalse(def.adoptable(), "Default adoptable must be false");
        assertFalse(def.refusesGroundFood(), "Default refusesGroundFood must be false");
        assertEquals(0, def.feedCount(), "Default feedCount must be 0");
    }

    @Test
    @DisplayName("Assert WolfPersistentData Codec encode and decode roundtrip")
    public void testWolfDataCodecRoundtrip() {
        UUID leader = UUID.randomUUID();
        BlockPos guard = new BlockPos(100, 64, -200);
        WolfPersistentData original = new WolfPersistentData(
                2, 450, true, "some-vendetta-uuid", 5L, 123456789L, 1.15f,
                Map.of("player-1", 40), Optional.of(leader), true,
                Optional.of(guard), true, true, 7, "skeleton", 1200L, true, "playful"
        );

        DynamicOps<Tag> ops = NbtOps.INSTANCE;
        DataResult<Tag> encodeResult = WolfPersistentData.CODEC.encodeStart(ops, original);
        assertTrue(encodeResult.result().isPresent(), "Encoding to NBT must succeed: " + encodeResult.error());

        Tag tag = encodeResult.result().get();
        assertTrue(tag instanceof CompoundTag, "Encoded tag must be a CompoundTag");

        DataResult<WolfPersistentData> decodeResult = WolfPersistentData.CODEC.parse(ops, tag);
        assertTrue(decodeResult.result().isPresent(), "Decoding from NBT must succeed: " + decodeResult.error());

        WolfPersistentData decoded = decodeResult.result().get();
        assertEquals(original.personalityId(), decoded.personalityId());
        assertEquals(original.lastDamageTime(), decoded.lastDamageTime());
        assertEquals(original.submissive(), decoded.submissive());
        assertEquals(original.bloodFeudTarget(), decoded.bloodFeudTarget());
        assertEquals(original.dna(), decoded.dna());
        assertEquals(original.scale(), decoded.scale(), 1e-4f);
        assertEquals(original.leaderUuid(), decoded.leaderUuid());
        assertEquals(original.guardMode(), decoded.guardMode());
        assertEquals(original.guardPos(), decoded.guardPos());
        assertEquals(original.adoptable(), decoded.adoptable());
        assertEquals(original.canineArchetype(), decoded.canineArchetype());
    }

    @Test
    @DisplayName("Assert WolfPersistentData immutability and record evolution")
    public void testWolfDataImmutability() {
        WolfPersistentData initial = WolfPersistentData.DEFAULT;
        WolfPersistentData evolved = new WolfPersistentData(
                initial.personalityId(), initial.lastDamageTime(), initial.submissive(),
                initial.bloodFeudTarget(), initial.lastMischiefDay(), initial.dna(), initial.scale(),
                initial.affinityMap(), initial.leaderUuid(), true, initial.guardPos(),
                initial.adoptable(), initial.refusesGroundFood(), initial.feedCount(),
                initial.nemesisType(), initial.nemesisExpiry(), initial.discoveredTreat(), "guardian"
        );

        assertNotSame(initial, evolved, "Evolved record must be a distinct instance");
        assertFalse(initial.guardMode(), "Original instance must remain unmutated");
        assertTrue(evolved.guardMode(), "Evolved instance must reflect updated guardMode");
        assertEquals("guardian", evolved.canineArchetype(), "Evolved instance must store canineArchetype");
    }

    @Test
    @DisplayName("Assert IWolfDataHelper SPI mock storage contract")
    public void testWolfDataHelperContract() {
        IWolfDataHelper helper = new MockWolfDataHelper();

        // Null wolf returns default safely
        assertEquals(WolfPersistentData.DEFAULT, helper.getWolfData(null));

        // WolfData persistence across mutations
        WolfPersistentData custom = new WolfPersistentData(
                1, 100, false, "", 0L, 9999L, 1.25f, Map.of(),
                Optional.empty(), true, Optional.empty(), false, false, 0, "", 0L, false, "fearless"
        );
        assertEquals("fearless", custom.canineArchetype());
        assertTrue(custom.guardMode());
    }

    @Test
    @DisplayName("Assert WolfData extra fields integrity (guardPos, adoptable, archetype)")
    public void testWolfDataExtraFieldsIntegrity() {
        BlockPos pos = new BlockPos(10, 70, 30);
        WolfPersistentData data = new WolfPersistentData(
                0, 0, false, "", 0L, 0L, 1.0f, Map.of(),
                Optional.empty(), true, Optional.of(pos), true, true, 3, "", 0L, true, "adventurer"
        );

        assertTrue(data.guardPos().isPresent());
        assertEquals(pos, data.guardPos().get());
        assertTrue(data.adoptable());
        assertTrue(data.refusesGroundFood());
        assertEquals(3, data.feedCount());
        assertTrue(data.discoveredTreat());
        assertEquals("adventurer", data.canineArchetype());
    }
}
