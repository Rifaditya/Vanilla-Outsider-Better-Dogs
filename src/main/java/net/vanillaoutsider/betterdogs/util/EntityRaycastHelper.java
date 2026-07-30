// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Entity.java (26.2+)
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class EntityRaycastHelper {

    /**
     * Performs a cone-inflated raycast along the player's view vector up to maxDistance blocks.
     * Returns the closest valid target LivingEntity or null if no candidate is intersected.
     */
    public static LivingEntity findCrosshairTarget(ServerPlayer player, double maxDistance) {
        if (player == null || !player.isAlive()) {
            return null;
        }

        Vec3 eyePos = player.getEyePosition(1.0f);
        Vec3 viewVec = player.getViewVector(1.0f);
        Vec3 endPos = eyePos.add(viewVec.x * maxDistance, viewVec.y * maxDistance, viewVec.z * maxDistance);

        AABB rayBox = new AABB(eyePos, endPos).inflate(1.5);
        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, rayBox, e -> isValidTargetCandidate(player, e));

        LivingEntity closest = null;
        double minSqDistance = maxDistance * maxDistance;

        for (LivingEntity entity : entities) {
            AABB boundingBox = entity.getBoundingBox().inflate(0.5);
            Optional<Vec3> hit = boundingBox.clip(eyePos, endPos);
            if (hit.isPresent()) {
                double sqDist = eyePos.distanceToSqr(hit.get());
                if (sqDist < minSqDistance) {
                    minSqDistance = sqDist;
                    closest = entity;
                }
            }
        }

        return closest;
    }

    private static boolean isValidTargetCandidate(ServerPlayer owner, LivingEntity candidate) {
        if (candidate == null || !candidate.isAlive() || candidate == owner) {
            return false;
        }
        if (candidate instanceof ArmorStand) {
            return false;
        }
        if (candidate instanceof Wolf wolf && wolf.isOwnedBy(owner)) {
            return false; // Don't target own pack dogs
        }
        return true;
    }
}
