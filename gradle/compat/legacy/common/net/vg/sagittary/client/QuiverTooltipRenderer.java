package net.vg.sagittary.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.component.BundleContents;
import net.vg.sagittary.mixin.BundleContentsAccessor;
import org.apache.commons.lang3.math.Fraction;

/** 1.21 tooltips contain ItemStacks and a Fraction, and render immediately. */
public class QuiverTooltipRenderer implements ClientTooltipComponent {
    private final ClientBundleTooltip delegate;
    private final int selectedIndex;

    public QuiverTooltipRenderer(QuiverTooltip tooltip) {
        selectedIndex = tooltip.selectedIndex();
        BundleContents contents = new BundleContents(tooltip.items());
        ((BundleContentsAccessor) (Object) contents).sagittary$setWeight(
                Fraction.getFraction(Math.round(tooltip.fullness() * 100), 100));
        delegate = new ClientBundleTooltip(contents);
    }

    @Override
    public int getHeight() { return delegate.getHeight(); }

    @Override
    public int getWidth(Font font) { return delegate.getWidth(font); }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        delegate.renderImage(font, x, y, graphics);
        if (selectedIndex >= 0) {
            // The 1.21 bundle grid has 18x20 cells and a one-pixel border.
            int columns = (delegate.getWidth(font) - 2) / 18;
            graphics.renderOutline(x + 1 + selectedIndex % columns * 18,
                    y + 1 + selectedIndex / columns * 20, 18, 20, 0xFFFFD36A);
        }
    }
}
