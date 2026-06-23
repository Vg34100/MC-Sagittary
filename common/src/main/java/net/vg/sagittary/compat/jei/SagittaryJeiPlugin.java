package net.vg.sagittary.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.vg.sagittary.Sagittary;
import net.vg.sagittary.component.ArrowComponent;
import net.vg.sagittary.item.ComponentArrowItem;
import net.vg.sagittary.registry.ObjectRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * JEI plugin for Sagittary mod.
 * Provides information about arrow components and their effects.
 */
@JeiPlugin
public class SagittaryJeiPlugin implements IModPlugin {
    private static final Identifier PLUGIN_ID = Identifier.fromNamespaceAndPath(Sagittary.MOD_ID, "jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Add information pages for all component arrow variants
        addComponentArrowInfo(registration);

        // Add information for special items
        addQuiverInfo(registration);
        addBowInfo(registration);
    }

    private void addComponentArrowInfo(IRecipeRegistration registration) {
        // Create info entries for each tip type
        for (ArrowComponent tip : ArrowComponent.values()) {
            if (tip.getType() != ArrowComponent.ComponentType.TIP) continue;

            ItemStack arrow = ComponentArrowItem.createComponentArrow(
                    tip,
                    ArrowComponent.STICK_SHAFT,
                    ArrowComponent.FEATHER_FLETCHING);

            List<Component> info = new ArrayList<>();
            info.add(Component.translatable("jei.sagittary.component_arrow.title"));
            info.add(Component.empty());
            info.add(Component.translatable("jei.sagittary.tip." + tip.getMaterialName() + ".desc"));
            info.add(Component.empty());
            info.add(Component.translatable("jei.sagittary.damage_modifier", String.format("%.1fx", tip.getDamageModifier())));

            registration.addIngredientInfo(arrow, VanillaTypes.ITEM_STACK, info.toArray(new Component[0]));
        }
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

        // Compound Bow
        ItemStack compoundBow = new ItemStack(ObjectRegistry.COMPOUND_BOW_ITEM.get());
        registration.addIngredientInfo(
                compoundBow,
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.sagittary.compound_bow.desc")
        );

        // Repeater Crossbow
        ItemStack repeaterCrossbow = new ItemStack(ObjectRegistry.REPEATER_CROSSBOW_ITEM.get());
        registration.addIngredientInfo(
                repeaterCrossbow,
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.sagittary.repeater_crossbow.desc")
        );
    }
}
