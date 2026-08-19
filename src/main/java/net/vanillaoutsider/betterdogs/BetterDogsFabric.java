// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.fabricmc.api.ModInitializer;

public class BetterDogsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        net.vanillaoutsider.betterdogs.util.ModVersionGuard.checkClass("Better Dogs", "net.minecraft.world.entity.animal.wolf.Wolf");
        BetterDogs.init();
    }
}
