// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose helper for puppy mischief targeting and adult disciplinary correction.
 */
public class WolfMischiefHelper {

    public static LivingEntity findMischiefTarget(Wolf puppy, double radius) {
        if (puppy == null || !puppy.isBaby()) {
            return null;
        }
        Level level = puppy.getCommandSenderWorld();
        if (level == null) {
            return null;
        }

        List<LivingEntity> entities = level.getEntitiesOfClass(
            LivingEntity.class,
            puppy.getBoundingBox().inflate(radius),
            e -> e.isAlive() && e != puppy && (
                (e instanceof Wolf adultWolf && !adultWolf.isBaby()) ||
                (e instanceof Chicken) ||
                (e instanceof Rabbit)
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
        Level level = adult.getCommandSenderWorld();
        if (level == null || level.isClientSide()) {
            return;
        }

        if (puppy instanceof WolfExtensions ext) {
            ext.betterdogs$setCalmTicks(160);
        }

        adult.getLookControl().setLookAt(puppy, 30.0F, 30.0F);
        puppy.getNavigation().stop();

        level.playSound(null, adult.getX(), adult.getY(), adult.getZ(), SoundEvents.WOLF_GROWL, SoundSource.NEUTRAL, 0.8F, 1.2F);
        level.playSound(null, puppy.getX(), puppy.getY(), puppy.getZ(), SoundEvents.WOLF_WHINE, SoundSource.NEUTRAL, 0.8F, 1.4F);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, adult.getX(), adult.getY() + 0.5, adult.getZ(), 2, 0.1, 0.1, 0.1, 0.0);
        }
    }
}
