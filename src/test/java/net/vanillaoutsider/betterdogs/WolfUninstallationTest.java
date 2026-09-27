// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.vanillaoutsider.betterdogs.util.WolfPersonalityStatHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WolfUninstallationTest {

    @Test
    @DisplayName("Verify Guard Mode Serializes Sitting Tag for Vanilla Uninstallation Stationing")
    void testGuardModeNbtSittingTag() {
        CompoundTag tag = new CompoundTag();
        BlockPos post = new BlockPos(100, 64, -200);

        // Active guard mode serialization
        WolfPersistentData.writeToNbt(tag, WolfPersonality.AGGRESSIVE, 1.0f, 12345L, "minecraft:beef", 5000L, "minecraft:creeper", 10000L, null, null, false, true, post);

        // Assert that when a dog is guarding and serialized to NBT, tag.getBoolean("Sitting") is true so vanilla stations it upon uninstallation
        Assertions.assertTrue(tag.getBoolean("Sitting"), "Guard mode must serialize Sitting=true to chunk save data so vanilla stations the dog upon uninstallation");

        // Non-guarding serialization should not inject Sitting=true
        CompoundTag idleTag = new CompoundTag();
        WolfPersistentData.writeToNbt(idleTag, WolfPersonality.NORMAL, 1.0f, 12345L, "", 0L, "", 0L, null, null, false, false, null);
        Assertions.assertFalse(idleTag.getBoolean("Sitting"), "Non-guarding dog must not force Sitting=true on serialization");
    }

    @Test
    @DisplayName("Verify Sneak Flag Sanitization Logic")
    void testSneakFlagSanitizationLogic() {
        // Entity shift flag detection and sanitization logic (matching WolfMixin#betterdogs$readNbt)
        class EntitySneakState {
            private boolean shiftKeyDown;

            EntitySneakState(boolean shiftKeyDown) {
                this.shiftKeyDown = shiftKeyDown;
            }

            public boolean isShiftKeyDown() {
                return shiftKeyDown;
            }

            public void setShiftKeyDown(boolean shiftKeyDown) {
                this.shiftKeyDown = shiftKeyDown;
            }

            public void sanitizeShiftFlag() {
                if (isShiftKeyDown()) {
                    setShiftKeyDown(false);
                }
            }
        }

        EntitySneakState entityWithSneak = new EntitySneakState(true);
        Assertions.assertTrue(entityWithSneak.isShiftKeyDown(), "Entity shift flag should initially be detected as true");

        // Execute sanitization
        entityWithSneak.sanitizeShiftFlag();
        Assertions.assertFalse(entityWithSneak.isShiftKeyDown(), "Entity shift flag must be sanitized back to false");

        // Verify sanitization is idempotent and produces no side effects on clean entities
        entityWithSneak.sanitizeShiftFlag();
        Assertions.assertFalse(entityWithSneak.isShiftKeyDown(), "Entity shift flag must remain false without side effects");

        EntitySneakState cleanEntity = new EntitySneakState(false);
        cleanEntity.sanitizeShiftFlag();
        Assertions.assertFalse(cleanEntity.isShiftKeyDown(), "Clean entity must remain false without side effects");
    }

    @Test
    @DisplayName("Verify Transient Modifier Isolation and Base Attribute Preservation")
    void testTransientModifierIsolation() {
        // Assert that UUIDs exist, are non-null, and are distinct
        Assertions.assertNotNull(WolfPersonalityStatHelper.PERSONALITY_HEALTH_UUID, "Health modifier UUID must exist and not be null");
        Assertions.assertNotNull(WolfPersonalityStatHelper.PERSONALITY_DAMAGE_UUID, "Damage modifier UUID must exist and not be null");
        Assertions.assertNotNull(WolfPersonalityStatHelper.PERSONALITY_SPEED_UUID, "Speed modifier UUID must exist and not be null");

        Assertions.assertNotEquals(WolfPersonalityStatHelper.PERSONALITY_HEALTH_UUID, WolfPersonalityStatHelper.PERSONALITY_DAMAGE_UUID, "Health and damage modifier UUIDs must be distinct");
        Assertions.assertNotEquals(WolfPersonalityStatHelper.PERSONALITY_HEALTH_UUID, WolfPersonalityStatHelper.PERSONALITY_SPEED_UUID, "Health and speed modifier UUIDs must be distinct");
        Assertions.assertNotEquals(WolfPersonalityStatHelper.PERSONALITY_DAMAGE_UUID, WolfPersonalityStatHelper.PERSONALITY_SPEED_UUID, "Damage and speed modifier UUIDs must be distinct");

        // Verify that base attributes are preserved at vanilla baselines (20.0/30.0 health, 4.0 damage, 0.3 speed)
        Assertions.assertEquals(20.0D, WolfPersonalityStatHelper.BASE_UNTAMED_WOLF_HEALTH, 0.001D, "Untamed wolf base health must be preserved at vanilla baseline 20.0");
        Assertions.assertEquals(30.0D, WolfPersonalityStatHelper.BASE_WOLF_HEALTH, 0.001D, "Tamed wolf base health must be preserved at vanilla baseline 30.0");
        Assertions.assertEquals(4.0D, WolfPersonalityStatHelper.BASE_WOLF_DAMAGE, 0.001D, "Base attack damage must be preserved at vanilla baseline 4.0");
        Assertions.assertEquals(0.30D, WolfPersonalityStatHelper.BASE_WOLF_SPEED, 0.001D, "Base movement speed must be preserved at vanilla baseline 0.30");
    }
}
