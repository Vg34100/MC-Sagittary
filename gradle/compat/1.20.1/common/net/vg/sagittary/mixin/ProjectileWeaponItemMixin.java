package net.vg.sagittary.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.vg.sagittary.item.QuiverItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** 1.20.1 consumes in BowItem itself; ProjectileWeaponItem.useAmmo does not exist. */
@Mixin(BowItem.class)
public class ProjectileWeaponItemMixin {
    @Redirect(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void sagittary$consumeQuiver(ItemStack ammo, int count, ItemStack bow, Level level, LivingEntity shooter, int timeLeft) {
        if (shooter instanceof Player player) {
            ItemStack quiver = QuiverItem.findQuiverWithArrows(player);
            if (!quiver.isEmpty() && ItemStack.isSameItemSameTags(ammo, QuiverItem.peekArrow(quiver))) {
                if (!level.isClientSide) QuiverItem.removeOneArrow(quiver);
                return;
            }
        }
        ammo.shrink(count);
    }
}
