//? if >=26.1 {
package net.vg.sagittary.mixin.client;

import net.minecraft.client.gui.BundleMouseActions;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.vg.sagittary.item.QuiverItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleMouseActions.class)
public class BundleMouseActionsMixin {

    /**
     * Quivers own their interaction and selection packets. Vanilla bundle mouse
     * actions must not write/reset another selection state (or send bundle packets).
     */
    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private void sagittary$ownQuiverSelection(Slot slot, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = slot.getItem();
        if (stack.getItem() instanceof QuiverItem) {
            cir.setReturnValue(false);
        }
    }
}
//? }
