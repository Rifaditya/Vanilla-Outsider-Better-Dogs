package net.vanillaoutsider.betterdogs.util

import net.minecraft.world.entity.Entity
import net.fabricmc.loader.api.FabricLoader
import java.lang.reflect.Method

/**
 * Handles soft compatibility with IndyPets mod.
 * Uses reflection to avoid hard dependency at runtime.
 */
object IndyPetsCompatibility {

    private val INDY_PETS_LOADED = FabricLoader.getInstance().isModLoaded("indypets")
    private var isActiveIndependentMethod: Method? = null

    init {
        if (INDY_PETS_LOADED) {
            try {
                val utilClass = Class.forName("com.lizin5ths.indypets.util.IndyPetsUtil")
                isActiveIndependentMethod = utilClass.getMethod("isActiveIndependent", Entity::class.java)
            } catch (e: Exception) {
                // Silently fail or log
            }
        }
    }

    /**
     * Checks if a pet is marked as independent by IndyPets.
     * @param entity The pet entity (usually a Wolf).
     * @return true if independent, false otherwise (or if IndyPets is missing).
     */
    @JvmStatic
    fun isIndependent(entity: Entity): Boolean {
        if (!INDY_PETS_LOADED || isActiveIndependentMethod == null) {
            return false
        }
        return try {
            isActiveIndependentMethod?.invoke(null, entity) as? Boolean ?: false
        } catch (e: Exception) {
            false
        }
    }
}
