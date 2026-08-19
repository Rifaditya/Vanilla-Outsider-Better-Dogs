// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import java.util.UUID;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;
import net.vanillaoutsider.betterdogs.util.DogSeatHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 30: Vehicle Navigation & Auto-Boarding Tests")
class VehicleBoardingTest {

    @Test
    @DisplayName("Assert distance constants match design specifications")
    void testDistanceConstants() {
        assertEquals(144.0D, DogSeatHelper.MAX_COMMAND_DISTANCE_SQ, 0.001D, "Max command range must be 12 blocks (144 distance squared)");
        assertEquals(2.25D, DogSeatHelper.MOUNT_DISTANCE_SQ, 0.001D, "Mount distance must be 1.5 blocks (2.25 distance squared)");
        assertEquals(0.8D, DogSeatHelper.DEFAULT_DISMOUNT_DISTANCE, 0.001D, "Default dismount offset must be 0.8 blocks");
    }

    @Test
    @DisplayName("Assert lateral dismount vector calculation pushes outward with zero vertical displacement")
    void testDismountVectorCalculation() {
        Vec3 lookX = new Vec3(1.0D, 0.5D, 0.0D);
        Vec3 offset = DogSeatHelper.calculateDismountOffset(lookX, 0.8D);

        assertEquals(-0.8D, offset.x, 0.001D, "X offset should be opposite to player look angle scaled by distance");
        assertEquals(0.0D, offset.y, 0.001D, "Y offset must strictly remain 0 to prevent vertical flight or suffocation");
        assertEquals(0.0D, offset.z, 0.001D, "Z offset should remain 0");

        Vec3 lookZ = new Vec3(0.0D, -0.9D, 1.0D);
        Vec3 offsetZ = DogSeatHelper.calculateDismountOffset(lookZ, 0.8D);
        assertEquals(0.0D, offsetZ.x, 0.001D);
        assertEquals(0.0D, offsetZ.y, 0.001D);
        assertEquals(-0.8D, offsetZ.z, 0.001D);
    }

    @Test
    @DisplayName("Assert command manager selection and target tracking")
    void testSelectionTracking() {
        UUID playerUuid = UUID.randomUUID();
        UUID dogUuid = UUID.randomUUID();

        DogCommandManager.selectDog(playerUuid, dogUuid);
        assertEquals(dogUuid, DogCommandManager.getSelectedDog(playerUuid));

        DogCommandManager.clearSelection(playerUuid);
        assertNull(DogCommandManager.getSelectedDog(playerUuid));

        DogCommandManager.clearVehicleTarget(dogUuid);
        assertNull(DogCommandManager.getVehicleTarget(dogUuid));
    }

    @Test
    @DisplayName("Assert strict null safety across seat and command helper methods")
    void testNullSafety() {
        assertFalse(DogSeatHelper.isSitTarget(null));
        assertFalse(DogSeatHelper.hasPassengerSpace(null));
        assertFalse(DogSeatHelper.isCommandItem(null));
        assertFalse(DogSeatHelper.isChairBlock(null));
        assertEquals(Vec3.ZERO, DogSeatHelper.calculateDismountOffset(null, 0.8D));
        assertDoesNotThrow(() -> DogCommandManager.clearSelection(null));
        assertDoesNotThrow(() -> DogCommandManager.clearVehicleTarget(null));
    }
}
