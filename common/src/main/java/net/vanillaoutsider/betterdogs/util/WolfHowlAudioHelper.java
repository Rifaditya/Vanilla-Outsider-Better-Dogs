// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.registry.BetterDogsSoundEvents;

/**
 * Single-purpose audio helper for dual sound delivery during wolf howling.
 * Sends custom howl audio to Better Dogs clients, and pitched-down vanilla baby whine to unmodded vanilla clients.
 */
public final class WolfHowlAudioHelper {

    private static Predicate<ServerPlayer> clientCapabilityChecker = player -> false;

    private WolfHowlAudioHelper() {}

    /**
     * Injects the loader-specific client capability checker.
     *
     * @param checker the predicate evaluating if a player has Better Dogs installed
     */
    public static void setClientCapabilityChecker(Predicate<ServerPlayer> checker) {
        clientCapabilityChecker = checker != null ? checker : (player -> false);
    }

    /**
     * Obtains the vanilla wolf whine sound event holder.
     * Uses the 1.21.11 classic wolf sound variant whine holder with direct fallback.
     */
    public static Holder<SoundEvent> getVanillaWhineHolder() {
        try {
            if (SoundEvents.WOLF_SOUNDS != null) {
                WolfSoundVariant classic = SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.SoundSet.CLASSIC);
                if (classic != null && classic.whineSound() != null) {
                    return classic.whineSound();
                }
            }
        } catch (Throwable ignored) {
        }
        return Holder.direct(SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("minecraft", "entity.wolf.whine")));
    }

    /**
     * Plays wolf howling audio with vanilla client desync protection.
     *
     * @param level  the current world level
     * @param wolf   the howling wolf entity
     * @param volume audio volume
     * @param pitch  audio pitch
     */
    public static void playHowlSound(Level level, Wolf wolf, float volume, float pitch) {
        if (!(level instanceof ServerLevel serverLevel) || wolf == null) {
            return;
        }

        double maxDistSq = Math.pow(Math.max(volume, 1.0F) * 16.0D, 2);
        double wolfX = wolf.getX();
        double wolfY = wolf.getY();
        double wolfZ = wolf.getZ();

        for (ServerPlayer player : serverLevel.players()) {
            if (player == null || player.connection == null) {
                continue;
            }

            if (player.distanceToSqr(wolfX, wolfY, wolfZ) > maxDistSq) {
                continue;
            }

            boolean hasMod = clientCapabilityChecker.test(player);

            ClientboundSoundPacket packet;
            if (hasMod) {
                packet = new ClientboundSoundPacket(
                        Holder.direct(SoundEvent.createVariableRangeEvent(BetterDogsSoundEvents.WOLF_HOWL_ID)),
                        SoundSource.NEUTRAL,
                        wolfX, wolfY, wolfZ,
                        volume, pitch,
                        serverLevel.getRandom().nextLong()
                );
            } else {
                packet = new ClientboundSoundPacket(
                        getVanillaWhineHolder(),
                        SoundSource.NEUTRAL,
                        wolfX, wolfY, wolfZ,
                        volume, computeVanillaFallbackPitch(pitch),
                        serverLevel.getRandom().nextLong()
                );
            }
            player.connection.send(packet);
        }
    }

    /**
     * Clamps and normalizes howl pitch to a valid vanilla baby whine fallback pitch range [0.50f, 0.80f].
     *
     * @param pitch the input pitch
     * @return saturated pitch within [0.50f, 0.80f]
     */
    public static float computeVanillaFallbackPitch(float pitch) {
        if (Float.isNaN(pitch) || Float.isInfinite(pitch)) {
            return 0.65f;
        }
        if (pitch < 0.50f) {
            return 0.50f;
        }
        if (pitch > 0.80f) {
            return 0.80f;
        }
        return pitch;
    }
}
