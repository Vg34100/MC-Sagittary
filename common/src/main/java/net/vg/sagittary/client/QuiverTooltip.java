package net.vg.sagittary.client;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom tooltip component for quivers that shows correct fullness based on 256 capacity.
 */
public record QuiverTooltip(List<ItemStack> items, int selectedIndex, float fullness) implements TooltipComponent {

    public static QuiverTooltip fromContents(BundleContents contents, int selectedIndex, int maxCapacity) {
        List<ItemStack> items = new ArrayList<>();
        contents.itemCopyStream().forEach(items::add);

        int totalCount = items.stream().mapToInt(ItemStack::getCount).sum();
        float fullness = (float) totalCount / maxCapacity;

        return new QuiverTooltip(items, selectedIndex, fullness);
    }
}
