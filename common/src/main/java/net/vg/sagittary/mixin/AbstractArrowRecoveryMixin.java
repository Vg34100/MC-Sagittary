package net.vg.sagittary.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.vg.sagittary.enchantment.QuiverEnchantments;
import net.vg.sagittary.item.QuiverItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowRecoveryMixin {
    @Redirect(
            method = "playerTouch",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;tryPickup(Lnet/minecraft/world/entity/player/Player;)Z")
    )
    private boolean sagittary$recoverIntoQuiver(AbstractArrow arrow, Player player) {
        ItemStack quiver = QuiverItem.findActiveQuiver(player);
        if (quiver.isEmpty() || !QuiverEnchantments.has(quiver, QuiverEnchantments.RECOVERY)) {
            return ((AbstractArrowAccessor) arrow).sagittary$tryPickup(player);
        }
        ItemStack pickup = ((AbstractArrowAccessor) arrow).sagittary$getPickupItem();
        if (pickup.isEmpty()) return ((AbstractArrowAccessor) arrow).sagittary$tryPickup(player);
        if (QuiverItem.storeArrows(quiver, pickup).isEmpty()) {
            // Returning true leaves the rest of vanilla playerTouch intact: it sends
            // the take-item packet, plays the normal pickup feedback and discards it.
            return true;
        }
        return ((AbstractArrowAccessor) arrow).sagittary$tryPickup(player);
    }
}
