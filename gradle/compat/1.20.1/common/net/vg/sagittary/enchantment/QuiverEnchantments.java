package net.vg.sagittary.enchantment;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.vg.sagittary.Sagittary;
import net.vg.sagittary.item.QuiverItem;
import net.vg.sagittary.util.ModIds;

/** The same IDs/effects as the later datapack enchantments, registered for 1.20.1. */
public final class QuiverEnchantments {
    public static final ResourceLocation RECOVERY = ModIds.of("recovery");
    public static final ResourceLocation RANDOMIZER = ModIds.of("randomizer");
    private static final DeferredRegister<Enchantment> TYPES = DeferredRegister.create(Sagittary.MOD_ID, Registries.ENCHANTMENT);
    static {
        TYPES.register(RECOVERY, QuiverEnchantment::new);
        TYPES.register(RANDOMIZER, QuiverEnchantment::new);
    }
    public static void init() { TYPES.register(); }
    public static boolean has(ItemStack stack, ResourceLocation id) {
        return BuiltInRegistries.ENCHANTMENT.getOptional(id)
                .map(enchantment -> EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) > 0).orElse(false);
    }
    private static final class QuiverEnchantment extends Enchantment {
        QuiverEnchantment() { super(Rarity.RARE, EnchantmentCategory.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}); }
        @Override public boolean canEnchant(ItemStack stack) { return stack.getItem() instanceof QuiverItem; }
        @Override public int getMinCost(int level) { return 18; }
        @Override public int getMaxCost(int level) { return 42; }
    }
}
