// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose helper for chorus howling propagation and natural musical pitch variation.
 */
public class WolfHowlHelper {

    public static void initiateChorusHowl(Wolf initiator, double radius) {
        if (initiator == null) {
            return;
        }
        Level level = initiator.level();
        if (level == null || level.isClientSide()) {
            return;
        }

        startHowl(initiator, 1.0F);

        List<Wolf> nearbyWolves = level.getEntitiesOfClass(
            Wolf.class,
            initiator.getBoundingBox().inflate(radius),
            w -> w.isAlive() && w != initiator && !w.isBaby() && !w.isOrderedToSit() && w.getTarget() == null
        );

        for (Wolf packWolf : nearbyWolves) {
            if (packWolf instanceof WolfExtensions ext && ext.betterdogs$getHowlingTicks() <= 0) {
                int delay = 10 + packWolf.getRandom().nextInt(25);
                ext.betterdogs$setHowlingTicks(60 + delay);
            }
        }

        List<net.minecraft.world.entity.player.Player> nearbyPlayers = level.getEntitiesOfClass(
            net.minecraft.world.entity.player.Player.class,
            initiator.getBoundingBox().inflate(radius)
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
            ext.betterdogs$setHowlingTicks(60);
        }

        level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.WOLF_SHAKE, SoundSource.NEUTRAL, 1.2F, pitch * 0.8F);
    }
}
