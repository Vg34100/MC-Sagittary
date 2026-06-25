package net.vg.sagittary.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.vg.sagittary.item.QuiverItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to make vanilla bows and crossbows check quivers for arrows.
 */
@Mixin(Player.class)
public class PlayerProjectileMixin {

    /**
     * Check quivers before falling back to vanilla inventory search.
     */
    @Inject(method = "getProjectile", at = @At("HEAD"), cancellable = true)
    private void sagittary$checkQuiverForProjectile(ItemStack weapon, CallbackInfoReturnable<ItemStack> cir) {
        Player player = (Player) (Object) this;

        // Find a quiver with arrows
        ItemStack quiver = QuiverItem.findQuiverWithArrows(player);
        if (!quiver.isEmpty()) {
            ItemStack arrow = QuiverItem.peekArrow(quiver);
            if (!arrow.isEmpty()) {
                // Check if this arrow type is supported by the weapon
                if (weapon.getItem() instanceof net.minecraft.world.item.ProjectileWeaponItem projectileWeapon) {
                    if (projectileWeapon.getAllSupportedProjectiles().test(arrow)) {
                        cir.setReturnValue(arrow);
                    }
                }
            }
        }
    }
}
