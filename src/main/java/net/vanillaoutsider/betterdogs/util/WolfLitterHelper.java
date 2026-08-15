// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for calculating multi-puppy litter sizes and spawning sibling puppies.
 */
public class WolfLitterHelper {

    public static int determineLitterSize(Level level, RandomSource random) {
        if (random == null) {
            return 1;
        }

        int maxLitter = level != null ? BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_WOLF_LITTER_MAX_SIZE, 4) : 4;
        if (maxLitter <= 1) {
            return 1;
        }

        int roll = random.nextInt(100);
        int targetSize;
        if (roll < 45) {
            targetSize = 1;
        } else if (roll < 80) {
            targetSize = 2;
        } else if (roll < 95) {
            targetSize = 3;
        } else {
            targetSize = 4;
        }

        return Math.min(targetSize, maxLitter);
    }

    public static void spawnExtraPuppies(ServerLevel level, Wolf parentA, AgeableMob otherParent, Wolf primaryChild) {
        if (level == null || parentA == null || primaryChild == null) {
            return;
        }

        int litterSize = determineLitterSize(level, primaryChild.getRandom());
        if (litterSize <= 1) {
            return;
        }

        WolfPersonality personalityA = parentA instanceof WolfExtensions extA ? extA.betterdogs$getPersonality() : WolfPersonality.NORMAL;
        WolfPersonality personalityB = otherParent instanceof WolfExtensions extB ? extB.betterdogs$getPersonality() : WolfPersonality.NORMAL;

        float scaleA = parentA instanceof WolfExtensions extA ? extA.betterdogs$getSocialScale() : 1.0f;
        float scaleB = otherParent instanceof WolfExtensions extB ? extB.betterdogs$getSocialScale() : 1.0f;

        for (int i = 1; i < litterSize; i++) {
            Wolf sibling = EntityType.WOLF.create(level);
            if (sibling != null) {
                double offsetX = (sibling.getRandom().nextDouble() - 0.5) * 0.8;
                double offsetZ = (sibling.getRandom().nextDouble() - 0.5) * 0.8;
                sibling.moveTo(primaryChild.getX() + offsetX, primaryChild.getY(), primaryChild.getZ() + offsetZ, primaryChild.getYRot(), primaryChild.getXRot());
                sibling.setAge(-24000);

                if (primaryChild.isTame()) {
                    sibling.setTame(true, true);
                    sibling.setOwnerUUID(primaryChild.getOwnerUUID());
                }

                if (sibling instanceof WolfExtensions siblingExt) {
                    siblingExt.betterdogs$setCollarColor(parentA.getCollarColor());
                    WolfPersonality inheritedP = WolfGeneticsHelper.calculateOffspringPersonality(level, personalityA, personalityB, sibling.getRandom());
                    float inheritedS = WolfScaleGeneticsHelper.calculateOffspringScale(level, scaleA, scaleB, sibling.getRandom());
                    siblingExt.betterdogs$setPersonality(inheritedP);
                    siblingExt.betterdogs$setSocialScale(inheritedS);
                }

                level.addFreshEntity(sibling);
                level.sendParticles(ParticleTypes.HEART, sibling.getX(), sibling.getY() + 0.5, sibling.getZ(), 4, 0.25, 0.25, 0.25, 0.0);
                level.playSound(null, sibling.getX(), sibling.getY(), sibling.getZ(), SoundEvents.WOLF_AMBIENT, SoundSource.NEUTRAL, 0.6f, 1.6f);
            }
        }
    }
}
