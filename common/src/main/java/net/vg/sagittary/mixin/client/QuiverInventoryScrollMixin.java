package net.vg.sagittary.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.vg.sagittary.client.SagittaryClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Old container screens have no bundle scroll handler; Creative overrides it too. */
@Mixin(MouseHandler.class)
public class QuiverInventoryScrollMixin {
    @WrapOperation(method = "onScroll", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDDD)Z"))
    private boolean sagittary$scrollQuiver(Screen screen, double x, double y,
                                           double horizontal, double vertical, Operation<Boolean> original) {
        return SagittaryClient.handleInventoryQuiverScroll(screen, vertical)
                || original.call(screen, x, y, horizontal, vertical);
    }
}
