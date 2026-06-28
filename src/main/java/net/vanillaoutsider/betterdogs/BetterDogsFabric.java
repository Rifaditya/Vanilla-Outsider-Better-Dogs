// SPDX-License-Identifier: GPL-3.0-or-later
// Mod Entrypoint (Fabric)
package net.vanillaoutsider.betterdogs;

import net.fabricmc.api.ModInitializer;

public class BetterDogsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        BetterDogs.init();
    }
}
