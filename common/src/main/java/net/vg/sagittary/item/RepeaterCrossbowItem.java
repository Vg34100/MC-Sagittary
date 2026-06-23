package net.vg.sagittary.item;

import net.minecraft.world.item.CrossbowItem;

/**
 * Repeater Crossbow - A rapid-fire crossbow with faster reload speed.
 * Fires faster but with slightly reduced damage.
 * Uses standard crossbow mechanics but with reduced charge time.
 */
public class RepeaterCrossbowItem extends CrossbowItem {
    // Charge time is reduced by having faster charge ticks
    private static final float CHARGE_TIME_MODIFIER = 0.5f; // 50% faster charging

    public RepeaterCrossbowItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 8; // Same as regular crossbow
    }

    /**
     * Get the charge duration modifier for this crossbow.
     * This can be used by mixins or events to modify charge speed.
     */
    public float getChargeTimeModifier() {
        return CHARGE_TIME_MODIFIER;
    }
}
