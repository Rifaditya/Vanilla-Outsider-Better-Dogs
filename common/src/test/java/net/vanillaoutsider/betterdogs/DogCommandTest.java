// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DogCommandTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testCommandItemValidation() {
        Assertions.assertTrue(DogCommandManager.isCommandItem(new ItemStack(Items.STICK)));
        Assertions.assertTrue(DogCommandManager.isCommandItem(new ItemStack(Items.BLAZE_ROD)));
        Assertions.assertTrue(DogCommandManager.isCommandItem(new ItemStack(Items.BREEZE_ROD)));
        Assertions.assertFalse(DogCommandManager.isCommandItem(new ItemStack(Items.BONE)), "Bone should be excluded from selection to allow guard mode");
        Assertions.assertFalse(DogCommandManager.isCommandItem(ItemStack.EMPTY));
    }

    @Test
    void testSelectionAndVehicleTargetMaps() {
        UUID playerUuid = UUID.randomUUID();
        UUID dogUuid = UUID.randomUUID();

        DogCommandManager.selectDog(playerUuid, dogUuid);
        Assertions.assertEquals(dogUuid, DogCommandManager.getSelectedDog(playerUuid));

        DogCommandManager.clearSelection(playerUuid);
        Assertions.assertNull(DogCommandManager.getSelectedDog(playerUuid));
    }
}
