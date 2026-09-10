package net.vg.sagittary.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.vg.sagittary.Sagittary;

import java.util.ArrayList;
import java.util.List;

/** Server-selected Topaz targets shown as a short, through-wall client pulse. */
public record TopazPulsePayload(List<BlockPos> targets) implements CustomPacketPayload {
    public static final Type<TopazPulsePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Sagittary.MOD_ID, "topaz_pulse"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TopazPulsePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, BlockPos.STREAM_CODEC, 12),
            TopazPulsePayload::targets, TopazPulsePayload::new);

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
