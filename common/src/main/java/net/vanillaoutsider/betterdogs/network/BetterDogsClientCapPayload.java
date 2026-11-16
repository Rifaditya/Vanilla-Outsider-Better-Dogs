// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Lightweight client capability marker payload.
 * Registered as a clientbound play payload so servers can detect if the client has Better Dogs installed
 * via channel query without mandatory packet exchange.
 */
public record BetterDogsClientCapPayload() implements CustomPacketPayload {

    public static final Type<BetterDogsClientCapPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("betterdogs", "client_audio_cap"));

    public static final StreamCodec<ByteBuf, BetterDogsClientCapPayload> CODEC =
            StreamCodec.unit(new BetterDogsClientCapPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
