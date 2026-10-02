package net.vg.sagittary.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.vg.sagittary.Sagittary;

/**
 * JEI category for fletching table recipes (tip + shaft + fletching = arrow).
 */
public class FletchingTableCategory implements IRecipeCategory<FletchingTableRecipe> {
    public static final RecipeType<FletchingTableRecipe> TYPE = RecipeType.create(Sagittary.MOD_ID, "fletching_table", FletchingTableRecipe.class);
    public static final Identifier UID = Identifier.fromNamespaceAndPath(Sagittary.MOD_ID, "fletching_table");

    private static final int WIDTH = 120;
    private static final int HEIGHT = 36;

    private final IDrawable icon;
    private final Component title;
    //? if <1.21.1 {
    /*private final IDrawable background;
    *///? }

    public FletchingTableCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Items.FLETCHING_TABLE));
        this.title = Component.translatable("jei.sagittary.fletching_table.title");
        //? if <1.21.1 {
        /*this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        *///? }
    }

    //? if <1.21.1 {
    /*@Override
    public IDrawable getBackground() { return background; }
    *///? }

    @Override
    public RecipeType<FletchingTableRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FletchingTableRecipe recipe, IFocusGroup focuses) {
        // Input slots: tip, shaft, fletching
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 10)
                .addItemStack(recipe.getTip());
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 10)
                .addItemStack(recipe.getShaft());
        builder.addSlot(RecipeIngredientRole.INPUT, 41, 10)
                .addItemStack(recipe.getFletching());

        // Output slot: component arrow
        builder.addSlot(RecipeIngredientRole.OUTPUT, 99, 10)
                .addItemStack(recipe.getResult());
    }
}
