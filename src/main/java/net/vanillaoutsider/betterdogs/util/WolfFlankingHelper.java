// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfPersistentData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Single-purpose helper managing tactical pack flanking calculations,
 * approach-time sorting, and collision-aware destination vectors.
 */
public final class WolfFlankingHelper {

    private WolfFlankingHelper() {
    }

    /**
     * Calculates the estimated approach time (distance / movement speed) for a wolf to reach a target.
     */
    public static double calculateApproachTime(Wolf wolf, LivingEntity target) {
        if (wolf == null || target == null) {
            return Double.MAX_VALUE;
        }
        double speed = Math.max(wolf.getAttributeValue(Attributes.MOVEMENT_SPEED), 0.01);
        return wolf.distanceTo(target) / speed;
    }

    /**
     * Pure math helper for approach time calculations without requiring live entity instances.
     */
    public static double calculateApproachTimeMath(double distance, double speed) {
        if (distance < 0.0 || speed <= 0.0) {
            return 0.0;
        }
        return distance / speed;
    }

    /**
     * Pure math helper to determine if a wolf at a given sorted index belongs to the slower flanking half.
     */
    public static boolean isSlowerHalf(int index, int totalSize) {
        if (totalSize <= 1 || index < 0 || index >= totalSize) {
            return false;
        }
        int flankCount = totalSize / 2;
        return index >= totalSize - flankCount;
    }

    /**
     * Determines whether the given wolf should execute a flanking maneuver based on its
     * position and approach time relative to other active pack members engaging the same target.
     */
    public static boolean isFlanker(Wolf wolf, LivingEntity target) {
        if (wolf == null || target == null) {
            return false;
        }

        if (wolf.isTame()) {
            LivingEntity owner = wolf.getOwner();
            if (owner != null) {
                List<Wolf> activePack = wolf.level().getEntitiesOfClass(
                        Wolf.class,
                        wolf.getBoundingBox().inflate(32.0),
                        w -> w.isTame() && w.getOwner() == owner && w.getTarget() == target && !w.isOrderedToSit()
                );

                if (activePack.size() > 1) {
                    activePack.sort((w1, w2) -> {
                        double t1 = calculateApproachTime(w1, target);
                        double t2 = calculateApproachTime(w2, target);
                        if (t1 != t2) {
                            return Double.compare(t1, t2);
                        }
                        return Integer.compare(w1.getId(), w2.getId());
                    });

                    int myIndex = activePack.indexOf(wolf);
                    return isSlowerHalf(myIndex, activePack.size());
                }
            }
        } else {
            Optional<UUID> leaderUuid = WolfPersistentData.getWolfData(wolf).leaderUuid();
            if (leaderUuid.isPresent()) {
                List<Wolf> activePack = wolf.level().getEntitiesOfClass(
                        Wolf.class,
                        wolf.getBoundingBox().inflate(32.0),
                        w -> !w.isTame() && WolfPersistentData.getWolfData(w).leaderUuid().equals(leaderUuid) && w.getTarget() == target
                );

                if (activePack.size() > 1) {
                    activePack.sort((w1, w2) -> {
                        double t1 = calculateApproachTime(w1, target);
                        double t2 = calculateApproachTime(w2, target);
                        if (t1 != t2) {
                            return Double.compare(t1, t2);
                        }
                        return Integer.compare(w1.getId(), w2.getId());
                    });

                    int myIndex = activePack.indexOf(wolf);
                    return isSlowerHalf(myIndex, activePack.size());
                }
            }
        }

        return false;
    }

    /**
     * Calculates the 3D flank offset vector from the target based on target bounding box and orientation.
     */
    public static Vec3 calculateFlankOffset(Vec3 forward, boolean isRightFlank, double targetWidth) {
        if (forward == null || forward.lengthSqr() < 0.001) {
            forward = new Vec3(1.0, 0.0, 0.0);
        } else {
            forward = forward.multiply(1.0, 0.0, 1.0).normalize();
        }

        double flankRadius = Math.max(3.0, targetWidth * 2.5);
        double rearShift = Math.max(1.0, targetWidth * 1.1);

        Vec3 sideVector;
        if (isRightFlank) {
            sideVector = new Vec3(-forward.z, 0.0, forward.x).scale(flankRadius);
        } else {
            sideVector = new Vec3(forward.z, 0.0, -forward.x).scale(flankRadius);
        }

        return sideVector.subtract(forward.scale(rearShift));
    }

    /**
     * Calculates a collision-checked flanking destination. Returns null if all flank paths are blocked by terrain.
     */
    public static Vec3 calculateFlankDestination(Wolf wolf, LivingEntity target, boolean performRaycast) {
        if (wolf == null || target == null) {
            return null;
        }

        Vec3 targetPos = target.position();
        Vec3 forward = target.getLookAngle().multiply(1.0, 0.0, 1.0).normalize();
        if (forward.lengthSqr() < 0.01) {
            forward = new Vec3(1.0, 0.0, 0.0);
        }

        Vec3 toWolf = wolf.position().subtract(targetPos);
        double cross = forward.x * toWolf.z - forward.z * toWolf.x;
        boolean isRightFlank = cross > 0.0;
        double targetWidth = target.getBbWidth();

        Vec3 flankOffset = calculateFlankOffset(forward, isRightFlank, targetWidth);
        Vec3 destination = targetPos.add(flankOffset);

        if (!performRaycast || isPathClear(wolf, destination)) {
            return destination;
        }

        // Primary flank path is blocked; attempt opposite flank
        flankOffset = calculateFlankOffset(forward, !isRightFlank, targetWidth);
        destination = targetPos.add(flankOffset);

        if (!performRaycast || isPathClear(wolf, destination)) {
            return destination;
        }

        // Both flank paths blocked
        return null;
    }

    private static boolean isPathClear(Wolf wolf, Vec3 destination) {
        Vec3 start = wolf.position().add(0.0, 0.5, 0.0);
        Vec3 end = destination.add(0.0, 0.5, 0.0);
        BlockHitResult result = wolf.level().clip(
                new ClipContext(
                        start,
                        end,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.ANY,
                        wolf
                )
        );
        return result.getType() == HitResult.Type.MISS;
    }
}
