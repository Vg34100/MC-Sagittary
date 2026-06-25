package net.vg.sagittary.mixin.client;

import net.minecraft.client.gui.BundleMouseActions;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.vg.sagittary.item.QuiverItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BundleMouseActions.class)
public class BundleMouseActionsMixin {

    /**
     * Prevent quiver selection from being reset when stop hovering.
     * Vanilla bundles reset selection because it's only visual.
     * For quivers, we want selection to persist for shooting.
     */
    @Inject(method = "onStopHovering", at = @At("HEAD"), cancellable = true)
    private void sagittary$preventQuiverSelectionReset(Slot slot, CallbackInfo ci) {
        ItemStack stack = slot.getItem();
        if (stack.getItem() instanceof QuiverItem) {
            ci.cancel(); // Don't reset selection for quivers
        }
    }
}
