// SPDX-License-Identifier: GPL-3.0-or-later
// Mod Entrypoint (Fabric)
package net.vanillaoutsider.betterdogs;

import net.fabricmc.api.ModInitializer;

public class BetterDogsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        net.vanillaoutsider.betterdogs.util.ModVersionGuard.checkClass("Better Dogs", "net.minecraft.world.entity.EntityType");
        BetterDogs.init();
    }
}
