package net.vg.sagittary.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.vg.sagittary.Sagittary;

public record CycleQuiverPayload(boolean forward) implements CustomPacketPayload {
    public static final Type<CycleQuiverPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Sagittary.MOD_ID, "cycle_quiver"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CycleQuiverPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CycleQuiverPayload::forward, CycleQuiverPayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
