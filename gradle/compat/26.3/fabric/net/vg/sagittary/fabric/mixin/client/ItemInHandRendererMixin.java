package net.vg.sagittary.fabric.mixin.client;

import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 26.3 separates hand selection/extraction from rendering. */
@Mixin(FirstPersonHandsAndItems.class)
public class ItemInHandRendererMixin {
    @Inject(method = "isChargedCrossbow", at = @At("HEAD"), cancellable = true)
    private static void sagittary$charged(ItemStack stack, CallbackInfoReturnable<Boolean> result) {
        if (stack.getItem() instanceof CrossbowItem) result.setReturnValue(CrossbowItem.isCharged(stack));
    }

    @Redirect(method = "evaluateWhichHandsToRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private static boolean sagittary$bowLike(ItemStack stack, @Coerce Object item) {
        if (item == Items.BOW) return stack.getItem() instanceof BowItem;
        if (item == Items.CROSSBOW) return stack.getItem() instanceof CrossbowItem;
        return item instanceof net.minecraft.world.item.Item value && stack.is(value);
    }
}
