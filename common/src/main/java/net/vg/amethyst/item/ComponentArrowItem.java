package net.vg.amethyst.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

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
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
        
        ArrowParts parts = getArrowPartsFromStack(stack);
        consumer.accept(Component.literal(""));
        consumer.accept(Component.translatable("tooltip.amethyst.component_arrow").withStyle(style -> style.withColor(0x9A7FBF)));
        
        ArrowComponent tip = parts.getTipComponent();
        ArrowComponent shaft = parts.getShaftComponent();
        ArrowComponent fletching = parts.getFletchingComponent();
        
        consumer.accept(Component.translatable("tooltip.amethyst.tip").append(": ").append(tip.getDisplayName()));
        consumer.accept(Component.translatable("tooltip.amethyst.shaft").append(": ").append(shaft.getDisplayName()));
        consumer.accept(Component.translatable("tooltip.amethyst.fletching").append(": ").append(fletching.getDisplayName()));
    }
    
    public static ItemStack createComponentArrow(ArrowComponent tip, ArrowComponent shaft, ArrowComponent fletching) {
        ItemStack stack = new ItemStack(ObjectRegistry.COMPONENT_ARROW_ITEM.get());
        setArrowPartsOnStack(stack, ArrowParts.of(tip, shaft, fletching));
        return stack;
    }
    
    public static void setArrowPartsOnStack(ItemStack stack, ArrowParts parts) {
        stack.set(ObjectRegistry.ARROW_PARTS.get(), parts);
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
}