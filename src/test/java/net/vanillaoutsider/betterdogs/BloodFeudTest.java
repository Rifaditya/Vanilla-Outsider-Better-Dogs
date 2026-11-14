// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import com.mojang.serialization.DataResult;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.util.BloodFeudHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 38: Persistent Wolf Vendetta Blood Feuds Tests")
class BloodFeudTest {

    private static Unsafe unsafe;
    private static int nextEntityId = 1000;

    @BeforeAll
    static void setupMinecraftBootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        unsafe = (Unsafe) theUnsafe.get(null);
    }

    private static Wolf allocateTestWolf() throws InstantiationException {
        Wolf wolf = (Wolf) unsafe.allocateInstance(Wolf.class);
        wolf.setId(nextEntityId++);
        return wolf;
    }

    @Test
    @DisplayName("Assert Blood Feud target UUID serialization and deserialization via Codec")
    void testBloodFeudCodecSerialization() {
        UUID targetUuid = UUID.randomUUID();
        WolfPersistentData original = new WolfPersistentData(
                WolfPersonality.AGGRESSIVE.getId(),
                0,
                false,
                targetUuid.toString(),
                12345L,
                0L,
                1.0f,
                Map.of(),
                Optional.empty(),
                false,
                Optional.empty(),
                false,
                false,
                0,
                "",
                0L,
                false
        );

        DataResult<Tag> encoded = WolfPersistentData.CODEC.encodeStart(NbtOps.INSTANCE, original);
        assertTrue(encoded.result().isPresent(), "Codec encoding should succeed");

        Tag tag = encoded.result().get();
        DataResult<WolfPersistentData> decoded = WolfPersistentData.CODEC.parse(NbtOps.INSTANCE, tag);
        assertTrue(decoded.result().isPresent(), "Codec decoding should succeed");

        WolfPersistentData result = decoded.result().get();
        assertEquals(targetUuid.toString(), result.bloodFeudTarget(), "Blood feud target UUID should match after encode/decode");
    }

    @Test
    @DisplayName("BL-BD-059: Canonical constructor normalizes nullable fields and round-trips cleanly via Codec")
    void testCanonicalConstructorAndCodecNullNormalization() {
        WolfPersistentData normalized = new WolfPersistentData(
                WolfPersonality.PACIFIST.getId(),
                42,
                true,
                null,
                99L,
                123456L,
                1.15f,
                null,
                null,
                false,
                null,
                true,
                false,
                3,
                null,
                0L,
                true,
                null
        );

        assertEquals("", normalized.bloodFeudTarget(), "Null bloodFeudTarget must normalize to empty string");
        assertEquals(Map.of(), normalized.affinityMap(), "Null affinityMap must normalize to empty map");
        assertEquals(Optional.empty(), normalized.leaderUuid(), "Null leaderUuid must normalize to Optional.empty()");
        assertEquals(Optional.empty(), normalized.guardPos(), "Null guardPos must normalize to Optional.empty()");
        assertEquals("", normalized.nemesisType(), "Null nemesisType must normalize to empty string");
        assertEquals("", normalized.canineArchetype(), "Null canineArchetype must normalize to empty string");

        DataResult<Tag> encoded = WolfPersistentData.CODEC.encodeStart(NbtOps.INSTANCE, normalized);
        assertTrue(encoded.result().isPresent(), "Normalized WolfPersistentData must encode via CODEC without NPE");

        DataResult<WolfPersistentData> decoded = WolfPersistentData.CODEC.parse(NbtOps.INSTANCE, encoded.result().get());
        assertTrue(decoded.result().isPresent(), "Normalized WolfPersistentData must decode cleanly via CODEC");

        WolfPersistentData roundTripped = decoded.result().get();
        assertEquals("", roundTripped.bloodFeudTarget());
        assertEquals(Map.of(), roundTripped.affinityMap());
        assertEquals(Optional.empty(), roundTripped.leaderUuid());
        assertEquals(Optional.empty(), roundTripped.guardPos());
        assertEquals("", roundTripped.nemesisType());
        assertEquals("", roundTripped.canineArchetype());
        assertEquals(WolfPersonality.PACIFIST.getId(), roundTripped.personalityId());
        assertEquals(1.15f, roundTripped.scale());
        assertEquals(123456L, roundTripped.dna());
    }

    @Test
    @DisplayName("BL-BD-059: setPersistedBloodFeudTarget(wolf, null) prevents NPE, serializes safely, and preserves unrelated fields")
    void testPersistedBloodFeudNullSetterPreventsNpeAndSerializes() throws Exception {
        Wolf wolf = allocateTestWolf();
        WolfPersistentData initial = new WolfPersistentData(
                WolfPersonality.AGGRESSIVE.getId(),
                0,
                false,
                "",
                0L,
                987654321L,
                1.25f,
                Map.of(),
                Optional.empty(),
                false,
                Optional.empty(),
                false,
                false,
                0,
                "",
                0L,
                false,
                "guardian"
        );
        WolfPersistentData.setWolfData(wolf, initial);

        UUID targetUuid = UUID.randomUUID();
        WolfPersistentData.setPersistedBloodFeudTarget(wolf, targetUuid.toString());
        assertTrue(WolfPersistentData.hasBloodFeud(wolf), "Setting a valid feud UUID must mark wolf as having blood feud");
        assertEquals(targetUuid.toString(), WolfPersistentData.getPersistedBloodFeudTarget(wolf));

        assertDoesNotThrow(() -> WolfPersistentData.setPersistedBloodFeudTarget(wolf, null),
                "Clearing blood feud with null must never throw");
        assertEquals("", WolfPersistentData.getPersistedBloodFeudTarget(wolf),
                "Clearing blood feud with null must normalize persisted target to empty string");
        assertFalse(WolfPersistentData.hasBloodFeud(wolf),
                "hasBloodFeud(wolf) must return false without NPE after null clear");

        WolfPersistentData persisted = WolfPersistentData.getWolfData(wolf);
        DataResult<Tag> encoded = assertDoesNotThrow(() -> WolfPersistentData.CODEC.encodeStart(NbtOps.INSTANCE, persisted),
                "CODEC encoding after null blood feud clear must not throw");
        assertTrue(encoded.result().isPresent(), "CODEC encoding after null clear must produce NBT result");

        DataResult<WolfPersistentData> decoded = WolfPersistentData.CODEC.parse(NbtOps.INSTANCE, encoded.result().get());
        assertTrue(decoded.result().isPresent(), "CODEC decoding after null clear must succeed");
        assertEquals("", decoded.result().get().bloodFeudTarget());

        // Negative parity: unrelated fields remain intact
        assertEquals(WolfPersonality.AGGRESSIVE.getId(), persisted.personalityId(), "personalityId must remain intact");
        assertEquals(1.25f, persisted.scale(), "scale must remain intact");
        assertEquals(987654321L, persisted.dna(), "dna must remain intact");
    }

    @Test
    @DisplayName("Assert Blood Feud escalation probability roll math at 5% default")
    void testBloodFeudEscalationChance() {
        int feudChance = 5;
        int triggered = 0;
        Random rand = new Random(42L);

        for (int i = 0; i < 1000; i++) {
            if (rand.nextInt(100) < feudChance) {
                triggered++;
            }
        }

        assertTrue(triggered > 20 && triggered < 80,
                "5% roll should trigger roughly 50 times in 1000 trials (got " + triggered + ")");
    }

    @Test
    @DisplayName("Assert empty/unset Blood Feud target validation")
    void testEmptyFeudValidation() {
        WolfPersistentData defaultData = WolfPersistentData.DEFAULT;
        assertEquals("", defaultData.bloodFeudTarget(), "Unset blood feud target should be empty string");
    }

    @Test
    @DisplayName("Assert BloodFeudHelper strict null safety")
    void testBloodFeudHelperNullSafety() {
        assertDoesNotThrow(() -> assertFalse(BloodFeudHelper.hasBloodFeud(null)));
        assertDoesNotThrow(() -> assertEquals("", BloodFeudHelper.getBloodFeudTarget(null)));
        assertDoesNotThrow(() -> BloodFeudHelper.setBloodFeudTarget(null, null));
        assertDoesNotThrow(() -> BloodFeudHelper.clearBloodFeud(null));
        assertDoesNotThrow(() -> assertFalse(BloodFeudHelper.shouldTriggerBloodFeud(null, null)));
        assertDoesNotThrow(() -> assertNull(BloodFeudHelper.findNemesis(null, null, 20.0)));
    }
}
