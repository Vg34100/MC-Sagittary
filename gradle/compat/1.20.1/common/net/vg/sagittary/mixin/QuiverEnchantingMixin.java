package net.vg.sagittary.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.vg.sagittary.enchantment.QuiverEnchantments;
import net.vg.sagittary.item.QuiverItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/** Old enchanting tables inspect enum categories rather than Enchantment.canEnchant. */
@Mixin(EnchantmentHelper.class)
public class QuiverEnchantingMixin {
    @Inject(method = "getAvailableEnchantmentResults", at = @At("RETURN"))
    private static void sagittary$quiverCandidates(int power, ItemStack stack, boolean treasure, CallbackInfoReturnable<List<EnchantmentInstance>> result) {
        if (!(stack.getItem() instanceof QuiverItem)) return;
        for (var id : List.of(QuiverEnchantments.RECOVERY, QuiverEnchantments.RANDOMIZER)) {
            BuiltInRegistries.ENCHANTMENT.getOptional(id).ifPresent(enchantment -> {
                if (power >= enchantment.getMinCost(1) && power <= enchantment.getMaxCost(1)
                        && result.getReturnValue().stream().noneMatch(candidate -> candidate.enchantment == enchantment)) {
                    result.getReturnValue().add(new EnchantmentInstance(enchantment, 1));
                }
            });
        }
    }
}
