// Verified against: BetterDogsFabric.java (26.1.2+)
package net.vanillaoutsider.betterdogs;

import net.fabricmc.api.ModInitializer;

public class BetterDogsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        BetterDogs.init();
    }
}
