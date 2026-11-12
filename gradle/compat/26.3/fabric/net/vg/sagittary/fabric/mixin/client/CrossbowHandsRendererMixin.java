package net.vg.sagittary.fabric.mixin.client;

import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class CrossbowHandsRendererMixin {
    @Redirect(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private boolean sagittary$crossbow(ItemStack stack, @Coerce Object item) {
        return item == Items.CROSSBOW ? stack.getItem() instanceof CrossbowItem : item instanceof net.minecraft.world.item.Item value && stack.is(value);
    }
}
