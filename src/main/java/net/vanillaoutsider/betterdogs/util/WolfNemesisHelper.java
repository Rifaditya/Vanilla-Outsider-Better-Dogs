// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for registering, validating, and managing pack nemesis grudges.
 */
public class WolfNemesisHelper {

    public static final long TICKS_PER_DAY = 24000L;

    public static void recordNemesis(Wolf victim, DamageSource source) {
        if (victim == null || !victim.isTame()) {
            return;
        }
        Level level = victim.level();
        if (level == null || level.isClientSide) {
            return;
        }
        if (!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_NEMESIS_SYSTEM, true)) {
            return;
        }
        if (source == null) {
            return;
        }

        Entity killerEntity = source.getEntity();
        if (killerEntity == null) {
            killerEntity = source.getDirectEntity();
        }
        if (!(killerEntity instanceof LivingEntity killer) || killer == victim) {
            return;
        }

        // Do not hold grudge against own player/owner
        LivingEntity owner = victim.getOwner();
        if (owner != null && killer == owner) {
            return;
        }

        // Do not hold grudge against another friendly dog of same owner
        if (killer instanceof Wolf killerWolf && killerWolf.isTame() && owner != null && killerWolf.isOwnedBy(owner)) {
            return;
        }

        String killerTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(killer.getType()).toString();
        int durationDays = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_NEMESIS_DURATION_DAYS, 3);
        long expiryTime = level.getGameTime() + (Math.max(1, durationDays) * TICKS_PER_DAY);

        // Broadcast to all tamed wolves of the same owner within 64 blocks
        List<Wolf> packWolves = level.getEntitiesOfClass(
                Wolf.class,
                victim.getBoundingBox().inflate(64.0),
                w -> w != null && w.isTame() && owner != null && w.isOwnedBy(owner)
        );

        for (Wolf packWolf : packWolves) {
            if (packWolf instanceof WolfExtensions ext) {
                ext.betterdogs$setNemesisEntityType(killerTypeId);
                ext.betterdogs$setNemesisExpiryTime(expiryTime);

                packWolf.playSound(SoundEvents.WOLF_GROWL, 1.2F, 0.8F);
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleTypes.ANGRY_VILLAGER,
                            packWolf.getX(),
                            packWolf.getY() + 0.8,
                            packWolf.getZ(),
                            4,
                            0.2, 0.2, 0.2, 0.02
                    );
                }
            }
        }
    }

    public static boolean isNemesisActive(Wolf wolf, LivingEntity target) {
        if (wolf == null || target == null || !wolf.isTame()) {
            return false;
        }
        Level level = wolf.level();
        if (level == null) {
            return false;
        }
        if (!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_NEMESIS_SYSTEM, true)) {
            return false;
        }
        if (!(wolf instanceof WolfExtensions ext)) {
            return false;
        }

        String nemesisType = ext.betterdogs$getNemesisEntityType();
        if (nemesisType == null || nemesisType.isEmpty()) {
            return false;
        }

        long expiry = ext.betterdogs$getNemesisExpiryTime();
        if (level.getGameTime() >= expiry) {
            ext.betterdogs$setNemesisEntityType("");
            ext.betterdogs$setNemesisExpiryTime(0L);
            return false;
        }

        String targetTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString();
        return nemesisType.equals(targetTypeId);
    }

    public static void clearNemesis(Wolf wolf) {
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setNemesisEntityType("");
            ext.betterdogs$setNemesisExpiryTime(0L);
        }
    }
}
