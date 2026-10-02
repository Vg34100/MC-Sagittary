package net.vg.sagittary.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
//? if >=26.1 {
import net.minecraft.world.item.component.TooltipDisplay;
//? }
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
    //? if >=26.1 {
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    //? } else {
    /*public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> lines, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, tooltipContext, lines, tooltipFlag);
        Consumer<Component> consumer = lines::add;
    *///? }
        
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
        //? if >=26.1 {
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(String.valueOf(getModelData(parts))), List.of()));
        //? } else {
        /*stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(getModelData(parts)));
        *///? }
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
        // Tips: vanilla materials (0-9), then Spelunkery materials (10-15).
        int tipIndex = switch (parts.getTipComponent()) {
            case FLINT_TIP -> 0;
            case AMETHYST_TIP -> 1;
            case COPPER_TIP -> 2;
            case SLIME_TIP -> 3;
            case GLOWSTONE_TIP -> 4;
            case ECHO_SHARD_TIP -> 5;
            case ENDER_PEARL_TIP -> 6;
            case IRON_TIP -> 7;
            case GOLD_TIP -> 8;
            case DIAMOND_TIP -> 9;
            case RUBY_TIP -> 10;
            case SAPPHIRE_TIP -> 11;
            case TOPAZ_TIP -> 12;
            case BRONZE_TIP -> 13;
            case ELECTRUM_TIP -> 14;
            case INVAR_TIP -> 15;
            default -> 0;
        };
        // Shafts: stick(0), bamboo(1), blaze_rod(2), breeze_rod(3), bone(4)
        int shaftIndex = switch (parts.getShaftComponent()) {
            case STICK_SHAFT -> 0;
            case BAMBOO_SHAFT -> 1;
            case BLAZE_ROD_SHAFT -> 2;
            case BREEZE_ROD_SHAFT -> 3;
            case BONE_SHAFT -> 4;
            default -> 0;
        };
        // Fletchings: feather(0), paper(1), phantom_membrane(2)
        int fletchingIndex = switch (parts.getFletchingComponent()) {
            case FEATHER_FLETCHING -> 0;
            case PAPER_FLETCHING -> 1;
            case PHANTOM_MEMBRANE_FLETCHING -> 2;
            default -> 0;
        };
        // Formula: 1 + (tipIndex * 15) + (shaftIndex * 3) + fletchingIndex
        // 16 tips * 5 shafts * 3 fletchings = 240 combinations (indices 1-240)
        return 1 + (tipIndex * 15) + (shaftIndex * 3) + fletchingIndex;
    }
}
