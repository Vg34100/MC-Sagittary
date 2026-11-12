package net.vg.sagittary.mixin.client;

//? if >=1.21 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//? }
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.vg.sagittary.client.SagittaryClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Old container screens have no bundle scroll handler; Creative overrides it too. */
@Mixin(MouseHandler.class)
public class QuiverInventoryScrollMixin {
    //? if >=1.21 {
    @WrapOperation(method = "onScroll", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDDD)Z"))
    private boolean sagittary$scrollQuiver(Screen screen, double x, double y,
                                           double horizontal, double vertical, Operation<Boolean> original) {
        return SagittaryClient.handleInventoryQuiverScroll(screen, vertical)
                || original.call(screen, x, y, horizontal, vertical);
    }
    //? } else {
    /*@Redirect(method = "onScroll", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDD)Z"))
    private boolean sagittary$scrollQuiver(Screen screen, double x, double y, double vertical) {
        return SagittaryClient.handleInventoryQuiverScroll(screen, vertical) || screen.mouseScrolled(x, y, vertical);
    }
    *///? }
}
