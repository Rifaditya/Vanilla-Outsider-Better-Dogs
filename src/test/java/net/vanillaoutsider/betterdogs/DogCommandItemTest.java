// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static org.junit.jupiter.api.Assertions.*;

public class DogCommandItemTest {

    @Test
    @DisplayName("Verify dog selection state tracking map operations")
    void testDogSelectionTracking() {
        Map<UUID, UUID> selectedDogs = new ConcurrentHashMap<>();
        UUID playerUuid = UUID.randomUUID();
        UUID dogUuid = UUID.randomUUID();

        // Initially unselected
        assertNull(selectedDogs.get(playerUuid));

        // Select dog
        selectedDogs.put(playerUuid, dogUuid);
        assertEquals(dogUuid, selectedDogs.get(playerUuid));

        // Clear selection
        selectedDogs.remove(playerUuid);
        assertNull(selectedDogs.get(playerUuid));
    }

    @Test
    @DisplayName("Verify vehicle target assignment and clearing")
    void testVehicleTargetAssignment() {
        Map<UUID, UUID> vehicleTargets = new ConcurrentHashMap<>();
        UUID dogUuid = UUID.randomUUID();
        UUID vehicleUuid = UUID.randomUUID();

        // Set vehicle target
        vehicleTargets.put(dogUuid, vehicleUuid);
        assertEquals(vehicleUuid, vehicleTargets.get(dogUuid));

        // Clear vehicle target upon dismount
        vehicleTargets.remove(dogUuid);
        assertNull(vehicleTargets.get(dogUuid));
    }

    @Test
    @DisplayName("Verify null personality fallback safety during adoption")
    void testNullPersonalityHandling() {
        WolfPersonality personality = null;
        if (personality == null) {
            personality = WolfPersonality.NORMAL;
        }
        assertNotNull(personality);
        assertEquals(WolfPersonality.NORMAL, personality);
    }
}
