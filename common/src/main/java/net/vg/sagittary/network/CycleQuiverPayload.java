package net.vg.sagittary.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.vg.sagittary.Sagittary;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.vg.sagittary.item.QuiverItem;

/** A selection request, not client-authored item data. -1 targets the active quiver. */
public record CycleQuiverPayload(int containerId, int slot, boolean forward) implements CustomPacketPayload {
    public CycleQuiverPayload(boolean forward) { this(-1, -1, forward); }

    public static final Type<CycleQuiverPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Sagittary.MOD_ID, "cycle_quiver"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CycleQuiverPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CycleQuiverPayload::containerId,
            ByteBufCodecs.VAR_INT, CycleQuiverPayload::slot,
            ByteBufCodecs.BOOL, CycleQuiverPayload::forward, CycleQuiverPayload::new);

    public void handle(Player player) {
        AbstractContainerMenu menu = player.containerMenu;
        Slot target = null;
        ItemStack quiver;
        if (containerId == -1 && slot == -1) {
            quiver = QuiverItem.findActiveQuiver(player);
        } else {
            // Creative's ItemPickerMenu exists only on the client. Its inventory
            // wrappers are translated to server inventory-menu slots by the caller.
            if (menu.containerId != containerId || !menu.stillValid(player)
                    || !menu.getCarried().isEmpty() || slot < 0 || slot >= menu.slots.size()) return;
            target = menu.getSlot(slot);
            if (!target.isActive() || !target.mayPickup(player)) return;
            quiver = target.getItem();
        }
        if (!(quiver.getItem() instanceof QuiverItem)) return;
        QuiverItem.cycleSelectedArrow(quiver, forward);
        if (target != null) target.setChanged();
        player.getInventory().setChanged();
        menu.broadcastChanges();
    }
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
