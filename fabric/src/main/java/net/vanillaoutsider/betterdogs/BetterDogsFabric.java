package net.vanillaoutsider.betterdogs;

import net.fabricmc.api.ModInitializer;
import net.vanillaoutsider.betterdogs.platform.FabricPlatformServices;
import net.vanillaoutsider.betterdogs.platform.Services;

public class BetterDogsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        // Register Fabric platform services
        Services.setPlatform(new FabricPlatformServices());
        
        // Bootstrap Common Logic
        BetterDogs.init();
    }
}
