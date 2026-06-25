package net.vg.sagittary.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.vg.sagittary.item.QuiverItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to make vanilla projectile weapons consume from quivers.
 */
@Mixin(ProjectileWeaponItem.class)
public class ProjectileWeaponItemMixin {

    /**
     * Override useAmmo to consume from quiver if the arrow came from there.
     */
    @Inject(method = "useAmmo", at = @At("HEAD"), cancellable = true)
    private static void sagittary$consumeFromQuiver(ItemStack weapon, ItemStack ammo, LivingEntity shooter, boolean isCreative, CallbackInfoReturnable<ItemStack> cir) {
        if (!(shooter instanceof Player player)) {
            return;
        }

        // Check if player has a quiver with this type of arrow
        ItemStack quiver = QuiverItem.findQuiverWithArrows(player);
        if (quiver.isEmpty()) {
            return;
        }

        ItemStack quiverArrow = QuiverItem.peekArrow(quiver);
        if (quiverArrow.isEmpty()) {
            return;
        }

        // Check if the ammo matches what's in the quiver (same item type)
        if (!ItemStack.isSameItemSameComponents(ammo, quiverArrow)) {
            return;
        }

        // This arrow is from the quiver - consume from quiver instead
        if (!isCreative && !player.hasInfiniteMaterials()) {
            QuiverItem.removeOneArrow(quiver);
        }

        // Return a copy of the ammo for the projectile
        cir.setReturnValue(ammo.copyWithCount(1));
    }
}
