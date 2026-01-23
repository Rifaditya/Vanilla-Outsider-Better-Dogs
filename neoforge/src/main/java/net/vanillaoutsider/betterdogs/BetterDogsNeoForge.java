package net.vanillaoutsider.betterdogs;

import net.neoforged.fml.common.Mod;
import net.vanillaoutsider.betterdogs.platform.NeoForgePlatformServices;
import net.vanillaoutsider.betterdogs.platform.Services;

@Mod("better_dogs")
public class BetterDogsNeoForge {
    
    public BetterDogsNeoForge() {
        // Register NeoForge platform services
        Services.setPlatform(new NeoForgePlatformServices());
        
        // Bootstrap Common Logic
        BetterDogs.init();
    }
}
