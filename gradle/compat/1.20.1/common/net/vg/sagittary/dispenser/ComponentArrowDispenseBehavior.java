package net.vg.sagittary.dispenser;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.vg.sagittary.entity.ComponentArrowEntity;
import net.vg.sagittary.item.ComponentArrowItem;

public class ComponentArrowDispenseBehavior extends AbstractProjectileDispenseBehavior {
    public ComponentArrowDispenseBehavior(ComponentArrowItem item) {}

    @Override protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
        var arrow = new ComponentArrowEntity(level, position.x(), position.y(), position.z(), stack.copyWithCount(1), ItemStack.EMPTY);
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        return arrow;
    }
}
