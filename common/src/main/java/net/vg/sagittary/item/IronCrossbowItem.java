package net.vg.sagittary.item;

import net.minecraft.world.item.CrossbowItem;

/**
 * Iron Crossbow - A more durable crossbow with increased projectile range.
 * Durability: 652 (1.5x regular crossbow's 465)
 * Range bonus: Increased projectile range
 */
public class IronCrossbowItem extends CrossbowItem {

    public IronCrossbowItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 12; // Higher than regular crossbow (8)
    }
}
