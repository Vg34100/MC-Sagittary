package net.vg.sagittary.component;

import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.vg.sagittary.component.effects.*;

import java.util.function.Supplier;

public enum ArrowComponent {
    // Tips
    FLINT_TIP("flint", ComponentType.TIP, Items.FLINT, 1.0f, 0.0f, FlintTipEffect::new),
    AMETHYST_TIP("amethyst", ComponentType.TIP, Items.AMETHYST_SHARD, 1.2f, 0.1f, AmethystTipEffect::new),
    COPPER_TIP("copper", ComponentType.TIP, Items.COPPER_INGOT, 1.0f, 0.0f, CopperTipEffect::new),
    SLIME_TIP("slime", ComponentType.TIP, Items.SLIME_BALL, 0.8f, -0.1f, SlimeTipEffect::new),
    GLOWSTONE_TIP("glowstone", ComponentType.TIP, Items.GLOWSTONE_DUST, 1.0f, 0.0f, GlowstoneTipEffect::new),
    ECHO_SHARD_TIP("echo_shard", ComponentType.TIP, Items.ECHO_SHARD, 1.3f, 0.0f, EchoShardTipEffect::new),
    ENDER_PEARL_TIP("ender_pearl", ComponentType.TIP, Items.ENDER_PEARL, 1.0f, 0.0f, EnderPearlTipEffect::new),
    IRON_TIP("iron", ComponentType.TIP, Items.IRON_NUGGET, 1.4f, 0.0f, IronTipEffect::new),
    GOLD_TIP("gold", ComponentType.TIP, Items.GOLD_NUGGET, 1.1f, 0.15f, GoldTipEffect::new),
    DIAMOND_TIP("diamond", ComponentType.TIP, Items.DIAMOND, 1.6f, 0.0f, DiamondTipEffect::new),
    RUBY_TIP("ruby", ComponentType.TIP, "spelunkery:ruby", 1.25f, 0.0f, SpelunkeryTipEffects::ruby),
    SAPPHIRE_TIP("sapphire", ComponentType.TIP, "spelunkery:sapphire", 1.05f, 0.0f, SpelunkeryTipEffects::sapphire),
    TOPAZ_TIP("topaz", ComponentType.TIP, "spelunkery:topaz_shard", 0.9f, 0.0f, SpelunkeryTipEffects::topaz),
    BRONZE_TIP("bronze", ComponentType.TIP, "spelunkery:bronze_ingot", 1.1f, 0.0f, SpelunkeryTipEffects::bronze),
    ELECTRUM_TIP("electrum", ComponentType.TIP, "spelunkery:electrum_ingot", 1.15f, 0.05f, SpelunkeryTipEffects::electrum),
    INVAR_TIP("invar", ComponentType.TIP, "spelunkery:invar_ingot", 1.45f, -0.1f, SpelunkeryTipEffects::invar),

    // Shafts
    STICK_SHAFT("stick", ComponentType.SHAFT, Items.STICK, 1.0f, 0.0f, StickShaftEffect::new),
    BAMBOO_SHAFT("bamboo", ComponentType.SHAFT, Items.BAMBOO, 1.0f, 0.90f, BambooShaftEffect::new),
    BLAZE_ROD_SHAFT("blaze_rod", ComponentType.SHAFT, Items.BLAZE_ROD, 1.1f, 0.0f, BlazeRodShaftEffect::new),
    BREEZE_ROD_SHAFT("breeze_rod", ComponentType.SHAFT, Items.BREEZE_ROD, 0.9f, 0.2f, BreezeRodShaftEffect::new),
    BONE_SHAFT("bone", ComponentType.SHAFT, Items.BONE, 1.15f, 0.0f, BoneShaftEffect::new),

    // Fletching
    FEATHER_FLETCHING("feather", ComponentType.FLETCHING, Items.FEATHER, 1.0f, 0.0f, FeatherFletchingEffect::new),
    PAPER_FLETCHING("paper", ComponentType.FLETCHING, Items.PAPER, 1.0f, 0.1f, PaperFletchingEffect::new),
    PHANTOM_MEMBRANE_FLETCHING("phantom_membrane", ComponentType.FLETCHING, Items.PHANTOM_MEMBRANE, 1.0f, -0.05f, PhantomMembraneFletchingEffect::new);
    
    private final String materialName;
    private final ComponentType type;
    private final Item craftingItem;
    private final Identifier optionalCraftingItemId;
    private final float damageModifier;
    private final float speedModifier;
    private final Supplier<ComponentEffect> effectSupplier;
    
    ArrowComponent(String materialName, ComponentType type, Item craftingItem, float damageModifier, float speedModifier, Supplier<ComponentEffect> effectSupplier) {
        this.materialName = materialName;
        this.type = type;
        this.craftingItem = craftingItem;
        this.optionalCraftingItemId = null;
        this.damageModifier = damageModifier;
        this.speedModifier = speedModifier;
        this.effectSupplier = effectSupplier;
    }

    ArrowComponent(String materialName, ComponentType type, String craftingItemId, float damageModifier, float speedModifier, Supplier<ComponentEffect> effectSupplier) {
        this.materialName = materialName;
        this.type = type;
        this.craftingItem = null;
        this.optionalCraftingItemId = Identifier.parse(craftingItemId);
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
        if (craftingItem != null) return craftingItem;
        //? if >=26.1 {
        return BuiltInRegistries.ITEM.getValue(optionalCraftingItemId);
        //? } else {
        /*return BuiltInRegistries.ITEM.get(optionalCraftingItemId);
        *///? }
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
