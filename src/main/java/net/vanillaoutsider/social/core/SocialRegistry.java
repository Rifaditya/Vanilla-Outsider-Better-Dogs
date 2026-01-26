package net.vanillaoutsider.social.core;

import net.minecraft.world.entity.LivingEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Shared Brain: A static registry where all species and entities "become one".
 */
public class SocialRegistry {
    // Species templates (behavior definitions)
    private static final Map<String, Object> SPECIES_TEMPLATES = new ConcurrentHashMap<>();
    
    // Global entity tracking using WeakReferences to prevent memory leaks
    private static final Map<LivingEntity, Boolean> ACTIVE_ENTITIES = new WeakHashMap<>();

    /**
     * Registers a mob into the global tracking system.
     */
    public static void registerEntity(SocialEntity socialEntity) {
        ACTIVE_ENTITIES.put(socialEntity.betterdogs$asEntity(), Boolean.TRUE);
    }

    /**
     * Removes a mob from tracking (though WeakHashMap handles this automatically on unload).
     */
    public static void unregisterEntity(LivingEntity entity) {
        ACTIVE_ENTITIES.remove(entity);
    }

    /**
     * Checks if an entity is already registered.
     */
    public static boolean containsEntity(LivingEntity entity) {
        return ACTIVE_ENTITIES.containsKey(entity);
    }

    /**
     * @return A list of all currently tracked entities for random selection.
     */
    public static List<LivingEntity> getTrackedEntities() {
        return new ArrayList<>(ACTIVE_ENTITIES.keySet());
    }

    public static void registerSpecies(String speciesId, Object template) {
        SPECIES_TEMPLATES.put(speciesId, template);
    }

    public static Object getTemplate(String speciesId) {
        return SPECIES_TEMPLATES.get(speciesId);
    }
}
