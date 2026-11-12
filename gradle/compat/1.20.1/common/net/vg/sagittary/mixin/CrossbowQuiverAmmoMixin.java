package net.vg.sagittary.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.vg.sagittary.item.QuiverItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CrossbowItem.class)
public class CrossbowQuiverAmmoMixin {
    @Redirect(method = "loadProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;split(I)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack sagittary$loadQuiver(ItemStack ammo, int count, LivingEntity shooter, ItemStack crossbow, ItemStack original, boolean copy, boolean creative) {
        if (shooter instanceof Player player) {
            ItemStack quiver = QuiverItem.findQuiverWithArrows(player);
            if (!quiver.isEmpty() && ItemStack.isSameItemSameTags(ammo, QuiverItem.peekArrow(quiver))) {
                return QuiverItem.removeOneArrow(quiver);
            }
        }
        return ammo.split(count);
    }
}
