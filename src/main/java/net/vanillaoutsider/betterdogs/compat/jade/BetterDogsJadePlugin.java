// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.compat.jade;

import net.minecraft.world.entity.animal.wolf.Wolf;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class BetterDogsJadePlugin implements IWailaPlugin {

    public static final String PLUGIN_ID = "betterdogs:jade_plugin";

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // Register custom Wolf info provider
        registration.registerEntityComponent(WolfInfoProvider.INSTANCE, Wolf.class);
        // Register custom Wolf health provider
        registration.registerEntityComponent(WolfHealthProvider.INSTANCE, Wolf.class);
        // Register custom Inbred status provider
        registration.registerEntityComponent(InbredStatusProvider.INSTANCE, Wolf.class);
    }

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(InbredStatusProvider.INSTANCE, Wolf.class);
        registration.registerEntityDataProvider(WolfInfoProvider.INSTANCE, Wolf.class);
    }
}
