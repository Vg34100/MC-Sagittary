package net.vg.sagittary.client;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
//? if >=1.21 {
import net.minecraft.world.item.component.BundleContents;
//? }

import java.util.ArrayList;
import java.util.List;

/**
 * Custom tooltip component for quivers that shows correct fullness based on 256 capacity.
 */
public record QuiverTooltip(List<ItemStack> items, int selectedIndex, float fullness) implements TooltipComponent {

    //? if >=1.21 {
    public static QuiverTooltip fromContents(BundleContents contents, int selectedIndex, int maxCapacity) {
        List<ItemStack> items = new ArrayList<>();
        net.vg.sagittary.item.QuiverItem.streamContents(contents).forEach(items::add);

        int totalCount = items.stream().mapToInt(ItemStack::getCount).sum();
        float fullness = (float) totalCount / maxCapacity;

        return new QuiverTooltip(items, selectedIndex, fullness);
    }
    //? }
}
