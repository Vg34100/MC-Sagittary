package net.vg.sagittary.compat.jei;

import net.minecraft.world.item.ItemStack;
import net.vg.sagittary.component.ArrowComponent;
import net.vg.sagittary.item.ComponentArrowItem;

/**
 * Recipe display for fletching table in JEI.
 */
public class FletchingTableRecipe {
    private final ItemStack tip;
    private final ItemStack shaft;
    private final ItemStack fletching;
    private final ItemStack result;

    public FletchingTableRecipe(ArrowComponent tipComponent, ArrowComponent shaftComponent, ArrowComponent fletchingComponent) {
        this.tip = new ItemStack(tipComponent.getCraftingItem());
        this.shaft = new ItemStack(shaftComponent.getCraftingItem());
        this.fletching = new ItemStack(fletchingComponent.getCraftingItem());
        this.result = ComponentArrowItem.createComponentArrow(tipComponent, shaftComponent, fletchingComponent);
    }

    public ItemStack getTip() {
        return tip;
    }

    public ItemStack getShaft() {
        return shaft;
    }

    public ItemStack getFletching() {
        return fletching;
    }

    public ItemStack getResult() {
        return result;
    }
}
