package net.vanillaoutsider.betterdogs;

import net.fabricmc.api.ModInitializer;

public class BetterDogsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        // Bootstrap Common Logic
        BetterDogs.init();
    }
}
