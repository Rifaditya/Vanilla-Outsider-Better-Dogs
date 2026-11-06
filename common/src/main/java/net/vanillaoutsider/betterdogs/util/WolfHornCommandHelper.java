// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Instruments;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.List;

/**
 * Dedicated single-purpose helper for goat horn tactical acoustic pack commands.
 */
public final class WolfHornCommandHelper {

    private WolfHornCommandHelper() {
    }

    public static void onHornUsed(Level level, Player player, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (!BetterDogsGameRules.isHornCommandsEnabled(serverLevel)) {
            return;
        }

        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof InstrumentItem)) {
            return;
        }

        InstrumentComponent component = stack.get(DataComponents.INSTRUMENT);
        if (component == null) {
            return;
        }

        Holder<Instrument> holder = component.instrument();
        if (holder == null) {
            return;
        }

        boolean isPonder = holder.is(Instruments.PONDER_GOAT_HORN);
        boolean isFeel = holder.is(Instruments.FEEL_GOAT_HORN);
        boolean isSing = holder.is(Instruments.SING_GOAT_HORN);
        boolean isYearn = holder.is(Instruments.YEARN_GOAT_HORN);
        boolean isSeek = holder.is(Instruments.SEEK_GOAT_HORN);

        if (!isPonder && !isFeel && !isSing && !isYearn && !isSeek) {
            return;
        }

        int range = BetterDogsGameRules.getInt(serverLevel, BetterDogsGameRules.BD_HORN_COMMAND_RANGE, 64);
        double rangeD = (double) range;

        AABB area = player.getBoundingBox().inflate(rangeD, rangeD, rangeD);
        List<Wolf> nearbyWolves = serverLevel.getEntitiesOfClass(Wolf.class, area);
        BlockPos callPos = player.blockPosition();

        if (isYearn) {
            // Stand Up / Resume Follow Command
            for (Wolf wolf : nearbyWolves) {
                if (wolf.isTame() && wolf.isOwnedBy(serverPlayer) && wolf.isOrderedToSit()) {
                    if (wolf instanceof WolfExtensions ext) {
                        wolf.setOrderedToSit(false);
                        ext.betterdogs$setSoundLocationTarget(null);
                        ext.betterdogs$setPassiveOverrideTicks(0);

                        serverLevel.sendParticles(ParticleTypes.NOTE, wolf.getX(), wolf.getY() + 0.8, wolf.getZ(), 5, 0.2, 0.2, 0.2, 0.0);
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, wolf.getX(), wolf.getY() + 0.8, wolf.getZ(), 4, 0.2, 0.2, 0.2, 0.0);
                    }
                }
            }
            return;
        }

        if (isSeek) {
            // Search & Attack Target Command
            LivingEntity target = EntityRaycastHelper.findCrosshairTarget(serverPlayer, 32.0);
            if (target == null) {
                target = findNearestHostile(serverPlayer, serverLevel, area);
            }

            for (Wolf wolf : nearbyWolves) {
                if (wolf.isTame() && wolf.isOwnedBy(serverPlayer) && !wolf.isOrderedToSit()) {
                    if (wolf instanceof WolfExtensions ext) {
                        ext.betterdogs$setSoundLocationTarget(null);
                        ext.betterdogs$setPassiveOverrideTicks(0);

                        if (target != null) {
                            wolf.setTarget(target);
                            wolf.setLastHurtByMob(target);
                            wolf.getNavigation().moveTo(target, 1.3);
                            serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, wolf.getX(), wolf.getY() + 0.8, wolf.getZ(), 4, 0.2, 0.2, 0.2, 0.0);
                        } else {
                            serverLevel.sendParticles(ParticleTypes.SMOKE, wolf.getX(), wolf.getY() + 0.8, wolf.getZ(), 5, 0.2, 0.2, 0.2, 0.0);
                        }
                    }
                }
            }
            if (target != null) {
                serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, target.getX(), target.getY() + target.getBbHeight() + 0.3, target.getZ(), 8, 0.3, 0.3, 0.3, 0.0);
            }
            return;
        }

        for (Wolf wolf : nearbyWolves) {
            if (wolf.isTame() && wolf.isOwnedBy(serverPlayer) && !wolf.isOrderedToSit()) {
                if (wolf instanceof WolfExtensions ext) {
                    if (isPonder) {
                        // Assemble / Rally Command
                        ext.betterdogs$setSoundLocationTarget(callPos);
                        ext.betterdogs$setPassiveOverrideTicks(0);
                        serverLevel.sendParticles(ParticleTypes.NOTE, wolf.getX(), wolf.getY() + 0.8, wolf.getZ(), 5, 0.2, 0.2, 0.2, 0.0);
                    } else if (isFeel) {
                        // Pacifist / Calm Override
                        int duration = BetterDogsGameRules.getInt(serverLevel, BetterDogsGameRules.BD_HORN_OVERRIDE_DURATION, 600);
                        ext.betterdogs$setPassiveOverrideTicks(duration);
                        ext.betterdogs$setSoundLocationTarget(null);
                        wolf.setTarget(null);
                        wolf.stopBeingAngry();
                        wolf.getNavigation().stop();
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, wolf.getX(), wolf.getY() + 0.8, wolf.getZ(), 6, 0.3, 0.3, 0.3, 0.0);
                    } else if (isSing) {
                        // Hold / Sit Command
                        wolf.setOrderedToSit(true);
                        ext.betterdogs$setSoundLocationTarget(null);
                        ext.betterdogs$setPassiveOverrideTicks(0);
                        wolf.setTarget(null);
                        wolf.stopBeingAngry();
                        wolf.getNavigation().stop();
                        serverLevel.sendParticles(ParticleTypes.NOTE, wolf.getX(), wolf.getY() + 0.8, wolf.getZ(), 5, 0.2, 0.2, 0.2, 0.0);
                    }
                }
            }
        }
    }

    private static LivingEntity findNearestHostile(ServerPlayer player, ServerLevel level, AABB area) {
        List<LivingEntity> hostiles = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player && e.isAlive() && (e instanceof Enemy || e instanceof Monster));
        LivingEntity nearest = null;
        double minSq = Double.MAX_VALUE;
        for (LivingEntity hostile : hostiles) {
            double dist = player.distanceToSqr(hostile);
            if (dist < minSq) {
                minSq = dist;
                nearest = hostile;
            }
        }
        return nearest;
    }
}
