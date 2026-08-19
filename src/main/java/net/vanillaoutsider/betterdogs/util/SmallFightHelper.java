// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;

import java.util.List;

/**
 * Dedicated single-purpose helper for harmless packmate social sparring / play fighting.
 */
public final class SmallFightHelper {

    public static final int SPARRING_DURATION_TICKS = 120; // 6 seconds
    public static final double DEFAULT_PARTNER_RADIUS = 6.0;
    public static final double DEFAULT_SPEED_MODIFIER = 1.15;

    private SmallFightHelper() {
    }

    /**
     * Checks whether the wolf is eligible to initiate or participate in play sparring.
     */
    public static boolean isEligibleForPlay(Wolf wolf) {
        if (wolf == null || !wolf.isAlive() || !wolf.isTame() || wolf.isOrderedToSit() || wolf.isLeashed() || wolf.getTarget() != null) {
            return false;
        }
        if (wolf instanceof WolfExtensions ext) {
            return !ext.betterdogs$isSocialModeActive() && !ext.betterdogs$hasBloodFeud();
        }
        return false;
    }

    /**
     * Checks whether two wolves are compatible packmates that can play spar together.
     */
    public static boolean canPlayTogether(Wolf wolfA, Wolf wolfB) {
        if (!isEligibleForPlay(wolfA) || !isEligibleForPlay(wolfB) || wolfA == wolfB) {
            return false;
        }
        LivingEntity ownerA = wolfA.getOwner();
        LivingEntity ownerB = wolfB.getOwner();
        if (ownerA == null || ownerA != ownerB) {
            return false;
        }
        return wolfA.distanceToSqr(wolfB) <= (DEFAULT_PARTNER_RADIUS * DEFAULT_PARTNER_RADIUS);
    }

    /**
     * Finds an eligible packmate for play fighting in the local vicinity.
     */
    public static Wolf findPlayPartner(Wolf wolf, double radius) {
        if (!isEligibleForPlay(wolf) || wolf.level() == null || wolf.level().isClientSide()) {
            return null;
        }
        List<Wolf> nearbyWolves = wolf.level().getEntitiesOfClass(
                Wolf.class,
                wolf.getBoundingBox().inflate(radius),
                w -> canPlayTogether(wolf, w)
        );
        if (nearbyWolves.isEmpty()) {
            return null;
        }
        return nearbyWolves.get(wolf.getRandom().nextInt(nearbyWolves.size()));
    }

    /**
     * Starts a play sparring session between two wolves.
     */
    public static void startPlaySession(Wolf wolfA, Wolf wolfB) {
        if (canPlayTogether(wolfA, wolfB)) {
            if (wolfA instanceof WolfExtensions extA) {
                extA.betterdogs$setSocialState(wolfB, WolfExtensions.SocialAction.PLAY_FIGHT, SPARRING_DURATION_TICKS);
            }
            if (wolfB instanceof WolfExtensions extB) {
                extB.betterdogs$setSocialState(wolfA, WolfExtensions.SocialAction.PLAY_FIGHT, SPARRING_DURATION_TICKS);
            }
        }
    }

    /**
     * Plays sensory cues (particles and audio) and increases inter-dog affinity.
     */
    public static void applyPlayFeedback(Wolf wolf, Wolf partner) {
        if (wolf == null || partner == null || wolf.level() == null || !(wolf.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.sendParticles(
                ParticleTypes.HAPPY_VILLAGER,
                (wolf.getX() + partner.getX()) * 0.5,
                (wolf.getY() + partner.getY()) * 0.5 + 0.5,
                (wolf.getZ() + partner.getZ()) * 0.5,
                3,
                0.2, 0.2, 0.2, 0.0
        );

        float pitch = 1.3F + wolf.getRandom().nextFloat() * 0.4F;
        if (wolf.getRandom().nextBoolean()) {
            serverLevel.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(),
                    SoundEvents.WOLF_GROWL_BABY.value(), SoundSource.NEUTRAL, 0.6F, pitch);
        } else {
            serverLevel.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(),
                    SoundEvents.WOLF_AMBIENT_BABY.value(), SoundSource.NEUTRAL, 0.8F, pitch);
        }

        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$adjustAffinity(partner.getStringUUID(), 1);
        }
        if (partner instanceof WolfExtensions partnerExt) {
            partnerExt.betterdogs$adjustAffinity(wolf.getStringUUID(), 1);
        }
    }
}
