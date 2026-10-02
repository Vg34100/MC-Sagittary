package net.vg.sagittary.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.vg.sagittary.Sagittary;
import net.vg.sagittary.component.ArrowComponent;
import net.vg.sagittary.item.ComponentArrowItem;
import net.vg.sagittary.registry.ObjectRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * JEI plugin for Sagittary mod.
 * Provides:
 * - Arrow component info category (accessible via fletching table or component items)
 * - Fletching table recipe category (tip + shaft + fletching = arrow)
 * - Item info for special items
 */
@JeiPlugin
public class SagittaryJeiPlugin implements IModPlugin {
    private static final Identifier PLUGIN_ID = Identifier.fromNamespaceAndPath(Sagittary.MOD_ID, "jei_plugin");
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("SagittaryJEI");

    private static List<ArrowComponent> availableComponents() {
        // Optional Spelunkery materials resolve to air when it is absent.
        // JEI rejects empty catalysts and cannot craft recipes using them.
        return java.util.Arrays.stream(ArrowComponent.values()).filter(component -> {
            var item = component.getCraftingItem();
            return item != null && item != Items.AIR;
        }).toList();
    }

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        LOGGER.info("Sagittary JEI: Registering categories");
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        // Register arrow component info category
        registration.addRecipeCategories(new ArrowComponentCategory(guiHelper));
        LOGGER.info("Sagittary JEI: Registered ArrowComponentCategory");

        // Register fletching table recipe category
        registration.addRecipeCategories(new FletchingTableCategory(guiHelper));
        LOGGER.info("Sagittary JEI: Registered FletchingTableCategory");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        LOGGER.info("Sagittary JEI: Registering recipes");
        List<ArrowComponent> components = availableComponents();

        // Register arrow component recipes (info entries)
        List<ArrowComponentRecipe> componentRecipes = new ArrayList<>();
        for (ArrowComponent component : components) {
            componentRecipes.add(new ArrowComponentRecipe(component));
        }
        registration.addRecipes(ArrowComponentCategory.TYPE, componentRecipes);
        LOGGER.info("Sagittary JEI: Added {} arrow component recipes", componentRecipes.size());

        // Register fletching table recipes (all valid combinations)
        List<FletchingTableRecipe> fletchingRecipes = new ArrayList<>();
        for (ArrowComponent tip : components) {
            if (tip.getType() != ArrowComponent.ComponentType.TIP) continue;
            for (ArrowComponent shaft : components) {
                if (shaft.getType() != ArrowComponent.ComponentType.SHAFT) continue;
                for (ArrowComponent fletching : components) {
                    if (fletching.getType() != ArrowComponent.ComponentType.FLETCHING) continue;
                    fletchingRecipes.add(new FletchingTableRecipe(tip, shaft, fletching));
                }
            }
        }
        registration.addRecipes(FletchingTableCategory.TYPE, fletchingRecipes);

        // Add info for special items
        addQuiverInfo(registration);
        addBowInfo(registration);
        addComponentInfo(registration);
    }

    private void addComponentInfo(IRecipeRegistration registration) {
        // Add ingredient info for each component item
        for (ArrowComponent component : availableComponents()) {
            ItemStack componentItem = new ItemStack(component.getCraftingItem());

            // Build the full component key (e.g., "ender_pearl_tip" instead of just "ender_pearl")
            String componentKey = component.getMaterialName() + "_" + component.getType().name().toLowerCase();

            // Build description with type and effect
            Component typeText = Component.translatable("jei.sagittary.type." + component.getType().name().toLowerCase());
            Component descText = Component.translatable("jei.sagittary.component." + componentKey + ".desc");

            registration.addIngredientInfo(
                    componentItem,
                    VanillaTypes.ITEM_STACK,
                    typeText,
                    Component.empty(),
                    descText
            );
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Fletching table opens both categories
        registration.addRecipeCatalyst(new ItemStack(Items.FLETCHING_TABLE), ArrowComponentCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.FLETCHING_TABLE), FletchingTableCategory.TYPE);

        // All component items can access the arrow component category
        for (ArrowComponent component : availableComponents()) {
            registration.addRecipeCatalyst(new ItemStack(component.getCraftingItem()), ArrowComponentCategory.TYPE);
        }

        // Component arrow can access fletching table category
        registration.addRecipeCatalyst(new ItemStack(ObjectRegistry.COMPONENT_ARROW_ITEM.get()), FletchingTableCategory.TYPE);
    }

    private void addQuiverInfo(IRecipeRegistration registration) {
        ItemStack quiver = new ItemStack(ObjectRegistry.QUIVER_ITEM.get());
        registration.addIngredientInfo(
                quiver,
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.sagittary.quiver.desc")
        );
    }

    private void addBowInfo(IRecipeRegistration registration) {
        // Iron Bow
        ItemStack ironBow = new ItemStack(ObjectRegistry.IRON_BOW_ITEM.get());
        registration.addIngredientInfo(
                ironBow,
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.sagittary.iron_bow.desc")
        );

        // Iron Crossbow
        ItemStack ironCrossbow = new ItemStack(ObjectRegistry.IRON_CROSSBOW_ITEM.get());
        registration.addIngredientInfo(
                ironCrossbow,
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.sagittary.iron_crossbow.desc")
        );

        // Compound Bow (structure loot)
        ItemStack compoundBow = new ItemStack(ObjectRegistry.COMPOUND_BOW_ITEM.get());
        registration.addIngredientInfo(
                compoundBow,
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.sagittary.compound_bow.desc")
        );

        // Repeater Crossbow (structure loot)
        ItemStack repeaterCrossbow = new ItemStack(ObjectRegistry.REPEATER_CROSSBOW_ITEM.get());
        registration.addIngredientInfo(
                repeaterCrossbow,
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.sagittary.repeater_crossbow.desc")
        );
    }
}
