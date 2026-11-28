// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import org.jspecify.annotations.Nullable;

/**
 * Dedicated single-purpose helper for managing Guard Mode state, toggling, and territory patrol radii.
 */
public final class WolfGuardHelper {

    private WolfGuardHelper() {
    }

    public static int getPatrolRadius(WolfPersonality personality) {
        if (personality == null) {
            return 8;
        }
        return switch (personality) {
            case AGGRESSIVE -> 12;
            case PACIFIST -> 4;
            case NORMAL -> 8;
        };
    }

    public static boolean canToggleGuard(Wolf wolf, Player player, ItemStack held) {
        if (wolf == null || player == null || !wolf.isTame()) {
            return false;
        }
        if (held == null || !held.is(Items.BONE)) {
            return false;
        }
        return wolf.isOwnedBy(player);
    }

    public static InteractionResult toggleGuardMode(Wolf wolf, Player player) {
        if (wolf == null || player == null || !(wolf instanceof WolfExtensions ext)) {
            return InteractionResult.PASS;
        }

        Level level = wolf.level();
        if (level == null) {
            return InteractionResult.PASS;
        }

        boolean currentGuarding = ext.betterdogs$isGuardMode();
        boolean newGuarding = !currentGuarding;

        if (!level.isClientSide()) {
            ext.betterdogs$setGuardMode(newGuarding);

            if (newGuarding) {
                BlockPos guardPos = wolf.blockPosition();
                ext.betterdogs$setGuardPos(guardPos);
                player.sendOverlayMessage(
                        Component.literal("§6Guard Mode: §aActive §7(" + guardPos.getX() + ", " + guardPos.getY() + ", " + guardPos.getZ() + ")")
                );
                if (player instanceof ServerPlayer serverPlayer) {
                    net.vanillaoutsider.betterdogs.BetterDogs.GUARD_WOLF_PERSONALITY.trigger(serverPlayer, ext.betterdogs$getPersonality());
                }
            } else {
                ext.betterdogs$setGuardPos(null);
                player.sendOverlayMessage(
                        Component.literal("§6Guard Mode: §7Inactive")
                );
            }

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 10, 0.3, 0.3, 0.3, 0.05);
                level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 0.8f, 1.2f);
            }
        }

        return InteractionResult.SUCCESS;
    }

    public static boolean shouldRunLeashedWatchdogAlarm(
            boolean isTame,
            boolean isGuardMode,
            boolean isSittingManually,
            boolean isLeashed,
            @Nullable WolfPersonality personality) {
        return isTame
                && isGuardMode
                && !isSittingManually
                && isLeashed
                && personality == WolfPersonality.PACIFIST;
    }

    public static @Nullable Monster findClosestWatchdogThreat(@Nullable Wolf wolf) {
        if (wolf == null || wolf.level() == null) {
            return null;
        }
        List<Monster> enemies = wolf.level().getEntitiesOfClass(
                Monster.class,
                wolf.getBoundingBox().inflate(16.0, 16.0, 16.0),
                enemy -> {
                    double dy = Math.abs(enemy.getY() - wolf.getY());
                    if (dy > 16.0) return false;
                    if (dy <= 4.0) return true;
                    return wolf.getSensing().hasLineOfSight(enemy);
                });
        Monster closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Monster enemy : enemies) {
            double dist = wolf.distanceToSqr(enemy);
            if (dist < closestDist) {
                closestDist = dist;
                closest = enemy;
            }
        }
        return closest;
    }

    public static void emitWatchdogAlarm(@Nullable Wolf wolf, @Nullable ServerLevel serverLevel) {
        if (wolf == null) {
            return;
        }
        wolf.playSound(((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().growlSound().value(), 1.0f, 1.0f);
        if (serverLevel == null) {
            return;
        }
        String density = net.vanillaoutsider.betterdogs.config.BetterDogsConfig.get().getGuardParticleDensity();
        int count = switch (density) {
            case "high" -> 12;
            case "medium" -> 6;
            case "low" -> 3;
            default -> 0;
        };
        if (count <= 0) {
            return;
        }
        DustParticleOptions alertDust = new DustParticleOptions(0xFF0000, 1.5f);
        net.minecraft.world.phys.Vec3 look = wolf.getLookAngle();
        double yaw = Math.atan2(look.z, look.x);
        double horizontalLength = Math.sqrt(look.x * look.x + look.z * look.z);
        double pitch = look.y;
        double speed = 0.5;
        double spread = Math.PI / 6.0;
        for (int i = 0; i < count; i++) {
            double angleOffset = (count > 1) ? (((double) i / (count - 1)) - 0.5) * spread : 0.0;
            double pAngle = yaw + angleOffset;
            double vx = Math.cos(pAngle) * horizontalLength;
            double vz = Math.sin(pAngle) * horizontalLength;
            double spawnX = wolf.getX() + vx * 0.5;
            double spawnY = wolf.getEyeY() - 0.1;
            double spawnZ = wolf.getZ() + vz * 0.5;
            serverLevel.sendParticles(alertDust, spawnX, spawnY, spawnZ, 0, vx, pitch, vz, speed);
        }
    }

    public static void tickLeashedWatchdogAlarm(
            @Nullable Wolf wolf,
            @Nullable WolfExtensions ext,
            @Nullable ServerLevel serverLevel) {
        if (wolf == null || ext == null || serverLevel == null) {
            return;
        }
        if (!shouldRunLeashedWatchdogAlarm(
                wolf.isTame(),
                ext.betterdogs$isGuardMode(),
                ext.betterdogs$isSittingManually(),
                wolf.isLeashed(),
                ext.betterdogs$getPersonality())) {
            return;
        }
        Monster closestThreat = findClosestWatchdogThreat(wolf);
        if (closestThreat != null && closestThreat.isAlive()) {
            wolf.getLookControl().setLookAt(closestThreat, 30.0f, 30.0f);
            if (wolf.tickCount % 60 == 0) {
                emitWatchdogAlarm(wolf, serverLevel);
            }
        }
    }
}
