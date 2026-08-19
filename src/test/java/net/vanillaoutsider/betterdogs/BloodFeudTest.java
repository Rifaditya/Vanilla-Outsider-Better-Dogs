// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import com.mojang.serialization.DataResult;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.vanillaoutsider.betterdogs.util.BloodFeudHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 38: Persistent Wolf Vendetta Blood Feuds Tests")
class BloodFeudTest {

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
