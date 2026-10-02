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
import net.vg.sagittary.component.ArrowComponent;

/**
 * JEI category showing all arrow components.
 * Click on a component item to see its description via ingredient info.
 */
public class ArrowComponentCategory implements IRecipeCategory<ArrowComponentRecipe> {
    public static final RecipeType<ArrowComponentRecipe> TYPE = RecipeType.create(Sagittary.MOD_ID, "arrow_components", ArrowComponentRecipe.class);
    public static final Identifier UID = Identifier.fromNamespaceAndPath(Sagittary.MOD_ID, "arrow_components");

    private static final int WIDTH = 18;
    private static final int HEIGHT = 18;

    private final IDrawable icon;
    private final Component title;
    //? if <1.21.1 {
    /*private final IDrawable background;
    *///? }

    public ArrowComponentCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Items.FLETCHING_TABLE));
        this.title = Component.translatable("jei.sagittary.arrow_components.title");
        //? if <1.21.1 {
        /*this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        *///? }
    }

    //? if <1.21.1 {
    /*@Override
    public IDrawable getBackground() { return background; }
    *///? }

    @Override
    public RecipeType<ArrowComponentRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, ArrowComponentRecipe recipe, IFocusGroup focuses) {
        ArrowComponent component = recipe.getComponent();

        // Build the full component key (e.g., "ender_pearl_tip" instead of just "ender_pearl")
        String componentKey = component.getMaterialName() + "_" + component.getType().name().toLowerCase();

        // Show the component item - clicking it shows ingredient info with description
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addItemStack(recipe.getComponentItem())
                //? if >1.21 {
                .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                //? } else {
                /*.addTooltipCallback((recipeSlotView, tooltip) -> {
                *///? }
                    tooltip.add(Component.translatable("jei.sagittary.type." + component.getType().name().toLowerCase())
                            .withStyle(style -> style.withColor(0xAAAAAA)));
                    tooltip.add(Component.translatable("jei.sagittary.component." + componentKey + ".desc")
                            .withStyle(style -> style.withColor(0xCCCCCC)));
                });
    }
}
