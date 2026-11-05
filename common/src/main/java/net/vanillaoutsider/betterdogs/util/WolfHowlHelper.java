// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose helper for chorus howling propagation and natural musical pitch variation.
 */
public class WolfHowlHelper {

    public static final double DEFAULT_HOWL_RADIUS = 24.0D;
    public static final int BASE_HOWL_DURATION = 60;

    public static float calculateHarmonicPitch(float randomFloat) {
        float clamped = Math.max(0.0f, Math.min(1.0f, randomFloat));
        return 0.85f + (clamped * 0.35f);
    }

    public static int calculateChorusDelay(int randomOffset) {
        int clamped = Math.max(0, Math.min(24, randomOffset));
        return 10 + clamped;
    }

    public static boolean canJoinHowl(Wolf wolf) {
        if (wolf == null || !wolf.isAlive() || !wolf.isTame() || wolf.isBaby()) {
            return false;
        }

        if (wolf.isOrderedToSit() || wolf.getTarget() != null) {
            return false;
        }

        if (WolfDispositionHelper.isQuietHowler(wolf)) {
            return false;
        }

        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$isGuardMode() || ext.betterdogs$isSittingManually()) {
                return false;
            }
        }

        return true;
    }

    public static void initiateChorusHowl(Wolf initiator, double radius) {
        if (initiator == null) {
            return;
        }
        Level level = initiator.level();
        if (level == null || level.isClientSide()) {
            return;
        }

        startHowl(initiator, 1.0F);

        double searchRadius = radius > 0 ? radius : DEFAULT_HOWL_RADIUS;
        List<Wolf> nearbyWolves = level.getEntitiesOfClass(
            Wolf.class,
            initiator.getBoundingBox().inflate(searchRadius),
            w -> w != initiator && canJoinHowl(w) && (!(w instanceof WolfExtensions ext) || ext.betterdogs$getHowlingTicks() <= 0)
        );

        for (Wolf packWolf : nearbyWolves) {
            if (packWolf instanceof WolfExtensions ext) {
                int delay = calculateChorusDelay(packWolf.getRandom().nextInt(25));
                ext.betterdogs$setHowlingTicks(BASE_HOWL_DURATION + delay);
            }
        }

        List<net.minecraft.world.entity.player.Player> nearbyPlayers = level.getEntitiesOfClass(
            net.minecraft.world.entity.player.Player.class,
            initiator.getBoundingBox().inflate(searchRadius)
        );
        for (net.minecraft.world.entity.player.Player player : nearbyPlayers) {
            WolfAdvancementHelper.grantAdvancement(player, "chorus_howl");
        }
    }

    public static void startHowl(Wolf wolf, float pitch) {
        if (wolf == null) {
            return;
        }
        Level level = wolf.level();
        if (level == null || level.isClientSide()) {
            return;
        }

        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setHowlingTicks(BASE_HOWL_DURATION);
        }

        level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), net.vanillaoutsider.betterdogs.registry.BetterDogsSoundEvents.WOLF_HOWL, SoundSource.NEUTRAL, 1.2F, pitch);
    }
}
