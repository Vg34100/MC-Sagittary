package net.vg.sagittary.enchantment;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/** Identifier-based lookup keeps custom quiver enchantments data-driven. */
public final class QuiverEnchantments {
    public static final Identifier RECOVERY = Identifier.fromNamespaceAndPath("sagittary", "recovery");
    public static final Identifier RANDOMIZER = Identifier.fromNamespaceAndPath("sagittary", "randomizer");
    private QuiverEnchantments() {}

    public static boolean has(ItemStack stack, Identifier enchantment) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        return enchantments.keySet().stream().anyMatch(holder -> holder.unwrapKey()
                //? if >=26.1 {
                .map(key -> key.identifier().equals(enchantment)).orElse(false));
                //? } else {
                /*.map(key -> key.location().equals(enchantment)).orElse(false));
                *///? }
    }
}
