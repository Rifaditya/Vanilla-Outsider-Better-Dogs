// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * High-performance, zero-allocation hazard detection engine for canine explosive reflex.
 * Identifies active Primed TNT, End Crystals, and swelling/ignited Creepers within threat radius.
 */
public final class HazardDetectionHelper {

    private HazardDetectionHelper() {
        // Utility class
    }

    /**
     * Categories of detonation hazards tracked by the canine explosive reflex system.
     */
    public enum HazardType {
        PRIMED_TNT,
        END_CRYSTAL,
        CREEPER
    }

    /**
     * Record representing a discovered detonation hazard and its relative spatial metrics.
     */
    public record DetonationHazard(
            Entity entity,
            Vec3 position,
            HazardType type,
            double distanceSq
    ) {
    }

    /**
     * Classifies an entity into a HazardType if it represents an active detonation hazard.
     * Returns null if the entity is null, inactive, dead, or not an explosive hazard.
     */
    public static HazardType classifyHazard(Entity entity) {
        if (entity == null || !entity.isAlive()) {
            return null;
        }

        if (entity instanceof PrimedTnt) {
            return HazardType.PRIMED_TNT;
        }

        if (entity instanceof EndCrystal) {
            return HazardType.END_CRYSTAL;
        }

        if (entity instanceof Creeper creeper) {
            if (creeper.isIgnited() || creeper.getSwellDir() > 0) {
                return HazardType.CREEPER;
            }
        }

        return null;
    }

    /**
     * Tests whether an entity is an active detonation hazard.
     */
    public static boolean isHazard(Entity entity) {
        return classifyHazard(entity) != null;
    }

    /**
     * Finds the nearest active detonation hazard within the specified radius of a wolf.
     * Returns null if no hazards are present or if arguments are invalid.
     */
    public static DetonationHazard findNearestHazard(Wolf wolf, double radius) {
        if (wolf == null || wolf.level() == null || !wolf.isAlive() || radius <= 0.0) {
            return null;
        }
        return findNearestHazard(wolf.level(), wolf.position(), radius);
    }

    /**
     * Finds the nearest active detonation hazard within the specified radius of an origin point in a level.
     */
    public static DetonationHazard findNearestHazard(Level level, Vec3 origin, double radius) {
        if (level == null || origin == null || radius <= 0.0) {
            return null;
        }

        AABB box = new AABB(
                origin.x - radius, origin.y - radius, origin.z - radius,
                origin.x + radius, origin.y + radius, origin.z + radius
        );

        List<Entity> candidates = level.getEntities((Entity) null, box, HazardDetectionHelper::isHazard);
        return findNearestHazard(candidates, origin, radius);
    }

    /**
     * Pure selection helper finding the nearest active detonation hazard from a list of candidate entities.
     * Guarantees 0 GC allocations if candidates list is empty or contains no hazards within radius.
     */
    public static DetonationHazard findNearestHazard(List<? extends Entity> candidates, Vec3 origin, double radius) {
        if (candidates == null || candidates.isEmpty() || origin == null || radius <= 0.0) {
            return null;
        }

        double maxDistSq = radius * radius;
        double minDistanceSq = maxDistSq;
        Entity nearestEntity = null;
        HazardType nearestType = null;
        Vec3 nearestPos = null;

        for (int i = 0; i < candidates.size(); i++) {
            Entity candidate = candidates.get(i);
            if (candidate == null || !candidate.isAlive()) {
                continue;
            }

            HazardType type = classifyHazard(candidate);
            if (type == null) {
                continue;
            }

            Vec3 pos = candidate.position();
            double distSq = origin.distanceToSqr(pos);
            if (distSq <= minDistanceSq) {
                minDistanceSq = distSq;
                nearestEntity = candidate;
                nearestType = type;
                nearestPos = pos;
            }
        }

        if (nearestEntity != null) {
            return new DetonationHazard(nearestEntity, nearestPos, nearestType, minDistanceSq);
        }

        return null;
    }
}
