package net.vg.sagittary.network;

import net.minecraft.core.BlockPos;
//? if >=1.21 {
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? }
import net.minecraft.resources.Identifier;
import net.vg.sagittary.Sagittary;

import java.util.ArrayList;
import java.util.List;

/** Server-selected Topaz targets shown as a short, through-wall client pulse. */
public record TopazPulsePayload(List<BlockPos> targets)
        //? if >=1.21 {
        implements CustomPacketPayload
        //? }
{
    //? if >=1.21 {
    public static final Type<TopazPulsePayload> TYPE = new Type<>(net.vg.sagittary.util.ModIds.of("topaz_pulse"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TopazPulsePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, BlockPos.STREAM_CODEC, 12),
            TopazPulsePayload::targets, TopazPulsePayload::new);

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    //? } else {
    /*public static final Identifier ID = net.vg.sagittary.util.ModIds.of("topaz_pulse");
    public static TopazPulsePayload read(net.minecraft.network.FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        if (size < 0 || size > 12) throw new IllegalArgumentException("Invalid Topaz target count");
        List<BlockPos> positions = new ArrayList<>();
        for (int i = 0; i < size; i++) positions.add(buffer.readBlockPos());
        return new TopazPulsePayload(positions);
    }
    *///? }

    public void sendToPlayer(net.minecraft.server.level.ServerPlayer player) {
        //? if >=1.21 {
        dev.architectury.networking.NetworkManager.sendToPlayer(player, this);
        //? } else {
        /*var buffer = new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        try {
            buffer.writeVarInt(targets.size());
            targets.forEach(buffer::writeBlockPos);
            dev.architectury.networking.NetworkManager.sendToPlayer(player, ID, buffer);
        } finally { buffer.release(); }
        *///? }
    }
}
