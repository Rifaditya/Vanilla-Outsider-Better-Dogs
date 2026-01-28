package net.vanillaoutsider.social.core;

import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Shared Brain: A static registry where all species and entities "become one".
 * Optimized for Zero-Overhead selection on 2010-era hardware.
 */
public class SocialRegistry {
    // Species templates (behavior definitions)
    private static final Map<String, Object> SPECIES_TEMPLATES = new ConcurrentHashMap<>();
    
    // O(1) Selection Pool: Zero-allocation random selection
    private static final List<WeakReference<LivingEntity>> ENTITY_POOL = new CopyOnWriteArrayList<>();
    private static final Random RANDOM = new Random();

    /**
     * Registers a mob into the global tracking system.
     */
    public static void registerEntity(SocialEntity socialEntity) {
        LivingEntity entity = socialEntity.betterdogs$asEntity();
        if (!containsEntity(entity)) {
            ENTITY_POOL.add(new WeakReference<>(entity));
        }
    }

    /**
     * Removes a mob from tracking.
     */
    public static void unregisterEntity(LivingEntity entity) {
        ENTITY_POOL.removeIf(ref -> {
            LivingEntity referent = ref.get();
            return referent == null || referent == entity;
        });
    }

    /**
     * Checks if an entity is already registered.
     */
    public static boolean containsEntity(LivingEntity entity) {
        for (WeakReference<LivingEntity> ref : ENTITY_POOL) {
            if (ref.get() == entity) return true;
        }
        return false;
    }

    /**
     * O(1) Selection logic. Returns a random living entity or null if pool is empty.
     * Also performs a "Lazy Clean" if it hits a dead reference.
     */
    @Nullable
    public static LivingEntity getRandomEntity() {
        if (ENTITY_POOL.isEmpty()) return null;
        
        int index = RANDOM.nextInt(ENTITY_POOL.size());
        WeakReference<LivingEntity> ref = ENTITY_POOL.get(index);
        LivingEntity entity = ref.get();
        
        if (entity == null) {
            // Lazy clean: Remove the dead reference
            ENTITY_POOL.remove(index);
            return null;
        }
        
        return entity;
    }

    /**
     * Active Scrubbing: Purges all dead references from the pool.
     * Called periodically by the GlobalSocialSystem.
     */
    public static void scrub() {
        int before = ENTITY_POOL.size();
        ENTITY_POOL.removeIf(ref -> ref.get() == null);
        int after = ENTITY_POOL.size();
        if (before != after) {
            // Silent cleanup for performance, but we could log here for debug
        }
    }

    /**
     * Legacy support/External access (Use getRandomEntity where possible)
     */
    public static List<LivingEntity> getTrackedEntities() {
        List<LivingEntity> list = new ArrayList<>();
        for (WeakReference<LivingEntity> ref : ENTITY_POOL) {
            LivingEntity e = ref.get();
            if (e != null) list.add(e);
        }
        return list;
    }

    public static void registerSpecies(String speciesId, Object template) {
        SPECIES_TEMPLATES.put(speciesId, template);
    }

    public static Object getTemplate(String speciesId) {
        return SPECIES_TEMPLATES.get(speciesId);
    }
}
