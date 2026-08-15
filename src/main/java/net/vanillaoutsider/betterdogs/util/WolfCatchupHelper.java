// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.Collections;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for high-speed fast-travel catchup and interdimensional teleport sync.
 */
public class WolfCatchupHelper {

    public static double calculateCatchupSpeed(Wolf wolf, LivingEntity owner, double baseSpeed) {
        if (wolf == null || owner == null || !wolf.isTame()) {
            return baseSpeed;
        }
        Level level = wolf.getCommandSenderWorld();
        if (level == null) {
            return baseSpeed;
        }

        if (!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_FAST_TRAVEL_CATCHUP, true)) {
            return baseSpeed;
        }

        double distSq = wolf.distanceToSqr(owner);
        boolean isOwnerFast = owner.isPassenger() || owner.getDeltaMovement().lengthSqr() > 0.04D || owner.isFallFlying();

        if (isOwnerFast || distSq > 100.0D) { // > 10 blocks away
            if (distSq > 400.0D) { // > 20 blocks away
                return baseSpeed * 2.0D;
            }
            return baseSpeed * 1.5D;
        }

        return baseSpeed;
    }

    public static void syncOwnerDimensionTeleport(ServerPlayer player, ServerLevel originLevel, ServerLevel destLevel, Vec3 destPos) {
        if (player == null || originLevel == null || destLevel == null || destPos == null) {
            return;
        }
        if (!BetterDogsGameRules.getBoolean(originLevel, BetterDogsGameRules.BD_SYNC_OWNER_TELEPORT, true)) {
            return;
        }

        List<Wolf> nearbyDogs = originLevel.getEntitiesOfClass(
                Wolf.class,
                player.getBoundingBox().inflate(16.0D),
                w -> w != null && w.isTame() && w.isOwnedBy(player) && !w.isInSittingPose() && !w.isLeashed()
        );

        for (Wolf dog : nearbyDogs) {
            dog.teleportTo(destLevel, destPos.x(), destPos.y(), destPos.z(), Collections.emptySet(), dog.getYRot(), dog.getXRot());
        }
    }
}
