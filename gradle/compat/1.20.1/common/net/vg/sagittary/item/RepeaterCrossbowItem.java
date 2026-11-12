package net.vg.sagittary.item;

import net.minecraft.world.item.ItemStack;

public class RepeaterCrossbowItem extends net.vg.sagittary.compat.LegacyCrossbowItem {
    public static final int MAGAZINE_SIZE = 5;
    public RepeaterCrossbowItem(Properties properties) { super(properties, MAGAZINE_SIZE, 8); }
    public static int getRemainingShots(ItemStack stack) { return projectiles(stack).size(); }
}
