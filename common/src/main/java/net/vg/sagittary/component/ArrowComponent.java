package net.vg.sagittary.component;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.vg.sagittary.component.effects.*;

import java.util.function.Supplier;

public enum ArrowComponent {
    // Tips
    FLINT_TIP("flint", ComponentType.TIP, Items.FLINT, 1.0f, 0.0f, FlintTipEffect::new),
    AMETHYST_TIP("amethyst", ComponentType.TIP, Items.AMETHYST_SHARD, 1.2f, 0.1f, AmethystTipEffect::new),
    COPPER_TIP("copper", ComponentType.TIP, Items.COPPER_INGOT, 1.0f, 0.0f, CopperTipEffect::new),
    
    // Shafts  
    STICK_SHAFT("stick", ComponentType.SHAFT, Items.STICK, 1.0f, 0.0f, StickShaftEffect::new),
    BAMBOO_SHAFT("bamboo", ComponentType.SHAFT, Items.BAMBOO, 1.0f, 0.90f, BambooShaftEffect::new),
    BLAZE_ROD_SHAFT("blaze_rod", ComponentType.SHAFT, Items.BLAZE_ROD, 1.1f, 0.0f, BlazeRodShaftEffect::new),
    
    // Fletching
    FEATHER_FLETCHING("feather", ComponentType.FLETCHING, Items.FEATHER, 1.0f, 0.0f, FeatherFletchingEffect::new),
    PAPER_FLETCHING("paper", ComponentType.FLETCHING, Items.PAPER, 1.0f, 0.1f, PaperFletchingEffect::new),
    PHANTOM_MEMBRANE_FLETCHING("phantom_membrane", ComponentType.FLETCHING, Items.PHANTOM_MEMBRANE, 1.0f, -0.05f, PhantomMembraneFletchingEffect::new);
    
    private final String materialName;
    private final ComponentType type;
    private final Item craftingItem;
    private final float damageModifier;
    private final float speedModifier;
    private final Supplier<ComponentEffect> effectSupplier;
    
    ArrowComponent(String materialName, ComponentType type, Item craftingItem, float damageModifier, float speedModifier, Supplier<ComponentEffect> effectSupplier) {
        this.materialName = materialName;
        this.type = type;
        this.craftingItem = craftingItem;
        this.damageModifier = damageModifier;
        this.speedModifier = speedModifier;
        this.effectSupplier = effectSupplier;
    }
    
    public String getMaterialName() {
        return materialName;
    }
    
    
    public ComponentType getType() {
        return type;
    }
    
    public Item getCraftingItem() {
        return craftingItem;
    }
    
    public float getDamageModifier() {
        return damageModifier;
    }
    
    public float getSpeedModifier() {
        return speedModifier;
    }
    
    public Component getDisplayName() {
        return Component.translatable("component.sagittary." + materialName + "_" + type.name().toLowerCase());
    }
    
    
    public ComponentEffect createEffect() {
        return effectSupplier.get();
    }

    
    public static ArrowComponent getByMaterialAndType(String material, ComponentType type) {
        for (ArrowComponent component : values()) {
            if (component.materialName.equals(material) && component.type == type) {
                return component;
            }
        }
        return getDefaultForType(type);
    }
    
    public static ArrowComponent getDefaultForType(ComponentType type) {
        return switch (type) {
            case TIP -> FLINT_TIP;
            case SHAFT -> STICK_SHAFT;
            case FLETCHING -> FEATHER_FLETCHING;
        };
    }
    
    public enum ComponentType {
        TIP,
        SHAFT, 
        FLETCHING
    }
}