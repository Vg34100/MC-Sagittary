package net.vg.sagittary.mixin;

import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface AbstractArrowAccessor {
    @Invoker("getPickupItem")
    ItemStack sagittary$getPickupItem();

    @Invoker("tryPickup")
    boolean sagittary$tryPickup(Player player);
}
