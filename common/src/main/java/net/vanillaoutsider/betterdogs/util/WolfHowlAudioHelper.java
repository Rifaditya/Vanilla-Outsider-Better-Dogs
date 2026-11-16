// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.BetterDogs;
import net.vanillaoutsider.betterdogs.platform.Services;

/**
 * Single-purpose audio helper for dual sound delivery during wolf howling.
 * Sends custom howl audio to Better Dogs clients, and pitched-down vanilla baby whine to unmodded vanilla clients.
 */
public final class WolfHowlAudioHelper {

    private WolfHowlAudioHelper() {}

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

            boolean hasMod = Services.PLATFORM.hasBetterDogsClient(player);

            ClientboundSoundPacket packet;
            if (hasMod) {
                packet = new ClientboundSoundPacket(
                        Holder.direct(SoundEvent.createVariableRangeEvent(BetterDogs.WOLF_HOWL_ID)),
                        SoundSource.NEUTRAL,
                        wolfX, wolfY, wolfZ,
                        volume, pitch,
                        serverLevel.getRandom().nextLong()
                );
            } else {
                packet = new ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.WOLF_WHINE_BABY.value()),
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
