package net.vg.sagittary.compat.jei;

import net.minecraft.world.item.ItemStack;
import net.vg.sagittary.component.ArrowComponent;

/**
 * Recipe display for an arrow component in JEI.
 */
public class ArrowComponentRecipe {
    private final ArrowComponent component;
    private final ItemStack componentItem;

    public ArrowComponentRecipe(ArrowComponent component) {
        this.component = component;
        this.componentItem = new ItemStack(component.getCraftingItem());
    }

    public ArrowComponent getComponent() {
        return component;
    }

    public ItemStack getComponentItem() {
        return componentItem;
    }
}
