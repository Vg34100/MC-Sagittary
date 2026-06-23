package net.vg.sagittary.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

import java.util.List;
import java.util.function.Consumer;
import net.vg.sagittary.component.ArrowComponent;
import net.vg.sagittary.component.ArrowParts;
import net.vg.sagittary.entity.ComponentArrowEntity;
import net.vg.sagittary.registry.ObjectRegistry;

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
        return Component.translatable("item.sagittary.component_arrow." + tip.getMaterialName());
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
        
        ArrowParts parts = getArrowPartsFromStack(stack);
        consumer.accept(Component.literal(""));
        consumer.accept(Component.translatable("tooltip.sagittary.component_arrow").withStyle(style -> style.withColor(0x9A7FBF)));
        
        ArrowComponent tip = parts.getTipComponent();
        ArrowComponent shaft = parts.getShaftComponent();
        ArrowComponent fletching = parts.getFletchingComponent();
        
        consumer.accept(Component.translatable("tooltip.sagittary.tip").append(": ").append(tip.getDisplayName()));
        consumer.accept(Component.translatable("tooltip.sagittary.shaft").append(": ").append(shaft.getDisplayName()));
        consumer.accept(Component.translatable("tooltip.sagittary.fletching").append(": ").append(fletching.getDisplayName()));
    }
    
    public static ItemStack createComponentArrow(ArrowComponent tip, ArrowComponent shaft, ArrowComponent fletching) {
        ItemStack stack = new ItemStack(ObjectRegistry.COMPONENT_ARROW_ITEM.get());
        setArrowPartsOnStack(stack, ArrowParts.of(tip, shaft, fletching));
        return stack;
    }
    
    public static void setArrowPartsOnStack(ItemStack stack, ArrowParts parts) {
        stack.set(ObjectRegistry.ARROW_PARTS.get(), parts);
        // 26.1.2: CustomModelData(floats, flags, strings, colors) - select property reads from strings list
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(String.valueOf(getModelData(parts))), List.of()));
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
