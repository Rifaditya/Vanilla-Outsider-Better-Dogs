// Verified against: IndyPetsCompatibility.java (26.1.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.util;

import java.lang.reflect.Method;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.Entity;

/**
 * Handles soft compatibility with IndyPets mod.
 * Uses reflection to avoid hard dependency at runtime.
 */
public class IndyPetsCompatibility {

    private static final boolean INDY_PETS_LOADED = FabricLoader.getInstance().isModLoaded("indypets");
    private static Method isActiveIndependentMethod;

    static {
        if (INDY_PETS_LOADED) {
            try {
                Class<?> utilClass = Class.forName("com.lizin5ths.indypets.util.IndyPetsUtil");
                isActiveIndependentMethod = utilClass.getMethod("isActiveIndependent", Entity.class);
            } catch (Exception e) {
                // BetterDogs.LOGGER.error("Failed to initialize IndyPets compatibility", e);
            }
        }
    }

    /**
     * Checks if a pet is marked as independent by IndyPets.
     * @param entity The pet entity (usually a Wolf).
     * @return true if independent, false otherwise (or if IndyPets is missing).
     */
    public static boolean isIndependent(Entity entity) {
        if (!INDY_PETS_LOADED || isActiveIndependentMethod == null) {
            return false;
        }
        try {
            return (boolean) isActiveIndependentMethod.invoke(null, entity);
        } catch (Exception e) {
            return false;
        }
    }
}
