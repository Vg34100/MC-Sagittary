package net.vg.amethyst.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;
import net.vg.amethyst.component.ArrowComponent;
import net.vg.amethyst.component.ArrowParts;
import net.vg.amethyst.entity.ComponentArrowEntity;
import net.vg.amethyst.registry.ObjectRegistry;

public class ComponentArrowItem extends ArrowItem {
    
    public ComponentArrowItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter, ItemStack bow) {
        return new ComponentArrowEntity(level, shooter, stack, bow);
    }

    @Override
    public Component getName(ItemStack stack) {
        ArrowComponent tip = getTipFromStack(stack);
        return Component.translatable("item.amethyst.component_arrow." + tip.getMaterialName());
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, tooltipContext, tooltipComponents, tooltipFlag);
        
        ArrowParts parts = getArrowPartsFromStack(stack);
        tooltipComponents.add(Component.literal(""));
        tooltipComponents.add(Component.translatable("tooltip.amethyst.component_arrow").withStyle(style -> style.withColor(0x9A7FBF)));
        
        ArrowComponent tip = parts.getTipComponent();
        ArrowComponent shaft = parts.getShaftComponent();
        ArrowComponent fletching = parts.getFletchingComponent();
        
        tooltipComponents.add(Component.translatable("tooltip.amethyst.tip").append(": ").append(tip.getDisplayName()));
        tooltipComponents.add(Component.translatable("tooltip.amethyst.shaft").append(": ").append(shaft.getDisplayName()));
        tooltipComponents.add(Component.translatable("tooltip.amethyst.fletching").append(": ").append(fletching.getDisplayName()));
    }
    
    public static ItemStack createComponentArrow(ArrowComponent tip, ArrowComponent shaft, ArrowComponent fletching) {
        ItemStack stack = new ItemStack(ObjectRegistry.COMPONENT_ARROW_ITEM.get());
        setArrowPartsOnStack(stack, ArrowParts.of(tip, shaft, fletching));
        return stack;
    }
    
    public static void setArrowPartsOnStack(ItemStack stack, ArrowParts parts) {
        stack.set(ObjectRegistry.ARROW_PARTS.get(), parts);
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(getModelData(parts)));
    }
    
    public static ArrowParts getArrowPartsFromStack(ItemStack stack) {
        ArrowParts parts = stack.get(ObjectRegistry.ARROW_PARTS.get());
        return parts != null ? parts : ArrowParts.DEFAULT;
    }
    
    public static ArrowComponent getTipFromStack(ItemStack stack) {
        return getArrowPartsFromStack(stack).getTipComponent();
    }
    
    public static ArrowComponent getShaftFromStack(ItemStack stack) {
        return getArrowPartsFromStack(stack).getShaftComponent();
    }
    
    public static ArrowComponent getFletchingFromStack(ItemStack stack) {
        return getArrowPartsFromStack(stack).getFletchingComponent();
    }

    private static int getModelData(ArrowParts parts) {
        int tipIndex = switch (parts.getTipComponent()) {
            case FLINT_TIP -> 0;
            case AMETHYST_TIP -> 1;
            case COPPER_TIP -> 2;
            default -> 0;
        };
        int shaftIndex = switch (parts.getShaftComponent()) {
            case STICK_SHAFT -> 0;
            case BAMBOO_SHAFT -> 1;
            case BLAZE_ROD_SHAFT -> 2;
            default -> 0;
        };
        int fletchingIndex = switch (parts.getFletchingComponent()) {
            case FEATHER_FLETCHING -> 0;
            case PAPER_FLETCHING -> 1;
            case PHANTOM_MEMBRANE_FLETCHING -> 2;
            default -> 0;
        };
        return 1 + (tipIndex * 9) + (shaftIndex * 3) + fletchingIndex;
    }
}
