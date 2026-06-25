package net.vg.sagittary.fabric.mixin.client;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to make custom crossbow items render with the same special
 * first-person display behavior as vanilla crossbows.
 */
@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    /**
     * Override isChargedCrossbow to also accept any CrossbowItem instance.
     */
    @Inject(method = "isChargedCrossbow", at = @At("HEAD"), cancellable = true)
    private static void sagittary$isChargedCrossbow(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof CrossbowItem && !stack.is(Items.CROSSBOW)) {
            cir.setReturnValue(CrossbowItem.isCharged(stack));
        }
    }

    /**
     * Redirect ItemStack.is() calls in evaluateWhichHandsToRender to also match custom crossbows/bows.
     * Note: The bytecode uses is(Object) due to how the method is resolved.
     */
    @Redirect(
        method = "evaluateWhichHandsToRender",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z")
    )
    private static boolean sagittary$redirectEvaluateIs(ItemStack stack, Object item) {
        if (item == Items.CROSSBOW) {
            return stack.getItem() instanceof CrossbowItem;
        }
        if (item == Items.BOW) {
            return stack.getItem() instanceof BowItem;
        }
        if (item instanceof Item i) {
            return stack.is(i);
        }
        return false;
    }

    /**
     * Redirect ItemStack.is() calls in selectionUsingItemWhileHoldingBowLike to also match custom crossbows/bows.
     */
    @Redirect(
        method = "selectionUsingItemWhileHoldingBowLike",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z")
    )
    private static boolean sagittary$redirectSelectionIs(ItemStack stack, Object item) {
        if (item == Items.CROSSBOW) {
            return stack.getItem() instanceof CrossbowItem;
        }
        if (item == Items.BOW) {
            return stack.getItem() instanceof BowItem;
        }
        if (item instanceof Item i) {
            return stack.is(i);
        }
        return false;
    }

    /**
     * Redirect ItemStack.is() calls in renderArmWithItem to also match custom crossbows.
     * This is the critical one that enables the crossbow-specific transforms (centered position).
     */
    @Redirect(
        method = "renderArmWithItem",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z")
    )
    private boolean sagittary$redirectRenderArmIs(ItemStack stack, Object item) {
        if (item == Items.CROSSBOW) {
            return stack.getItem() instanceof CrossbowItem;
        }
        if (item == Items.BOW) {
            return stack.getItem() instanceof BowItem;
        }
        if (item instanceof Item i) {
            return stack.is(i);
        }
        return false;
    }
}
