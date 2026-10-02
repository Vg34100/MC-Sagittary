package net.vg.sagittary.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/** Immutable, structurally comparable count-one prototype of the selected arrow type. */
public record QuiverSelection(ItemStack arrow) {
    public static final Codec<QuiverSelection> CODEC = ItemStack.CODEC.xmap(QuiverSelection::new, QuiverSelection::arrow);
    public static final StreamCodec<RegistryFriendlyByteBuf, QuiverSelection> STREAM_CODEC =
            ItemStack.STREAM_CODEC.map(QuiverSelection::new, QuiverSelection::arrow);

    public QuiverSelection {
        arrow = arrow.copyWithCount(1);
    }

    @Override public ItemStack arrow() { return arrow.copy(); }

    @Override public boolean equals(Object other) {
        return other instanceof QuiverSelection selection
                && ItemStack.isSameItemSameComponents(arrow, selection.arrow);
    }

    @Override public int hashCode() { return ItemStack.hashItemAndComponents(arrow); }
}
