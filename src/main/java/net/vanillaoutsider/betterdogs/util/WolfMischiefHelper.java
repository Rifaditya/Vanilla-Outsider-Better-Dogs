// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for puppy mischief targeting and adult disciplinary correction.
 */
public final class WolfMischiefHelper {

    private WolfMischiefHelper() {
    }

    public static LivingEntity findMischiefTarget(Wolf puppy, double radius) {
        if (puppy == null || !puppy.isBaby()) {
            return null;
        }
        Level level = puppy.level();
        if (level == null) {
            return null;
        }

        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class,
                puppy.getBoundingBox().inflate(radius),
                e -> e.isAlive() && e != puppy && (
                        (e instanceof Wolf adultWolf && !adultWolf.isBaby())
                                || (e instanceof Chicken)
                                || (e instanceof Rabbit)
                                || (e instanceof Player)
                )
        );

        LivingEntity closest = null;
        double closestDistSq = Double.MAX_VALUE;
        for (LivingEntity e : entities) {
            double distSq = puppy.distanceToSqr(e);
            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                closest = e;
            }
        }
        return closest;
    }

    public static void performDiscipline(Wolf adult, Wolf puppy) {
        if (adult == null || puppy == null || !puppy.isBaby() || adult.isBaby()) {
            return;
        }
        Level level = adult.level();
        if (level == null || level.isClientSide()) {
            return;
        }

        if (puppy instanceof WolfExtensions ext) {
            WolfPersonality personality = ext.betterdogs$hasPersonality()
                    ? ext.betterdogs$getPersonality()
                    : WolfPersonality.NORMAL;

            if (personality == WolfPersonality.AGGRESSIVE) {
                int retaliateChance = BetterDogsGameRules.getInt(
                        level,
                        BetterDogsGameRules.BD_BABY_RETALIATE_PERCENT,
                        50
                );
                if (puppy.getRandom().nextInt(100) < retaliateChance) {
                    ext.betterdogs$setSocialState(adult, WolfExtensions.SocialAction.RETALIATION, 100);

                    adult.getLookControl().setLookAt(puppy, 30.0F, 30.0F);

                    try {
                        var adultSounds = ((WolfAccessor) adult).betterdogs$invokeGetSoundSet();
                        var pupSounds = ((WolfAccessor) puppy).betterdogs$invokeGetSoundSet();
                        if (adultSounds != null && adultSounds.growlSound() != null) {
                            adult.playSound(adultSounds.growlSound().value(), 0.8F, 1.2F);
                        }
                        if (pupSounds != null && pupSounds.growlSound() != null) {
                            puppy.playSound(pupSounds.growlSound().value(), 0.8F, 1.6F);
                        }
                    } catch (Exception ignored) {
                    }

                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, adult.getX(), adult.getY() + 0.5, adult.getZ(), 2, 0.1, 0.1, 0.1, 0.0);
                        serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, puppy.getX(), puppy.getY() + 0.5, puppy.getZ(), 2, 0.1, 0.1, 0.1, 0.0);
                    }

                    int feudChance = BetterDogsGameRules.getInt(
                            level,
                            BetterDogsGameRules.BD_BLOOD_FEUD_PERCENT,
                            5
                    );
                    if (puppy.getRandom().nextInt(100) < feudChance) {
                        ext.betterdogs$setBloodFeudTarget(adult.getStringUUID());
                        if (adult instanceof WolfExtensions adultExt) {
                            adultExt.betterdogs$setBloodFeudTarget(puppy.getStringUUID());
                        }
                    }
                    return;
                }
            }

            ext.betterdogs$setSocialState(adult, WolfExtensions.SocialAction.DISCIPLINE, 160);
        }

        adult.getLookControl().setLookAt(puppy, 30.0F, 30.0F);
        puppy.getNavigation().stop();

        try {
            var adultSounds = ((WolfAccessor) adult).betterdogs$invokeGetSoundSet();
            if (adultSounds != null && adultSounds.growlSound() != null) {
                adult.playSound(adultSounds.growlSound().value(), 0.6F, 1.3F);
            }
        } catch (Exception ignored) {
        }
    }
}
