package net.vg.sagittary.client;

import com.mojang.serialization.DataResult;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.vg.sagittary.mixin.BundleContentsAccessor;
import net.vg.sagittary.mixin.BundleContentsMutableAccessor;
import org.apache.commons.lang3.math.Fraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders the QuiverTooltip by delegating to ClientBundleTooltip
 * but with corrected weight/fullness for 256 capacity.
 */
public class QuiverTooltipRenderer implements ClientTooltipComponent {
    private final ClientBundleTooltip delegate;

    public QuiverTooltipRenderer(QuiverTooltip tooltip) {
        // Build BundleContents with corrected weight (fullness based on 256 capacity)
        BundleContents correctedContents = buildContentsWithCorrectWeight(tooltip.items(), tooltip.selectedIndex(), tooltip.fullness());
        this.delegate = new ClientBundleTooltip(correctedContents);
    }

    private static BundleContents buildContentsWithCorrectWeight(List<ItemStack> items, int selectedIndex, float fullness) {
        // Convert ItemStacks to ItemStackTemplates
        List<ItemStackTemplate> templates = new ArrayList<>();
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                templates.add(ItemStackTemplate.fromNonEmptyStack(item));
            }
        }

        // Create BundleContents normally - this will compute weight incorrectly
        BundleContents contents = new BundleContents(templates);

        // Now override the weight and selectedItem with our correct values
        // fullness is 0-1 based on 256 capacity
        Fraction correctWeight = Fraction.getFraction(Math.round(fullness * 100), 100);
        BundleContentsAccessor accessor = (BundleContentsAccessor) (Object) contents;
        accessor.sagittary$setWeight(() -> DataResult.success(correctWeight));

        // Set selected item
        if (selectedIndex >= 0 && selectedIndex < templates.size()) {
            accessor.sagittary$setSelectedItem(selectedIndex);
        }

        return contents;
    }

    @Override
    public int getHeight(Font font) {
        return delegate.getHeight(font);
    }

    @Override
    public int getWidth(Font font) {
        return delegate.getWidth(font);
    }

    @Override
    public boolean showTooltipWithItemInHand() {
        return delegate.showTooltipWithItemInHand();
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        delegate.extractImage(font, x, y, width, height, graphics);
    }
}
